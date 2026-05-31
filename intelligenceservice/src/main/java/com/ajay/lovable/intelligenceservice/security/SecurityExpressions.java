package com.ajay.lovable.intelligenceservice.security;


import com.ajay.lovable.commonlib.enums.ProjectPermission;
import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.intelligenceservice.client.WorkspaceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("security")
@RequiredArgsConstructor
@Slf4j
public class SecurityExpressions {


    private final AuthUtil authUtil;
    private final WorkspaceClient workspaceClient;

    public boolean hasPermission(Long projectId, ProjectPermission permission)
    {
        System.out.println("HAS_PERMISSION THREAD = " + Thread.currentThread().getName());
        System.out.println("HAS_PERMISSION AUTH = " +
                SecurityContextHolder.getContext().getAuthentication());

        try {
            return workspaceClient.checkPermission(projectId, permission);
        } catch (FeignException.Unauthorized e) {
            log.warn("Token expired or invalid during permission check for project: {}", projectId);
            throw new CredentialsExpiredException("JWT token is expired or invalid");
        } catch (FeignException e) {
            log.error("Workspace-service failed during permission check: {}", e.getMessage());
            return false;
        }
    }


    public boolean canViewProject(Long projectId)
    {
        return hasPermission(projectId,ProjectPermission.VIEW);

    }

    public boolean canEditProject(Long projectId)
    {
        System.out.println("HAS_PERMISSION THREAD = " + Thread.currentThread().getName());
        System.out.println("HAS_PERMISSION AUTH = " +
                SecurityContextHolder.getContext().getAuthentication());
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
