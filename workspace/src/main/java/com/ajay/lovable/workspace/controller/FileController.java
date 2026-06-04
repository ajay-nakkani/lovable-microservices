package com.ajay.lovable.workspace.controller;


import com.ajay.lovable.commonlib.dto.FileTreeDto;
import com.ajay.lovable.workspace.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/files")
@RequiredArgsConstructor
public class FileController {

    private final ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<FileTreeDto> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<String> getFile(@PathVariable Long projectId, @RequestParam String path)
    {
        return ResponseEntity.ok(fileService.getFileContent(projectId,path));
    }

    @GetMapping("/download-zip")
    public ResponseEntity<byte[]> downloadProjectZip(@PathVariable Long projectId) {
        byte[] zipBytes = fileService.downloadProjectZip(projectId);
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"project-" + projectId + ".zip\"")
                             .contentType(MediaType.APPLICATION_OCTET_STREAM)
                             .body(zipBytes);
    }

}
