package com.ajay.lovable.workspace.dto.member;


import com.ajay.lovable.commonlib.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
