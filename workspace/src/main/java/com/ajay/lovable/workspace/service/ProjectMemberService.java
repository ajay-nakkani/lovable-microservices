package com.ajay.lovable.workspace.service;

import com.ajay.lovable.workspace.dto.member.InviteMemberRequest;
import com.ajay.lovable.workspace.dto.member.MemberResponse;
import com.ajay.lovable.workspace.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    public  List<MemberResponse> getProjectMembers(Long projectId) ;

    public  MemberResponse inviteMember(Long projectId, InviteMemberRequest request) ;


    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

     void deleteMember(Long projectId, Long memberId);
}
