package com.isxcode.torch.agent.controller;

import com.isxcode.torch.agent.service.PytorchYunAgentBizService;
import com.isxcode.torch.api.agent.DeployAiReq;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Tag(name = "至慧云代理模块")
@RestController
@RequiredArgsConstructor
public class PytorchYunAgentController {

    private final PytorchYunAgentBizService pytorchYunAgentBizService;

    @Operation(summary = "部署ai接口")
    @PostMapping(AgentUrl.DEPLOY_AI)
    @SuccessResponse("部署成功")
    public void deployAi(@Valid @RequestBody DeployAiReq deployAiReq) {

        pytorchYunAgentBizService.deployAi(deployAiReq);
    }

    @Operation(summary = "调用ai接口")
    @PostMapping(AgentUrl.CHAT_AI)
    @SuccessResponse("对话请求成功")
    public void chatAi() {

    }

    @Operation(summary = "心跳检测接口")
    @PostMapping(AgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}
}
