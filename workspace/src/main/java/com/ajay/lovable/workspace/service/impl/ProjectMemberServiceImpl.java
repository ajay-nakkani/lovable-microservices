package com.ajay.lovable.workspace.service.impl;

import com.ajay.lovable.commonlib.dto.UserDto;
import com.ajay.lovable.commonlib.error.ResourceNotFoundException;
import com.ajay.lovable.commonlib.security.AuthUtil;
import com.ajay.lovable.workspace.client.AccountClient;
import com.ajay.lovable.workspace.dto.member.InviteMemberRequest;
import com.ajay.lovable.workspace.dto.member.MemberResponse;
import com.ajay.lovable.workspace.dto.member.UpdateMemberRoleRequest;
import com.ajay.lovable.workspace.entitiy.Project;
import com.ajay.lovable.workspace.entitiy.ProjectMember;
import com.ajay.lovable.workspace.entitiy.ProjectMemberId;
import com.ajay.lovable.workspace.mapper.ProjectMemberMapper;
import com.ajay.lovable.workspace.repository.ProjectMemberRepository;
import com.ajay.lovable.workspace.repository.ProjectRepository;
import com.ajay.lovable.workspace.service.ProjectMemberService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;


    AuthUtil authUtil;
    AccountClient accountClient;
    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {

        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProjectById(projectId, userId);

        return new ArrayList<>((projectMemberRepository.findByIdProjectId(
                projectId)).stream()
                           .map(projectMemberMapper::toMemberResponseFromProjectMember)
                           .toList());
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProjectById(projectId, userId);
        UserDto invitee = accountClient.getUserByEmail(request.username()).orElseThrow(
                () -> new ResourceNotFoundException("User", request.username())
        );


        if(invitee.id().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId newMemberId = ProjectMemberId.builder()
                                                     .projectId(projectId)
                                                     .userId(invitee.id())
                                                     .build();
        if (projectMemberRepository.existsById(newMemberId)) {
            throw new RuntimeException("already invited");
        }

        ProjectMember newMember = ProjectMember.builder()
                                               .id(newMemberId)
                                               .project(project)
                                               .projectRole(request.role())
                                               .invitedAt(Instant.now())
                                               .build();

            projectMemberRepository.save(newMember);
        return projectMemberMapper.toMemberResponseFromProjectMember(newMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {

        Long userId = authUtil.getCurrentUserId();


        Project project = getAccessibleProjectById(projectId,userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        if(!projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("member doesnot exist");
        }

        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());
        return projectMemberMapper.toMemberResponseFromProjectMember(projectMemberRepository.save(projectMember));

    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void deleteMember(Long projectId, Long memberId) {

        Long userId = authUtil.getCurrentUserId();


        Project project = getAccessibleProjectById(projectId,userId);


        ProjectMemberId projectMemberId = new ProjectMemberId(projectId,memberId);
        if(!projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("member doesnot exist");
        }

        projectMemberRepository.deleteById(projectMemberId);



    }

    //INTERNAL FUNC
    //INTERNAL FUNCTIONS
    public Project getAccessibleProjectById(Long id, Long userId) {
        return projectRepository.findAccessibleProjectById(id, userId)
                                .orElseThrow();
    }


}
