package com.isxcode.torch.modules.workflow.repository;

import com.isxcode.torch.modules.workflow.entity.WorkflowEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 只负责数据库查询逻辑.
 */
@Repository
@CacheConfig(cacheNames = {"sy_engines"})
public interface WorkflowRepository extends JpaRepository<WorkflowEntity, String> {

    @Query("SELECT w FROM WorkflowEntity w WHERE w.name LIKE %:keyword% OR w.remark LIKE %:keyword% order by w.createDateTime desc ")
    Page<WorkflowEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    long countByTenantId(String tenantId);

    Optional<WorkflowEntity> findByName(String name);

    long countByStatus(String status);
}
