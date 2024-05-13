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
public class FlinkRestRunReq {

    private String entryClass;

    private String parallelism;

    private String programArgs;

    private List<String> programArgsList;

    private String savepointPath;

    private boolean allowNonRestoredState;
}
