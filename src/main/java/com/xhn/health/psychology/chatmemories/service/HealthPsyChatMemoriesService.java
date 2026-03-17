package com.xhn.health.psychology.chatmemories.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.psychology.chatmemories.model.HealthPsyChatMemories;

import java.util.List;

/**
 * 聊天记录 服务接口
 *
 * @author xhn
 * @date 2026-03-16
 */
public interface HealthPsyChatMemoriesService extends IService<HealthPsyChatMemories> {

    /**
     * 根据用户ID查询聊天记录列表
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<HealthPsyChatMemories> getChatMemoriesByUserId(Long userId);

    /**
     * 根据用户ID和角色查询聊天记录列表
     *
     * @param userId 用户ID
     * @param role 角色
     * @return 聊天记录列表
     */
    List<HealthPsyChatMemories> getChatMemoriesByUserIdAndRole(Long userId, String role);

    /**
     * 根据用户ID查询最近N条聊天记录
     *
     * @param userId 用户ID
     * @param limit 记录数量
     * @return 聊天记录列表
     */
    List<HealthPsyChatMemories> getRecentChatMemoriesByUserId(Long userId, int limit);
}
