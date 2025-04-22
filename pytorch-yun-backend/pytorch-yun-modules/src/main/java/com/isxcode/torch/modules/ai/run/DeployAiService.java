package com.isxcode.torch.modules.ai.run;

import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.properties.IsxAppProperties;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.path.PathUtils;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployAiService {

    private final AiService aiService;

    private final AiRepository aiRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ScpFileService scpFileService;

    private final IsxAppProperties isxAppProperties;

    @Async
    public void deployAi(DeployAiContext deployAiContext) {

        AiEntity ai = aiService.getAi(deployAiContext.getAiId());

        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
            .findAllByClusterIdAndStatus(deployAiContext.getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        try {
            String srcPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + engineNode.getTenantId() + File.separator + deployAiContext.getModelFileId();
            String distPath =
                engineNode.getAgentHomePath() + "/zhihuiyun-agent/file/" + deployAiContext.getModelFileId();

            // 看看模型是否存在
            boolean fileIsUpload = scpFileService.modelFileIsUpload(scpFileEngineNodeDto, srcPath, distPath);
            if (!fileIsUpload) {
                // 异步上传安装包
                scpFileService.scpFile(scpFileEngineNodeDto, srcPath, distPath);

                // 添加日志
                ai.setAiLog("开始上传模型");
                aiRepository.saveAndFlush(ai);

                // 同步监听进度
                scpFileService.listenScpPercent(scpFileEngineNodeDto, srcPath, distPath, ai);
            } else {
                // 添加日志
                ai.setAiLog("模型已经上传");
                aiRepository.saveAndFlush(ai);
            }

            // 调用模型部署接口


            // 运行返回端口号
            ai = aiService.getAi(deployAiContext.getAiId());
            ai.setAiLog(ai.getAiLog() + "\n运行中");
            ai.setStatus(AiStatus.ENABLE);
            ai.setAiPort("123");
            aiRepository.save(ai);
        } catch (Exception e) {
            ai = aiService.getAi(deployAiContext.getAiId());
            ai.setStatus(AiStatus.DEPLOY_FAIL);
            ai.setAiLog("部署失败：" + e.getMessage());
            aiRepository.save(ai);
        }

    }
}
