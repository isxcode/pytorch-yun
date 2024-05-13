package com.isxcode.torch.api.agent.pojos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitJobReq {

    private String agentType;

    private String flinkHome;

    private AcornPluginReq acornPluginReq;

    private String entryClass;

    private String appResource;

    private String appName;

    private String agentHomePath;

    private String workInstanceId;

    private String workType;

    private List<String> programArgs;
}
