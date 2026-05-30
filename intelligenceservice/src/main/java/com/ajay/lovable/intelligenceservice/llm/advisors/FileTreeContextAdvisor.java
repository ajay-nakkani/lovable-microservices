package com.ajay.lovable.intelligenceservice.llm.advisors;

import com.ajay.lovable.commonlib.dto.FileNode;
import com.ajay.lovable.intelligenceservice.client.WorkspaceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final WorkspaceClient workspaceClient;
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {

        Map<String, Object> context = chatClientRequest.context();

        Long projectId = Long.parseLong(context.getOrDefault("projectId",0).toString());

        ChatClientRequest agumentedChatClientRequest = agumentRequestWithFileTree(chatClientRequest,projectId);

        return streamAdvisorChain.nextStream(agumentedChatClientRequest);
    }

    private ChatClientRequest agumentRequestWithFileTree(ChatClientRequest request, Long projectId){

        List<Message> incomingMessages = request.prompt().getInstructions();

        Message systemMessage = incomingMessages.stream()
                .filter(m->m.getMessageType() == MessageType.SYSTEM)
                .findFirst().orElseGet(null);

        List<Message> userMessages = incomingMessages.stream()
                .filter(m->m.getMessageType() == MessageType.USER).toList();

        List<Message> allMessages = new ArrayList<>();

        // Add original system message
        if(systemMessage != null) allMessages.add(systemMessage);

        List<FileNode> fileNodeList = workspaceClient.getFileTree(projectId).files();

        String fileTreeContext = "\n\n --------FILE TREE------ \n\n" + fileNodeList.toString();


        allMessages.add(new SystemMessage(fileTreeContext));

        allMessages.addAll(userMessages);

        return request.mutate().prompt(new Prompt(allMessages,request.prompt().getOptions())).build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
