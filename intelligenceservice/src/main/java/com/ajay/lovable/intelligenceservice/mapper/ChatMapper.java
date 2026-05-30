package com.ajay.lovable.intelligenceservice.mapper;


import com.ajay.lovable.intelligenceservice.dto.chat.ChatResponse;
import com.ajay.lovable.intelligenceservice.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
