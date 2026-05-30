package com.ajay.lovable.intelligenceservice.dto.chat;


import com.ajay.lovable.commonlib.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        MessageRole role, // USER, ASSISTANT
        List<ChatEventResponse> events,
        String content,
        Integer tokensUsed,
        Instant createdAt
) {
}
