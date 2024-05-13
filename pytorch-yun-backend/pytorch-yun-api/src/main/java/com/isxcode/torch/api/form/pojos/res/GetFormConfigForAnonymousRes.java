package com.isxcode.torch.api.form.pojos.res;

import com.isxcode.torch.api.form.pojos.dto.FormComponentDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetFormConfigForAnonymousRes {

    private String formId;

    private List<FormComponentDto> components;

    private String datasourceId;

    private String status;

    private String mainTable;

    private String formVersion;
}
