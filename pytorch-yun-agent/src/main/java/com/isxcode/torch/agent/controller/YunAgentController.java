package com.isxcode.torch.agent.controller;

import com.isxcode.torch.agent.service.AcornAgentBizService;
import com.isxcode.torch.api.agent.pojos.req.GetJobInfoReq;
import com.isxcode.torch.api.agent.pojos.req.GetJobLogReq;
import com.isxcode.torch.api.agent.pojos.req.StopJobReq;
import com.isxcode.torch.api.agent.pojos.req.SubmitJobReq;
import com.isxcode.torch.api.agent.pojos.res.GetJobInfoRes;
import com.isxcode.torch.api.agent.pojos.res.GetJobLogRes;
import com.isxcode.torch.api.agent.pojos.res.StopJobRes;
import com.isxcode.torch.api.agent.pojos.res.SubmitJobRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.immutables.value.Value;
import org.springframework.web.bind.annotation.*;

@Tag(name = "代理模块")
@RequestMapping("acorn")
@RestController
@RequiredArgsConstructor
public class YunAgentController {

    private final AcornAgentBizService acornAgentBizService;

    @Operation(summary = "提交作业接口")
    @PostMapping("/submitJob")
    public SubmitJobRes submitJob(@Value @RequestBody SubmitJobReq submitJobReq) {

        return acornAgentBizService.submitJob(submitJobReq);
    }

    @Operation(summary = "获取作业信息接口")
    @PostMapping("/getJobInfo")
    public GetJobInfoRes getJobInfo(@Value @RequestBody GetJobInfoReq getJobInfoReq) {

        return acornAgentBizService.getJobInfo(getJobInfoReq);
    }

    @Operation(summary = "获取日志接口")
    @PostMapping("/getJobLog")
    public GetJobLogRes getJobLog(@Value @RequestBody GetJobLogReq getJobLogReq) {

        return acornAgentBizService.getJobLog(getJobLogReq);
    }

    @Operation(summary = "停止作业接口")
    @PostMapping("/stopJob")
    public StopJobRes stopJob(@Value @RequestBody StopJobReq stopJobReq) {

        return acornAgentBizService.stopJob(stopJobReq);
    }

    @Operation(summary = "心跳检测接口")
    @GetMapping("/heartCheck")
    public void heartCheck() {

    }
}
