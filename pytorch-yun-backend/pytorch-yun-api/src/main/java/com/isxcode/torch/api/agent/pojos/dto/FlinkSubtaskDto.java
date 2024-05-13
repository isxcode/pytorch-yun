package com.isxcode.torch.api.agent.pojos.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class FlinkSubtaskDto {

    @JsonAlias("taskmanager-id")
    private String taskmanagerId;
}
