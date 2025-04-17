package com.isxcode.torch.agent.controller;

import com.isxcode.torch.agent.service.PytorchYunAgentBizService;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "至慧云代理模块")
@RestController
@RequiredArgsConstructor
public class PytorchYunAgentController {

    private final PytorchYunAgentBizService sparkYunAgentBizService;

    @Operation(summary = "心跳检测接口")
    @PostMapping(AgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}
}
