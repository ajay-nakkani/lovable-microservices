package com.ajay.lovable.intelligenceservice.repository;

import com.ajay.lovable.intelligenceservice.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent,Long> {
}
