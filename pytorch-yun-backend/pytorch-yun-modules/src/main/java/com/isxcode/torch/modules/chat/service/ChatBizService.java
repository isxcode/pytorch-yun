package com.isxcode.torch.modules.chat.service;

import com.isxcode.torch.api.chat.req.GetMaxChatIdReq;
import com.isxcode.torch.api.chat.res.GetMaxChatIdRes;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.service.AppService;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

            // 初始化会话第一句话
        } else {

            // 判断chat是否存在
            ChatEntity chat = chatService.getChat(getMaxChatIdReq.getChatId());

            Integer maxIndex =
                chatSessionRepository.getMaxIndex(getMaxChatIdReq.getChatId(), getMaxChatIdReq.getAppId());
            getMaxChatIdRes.setChatIndexId(maxIndex);
            getMaxChatIdRes.setChatId(chat.getId());
            getMaxChatIdRes.setAppId(getMaxChatIdReq.getAppId());
        }

        return getMaxChatIdRes;
    }
}
