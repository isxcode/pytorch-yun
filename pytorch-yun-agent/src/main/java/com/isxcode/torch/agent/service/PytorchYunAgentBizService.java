package com.isxcode.torch.agent.service;

import cn.hutool.core.util.RuntimeUtil;
import com.isxcode.torch.api.agent.req.DeployAiReq;
import com.isxcode.torch.api.agent.res.DeployAiRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
@Service
@RequiredArgsConstructor
public class PytorchYunAgentBizService {

    public static int findUnusedPort() {

        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException("未存在可使用端口号");
        }
    }

    public DeployAiRes deployAi(DeployAiReq deployAiReq) {

        // 先解压
        String unzipModelCommand =
            "unzip -oj " + deployAiReq.getAgentHomePath() + "/zhihuiyun-agent/file/" + deployAiReq.getModelFileId()
                + " -d " + deployAiReq.getAgentHomePath() + "/zhihuiyun-agent/ai/" + deployAiReq.getAiId();

        // 执行命令
        Process execUnzip = RuntimeUtil.exec(unzipModelCommand);
        try {
            if (execUnzip.waitFor() != 0) {
                throw new IsxAppException(RuntimeUtil.getErrorResult(execUnzip));
            }
        } catch (InterruptedException e) {
            throw new IsxAppException(e.getMessage());
        }

        // 然后找到对应的插件
        String pluginName = "";
        if ("Qwen2.5-0.5B".equals(deployAiReq.getModelCode())) {
            pluginName = "qwen2.5";
        }

        // 获取端口
        int aiPort = findUnusedPort();

        // 部署命令
        String aiPath = deployAiReq.getAgentHomePath() + "/zhihuiyun-agent/ai/" + deployAiReq.getAiId();
        String pluginPath = deployAiReq.getAgentHomePath() + "/zhihuiyun-agent/plugins/" + pluginName;
        String[] deployCommand = {"bash", "-c",
                "cd " + aiPath + " && nohup bash -c \"MODEL_PATH='" + aiPath
                    + "' uvicorn ai:app --host 127.0.0.1 --app-dir " + pluginPath + " --port " + aiPort
                    + " \" > ai.log 2>&1 & echo $!"};
        String pid = RuntimeUtil.execForStr(deployCommand);
        return DeployAiRes.builder().aiPort(String.valueOf(aiPort)).aiPid(pid.replace("\n", "")).build();
    }
}
