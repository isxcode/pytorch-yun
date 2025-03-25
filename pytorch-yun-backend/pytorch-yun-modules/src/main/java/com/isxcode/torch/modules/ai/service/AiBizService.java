package com.isxcode.torch.modules.ai.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.ai.req.AddAiReq;
import com.isxcode.torch.api.ai.req.PageAiReq;
import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.ai.res.PageAiRes;

import javax.transaction.Transactional;

import com.isxcode.torch.api.app.constants.DefaultAppStatus;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.mapper.AiMapper;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.cluster.service.ClusterService;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.service.ModelService;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.isxcode.torch.common.config.CommonConfig.JPA_TENANT_MODE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AiBizService {

    private final AiRepository aiRepository;

    private final AiMapper aiMapper;

    private final AiService aiService;

    private final UserService userService;

    private final ClusterService clusterService;

    private final ModelService modelService;

    private final AppRepository appRepository;

    public void addAi(AddAiReq addAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(addAiReq.getName());
        if (aiEntityByName.isPresent()) {
            throw new IsxAppException("Ai名称重复");
        }

        AiEntity aiEntity = aiMapper.addAiReqToAiEntity(addAiReq);
        if (addAiReq.getAuthConfig() != null) {
            aiEntity.setAuthConfig(JSON.toJSONString(addAiReq.getAuthConfig()));
        }
        aiEntity.setCheckDateTime(LocalDateTime.now());
        aiEntity.setStatus(AiStatus.ENABLE);
        aiEntity = aiRepository.save(aiEntity);

        // 自动创建对应的应用
        AppEntity appEntity = new AppEntity();
        appEntity.setStatus(AiStatus.ENABLE);
        appEntity.setName(addAiReq.getName());
        appEntity.setAiId(aiEntity.getId());
        appEntity.setRemark("默认自建");

        // 判断是否需要制定默认app应用
        List<AppEntity> allApp = appRepository.findAll();
        if (allApp.isEmpty()) {
            appEntity.setDefaultApp(DefaultAppStatus.ENABLE);
        }else{
            appEntity.setDefaultApp(DefaultAppStatus.DISABLE);
        }
        appRepository.save(appEntity);
    }

    public void updateAi(UpdateAiReq updateAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(updateAiReq.getName());
        if (aiEntityByName.isPresent() && !aiEntityByName.get().getId().equals(updateAiReq.getId())) {
            throw new IsxAppException("ai名称重复");
        }

        AiEntity ai = aiService.getAi(updateAiReq.getId());

        AiEntity aiEntity = aiMapper.updateAiReqToAiEntity(updateAiReq, ai);
        if (updateAiReq.getAuthConfig() != null) {
            aiEntity.setAuthConfig(JSON.toJSONString(updateAiReq.getAuthConfig()));
        }
        aiRepository.save(aiEntity);
    }

    public Page<PageAiRes> pageAi(PageAiReq pageAiReq) {

        Page<AiEntity> aiEntityPage = aiRepository.searchAll(pageAiReq.getSearchKeyWord(),
            PageRequest.of(pageAiReq.getPage(), pageAiReq.getPageSize()));

        Page<PageAiRes> result = aiEntityPage.map(aiMapper::aiEntityToPageAiRes);
        result.forEach(aiEntity -> {
            if (aiEntity.getClusterId() != null) {
                aiEntity.setClusterName(clusterService.getClusterName(aiEntity.getClusterId()));
            }
            aiEntity.setCreateByUsername(userService.getUserName(aiEntity.getCreateBy()));
            JPA_TENANT_MODE.set(false);
            aiEntity.setModelName(modelService.getModelName(aiEntity.getModelId()));
            JPA_TENANT_MODE.set(true);
            ModelEntity model = modelService.getModel(aiEntity.getModelId());
            aiEntity.setAiType(model.getModelType());
        });

        return result;
    }
}
