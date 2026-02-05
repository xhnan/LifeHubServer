package com.xhn.fin.accounts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.fin.accounts.mapper.FinAccountsMapper;
import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.model.SubjectTreeDTO;
import com.xhn.fin.accounts.service.FinAccountsService;
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
    public List<SubjectTreeDTO> getSubjectTree() {
        // 查询所有未归档的科目（@TableLogic会自动过滤已归档的数据）
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
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
        dto.setSubjectCode(account.getCode());
        dto.setSubjectName(account.getName());
        dto.setSubjectType(account.getAccountTypeEnum());
        // direction 暂时根据科目类型设置默认值，后续可以从数据库读取
        dto.setDirection(determineDirection(account.getAccountTypeEnum()));
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
    private com.xhn.fin.enums.Direction determineDirection(com.xhn.fin.enums.AccountType accountType) {
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
}