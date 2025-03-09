package com.isxcode.torch.modules.chat.controller;

import com.isxcode.torch.api.chat.req.GetChatReq;
import com.isxcode.torch.api.chat.req.GetMaxChatIdReq;
import com.isxcode.torch.api.chat.req.SendChatReq;
import com.isxcode.torch.api.chat.res.GetChatRes;
import com.isxcode.torch.api.chat.res.GetMaxChatIdRes;
import com.isxcode.torch.api.chat.res.SendChatRes;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.chat.service.ChatBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "对话模块")
@RequestMapping(ModuleCode.CHAT)
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatBizService chatBizService;

    @Operation(summary = "获取最大的对话")
    @PostMapping("/getMaxChatId")
    @SuccessResponse("获取成功")
    public GetMaxChatIdRes getMaxChatId(@Valid @RequestBody GetMaxChatIdReq getMaxChatIdReq) {

        return chatBizService.getMaxChatId(getMaxChatIdReq);
    }

    @Operation(summary = "发送对话")
    @PostMapping("/sendChat")
    @SuccessResponse("发送成功")
    public SendChatRes sendChat(@Valid @RequestBody SendChatReq sendChatReq) {

        return chatBizService.sendChat(sendChatReq);
    }

    @Operation(summary = "接受对话")
    @PostMapping("/getChat")
    @SuccessResponse("获取成功")
    public GetChatRes getChat(@Valid @RequestBody GetChatReq getChatReq) {

        return chatBizService.getChat(getChatReq);
    }
}
