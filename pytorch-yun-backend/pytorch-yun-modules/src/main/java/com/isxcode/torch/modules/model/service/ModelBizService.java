package com.isxcode.torch.modules.model.service;

import com.isxcode.torch.api.datasource.req.*;
import com.isxcode.torch.api.datasource.res.*;
import com.isxcode.torch.api.model.req.AddModelReq;
import com.isxcode.torch.api.model.req.PageModelReq;
import com.isxcode.torch.api.model.req.UpdateModelReq;
import com.isxcode.torch.api.model.res.PageModelRes;

import javax.transaction.Transactional;

import com.isxcode.torch.modules.file.service.FileService;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.mapper.ModelMapper;
import com.isxcode.torch.modules.model.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.isxcode.torch.common.config.CommonConfig.JPA_TENANT_MODE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ModelBizService {

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    private final FileService fileService;

    public Page<PageModelRes> pageModel(PageModelReq pageModelReq) {

        JPA_TENANT_MODE.set(false);

        Page<ModelEntity> modelEntityPage = modelRepository.searchAll(pageModelReq.getSearchKeyWord(),
            PageRequest.of(pageModelReq.getPage(), pageModelReq.getPageSize()));

        // 翻译模型文件名称
        Page<PageModelRes> map = modelEntityPage.map(modelMapper::modelEntityToPageModelRes);
        map.getContent().forEach(e -> {
            if (!Strings.isEmpty(e.getModelFile())) {
                e.setModelFileName(fileService.getFileName(e.getModelFile()));
            }
        });

        return map;
    }

    public void addModel(AddModelReq addModelReq) {


    }

    public void updateModel(UpdateModelReq updateModelReq) {

    }
}
