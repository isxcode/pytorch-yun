package com.isxcode.torch.api.cluster.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CheckAgentReq {
    @Schema(title = "计算引擎唯一id", example = "py_b0288cadb2ab4325ae519ff329a95cda")
    @NotEmpty(message = "节点id不能为空")
    private String engineNodeId;
}
