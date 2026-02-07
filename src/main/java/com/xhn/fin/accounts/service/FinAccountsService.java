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
     * 修改账户并验证账本权限
     *
     * @param finAccounts 账户信息
     * @param bookId 账本ID
     * @return 是否成功
     */
    boolean updateAccountAndBookId(FinAccounts finAccounts, Long bookId);

    /**
     * 根据账本ID查询所有账户
     *
     * @param bookId 账本ID
     * @return 账户列表
     */
    List<FinAccounts> listByBookId(Long bookId);

    /**
     * 根据账本ID分页查询账户
     *
     * @param page 分页对象
     * @param bookId 账本ID
     * @return 分页结果
     */
    Page<FinAccounts> pageByBookId(Page<FinAccounts> page, Long bookId);

    /**
     * 根据ID和账本ID查询账户
     *
     * @param id 账户ID
     * @param bookId 账本ID
     * @return 账户信息
     */
    FinAccounts getByIdAndBookId(Long id, Long bookId);

    /**
     * 根据ID和账本ID删除账户
     *
     * @param id 账户ID
     * @param bookId 账本ID
     * @return 是否成功
     */
    boolean removeByIdAndBookId(Long id, Long bookId);

    /**
     * 获取科目树形结构
     *
     * @param accountType 账户类型（可选），null表示查询全部
     * @return 树形结构的科目列表
     */
    List<SubjectTreeDTO> getSubjectTree(String accountType);

    /**
     * 根据账本ID获取科目树形结构
     *
     * @param accountType 账户类型（可选），null表示查询全部
     * @param bookId 账本ID
     * @return 树形结构的科目列表
     */
    List<SubjectTreeDTO> getSubjectTreeByBookId(String accountType, Long bookId);

    /**
     * 根据父级ID获取子科目列表
     *
     * @param parentId 父级ID，null表示查询根节点
     * @return 子科目列表
     */
    List<SubjectTreeDTO> getSubjectsByParentId(Long parentId);

    /**
     * 根据父级ID和账本ID获取子科目列表
     *
     * @param parentId 父级ID，null表示查询根节点
     * @param bookId 账本ID
     * @return 子科目列表
     */
    List<SubjectTreeDTO> getSubjectsByParentIdAndBookId(Long parentId, Long bookId);

    /**
     * 为账本初始化默认科目
     *
     * @param bookId 账本ID
     * @return 是否成功
     */
    boolean initDefaultAccounts(Long bookId);
}
