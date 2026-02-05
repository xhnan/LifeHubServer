package com.xhn.fin.accounts.service;

import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.model.SubjectTreeDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 财务账户信息 服务接口
 *
 * @author xhn
 * @since 2026-02-04
 */
public interface FinAccountsService extends IService<FinAccounts> {

    /**
     * 获取科目树形结构
     *
     * @return 树形结构的科目列表
     */
    List<SubjectTreeDTO> getSubjectTree();

    /**
     * 根据父级ID获取子科目列表
     *
     * @param parentId 父级ID，null表示查询根节点
     * @return 子科目列表
     */
    List<SubjectTreeDTO> getSubjectsByParentId(Long parentId);
}