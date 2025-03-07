package com.isxcode.torch.modules.chat.service;

import com.isxcode.torch.modules.chat.mapper.ChatMapper;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatBizService {

    private final ChatRepository aiRepository;

    private final ChatMapper aiMapper;

}
