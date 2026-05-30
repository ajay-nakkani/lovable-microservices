package com.ajay.lovable.workspace.dto.project;

import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @Size(min = 4,max = 50) String name
) {
}
