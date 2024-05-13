package com.isxcode.torch.api.tenant.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckTenantReq {

    @Schema(title = "租户唯一id", example = "sy_123456789")
    @NotEmpty(message = "租户id不能为空")
    private String tenantId;
}
