package com.isxcode.torch.modules.chat.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.constants.ChatSessionType;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.chat.req.GetChatReq;
import com.isxcode.torch.api.chat.req.GetMaxChatIdReq;
import com.isxcode.torch.api.chat.req.SendChatReq;
import com.isxcode.torch.api.chat.res.GetChatRes;
import com.isxcode.torch.api.chat.res.GetMaxChatIdRes;
import com.isxcode.torch.api.chat.res.SendChatRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.bot.BotFactory;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.service.AppService;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

import static com.isxcode.torch.common.config.CommonConfig.JPA_TENANT_MODE;
import static com.isxcode.torch.common.config.CommonConfig.USER_ID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatBizService {

    private final ChatRepository chatRepository;

    private final ChatSessionRepository chatSessionRepository;

    private final AppService appService;

    private final ChatService chatService;

    private final AiService aiService;

    private final BotFactory botFactory;

    private final ModelService modelService;

    public GetMaxChatIdRes getMaxChatId(GetMaxChatIdReq getMaxChatIdReq) {

        // 判断应用是否存在
        AppEntity app = appService.getApp(getMaxChatIdReq.getAppId());

        GetMaxChatIdRes getMaxChatIdRes = new GetMaxChatIdRes();

        // 如果chatId为空，则新建一个会话
        if (getMaxChatIdReq.getChatId() == null) {

            ChatEntity chat = new ChatEntity();
            chat.setAppId(app.getId());
            chat.setSubmitter(USER_ID.get());
            chat = chatRepository.save(chat);
            getMaxChatIdRes.setChatIndexId(0);
            getMaxChatIdRes.setChatId(chat.getId());
            getMaxChatIdRes.setAppId(getMaxChatIdReq.getAppId());
        } else {

            // 判断chat是否存在
            ChatEntity chat = chatService.getChat(getMaxChatIdReq.getChatId());

            Integer maxIndex =
                chatSessionRepository.getMaxIndex(getMaxChatIdReq.getChatId(), getMaxChatIdReq.getAppId());

            // 判断上一个会话是否结束
            if (maxIndex != 0) {
                ChatSessionEntity chatSessionEntity =
                    chatSessionRepository.findBySessionIndexAndChatId(maxIndex, getMaxChatIdReq.getChatId()).get();
                if (ChatSessionStatus.CHATTING.equals(chatSessionEntity.getStatus())) {
                    throw new IsxAppException("请稍后");
                }
            } else {
                maxIndex = maxIndex - 1;
            }

            getMaxChatIdRes.setChatIndexId(maxIndex + 1);
            getMaxChatIdRes.setChatId(chat.getId());
            getMaxChatIdRes.setAppId(getMaxChatIdReq.getAppId());
        }

        return getMaxChatIdRes;
    }

    public SendChatRes sendChat(SendChatReq sendChatReq) {

        // 判断应用是否存在
        AppEntity app = appService.getApp(sendChatReq.getAppId());

        // 判断会话是否存在
        ChatEntity chat = chatService.getChat(sendChatReq.getChatId());

        // 判断上一个会话是否结束
        if (sendChatReq.getMaxChatIndexId() != 0) {
            ChatSessionEntity chatSession = chatSessionRepository
                .findBySessionIndexAndChatId(sendChatReq.getMaxChatIndexId() - 1, sendChatReq.getChatId()).get();
            if (ChatSessionStatus.CHATTING.equals(chatSession.getStatus())) {
                throw new IsxAppException("等待会话结束");
            }
        }

        // 初始化当前会话
        ChatSessionEntity chatSession = new ChatSessionEntity();
        chatSession.setSessionType(ChatSessionType.USER);
        chatSession.setStatus(ChatSessionStatus.OVER);
        chatSession.setAppId(sendChatReq.getAppId());
        chatSession.setChatId(sendChatReq.getChatId());
        chatSession.setSessionIndex(sendChatReq.getMaxChatIndexId());
        chatSession.setSessionContent(JSON.toJSONString(sendChatReq.getChatContent()));

        // 获取上下文
        List<ChatSessionEntity> chatSessionList = chatSessionRepository.findAllByChatId(chat.getId());
        chatSessionList.add(chatSession);

        // 获取机器人id
        AiEntity ai = aiService.getAi(app.getAiId());
        String modelId = ai.getModelId();
        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(modelId);
        JPA_TENANT_MODE.set(true);

        // 封装会话请求体
        BotChatContext botChatContext = chatService.transSessionListToBotChatContext(chatSessionList, app, ai,
            sendChatReq.getMaxChatIndexId(), sendChatReq.getChatId());

        // 发送请求
        Bot bot = botFactory.getBot(model.getCode());
        bot.sendChat(botChatContext);

        // 保存请求会话
        chatSessionRepository.save(chatSession);

        // 初始化当前会话
        ChatSessionEntity nowChatSession = new ChatSessionEntity();
        nowChatSession.setSessionType(ChatSessionType.ASSISTANT);
        nowChatSession.setStatus(ChatSessionStatus.CHATTING);
        nowChatSession.setAppId(sendChatReq.getAppId());
        nowChatSession.setChatId(sendChatReq.getChatId());
        nowChatSession.setSessionIndex(sendChatReq.getMaxChatIndexId() + 1);
        nowChatSession.setSessionContent("{}");
        chatSessionRepository.save(nowChatSession);

        // 返回成功和响应的index
        return SendChatRes.builder().chatId(sendChatReq.getChatId())
            .responseIndexId(sendChatReq.getMaxChatIndexId() + 1).appId(sendChatReq.getAppId()).build();
    }

    public GetChatRes getChat(GetChatReq getChatReq) {

        ChatSessionEntity chatSessionEntity =
            chatSessionRepository.findBySessionIndexAndChatId(getChatReq.getChatIndex(), getChatReq.getChatId()).get();

        return GetChatRes.builder()
            .chatContent(JSON.parseObject(chatSessionEntity.getSessionContent(), ChatContent.class)).build();
    }


}
