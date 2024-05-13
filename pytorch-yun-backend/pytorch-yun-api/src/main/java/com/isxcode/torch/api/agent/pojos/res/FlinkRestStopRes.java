package com.isxcode.torch.api.agent.pojos.res;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestStopRes {

    @JsonAlias("request-id")
    private String requestId;
}
