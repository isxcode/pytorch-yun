package com.isxcode.torch.api.agent.pojos.res;

import com.isxcode.torch.api.agent.pojos.dto.FlinkSubtaskDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlinkRestVerticesRes {

    private List<FlinkSubtaskDto> subtasks;
}
