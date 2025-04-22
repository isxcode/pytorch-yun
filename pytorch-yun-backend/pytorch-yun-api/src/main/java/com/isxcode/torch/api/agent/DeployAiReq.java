package com.isxcode.torch.api.agent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployAiReq {

    private String modelFileId;

    private String modelCode;

    private String agentHomePath;

    private String aiId;
}
