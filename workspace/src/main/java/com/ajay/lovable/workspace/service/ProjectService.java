package com.ajay.lovable.workspace.service;

import com.ajay.lovable.commonlib.enums.ProjectPermission;
import com.ajay.lovable.workspace.dto.project.ProjectRequest;
import com.ajay.lovable.workspace.dto.project.ProjectResponse;
import com.ajay.lovable.workspace.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
     List<ProjectSummaryResponse> getUserProjects();

     ProjectSummaryResponse getUserProjectById(Long id);

     ProjectResponse createProject(ProjectRequest request);

     ProjectResponse updateProject(Long id, ProjectRequest request);

    Void deleteProject(Long id);

    boolean hasPermission(Long projectId, ProjectPermission permission);

}
