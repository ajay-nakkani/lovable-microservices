package com.ajay.lovable.intelligenceservice.repository;

import com.ajay.lovable.intelligenceservice.entity.ChatSession;
import com.ajay.lovable.intelligenceservice.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
