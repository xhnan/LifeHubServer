package com.xhn.fin.accounts.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.model.SubjectTreeDTO;

import java.util.List;

/**
 * 财务账户信息 服务接口
 *
 * @author xhn
 * @since 2026-02-04
 */
public interface FinAccountsService extends IService<FinAccounts> {

    /**
     * 新增账户（自动设置借贷方向）
     *
     * @param finAccounts 账户信息
     * @return 是否成功
     */
    boolean saveAccount(FinAccounts finAccounts);

    /**
     * 修改账户（自动更新借贷方向）
     *
     * @param finAccounts 账户信息
     * @return 是否成功
     */
    boolean updateAccount(FinAccounts finAccounts);

    /**
     * 修改账户并验证用户权限
     *
     * @param finAccounts 账户信息
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateAccountAndUserId(FinAccounts finAccounts, Long userId);

    /**
     * 根据用户ID查询所有账户
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    List<FinAccounts> listByUserId(Long userId);

    /**
     * 根据用户ID分页查询账户
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    Page<FinAccounts> pageByUserId(Page<FinAccounts> page, Long userId);

    /**
     * 根据ID和用户ID查询账户
     *
     * @param id 账户ID
     * @param userId 用户ID
     * @return 账户信息
     */
    FinAccounts getByIdAndUserId(Long id, Long userId);

    /**
     * 根据ID和用户ID删除账户
     *
     * @param id 账户ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeByIdAndUserId(Long id, Long userId);

    /**
     * 获取科目树形结构
     *
     * @param accountType 账户类型（可选），null表示查询全部
     * @return 树形结构的科目列表
     */
    List<SubjectTreeDTO> getSubjectTree(String accountType);

    /**
     * 根据用户ID获取科目树形结构
     *
     * @param accountType 账户类型（可选），null表示查询全部
     * @param userId 用户ID
     * @return 树形结构的科目列表
     */
    List<SubjectTreeDTO> getSubjectTreeByUserId(String accountType, Long userId);

    /**
     * 根据父级ID获取子科目列表
     *
     * @param parentId 父级ID，null表示查询根节点
     * @return 子科目列表
     */
    List<SubjectTreeDTO> getSubjectsByParentId(Long parentId);

    /**
     * 根据父级ID和用户ID获取子科目列表
     *
     * @param parentId 父级ID，null表示查询根节点
     * @param userId 用户ID
     * @return 子科目列表
     */
    List<SubjectTreeDTO> getSubjectsByParentIdAndUserId(Long parentId, Long userId);
}