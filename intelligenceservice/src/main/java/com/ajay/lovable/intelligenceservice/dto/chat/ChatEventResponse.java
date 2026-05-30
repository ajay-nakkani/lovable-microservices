package com.ajay.lovable.intelligenceservice.dto.chat;

import com.ajay.lovable.commonlib.enums.ChatEventType;


public record ChatEventResponse (
    Long id,
    ChatEventType type,
    Integer sequenceOrder,
    String content,
    String filePath ,// NULL unless FILE_EDIT
    String metadata
){}
