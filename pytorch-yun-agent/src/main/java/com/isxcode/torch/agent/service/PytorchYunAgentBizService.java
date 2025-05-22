package com.isxcode.torch.agent.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.isxcode.torch.api.agent.req.ChatAgentAiContent;
import com.isxcode.torch.api.agent.req.*;
import com.isxcode.torch.api.agent.res.ChatAgentAiRes;
import com.isxcode.torch.api.agent.res.DeployAiRes;
import com.isxcode.torch.api.agent.res.GetAgentAiLogRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.common.utils.http.HttpUtils;
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
        String[] deployCommand =
            {"bash", "-c", "nohup bash -c \"MODEL_PATH='" + aiPath + "' uvicorn ai:app --host 127.0.0.1 --app-dir "
                + pluginPath + " --port " + aiPort + " \" > " + aiPath + "/ai.log 2>&1 & echo $!"};
        String pid = RuntimeUtil.execForStr(deployCommand);
        return DeployAiRes.builder().aiPort(String.valueOf(aiPort)).aiPid(pid.replace("\n", "")).build();
    }

    public void stopAi(StopAgentAiReq stopAgentAiReq) {

        String stopAiCommand = "kill -9 " + stopAgentAiReq.getPid();

        // 执行命令
        Process stopAi = RuntimeUtil.exec(stopAiCommand);
        try {
            if (stopAi.waitFor() != 0) {
                throw new IsxAppException(RuntimeUtil.getErrorResult(stopAi));
            }
        } catch (InterruptedException e) {
            throw new IsxAppException(e.getMessage());
        }
    }

    public GetAgentAiLogRes getAiLog(GetAgentAiLogReq getAgentAiLogReq) {

        String aiPath = getAgentAiLogReq.getAgentHomePath() + "/zhihuiyun-agent/ai/" + getAgentAiLogReq.getAiId();

        // 如果ai.log文件不存在则报错，再部署中
        if (!FileUtil.exist(aiPath + "/ai.log")) {
            throw new IsxAppException("模型在部署中，请稍后");
        }

        String[] getAiLog = {"bash", "-c", "cat " + aiPath + "/ai.log"};

        return GetAgentAiLogRes.builder().log(RuntimeUtil.execForStr(getAiLog)).build();
    }

    public ChatAgentAiRes chatAi(ChatAgentAiReq chatAgentAiReq) {

        ChatAgentAiContent chatAgentAiContent = new ChatAgentAiContent();
        chatAgentAiContent.setPrompt(chatAgentAiReq.getPrompt());

        return HttpUtils.doPost("http://127.0.0.1:" + chatAgentAiReq.getAiPort() + "/chat", chatAgentAiContent,
            ChatAgentAiRes.class);

    }
}
