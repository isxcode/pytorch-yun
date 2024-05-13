package com.isxcode.torch.api.agent.pojos.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestRunRes {

    private String jobid;
}
