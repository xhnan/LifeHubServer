package com.xhn.fin.accounts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.fin.accounts.mapper.FinAccountsMapper;
import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.model.SubjectTreeDTO;
import com.xhn.fin.accounts.service.FinAccountsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账户信息 服务实现类
 *
 * @author xhn
 * @date 2026-02-04
 */
@Slf4j
@Service
public class FinAccountsServiceImpl extends ServiceImpl<FinAccountsMapper, FinAccounts> implements FinAccountsService {

    @Override
    public boolean saveAccount(FinAccounts finAccounts) {
        // 自动设置借贷方向
        setBalanceDirectionByAccountType(finAccounts);

        // 验证：如果有父节点，检查父节点的余额
        if (finAccounts.getParentId() != null && finAccounts.getParentId() != 0) {
            FinAccounts parentAccount = this.getById(finAccounts.getParentId());
            if (parentAccount == null) {
                throw new RuntimeException("父节点不存在");
            }

            // 检查父节点是否有余额
            if (parentAccount.getInitialBalance() != null &&
                parentAccount.getInitialBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("父节点有余额，不允许添加子节点。父节点名称: " + parentAccount.getName());
            }

            // 检查父节点是否已经是叶子节点（已经有子节点的话 isLeaf 应该为 false）
            if (parentAccount.getIsLeaf() == null || parentAccount.getIsLeaf()) {
                // 更新父节点的 isLeaf 为 false
                parentAccount.setIsLeaf(false);
                this.updateById(parentAccount);
                log.info("更新父节点 isLeaf=false: parentId={}, parentName={}",
                    parentAccount.getId(), parentAccount.getName());
            }
        }

        boolean result = this.save(finAccounts);
        if (result) {
            log.info("成功创建账户: id={}, name={}, parentId={}",
                finAccounts.getId(), finAccounts.getName(), finAccounts.getParentId());
        }
        return result;
    }

    @Override
    public boolean updateAccount(FinAccounts finAccounts) {
        // 自动更新借贷方向
        setBalanceDirectionByAccountType(finAccounts);
        return this.updateById(finAccounts);
    }

    @Override
    public boolean updateAccountAndUserId(FinAccounts finAccounts, Long userId) {
        // 先验证数据是否属于当前用户
        FinAccounts existing = this.getById(finAccounts.getId());
        if (existing == null || !userId.equals(existing.getUserId())) {
            log.warn("更新账户失败：数据不存在或无权限, accountId={}, userId={}", finAccounts.getId(), userId);
            return false;
        }
        // 自动更新借贷方向
        setBalanceDirectionByAccountType(finAccounts);
        return this.updateById(finAccounts);
    }

