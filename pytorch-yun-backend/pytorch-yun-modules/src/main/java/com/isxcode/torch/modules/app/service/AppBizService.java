package com.isxcode.torch.modules.app.service;


import javax.transaction.Transactional;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.constants.AppStatus;
import com.isxcode.torch.api.app.req.AddAppReq;
import com.isxcode.torch.api.app.req.ConfigAppReq;
import com.isxcode.torch.api.app.req.PageAppReq;
import com.isxcode.torch.api.app.req.UpdateAppReq;
import com.isxcode.torch.api.app.res.AddAppRes;
import com.isxcode.torch.api.app.res.PageAppRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.mapper.AppMapper;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AppBizService {

    private final AppRepository appRepository;

    private final AppMapper appMapper;

    private final AppService appService;

    private final AiService aiService;

    private final UserService userService;

    public AddAppRes addApp(AddAppReq addAppReq) {

        Optional<AppEntity> appEntityByName = appRepository.findByName(addAppReq.getName());
        if (appEntityByName.isPresent()) {
            throw new IsxAppException("应用重复");
        }

        AppEntity appEntity = appMapper.addAppReqToAppEntity(addAppReq);
        appEntity.setStatus(AppStatus.ENABLE);
        appEntity.setCheckDateTime(LocalDateTime.now());
        appEntity = appRepository.save(appEntity);

        return appMapper.appEntityToAddAppRes(appEntity);
    }

    public void updateApp(UpdateAppReq updateAppReq) {

        // 检测数据源名称重复
        Optional<AppEntity> appEntityByName = appRepository.findByName(updateAppReq.getName());
        if (appEntityByName.isPresent() && !appEntityByName.get().getId().equals(updateAppReq.getId())) {
            throw new IsxAppException("应用名称重复");
        }

        AppEntity appEntity = appMapper.updateAppReqToAppEntity(updateAppReq);
        appRepository.save(appEntity);
    }

    public void configApp(ConfigAppReq configAppReq) {

        AppEntity app = appService.getApp(configAppReq.getId());

        app.setPrompt(configAppReq.getPrompt());
        app.setBaseConfig(JSON.toJSONString(configAppReq.getBaseConfig()));
        app.setResources(JSON.toJSONString(configAppReq.getResources()));

        appRepository.save(app);
    }

    public Page<PageAppRes> pageApp(PageAppReq pageAppReq) {

        Page<AppEntity> appEntityPage = appRepository.searchAll(pageAppReq.getSearchKeyWord(),
            PageRequest.of(pageAppReq.getPage(), pageAppReq.getPageSize()));

        Page<PageAppRes> result = appEntityPage.map(appMapper::appEntityToPageAppRes);
        result.forEach(appEntity -> {
            appEntity.setAiName(aiService.getAiName(appEntity.getAiId()));
            appEntity.setCreateByUsername(userService.getUserName(appEntity.getCreateBy()));
        });
        return result;
    }
}
