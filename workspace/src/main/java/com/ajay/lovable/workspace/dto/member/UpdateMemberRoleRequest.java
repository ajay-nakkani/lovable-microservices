package com.ajay.lovable.workspace.dto.member;

import com.ajay.lovable.commonlib.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