    @Override
    public List<FinAccounts> listByUserId(Long userId) {
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getUserId, userId);
        return this.list(queryWrapper);
    }

    @Override
    public Page<FinAccounts> pageByUserId(Page<FinAccounts> page, Long userId) {
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getUserId, userId);
        return this.page(page, queryWrapper);
    }

    @Override
    public FinAccounts getByIdAndUserId(Long id, Long userId) {
        FinAccounts account = this.getById(id);
        if (account != null && !userId.equals(account.getUserId())) {
            log.warn("查询账户失败：无权限, accountId={}, userId={}", id, userId);
            return null;
        }
        return account;
    }

    @Override
    public boolean removeByIdAndUserId(Long id, Long userId) {
        FinAccounts account = this.getById(id);
        if (account == null || !userId.equals(account.getUserId())) {
            log.warn("删除账户失败：数据不存在或无权限, accountId={}, userId={}", id, userId);
            return false;
        }
        return this.removeById(id);
    }

    /**
     * 根据账户类型自动设置借贷方向
     * 资产、费用类：借方增加
     * 负债、权益、收入类：贷方增加
     *
     * @param account 账户实体
     */
    private void setBalanceDirectionByAccountType(FinAccounts account) {
        if (account == null || account.getAccountType() == null) {
            return;
        }

        String type = account.getAccountType();
        if ("ASSET".equals(type) || "EXPENSE".equals(type)) {
            account.setBalanceDirection("DEBIT"); // 借方余额
        } else {
            account.setBalanceDirection("CREDIT"); // 贷方余额
        }

        log.info("自动设置账户借贷方向: 账户类型={}, 借贷方向={}", type, account.getBalanceDirection());
    }

    @Override
    public List<SubjectTreeDTO> getSubjectTree(String accountType) {
        // 查询所有未归档的科目（@TableLogic会自动过滤已归档的数据）
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();

        // 如果传入了账户类型，添加筛选条件
        if (accountType != null && !accountType.trim().isEmpty()) {
            queryWrapper.eq(FinAccounts::getAccountType, accountType);
        }

        queryWrapper.orderByAsc(FinAccounts::getCode);

        List<FinAccounts> allAccounts = this.list(queryWrapper);

        // 转换为 DTO
        List<SubjectTreeDTO> allDTOs = allAccounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allDTOs, null);
    }

    @Override
    public List<SubjectTreeDTO> getSubjectsByParentId(Long parentId) {
        // 查询指定父级ID下的子科目（@TableLogic会自动过滤已归档的数据）
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();

        if (parentId == null || parentId == 0) {
            queryWrapper.isNull(FinAccounts::getParentId)
                    .or()
                    .eq(FinAccounts::getParentId, 0);
        } else {
            queryWrapper.eq(FinAccounts::getParentId, parentId);
        }

        queryWrapper.orderByAsc(FinAccounts::getCode);

        List<FinAccounts> accounts = this.list(queryWrapper);
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 构建树形结构
     *
     * @param allDTOs 所有科目DTO列表
     * @param parentId 父级ID
     * @return 树形结构列表
     */
    private List<SubjectTreeDTO> buildTree(List<SubjectTreeDTO> allDTOs, Long parentId) {
        List<SubjectTreeDTO> tree = new ArrayList<>();

        for (SubjectTreeDTO dto : allDTOs) {
            // 判断是否为当前父级的子节点
            boolean isChild = (parentId == null || parentId == 0) &&
                    (dto.getParentId() == null || dto.getParentId() == 0);
            boolean isMatchChild = parentId != null && parentId != 0 &&
                    parentId.equals(dto.getParentId());

            if (isChild || isMatchChild) {
                // 递归查找子节点
                List<SubjectTreeDTO> children = buildTree(allDTOs, dto.getId());
                if (!children.isEmpty()) {
                    dto.setChildren(children);
                }
                tree.add(dto);
            }
        }

        return tree;
    }

    /**
     * 将 FinAccounts 转换为 SubjectTreeDTO
     *
     * @param account 账户实体
     * @return 科目树形DTO
     */
    private SubjectTreeDTO convertToDTO(FinAccounts account) {
        SubjectTreeDTO dto = new SubjectTreeDTO();
        dto.setId(account.getId());
        dto.setParentId(account.getParentId());
        dto.setCode(account.getCode());
        dto.setName(account.getName());
        dto.setAccountTypeEnum(account.getAccountTypeEnum());

        // 如果数据库中已存储借贷方向，使用数据库的值；否则根据科目类型自动判断
        com.xhn.fin.enums.Direction direction = account.getBalanceDirectionEnum();
        if (direction == null) {
            direction = determineDirectionByAccountType(account.getAccountTypeEnum());
        }
        dto.setBalanceDirectionEnum(direction);

        dto.setCurrencyCode(account.getCurrencyCode());
        dto.setInitialBalance(account.getInitialBalance());
        // currentBalance 目前设置为 initialBalance，后续需要根据凭证累计发生额计算
        dto.setCurrentBalance(account.getInitialBalance() != null ? account.getInitialBalance() : BigDecimal.ZERO);
        dto.setIsArchived(account.getIsArchived());
        dto.setDescription(account.getDescription());
        dto.setIsLeaf(account.getIsLeaf());
        return dto;
    }

    /**
     * 根据科目类型确定借贷方向
     * 资产、费用类：借方增加
     * 负债、权益、收入类：贷方增加
     *
     * @param accountType 科目类型
     * @return 借贷方向
     */
    private com.xhn.fin.enums.Direction determineDirectionByAccountType(com.xhn.fin.enums.AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        switch (accountType) {
            case ASSET:
            case EXPENSE:
                return com.xhn.fin.enums.Direction.DEBIT;
            case LIABILITY:
            case EQUITY:
            case INCOME:
                return com.xhn.fin.enums.Direction.CREDIT;
            default:
                return null;
        }
    }

    @Override
    public List<SubjectTreeDTO> getSubjectTreeByUserId(String accountType, Long userId) {
        // 查询指定用户的未归档科目
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getUserId, userId);

        // 如果传入了账户类型，添加筛选条件
        if (accountType != null && !accountType.trim().isEmpty()) {
            queryWrapper.eq(FinAccounts::getAccountType, accountType);
        }

        queryWrapper.orderByAsc(FinAccounts::getCode);

        List<FinAccounts> allAccounts = this.list(queryWrapper);

        // 转换为 DTO
        List<SubjectTreeDTO> allDTOs = allAccounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allDTOs, null);
    }

    @Override
    public List<SubjectTreeDTO> getSubjectsByParentIdAndUserId(Long parentId, Long userId) {
        // 查询指定用户和父级ID下的子科目
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getUserId, userId);

        if (parentId == null || parentId == 0) {
            queryWrapper.isNull(FinAccounts::getParentId)
                    .or()
                    .eq(FinAccounts::getParentId, 0);
        } else {
            queryWrapper.eq(FinAccounts::getParentId, parentId);
        }

        queryWrapper.orderByAsc(FinAccounts::getCode);

        List<FinAccounts> accounts = this.list(queryWrapper);
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}