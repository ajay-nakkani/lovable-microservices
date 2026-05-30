package com.ajay.lovable.intelligenceservice.security;


import com.ajay.lovable.commonlib.enums.ProjectPermission;
import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.intelligenceservice.client.WorkspaceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {


    private final AuthUtil authUtil;
    private final WorkspaceClient workspaceClient;

    public boolean hasPermission(Long projectId, ProjectPermission permission)
    {
       return workspaceClient.checkPermission(projectId,permission);
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
