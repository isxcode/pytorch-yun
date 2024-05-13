package com.isxcode.torch.api.user.pojos.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAnonymousTokenRes {

    private String token;
}
