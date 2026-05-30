package com.ajay.lovable.intelligenceservice.service;




import com.ajay.lovable.intelligenceservice.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}
