package com.isxcode.torch.api.agent.pojos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopJobReq {

    private String flinkHome;

    private String jobId;

    private String agentType;

    private String agentHomePath;
}
