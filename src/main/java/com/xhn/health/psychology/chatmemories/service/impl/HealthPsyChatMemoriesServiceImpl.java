package com.xhn.health.psychology.chatmemories.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.psychology.chatmemories.mapper.HealthPsyChatMemoriesMapper;
import com.xhn.health.psychology.chatmemories.model.HealthPsyChatMemories;
import com.xhn.health.psychology.chatmemories.service.HealthPsyChatMemoriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 聊天记录 服务实现类
 *
 * @author xhn
 * @date 2026-03-16
 */
@Slf4j
@Service
public class HealthPsyChatMemoriesServiceImpl extends ServiceImpl<HealthPsyChatMemoriesMapper, HealthPsyChatMemories> implements HealthPsyChatMemoriesService {

    @Override
    public List<HealthPsyChatMemories> getChatMemoriesByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyChatMemories> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyChatMemories::getUserId, userId)
                .orderByAsc(HealthPsyChatMemories::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthPsyChatMemories> getChatMemoriesByUserIdAndRole(Long userId, String role) {
        LambdaQueryWrapper<HealthPsyChatMemories> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyChatMemories::getUserId, userId)
                .eq(HealthPsyChatMemories::getRole, role)
                .orderByAsc(HealthPsyChatMemories::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthPsyChatMemories> getRecentChatMemoriesByUserId(Long userId, int limit) {
        LambdaQueryWrapper<HealthPsyChatMemories> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyChatMemories::getUserId, userId)
                .orderByDesc(HealthPsyChatMemories::getCreatedAt)
                .last("LIMIT " + limit);
        return this.list(wrapper);
    }
}
