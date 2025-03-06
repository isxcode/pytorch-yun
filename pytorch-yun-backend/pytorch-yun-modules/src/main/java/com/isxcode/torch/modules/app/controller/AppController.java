package com.isxcode.torch.modules.app.controller;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据源模块")
@RequestMapping(ModuleCode.APP)
@RestController
@RequiredArgsConstructor
public class AppController {

    private final BotFactory botFactory;

    @Operation(summary = "提交对话")
    @PostMapping("/submitChat")
    @SuccessResponse("提交成功")
    public void addDatasource() {

        Bot bot = botFactory.getBot("ali-qwen");
        bot.normalChat();
    }

}
