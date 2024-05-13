package com.isxcode.torch.api.workflow.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnExternalCallRes {

    private String url;

    private String accessKey;

}
