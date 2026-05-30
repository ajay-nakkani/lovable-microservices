package com.ajay.lovable.workspace.dto.project;


import com.ajay.lovable.commonlib.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(Long id, String name, Instant createdAt, Instant updatedAt, ProjectRole role) {
}
