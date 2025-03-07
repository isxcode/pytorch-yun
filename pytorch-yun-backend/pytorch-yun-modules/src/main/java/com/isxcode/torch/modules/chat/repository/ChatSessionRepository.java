package com.isxcode.torch.modules.chat.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
@CacheConfig(cacheNames = {ModuleCode.CHAT})
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {

    @Query(value = "select max(C.sessionIndex) from ChatSessionEntity C where C.chatId = :chatId and C.appId= :appId")
    Integer getMaxIndex(@Param("chatId") String chatId, @Param("appId") String appId);
}
