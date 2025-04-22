package com.isxcode.torch.modules.ai.run;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployAiContext {

    private String aiId;

    private String clusterId;

    private String modelCode;

    private String modelFileId;
}
