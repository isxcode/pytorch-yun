package com.isxcode.torch.api.alarm.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteAlarmReq {

    @Schema(title = "告警id", example = "py_123")
    @NotEmpty(message = "告警id不能为空")
    private String id;
}
