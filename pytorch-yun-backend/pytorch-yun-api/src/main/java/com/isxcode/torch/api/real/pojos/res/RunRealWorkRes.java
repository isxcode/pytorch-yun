package com.isxcode.torch.api.real.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunRealWorkRes {

    private String status;

    private String errLog;

    private String appId;
}
