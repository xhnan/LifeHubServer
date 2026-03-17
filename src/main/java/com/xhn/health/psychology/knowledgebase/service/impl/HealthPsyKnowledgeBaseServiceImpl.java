package com.xhn.health.psychology.knowledgebase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.psychology.knowledgebase.mapper.HealthPsyKnowledgeBaseMapper;
import com.xhn.health.psychology.knowledgebase.model.HealthPsyKnowledgeBase;
import com.xhn.health.psychology.knowledgebase.service.HealthPsyKnowledgeBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理知识库 服务实现类
 *
 * @author xhn
 * @date 2026-03-16
 */
@Slf4j
@Service
public class HealthPsyKnowledgeBaseServiceImpl extends ServiceImpl<HealthPsyKnowledgeBaseMapper, HealthPsyKnowledgeBase> implements HealthPsyKnowledgeBaseService {

    @Override
    public List<HealthPsyKnowledgeBase> getKnowledgeByCategory(String category) {
        LambdaQueryWrapper<HealthPsyKnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyKnowledgeBase::getCategory, category)
                .orderByDesc(HealthPsyKnowledgeBase::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthPsyKnowledgeBase> searchKnowledgeByTitle(String title) {
        LambdaQueryWrapper<HealthPsyKnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(HealthPsyKnowledgeBase::getTitle, title)
                .orderByDesc(HealthPsyKnowledgeBase::getCreatedAt);
        return this.list(wrapper);
    }
}
