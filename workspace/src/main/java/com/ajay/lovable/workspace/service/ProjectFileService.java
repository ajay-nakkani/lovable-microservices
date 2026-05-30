package com.ajay.lovable.workspace.service;

import com.ajay.lovable.workspace.dto.files.FileContentResponse;
import com.ajay.lovable.workspace.dto.files.FileTreeResponse;


public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
