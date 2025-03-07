package com.isxcode.torch.modules.chat.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatEntity getChat(String chatId) {

        return chatRepository.findById(chatId).orElseThrow(() -> new IsxAppException("会话不存在"));
    }
}
