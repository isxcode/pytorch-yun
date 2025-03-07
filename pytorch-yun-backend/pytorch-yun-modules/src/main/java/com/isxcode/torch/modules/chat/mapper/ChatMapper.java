package com.isxcode.torch.modules.chat.mapper;

import com.isxcode.torch.api.ai.req.AddAiReq;
import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.ai.res.PageAiRes;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    PageAiRes aiEntityToPageAiRes(ChatEntity aiEntity);

    ChatEntity addAiReqToAiEntity(AddAiReq addAiReq);

    ChatEntity updateAiReqToAiEntity(UpdateAiReq updateAiReq);
}
