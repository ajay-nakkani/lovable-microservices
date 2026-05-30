package com.ajay.lovable.workspace.service;

import com.ajay.lovable.workspace.dto.DeployResponse;

public interface DeploymentService {
     DeployResponse deploy(Long projectId);
}
