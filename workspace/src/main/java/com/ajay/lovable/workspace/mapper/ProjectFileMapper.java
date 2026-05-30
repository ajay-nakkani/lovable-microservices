package com.ajay.lovable.workspace.mapper;

import com.ajay.lovable.commonlib.dto.FileNode;
import com.ajay.lovable.workspace.entitiy.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
