package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AliQwen extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    public AliQwen(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public void chat(BotChatContext botChatContext) {

        // 重新封装对应的请求
        List<Message> messages = new ArrayList<>();
        botChatContext.getChats().forEach(chat -> {
            messages.add(Message.builder().role(chat.getRole()).content(chat.getContent()).build());
        });

        Generation gen = new Generation();

        QwenParam qwenParam = QwenParam.builder().prompt(botChatContext.getPrompt()) // 提示词
            // .maxTokens(50) // 生成文本的最大长度
            .model("qwen-plus") // 使用的模型
            // .seed(12) // 随机种子
            // .temperature((float) 0.85) // 温度参数
            // .repetitionPenalty((float) 1) // 重复惩罚因子
            // .resultFormat("message") // 返回格式
            .messages(messages)
            // .topK()
            // .topP()
            // .responseFormat(ResponseFormat.from("text")) // 返回格式
            // .resources() //
            .apiKey(botChatContext.getAuthConfig().getApiKey()) // key
            // .enableSearch(false) // 是否联网搜索
            // .enableEncrypt(false) // 是否加密
            .build();

        try {
            GenerationResult call = gen.call(qwenParam);

            // 提交当前会话
            ChatSessionEntity nowChatSession = chatSessionRepository
                .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();
            nowChatSession.setStatus(ChatSessionStatus.OVER);
            ChatContent build = ChatContent.builder().content(call.getOutput().getText()).build();
            nowChatSession.setSessionContent(JSON.toJSONString(build));
            chatSessionRepository.save(nowChatSession);

        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return ModelCode.HUI_ALI_QWEN;
    }
}
