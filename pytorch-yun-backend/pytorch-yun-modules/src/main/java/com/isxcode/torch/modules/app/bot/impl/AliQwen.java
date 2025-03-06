package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResponseFormat;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.isxcode.torch.modules.app.bot.Bot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AliQwen extends Bot {

    @Override
    public void normalChat() {

        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder().role("user").content("9.9和9.11谁大").build());
        Message.builder().role("user").content("9.9和9.11谁大").role("assistant").build();

        Generation gen = new Generation();

        QwenParam qwenParam = QwenParam.builder().prompt("只返回纯英文内容") // 提示词
            .maxTokens(50) // 生成文本的最大长度
            .model("deepseek-r1") // 使用的模型
            .seed(12) // 随机种子
            .temperature((float) 0.85) // 温度参数
            .repetitionPenalty((float) 1) // 重复惩罚因子
            .resultFormat("message") // 返回格式
            .messages(messages)
            // .topK()
            // .topP()
            .responseFormat(ResponseFormat.from("text")) // 返回格式
            // .resources() //
            .apiKey("sk-826c9bb6a2c544648ba2270095c411e0") // key
            .enableSearch(false) // 是否联网搜索
            .enableEncrypt(false) // 是否加密
            .build();

        try {
            GenerationResult call = gen.call(qwenParam);
            System.out.println(call);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void streamChat() {

    }

    @Override
    public String name() {
        return "ali-qwen";
    }
}
