package com.ajay.lovable.intelligenceservice.service.impl;

import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.intelligenceservice.dto.chat.ChatResponse;
import com.ajay.lovable.intelligenceservice.entity.ChatMessage;
import com.ajay.lovable.intelligenceservice.entity.ChatSession;
import com.ajay.lovable.intelligenceservice.entity.ChatSessionId;
import com.ajay.lovable.intelligenceservice.mapper.ChatMapper;
import com.ajay.lovable.intelligenceservice.repository.ChatMessageRepository;
import com.ajay.lovable.intelligenceservice.repository.ChatSessionRepository;
import com.ajay.lovable.intelligenceservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;


    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {

        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId,userId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
