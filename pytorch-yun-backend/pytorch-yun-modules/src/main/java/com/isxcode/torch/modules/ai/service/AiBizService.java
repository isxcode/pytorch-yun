package com.isxcode.torch.modules.ai.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.ai.req.AddAiReq;
import com.isxcode.torch.api.ai.req.PageAiReq;
import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.ai.res.PageAiRes;

import javax.transaction.Transactional;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.mapper.AiMapper;
import com.isxcode.torch.modules.ai.repository.AiRepository;
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
public class AiBizService {

    private final AiRepository aiRepository;

    private final AiMapper aiMapper;

    private final AiService aiService;

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
        aiRepository.save(aiEntity);
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

        return aiEntityPage.map(aiMapper::aiEntityToPageAiRes);
    }
}
