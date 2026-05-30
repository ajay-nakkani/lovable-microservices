package com.ajay.lovable.intelligenceservice.controller;


import com.ajay.lovable.intelligenceservice.dto.chat.ChatRequest;
import com.ajay.lovable.intelligenceservice.dto.chat.ChatResponse;
import com.ajay.lovable.intelligenceservice.dto.chat.StreamResponse;
import com.ajay.lovable.intelligenceservice.service.AiGenerationService;
import com.ajay.lovable.intelligenceservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final AiGenerationService aiGenerationService;
    private final ChatService chatservice;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamResponse>> streamChat(@RequestBody ChatRequest request)
    {

        return aiGenerationService
                .streamResponse(request.message(),request.projectId())
                .map(data-> ServerSentEvent.<StreamResponse>builder().data(data).build());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId){
        return ResponseEntity.ok(chatservice.getProjectChatHistory(projectId));
    }

}
