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
        // 验证：非叶子节点不允许有期初余额
        FinAccounts existing = this.getById(finAccounts.getId());
        if (existing != null) {
            // 如果从叶子节点变成非叶子节点，需要检查是否有余额
            Boolean isLeaf = finAccounts.getIsLeaf();
            if (isLeaf != null && !isLeaf) {
                // 变成非叶子节点，检查期初余额
                if (finAccounts.getInitialBalance() != null &&
                    finAccounts.getInitialBalance().compareTo(BigDecimal.ZERO) != 0) {
                    throw new RuntimeException("非叶子节点不允许有期初余额");
                }
                // 检查是否有子节点
                LambdaQueryWrapper<FinAccounts> childWrapper = new LambdaQueryWrapper<>();
                childWrapper.eq(FinAccounts::getParentId, finAccounts.getId());
                long childCount = this.count(childWrapper);
                if (childCount > 0) {
                    throw new RuntimeException("已有子节点的科目不能设置为期初余额非零");
                }
            }

            // 如果是非叶子节点，不允许设置期初余额
            Boolean existingIsLeaf = existing.getIsLeaf();
            if (existingIsLeaf != null && !existingIsLeaf &&
                finAccounts.getInitialBalance() != null &&
                finAccounts.getInitialBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("非叶子节点不允许有期初余额");
            }
        }

        // 自动更新借贷方向
        setBalanceDirectionByAccountType(finAccounts);
        return this.updateById(finAccounts);
    }

    @Override
    public boolean updateAccountAndBookId(FinAccounts finAccounts, Long bookId) {
        // 先验证数据是否属于当前账本
        FinAccounts existing = this.getById(finAccounts.getId());
        if (existing == null || !bookId.equals(existing.getBookId())) {
            log.warn("更新账户失败：数据不存在或无权限, accountId={}, bookId={}", finAccounts.getId(), bookId);
            return false;
        }

        // 验证：非叶子节点不允许有期初余额
        Boolean isLeaf = finAccounts.getIsLeaf();
        if (isLeaf != null && !isLeaf) {
            // 变成非叶子节点，检查期初余额
            if (finAccounts.getInitialBalance() != null &&
                finAccounts.getInitialBalance().compareTo(BigDecimal.ZERO) != 0) {
                log.warn("更新账户失败：非叶子节点不允许有期初余额, accountId={}", finAccounts.getId());
                return false;
            }
            // 检查是否有子节点
            LambdaQueryWrapper<FinAccounts> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(FinAccounts::getParentId, finAccounts.getId());
            long childCount = this.count(childWrapper);
            if (childCount > 0) {
                log.warn("更新账户失败：已有子节点的科目不能设置为期初余额非零, accountId={}", finAccounts.getId());
                return false;
            }
        }

        // 如果是非叶子节点，不允许设置期初余额
        Boolean existingIsLeaf = existing.getIsLeaf();
        if (existingIsLeaf != null && !existingIsLeaf &&
            finAccounts.getInitialBalance() != null &&
            finAccounts.getInitialBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("更新账户失败：非叶子节点不允许有期初余额, accountId={}", finAccounts.getId());
            return false;
        }

        // 自动更新借贷方向
        setBalanceDirectionByAccountType(finAccounts);
        return this.updateById(finAccounts);
    }

    @Override
    public List<FinAccounts> listByBookId(Long bookId) {
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getBookId, bookId);
        return this.list(queryWrapper);
    }

    @Override
    public Page<FinAccounts> pageByBookId(Page<FinAccounts> page, Long bookId) {
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getBookId, bookId);
        return this.page(page, queryWrapper);
    }

    @Override
    public FinAccounts getByIdAndBookId(Long id, Long bookId) {
        FinAccounts account = this.getById(id);
        if (account != null && !bookId.equals(account.getBookId())) {
            log.warn("查询账户失败：无权限, accountId={}, bookId={}", id, bookId);
            return null;
        }
        return account;
    }

    @Override
    public boolean removeByIdAndBookId(Long id, Long bookId) {
        FinAccounts account = this.getById(id);
        if (account == null || !bookId.equals(account.getBookId())) {
            log.warn("删除账户失败：数据不存在或无权限, accountId={}, bookId={}", id, bookId);
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
    public List<SubjectTreeDTO> getSubjectTreeByBookId(String accountType, Long bookId) {
        // 查询指定账本的未归档科目
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getBookId, bookId);

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
    public List<SubjectTreeDTO> getSubjectsByParentIdAndBookId(Long parentId, Long bookId) {
        // 查询指定账本和父级ID下的子科目
        LambdaQueryWrapper<FinAccounts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinAccounts::getBookId, bookId);

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

    @Override
    public boolean initDefaultAccounts(Long bookId) {
        // 检查账本是否已经初始化过
        LambdaQueryWrapper<FinAccounts> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(FinAccounts::getBookId, bookId);
        long count = this.count(checkWrapper);
        if (count > 0) {
            log.info("账本已初始化过科目，跳过初始化, bookId={}", bookId);
            return true;
        }

        log.info("开始为账本初始化默认科目, bookId={}", bookId);

        // 第一批：保存所有一级科目（父节点）
        List<FinAccounts> firstLevelAccounts = new ArrayList<>();

        FinAccounts account1 = createAccount(bookId, null, "资产", "ASSET", "1", false);
        firstLevelAccounts.add(account1);

        FinAccounts account2 = createAccount(bookId, null, "负债", "LIABILITY", "2", false);
        firstLevelAccounts.add(account2);

        FinAccounts account3 = createAccount(bookId, null, "权益", "EQUITY", "3", false);
        firstLevelAccounts.add(account3);

        FinAccounts account4 = createAccount(bookId, null, "收入", "INCOME", "4", false);
        firstLevelAccounts.add(account4);

        FinAccounts account5 = createAccount(bookId, null, "支出", "EXPENSE", "5", false);
        firstLevelAccounts.add(account5);

        if (!this.saveBatch(firstLevelAccounts)) {
            log.error("保存一级科目失败, bookId={}", bookId);
            return false;
        }

        // 第二批：保存所有二级科目（子节点）
        List<FinAccounts> secondLevelAccounts = new ArrayList<>();

        // 1. 资产类子科目
        FinAccounts account101 = createAccount(bookId, account1.getId(), "流动资产", "ASSET", "101", false);
        secondLevelAccounts.add(account101);

        FinAccounts account102 = createAccount(bookId, account1.getId(), "投资资产", "ASSET", "102", true);
        secondLevelAccounts.add(account102);

        FinAccounts account103 = createAccount(bookId, account1.getId(), "应收账款", "ASSET", "103", false);
        secondLevelAccounts.add(account103);

        FinAccounts account104 = createAccount(bookId, account1.getId(), "固定资产", "ASSET", "104", false);
        secondLevelAccounts.add(account104);

        FinAccounts account105 = createAccount(bookId, account1.getId(), "受限资产", "ASSET", "105", false);
        secondLevelAccounts.add(account105);

        // 2. 负债类子科目
        FinAccounts account201 = createAccount(bookId, account2.getId(), "流动负债", "LIABILITY", "201", false);
        secondLevelAccounts.add(account201);

        FinAccounts account202 = createAccount(bookId, account2.getId(), "长期负债", "LIABILITY", "202", false);
        secondLevelAccounts.add(account202);

        // 3. 权益类子科目
        secondLevelAccounts.add(createAccount(bookId, account3.getId(), "期初权益", "EQUITY", "301", true));
        secondLevelAccounts.add(createAccount(bookId, account3.getId(), "余额调整", "EQUITY", "302", true));

        // 4. 收入类子科目
        FinAccounts account401 = createAccount(bookId, account4.getId(), "主动收入", "INCOME", "401", false);
        secondLevelAccounts.add(account401);

        FinAccounts account402 = createAccount(bookId, account4.getId(), "被动收入", "INCOME", "402", false);
        secondLevelAccounts.add(account402);

        // 5. 支出类子科目
        FinAccounts account501 = createAccount(bookId, account5.getId(), "餐饮", "EXPENSE", "501", false);
        secondLevelAccounts.add(account501);

        FinAccounts account502 = createAccount(bookId, account5.getId(), "日常交通", "EXPENSE", "502", false);
        secondLevelAccounts.add(account502);

        FinAccounts account503 = createAccount(bookId, account5.getId(), "居住", "EXPENSE", "503", false);
        secondLevelAccounts.add(account503);

        FinAccounts account504 = createAccount(bookId, account5.getId(), "购物", "EXPENSE", "504", false);
        secondLevelAccounts.add(account504);

        FinAccounts account505 = createAccount(bookId, account5.getId(), "服务与订阅", "EXPENSE", "505", false);
        secondLevelAccounts.add(account505);

        FinAccounts account506 = createAccount(bookId, account5.getId(), "医疗", "EXPENSE", "506", false);
        secondLevelAccounts.add(account506);

        FinAccounts account507 = createAccount(bookId, account5.getId(), "个人提升", "EXPENSE", "507", false);
        secondLevelAccounts.add(account507);

        FinAccounts account508 = createAccount(bookId, account5.getId(), "差旅与度假", "EXPENSE", "508", false);
        secondLevelAccounts.add(account508);

        FinAccounts account509 = createAccount(bookId, account5.getId(), "情感与社交", "EXPENSE", "509", false);
        secondLevelAccounts.add(account509);

        FinAccounts account511 = createAccount(bookId, account5.getId(), "折旧与摊销", "EXPENSE", "511", false);
        secondLevelAccounts.add(account511);

        if (!this.saveBatch(secondLevelAccounts)) {
            log.error("保存二级科目失败, bookId={}", bookId);
            return false;
        }

        // 第三批：保存所有三级科目（叶子节点）
        List<FinAccounts> thirdLevelAccounts = new ArrayList<>();

        // 101 流动资产 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account101.getId(), "现金", "ASSET", "10101", true));
        thirdLevelAccounts.add(createAccount(bookId, account101.getId(), "支付宝", "ASSET", "10102", true));
        thirdLevelAccounts.add(createAccount(bookId, account101.getId(), "微信", "ASSET", "10103", true));

        // 102 投资资产 - 无子科目（叶子节点）

        // 103 应收账款 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account103.getId(), "公司报销款", "ASSET", "10301", true));
        thirdLevelAccounts.add(createAccount(bookId, account103.getId(), "借出款项", "ASSET", "10302", true));

        // 104 固定资产 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account104.getId(), "汽车", "ASSET", "10401", true));

        // 105 受限资产 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account105.getId(), "公积金", "ASSET", "10501", true));

        // 201 流动负债 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account201.getId(), "花呗", "LIABILITY", "20101", true));

        // 202 长期负债 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account202.getId(), "车贷", "LIABILITY", "20201", true));
        thirdLevelAccounts.add(createAccount(bookId, account202.getId(), "房贷", "LIABILITY", "20202", true));

        // 401 主动收入 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account401.getId(), "工资", "INCOME", "40101", true));
        thirdLevelAccounts.add(createAccount(bookId, account401.getId(), "奖金", "INCOME", "40102", true));

        // 402 被动收入 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account402.getId(), "利息", "INCOME", "40201", true));
        thirdLevelAccounts.add(createAccount(bookId, account402.getId(), "股息", "INCOME", "40202", true));
        thirdLevelAccounts.add(createAccount(bookId, account402.getId(), "二手交易", "INCOME", "40203", true));

        // 501 餐饮 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account501.getId(), "买菜生鲜", "EXPENSE", "50101", true));
        thirdLevelAccounts.add(createAccount(bookId, account501.getId(), "一日三餐", "EXPENSE", "50102", true));
        thirdLevelAccounts.add(createAccount(bookId, account501.getId(), "零食饮料", "EXPENSE", "50103", true));

        // 502 交通 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account502.getId(), "公共交通", "EXPENSE", "50201", true));
        thirdLevelAccounts.add(createAccount(bookId, account502.getId(), "打车", "EXPENSE", "50202", true));
        thirdLevelAccounts.add(createAccount(bookId, account502.getId(), "车辆日常", "EXPENSE", "50203", true));
        thirdLevelAccounts.add(createAccount(bookId, account502.getId(), "车辆养护", "EXPENSE", "50204", true));

        // 503 居住 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account503.getId(), "房租", "EXPENSE", "50301", true));
        thirdLevelAccounts.add(createAccount(bookId, account503.getId(), "水电网", "EXPENSE", "50302", true));

        // 504 购物 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account504.getId(), "数码电子", "EXPENSE", "50401", true));
        thirdLevelAccounts.add(createAccount(bookId, account504.getId(), "服饰", "EXPENSE", "50402", true));
        thirdLevelAccounts.add(createAccount(bookId, account504.getId(), "日用百货", "EXPENSE", "50403", true));

        // 505 服务与订阅 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account505.getId(), "软件订阅", "EXPENSE", "50501", true));
        thirdLevelAccounts.add(createAccount(bookId, account505.getId(), "手机话费", "EXPENSE", "50502", true));

        // 506 医疗 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account506.getId(), "看病", "EXPENSE", "50601", true));
        thirdLevelAccounts.add(createAccount(bookId, account506.getId(), "药品", "EXPENSE", "50602", true));

        // 507 个人提升 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account507.getId(), "书籍课程", "EXPENSE", "50701", true));

        // 508 差旅与度假 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account508.getId(), "交通", "EXPENSE", "50801", true));
        thirdLevelAccounts.add(createAccount(bookId, account508.getId(), "酒店住宿", "EXPENSE", "50802", true));
        thirdLevelAccounts.add(createAccount(bookId, account508.getId(), "景点玩乐", "EXPENSE", "50803", true));
        thirdLevelAccounts.add(createAccount(bookId, account508.getId(), "度假消费", "EXPENSE", "50804", true));

        // 509 情感与社交 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account509.getId(), "伴侣投入", "EXPENSE", "50901", true));
        thirdLevelAccounts.add(createAccount(bookId, account509.getId(), "孝敬长辈", "EXPENSE", "50902", true));
        thirdLevelAccounts.add(createAccount(bookId, account509.getId(), "朋友人情", "EXPENSE", "50903", true));

        // 511 折旧与摊销 - 子科目
        thirdLevelAccounts.add(createAccount(bookId, account511.getId(), "汽车折旧", "EXPENSE", "51101", true));

        boolean result = this.saveBatch(thirdLevelAccounts);

        if (result) {
            int totalCount = firstLevelAccounts.size() + secondLevelAccounts.size() + thirdLevelAccounts.size();
            log.info("成功为用户初始化默认科目, bookId={}, 科目数量={}", bookId, totalCount);
        } else {
            log.error("为用户初始化默认科目失败, bookId={}", bookId);
        }

        return result;
    }

    /**
     * 创建账户对象
     *
     * @param bookId 用户ID
     * @param parentId 父账户ID
     * @param name 账户名称
     * @param accountType 账户类型
     * @param code 业务编码
     * @param isLeaf 是否叶子节点
     * @return 账户对象
     */
    private FinAccounts createAccount(Long bookId, Long parentId, String name, String accountType,
                                       String code, boolean isLeaf) {
        FinAccounts account = new FinAccounts();
        account.setBookId(bookId);
        account.setParentId(parentId);
        account.setName(name);
        account.setAccountType(accountType);
        account.setCode(code);
        account.setIsLeaf(isLeaf);
        account.setCurrencyCode("CNY");
        account.setInitialBalance(BigDecimal.ZERO);
        account.setIsArchived(false);

        // 自动设置借贷方向
        setBalanceDirectionByAccountType(account);

        return account;
    }
}