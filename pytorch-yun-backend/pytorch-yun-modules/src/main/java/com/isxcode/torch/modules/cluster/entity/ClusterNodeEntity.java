package com.isxcode.torch.modules.cluster.entity;

import static com.isxcode.torch.common.config.CommonConfig.TENANT_ID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE PY_CLUSTER_NODE SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "PY_CLUSTER_NODE")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
public class ClusterNodeEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.torch.config.GeneratedValueConfig")
    private String id;

    private String name;

    private String remark;

    private String status;

    private LocalDateTime checkDateTime;

    private Double allMemory;

    private Double usedMemory;

    private Double allStorage;

    private Double usedStorage;

    private Double cpuPercent;

    private String clusterId;

    private String host;

    private String port;

    private String username;

    private String passwd;

    private Boolean installSparkLocal;

    private String agentHomePath;

    private String agentPort;

    private String hadoopHomePath;

    private String sparkHomePath;

    private String agentLog;

    @CreatedDate
    private LocalDateTime createDateTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

    @CreatedBy
    private String createBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Transient
    private Integer deleted;

    private String tenantId;

    @PrePersist
    public void prePersist() {
        this.tenantId = TENANT_ID.get();
    }
}
