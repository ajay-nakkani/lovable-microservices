package com.ajay.lovable.workspace.service.impl;

import com.ajay.lovable.commonlib.dto.FileNode;
import com.ajay.lovable.commonlib.dto.FileTreeDto;
import com.ajay.lovable.commonlib.error.ResourceNotFoundException;
import com.ajay.lovable.workspace.entitiy.Project;
import com.ajay.lovable.workspace.entitiy.ProjectFile;
import com.ajay.lovable.workspace.mapper.ProjectFileMapper;
import com.ajay.lovable.workspace.repository.ProjectFileRepository;
import com.ajay.lovable.workspace.repository.ProjectRepository;
import com.ajay.lovable.workspace.service.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectFileMapper projectFileMapper;


    @Value("${minio.project-bucket}")
    private String projectBucket;

    @Override
    public FileTreeDto getFileTree(Long projectId) {

        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);

        List<FileNode> fileNodes = projectFileMapper.toListOfFileNode(projectFileList);

        return new FileTreeDto(fileNodes);
    }


    @Override
    public String getFileContent(Long projectId, String path) {

        String objectName = projectId + "/" + path;
        try{
            InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                                 .bucket(projectBucket)
                                 .object(objectName)
                                 .build()
            );
           // String content = new String(is.readAllBytes(),StandardCharsets.UTF_8);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (Exception e){
            log.error("Failed to read file: {}/{}",projectId,path,e);
            throw new RuntimeException("Failed to read the file content",e);
        }
    }


    @Override
    @Transactional
    public void saveFile(Long projectId, String filePath, String content) {
        log.info("Saving File: {}", filePath);

        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ResourceNotFoundException("Project",projectId.toString()));

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);


            // saving the file content
            minioClient.putObject(
                    PutObjectArgs.builder()
                                 .bucket(projectBucket)
                                 .object(objectKey)
                                 .stream(inputStream, contentBytes.length, -1)
                                 .contentType(determineContentType(filePath))
                                 .build());

            // Saving the metaData
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                                                    .orElseGet(() -> ProjectFile.builder()
                                                                                .project(project)
                                                                                .path(cleanPath)
                                                                                .miniObjectKey(objectKey) // Use the key we generated
                                                                                .createdAt(Instant.now())
                                                                                .build());

            file.setUpdatedAt(Instant.now());

            projectFileRepository.save(file);

            log.info("Saved file: {}", objectKey);

        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }


    }

    @Override
    public byte[] downloadProjectZip(Long projectId) {
        List<ProjectFile> projectFiles = projectFileRepository.findByProjectId(projectId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (ProjectFile file : projectFiles) {
                String objectName = projectId + "/" + file.getPath();
                try (InputStream is = minioClient.getObject(
                        GetObjectArgs.builder()
                                     .bucket(projectBucket)
                                     .object(objectName)
                                     .build())) {

                    zos.putNextEntry(new ZipEntry(file.getPath()));
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    zos.closeEntry();
                }
            }

            zos.finish();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to create zip for project: {}", projectId, e);
            throw new RuntimeException("Failed to create project zip", e);
        }
    }

    private String determineContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }
}
