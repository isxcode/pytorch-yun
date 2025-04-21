package com.isxcode.torch.modules.model.mapper;

import com.isxcode.torch.api.model.res.PageModelRes;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    PageModelRes modelEntityToPageModelRes(ModelEntity modelEntity);
}
