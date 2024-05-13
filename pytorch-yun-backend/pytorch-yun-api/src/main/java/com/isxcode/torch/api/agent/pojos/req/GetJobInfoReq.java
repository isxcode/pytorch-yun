package com.isxcode.torch.api.agent.pojos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetJobInfoReq {

    private String jobId;

    private String agentHome;

    private String agentType;

    private String flinkHome;
}
