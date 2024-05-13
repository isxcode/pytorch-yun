package com.isxcode.torch.api.instance.pojos.res;

import lombok.Data;

@Data
public class WfiQueryWorkFlowInstancesRes {

    private String workflowInstanceId;

    private String workflowName;

    private String startDateTime;

    private String endDateTime;

    private String status;

    private String type;

    private Long duration;

    private String nextPlanDateTime;

    private String planStartDateTime;
}
