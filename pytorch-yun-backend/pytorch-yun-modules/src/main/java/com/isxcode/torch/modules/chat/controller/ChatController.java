package com.isxcode.torch.modules.chat.controller;

import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.chat.req.GetMaxChatIdReq;
import com.isxcode.torch.api.chat.res.GetMaxChatIdRes;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.api.user.constants.RoleType;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.chat.service.ChatBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "发送对话")
    @PostMapping("/send")
    @SuccessResponse("更新成功")
    public void updateAi(@Valid @RequestBody UpdateAiReq updateAiReq) {

        // aiBizService.updateAi(updateAiReq);
    }
}
