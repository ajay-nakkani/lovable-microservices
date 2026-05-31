package com.ajay.lovable.workspace.repository;

import com.ajay.lovable.workspace.entitiy.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
