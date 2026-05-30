package com.ajay.lovable.workspace.mapper;


import com.ajay.lovable.workspace.dto.member.MemberResponse;
import com.ajay.lovable.workspace.entitiy.ProjectMember;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {


    @Mapping(target ="userId", source = "id.userId")
    MemberResponse toMemberResponseFromProjectMember(ProjectMember projectMember);
}
