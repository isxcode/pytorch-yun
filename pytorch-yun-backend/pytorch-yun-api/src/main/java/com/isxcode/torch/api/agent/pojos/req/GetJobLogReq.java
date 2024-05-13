package com.isxcode.torch.api.agent.pojos.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJobLogReq {

    private String jobId;

    private String flinkHome;

    private String agentType;

    private String agentHomePath;

    private String workInstanceId;
}
