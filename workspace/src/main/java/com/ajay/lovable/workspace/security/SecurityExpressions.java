package com.ajay.lovable.workspace.security;



import com.ajay.lovable.commonlib.enums.ProjectPermission;
import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.workspace.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {


    private final ProjectMemberRepository projectMemberRepository;

    private final AuthUtil authUtil;

    public boolean hasPermission(Long projectId, ProjectPermission permission)
    {
        Long userId = authUtil.getCurrentUserId();

        Optional<ProjectRole> role = projectMemberRepository.findRoleByUserIdAndProjectId(userId,projectId);

        return role.map(x->{
            return x.getPermissions().contains(permission);
        }).orElse(false);
    }


    public boolean canViewProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.VIEW);

    }

    public boolean canEditProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.MANAGE_MEMBERS);
    }

}
