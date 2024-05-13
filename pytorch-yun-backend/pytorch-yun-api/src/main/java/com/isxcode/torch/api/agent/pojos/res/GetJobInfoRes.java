package com.isxcode.torch.api.agent.pojos.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetJobInfoRes {

    private String jobId;

    private String status;

    private List<String> vertices;
}
