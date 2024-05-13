package com.isxcode.torch.backend.api.base.exceptions;

import lombok.Getter;
import lombok.Setter;

public class AgentResponseException extends RuntimeException {

    @Setter
    @Getter
    private String msg;

    public AgentResponseException(String msg) {

        this.msg = msg;
    }
}
