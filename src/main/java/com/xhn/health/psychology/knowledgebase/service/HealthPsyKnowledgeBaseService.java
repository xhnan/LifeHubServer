package com.xhn.health.psychology.knowledgebase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.psychology.knowledgebase.model.HealthPsyKnowledgeBase;

import java.util.List;

/**
 * 心理知识库 服务接口
 *
 * @author xhn
 * @date 2026-03-16
 */
public interface HealthPsyKnowledgeBaseService extends IService<HealthPsyKnowledgeBase> {

    /**
     * 根据分类查询知识列表
     *
     * @param category 分类
     * @return 知识列表
     */
    List<HealthPsyKnowledgeBase> getKnowledgeByCategory(String category);

    /**
     * 根据标题搜索知识列表
     *
     * @param title 标题关键词
     * @return 知识列表
     */
    List<HealthPsyKnowledgeBase> searchKnowledgeByTitle(String title);
}
