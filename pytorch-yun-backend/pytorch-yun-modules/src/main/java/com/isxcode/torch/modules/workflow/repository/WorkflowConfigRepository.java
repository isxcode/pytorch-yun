package com.isxcode.torch.modules.workflow.repository;

import com.isxcode.torch.modules.workflow.entity.WorkflowConfigEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {"sy_workflow"})
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfigEntity, String> {
}
