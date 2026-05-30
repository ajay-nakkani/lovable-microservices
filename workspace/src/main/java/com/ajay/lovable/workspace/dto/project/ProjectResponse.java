package com.ajay.lovable.workspace.dto.project;


import java.time.Instant;

public record ProjectResponse(
        Long id, String name, Instant createdAt, Instant updatedA
) {
}
