package com.isxcode.torch.api.real.pojos.res;

import com.isxcode.torch.api.work.pojos.dto.SyncWorkConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRealRes {

    private String id;

    private List<String> libConfig;

    private List<String> funcConfig;

    private String sparkConfig;

    private String clusterId;

    private SyncWorkConfig syncConfig;
}
