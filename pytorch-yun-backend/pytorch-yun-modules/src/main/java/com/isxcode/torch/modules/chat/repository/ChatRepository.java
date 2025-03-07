package com.isxcode.torch.modules.chat.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@CacheConfig(cacheNames = {ModuleCode.AI})
public interface ChatRepository extends JpaRepository<ChatEntity, String> {
}
