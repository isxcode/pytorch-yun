package com.isxcode.torch.api.workflow.pojos.res;

import com.isxcode.torch.api.work.pojos.dto.CronConfig;
import lombok.Data;

@Data
public class GetWorkflowRes {

    private Object webConfig;

    private CronConfig cronConfig;
}
