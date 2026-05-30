package com.ajay.lovable.workspace.service.impl;


import com.ajay.lovable.commonlib.dto.PlanDto;
import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.commonlib.error.BadRequestException;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.workspace.client.AccountClient;
import com.ajay.lovable.workspace.dto.project.ProjectRequest;
import com.ajay.lovable.workspace.dto.project.ProjectResponse;
import com.ajay.lovable.workspace.dto.project.ProjectSummaryResponse;
import com.ajay.lovable.workspace.entitiy.Project;
import com.ajay.lovable.workspace.entitiy.ProjectMember;
import com.ajay.lovable.workspace.entitiy.ProjectMemberId;
import com.ajay.lovable.workspace.mapper.ProjectMapper;
import com.ajay.lovable.workspace.repository.ProjectMemberRepository;
import com.ajay.lovable.workspace.repository.ProjectRepository;
import com.ajay.lovable.workspace.service.ProjectService;
import com.ajay.lovable.workspace.service.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;

    AuthUtil authUtil ;

    ProjectTemplateService projectTemplateService;

    ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;

    AccountClient accountClient;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
//        if(!canCreateProject()) {
//            throw new BadRequestException("User cannot create a New project with current Plan, Upgrade plan now.");
//        }
        Long ownerUserId = authUtil.getCurrentUserId();


        Project newProject = Project.builder().name(request.name()).isPublic(false).build();

        newProject = projectRepository.save(newProject);

        ProjectMemberId projectMemberId = new ProjectMemberId(newProject.getId(), ownerUserId);

        ProjectMember newMember = ProjectMember.builder()
                                               .projectRole(ProjectRole.OWNER)
                                               .project(newProject)
                                               .acceptedAt(Instant.now())
                                               .id(projectMemberId)
                                               .invitedAt(Instant.now())
                                               .build();

        projectMemberRepository.save(newMember);

        projectTemplateService.initializeProjectFromTemplate(newProject.getId());

        return projectMapper.toProjectResponse(newProject);

    }


    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();

        List<ProjectRepository.ProjectWithRole> projectsWithRole = projectRepository.getAllAccessibleByUser(userId);
        return projectsWithRole.stream().map((p)->projectMapper.toProjectSummaryResponse(p.getProject(),p.getRole())).toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#id)")
    public ProjectSummaryResponse getUserProjectById(Long id) {
        Long userId = authUtil.getCurrentUserId();
        var projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(id,userId)
                .orElseThrow(()->new BadRequestException("Project Not Found"));
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(),projectWithRole.getRole());
    }



    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public Void deleteProject(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProjectById(projectId,userId);

        project.setDeletedAt(Instant.now());

        projectRepository.save(project);

        return null;
    }

    //INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long id,Long userId)
    {
        return projectRepository.findAccessibleProjectById(id,userId).orElseThrow();
    }

    private boolean canCreateProject() {
        Long userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();

        int maxAllowed = plan.maxProjects();
        int ownedCount = projectMemberRepository.countProjectOwnedByUser(userId);

        return ownedCount < maxAllowed;
    }

}
