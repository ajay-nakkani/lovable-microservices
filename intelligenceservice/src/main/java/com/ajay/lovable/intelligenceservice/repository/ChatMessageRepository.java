package com.ajay.lovable.intelligenceservice.repository;


import com.ajay.lovable.intelligenceservice.entity.ChatMessage;
import com.ajay.lovable.intelligenceservice.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    @Query("""
            SELECT DISTINCT m FROM ChatMessage m 
            LEFT JOIN FETCH m.events e 
            where m.chatSession = :chatSession 
            ORDER BY m.createdAt ASC, e.sequenceOrder ASC
            """)
    List<ChatMessage> findByChatSession(ChatSession chatSession);
}

// N+1 Query Problem
// chat_messages query 1
// chat_events with chat_message id: 1
// chat_events with chat_message id: 1
// chat_events with chat_message id: 1
// ..
// chat_events with chat_message id: N