package com.ajay.lovable.workspace.controller;

import com.ajay.lovable.workspace.dto.member.InviteMemberRequest;
import com.ajay.lovable.workspace.dto.member.MemberResponse;
import com.ajay.lovable.workspace.dto.member.UpdateMemberRoleRequest;
import com.ajay.lovable.workspace.service.ProjectMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController{

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers(@PathVariable Long projectId)
    {


        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));

    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(@PathVariable Long projectId, @RequestBody @Valid InviteMemberRequest memberRequest)
    {


        return ResponseEntity.ok(projectMemberService.inviteMember(projectId,memberRequest));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId, @RequestBody @Valid UpdateMemberRoleRequest request)
    {


        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId,memberId,request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long projectId,@PathVariable Long memberId)
    {
        projectMemberService.deleteMember(projectId,memberId);
        return ResponseEntity.noContent().build();
    }

}
