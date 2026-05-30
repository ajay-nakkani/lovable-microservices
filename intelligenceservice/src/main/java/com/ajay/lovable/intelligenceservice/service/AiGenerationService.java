package com.ajay.lovable.intelligenceservice.service;


import com.ajay.lovable.intelligenceservice.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;


public interface AiGenerationService {
    public Flux<StreamResponse> streamResponse(String message, Long projectId);
}
