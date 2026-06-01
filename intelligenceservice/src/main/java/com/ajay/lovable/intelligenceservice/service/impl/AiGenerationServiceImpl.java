package com.ajay.lovable.intelligenceservice.service.impl;

import com.ajay.lovable.commonlib.enums.ChatEventStatus;
import com.ajay.lovable.commonlib.enums.ChatEventType;
import com.ajay.lovable.commonlib.enums.MessageRole;
import com.ajay.lovable.commonlib.event.FileStoreRequestEvent;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.intelligenceservice.client.WorkspaceClient;
import com.ajay.lovable.intelligenceservice.dto.chat.StreamResponse;
import com.ajay.lovable.intelligenceservice.entity.ChatEvent;
import com.ajay.lovable.intelligenceservice.entity.ChatMessage;
import com.ajay.lovable.intelligenceservice.entity.ChatSession;
import com.ajay.lovable.intelligenceservice.entity.ChatSessionId;
import com.ajay.lovable.intelligenceservice.llm.LlmResponseParser;
import com.ajay.lovable.intelligenceservice.llm.UtilsPrompt;
import com.ajay.lovable.intelligenceservice.llm.advisors.FileTreeContextAdvisor;
import com.ajay.lovable.intelligenceservice.llm.tools.CodeGenerationTools;
import com.ajay.lovable.intelligenceservice.repository.ChatEventRepository;
import com.ajay.lovable.intelligenceservice.repository.ChatMessageRepository;
import com.ajay.lovable.intelligenceservice.repository.ChatSessionRepository;
import com.ajay.lovable.intelligenceservice.service.AiGenerationService;

import com.ajay.lovable.intelligenceservice.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.springframework.kafka.core.KafkaTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {
    private final ChatEventRepository chatEventRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final ChatClient chatClient;

    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepository;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    private final WorkspaceClient workspaceClient;

    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final UsageService usageService;

    private final KafkaTemplate kafkaTemplate;


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userPrompt, Long projectId) {
        System.out.println("STREAM THREAD = " + Thread.currentThread().getName());
        System.out.println("STREAM AUTH = " +
                SecurityContextHolder.getContext().getAuthentication());
        Long userId = authUtil.getCurrentUserId();

//        usageService.checkDailyTokensUsage();

        ChatSession chatSession = createChatSessionIfNotExists(userId, projectId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectId,workspaceClient);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                         .system(UtilsPrompt.CODE_GENERATION_SYSTEM_PROMPT)
                         .user(userPrompt)
                         .tools(codeGenerationTools)
                         .advisors(
                                 advisorSpec -> {
                                     advisorSpec.params(advisorParams);
                                     advisorSpec.advisors(fileTreeContextAdvisor);
                                 }

                         )
                         .stream()
                         .chatResponse()
                         .doOnNext(chatResponse ->
                         {
                             if (chatResponse.getResults() != null && !chatResponse.getResults().isEmpty()) {

                                 String content = chatResponse.getResult()
                                                              .getOutput()
                                                              .getText();
                                 if (content != null && !content.isEmpty() && endTime.get() == 0) {
                                     endTime.set(System.currentTimeMillis());
                                 }


                                 if (chatResponse.getMetadata()
                                                 .getUsage() != null) {
                                     usageRef.set(chatResponse.getMetadata()
                                                              .getUsage());
                                 }

                                 fullResponseBuffer.append(content);
                             }
                         })
                         .doOnComplete(() -> {
                             Schedulers.boundedElastic()
                                       .schedule(() -> {
//                      parseAndSaveFiles(fullResponseBuffer.toString(), projectId);

                                           long duration = endTime.get() - startTime.get();

                                           finalizeChats(userPrompt, chatSession, fullResponseBuffer.toString(), duration,usageRef.get(),userId);
                                       });
                         })
                         .doOnCancel(() -> log.warn("Stream CANCELLED"))
                         .doOnError(error -> log.error("Error duing streaming for projectId" + projectId))
                         .filter(response -> response.getResult()
                                                     .getOutput()
                                                     .getText() != null)
                         .map(response -> {
                             if (response.getResults() != null && !response.getResults().isEmpty()) {
                                 String text = response.getResult().getOutput().getText();
                                 return new StreamResponse(text != null ? text : "");
                             }

                             return new StreamResponse("");
                         });
    }


    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration,Usage usage,Long userId) {

        Long projectId = chatSession.
                                    getId().getProjectId();

        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getId().getUserId(), totalTokens);
        }

        // Save the user Message in the chatmessage repository
        chatMessageRepository.save(
                ChatMessage
                        .builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()


        );
        // Now store assistant chat message (-- no content for assistant chat message )
        ChatMessage assistantChatMessage = ChatMessage.builder()
                                                      .chatSession(chatSession)
                                                      .role(MessageRole.ASSISTANT)
                                                      .content("Assistant Message here..")
                                                      .tokensUsed(usage.getCompletionTokens())
                                                      .build();
        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);


        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);

        chatEventList.addFirst(ChatEvent.builder().
                                        type(ChatEventType.THOUGHT)
                                        .status(ChatEventStatus.CONFIRMED)
                                        .chatMessage(assistantChatMessage)
                                        .content("Thought for " + duration + "s")
                                        .sequenceOrder(0)
                                        .build());

        chatEventList.stream()
                     .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                     .forEach(e -> {
                         String sagaId = UUID.randomUUID().toString();
                         e.setSagaId(sagaId);
                         FileStoreRequestEvent fileStoreRequestEvent = new FileStoreRequestEvent(
                                 projectId,
                                 sagaId,
                                 e.getFilePath(),
                                 e.getContent(),
                                 userId
                         );
                         log.info("Storage request event sent: {}", e.getFilePath());
                         kafkaTemplate.send("file-storage-request-event", "project-"+projectId, fileStoreRequestEvent);                     });




        chatEventRepository.saveAll(chatEventList);

    }
//
//    private void parseAndSaveFiles(StringBuilder responseBuffer, Long projectId) {
//
//        Matcher matcher = FILE_TAG_PATTERN.matcher(responseBuffer);
//        while (matcher.find()) {
//            String filePath = matcher.group(1);
//            String fileContent = matcher.group(2).trim();
//
//            projectFileService.saveFile(projectId, filePath, fileContent);
//    }}

    private ChatSession createChatSessionIfNotExists(Long userId, Long projectId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
                                                       .orElse(null);

        if (chatSession == null) {

            chatSession = ChatSession.builder()
                                     .id(chatSessionId)
                                     .build();
            chatSessionRepository.save(chatSession);
        }
        return chatSession;

    }


}
