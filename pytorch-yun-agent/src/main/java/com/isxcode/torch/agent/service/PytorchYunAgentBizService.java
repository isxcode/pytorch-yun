package com.isxcode.torch.agent.service;

import com.isxcode.torch.api.agent.DeployAiReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PytorchYunAgentBizService {

    public void deployAi(DeployAiReq deployAiReq) {

        // 先解压
        // String command = "unzip " + deployAiReq.getAgentHomePath() + "/zhihuiyun-agent/file/" +
        // deployAiReq.getModelFileId() + " -d " + deployAiReq.getAgentHomePath() +
        // "/zhihuiyun-agent/model/" + deployAiReq.getAiId();
        // String mkdir = "mkdir -p /root/zhihuiyun-agent/ai";
        // String command = "unzip -j /root/zhihuiyun-agent/file/py_1914577920949641216 -d
        // /root/zhihuiyun-agent/ai/py_1914598559278309376";
        //
        // // 然后找到对应的插件
        // if ("Qwen2.5-0.5B".equals(deployAiReq.getModelCode())) {
        // String deployCommand = "MODEL_PATH='/root/zhihuiyun-agent/ai/py_1914598559278309376' uvicorn
        // ai4:app --host 127.0.0.1 --port 8000";
        // }
        //
        // nohup bash -c "MODEL_PATH='/root/zhihuiyun-agent/ai/py_1914598559278309376' uvicorn ai4:app
        // --host 127.0.0.1 --port 8000" > app.log 2>&1 &
        // echo $! > uvicorn.pid
        // cat app.log
        // cat uvicorn.pid
        // netstat -nlpt | grep 8000
        //
        // curl -X POST http://localhost:8000/chat \
        // -H "Content-Type: application/json" \
        // -d '{"prompt": "你是谁？"}'

        // 获得一个随机端口号

        // 然后调用命令
        // xxxx:app --模型的地址

        // 检测接口是否运行成功

        // 返回日志，返回端口号，返回进程号，模型日志文件，models/
    }
}
