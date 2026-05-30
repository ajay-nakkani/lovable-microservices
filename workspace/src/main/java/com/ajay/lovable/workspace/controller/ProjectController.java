package com.ajay.lovable.workspace.controller;


import com.ajay.lovable.workspace.dto.DeployResponse;
import com.ajay.lovable.workspace.dto.project.ProjectRequest;
import com.ajay.lovable.workspace.dto.project.ProjectResponse;
import com.ajay.lovable.workspace.dto.project.ProjectSummaryResponse;
import com.ajay.lovable.workspace.service.DeploymentService;
import com.ajay.lovable.workspace.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final DeploymentService deploymentService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects()
    {
        return ResponseEntity.ok(projectService.getUserProjects());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSummaryResponse> getProjectById(@PathVariable Long id){


        return ResponseEntity.ok(projectService.getUserProjectById(id));

    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,@RequestBody @Valid ProjectRequest request)
    {
        return ResponseEntity.ok(projectService.updateProject(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        return ResponseEntity.ok(projectService.deleteProject(id));
    }


    @PostMapping("/{id}/deploy")
    public ResponseEntity<DeployResponse> deployProject(@PathVariable Long id) {
        return ResponseEntity.ok(deploymentService.deploy(id));
    }








}
