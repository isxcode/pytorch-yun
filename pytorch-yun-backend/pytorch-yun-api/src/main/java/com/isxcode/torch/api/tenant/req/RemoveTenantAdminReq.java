package com.isxcode.torch.api.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RemoveTenantAdminReq {

    @Schema(description = "关系唯一id", example = "py_ff3c1b52f8b34c45ab2cf24b6bccd480")
    @NotEmpty(message = "关系id不能为空")
    private String tenantUserId;
}
