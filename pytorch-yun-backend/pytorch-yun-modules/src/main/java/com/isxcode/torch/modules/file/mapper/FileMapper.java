package com.isxcode.torch.modules.file.mapper;

import com.isxcode.torch.api.file.pojos.res.PageFileRes;
import com.isxcode.torch.modules.file.entity.FileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {

    PageFileRes fileEntityToPageFileRes(FileEntity fileEntity);
}
