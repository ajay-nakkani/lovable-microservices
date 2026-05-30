package com.ajay.lovable.workspace.mapper;


import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.workspace.dto.project.ProjectResponse;
import com.ajay.lovable.workspace.dto.project.ProjectSummaryResponse;
import com.ajay.lovable.workspace.entitiy.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects);

}
