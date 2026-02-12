package com.xhn.fin.transactions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.fin.entries.mapper.FinEntriesMapper;
import com.xhn.fin.entries.model.FinEntries;
import com.xhn.fin.entries.service.FinEntriesService;
import com.xhn.fin.transtags.model.FinTransTags;
import com.xhn.fin.transtags.service.FinTransTagsService;
import com.xhn.fin.transactions.dto.*;
import com.xhn.fin.transactions.mapper.FinTransactionsMapper;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 财务交易服务实现类
 *
 * @author xhn
 * @date 2026-02-04
 */
@Slf4j
@Service
public class FinTransactionsServiceImpl extends ServiceImpl<FinTransactionsMapper, FinTransactions> implements FinTransactionsService {

    @Autowired
    private FinEntriesService finEntriesService;

    @Autowired
    private FinTransTagsService finTransTagsService;

    @Autowired
    private FinEntriesMapper finEntriesMapper;

    @Override
    public Page<FinTransactions> pageByBookIdAndDateRange(Page<FinTransactions> page, Long bookId, String startDate, String endDate) {
        LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinTransactions::getBookId, bookId);

        // 日期范围筛选
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                wrapper.ge(FinTransactions::getTransDate, startDateTime);
            } catch (Exception e) {
                log.warn("开始日期格式错误: {}", startDate);
            }
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
                wrapper.le(FinTransactions::getTransDate, endDateTime);
            } catch (Exception e) {
                log.warn("结束日期格式错误: {}", endDate);
            }
        }

        // 按交易日期倒序排列
        wrapper.orderByDesc(FinTransactions::getTransDate);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransactionWithEntries(TransactionEntryDTO dto, Long userId) {
        // 1. 参数校验
        if (dto.getEntries() == null || dto.getEntries().isEmpty()) {
            throw new IllegalArgumentException("分录列表不能为空");
        }
        if (dto.getTransDate() == null) {
            throw new IllegalArgumentException("交易日期不能为空");
        }
        if (dto.getBookId() == null) {
            throw new IllegalArgumentException("账本ID不能为空");
        }

        // 2. 校验借贷平衡
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (TransactionEntryDTO.EntryRequest entry : dto.getEntries()) {
            // 校验金额格式
            BigDecimal amount;
            try {
                amount = new BigDecimal(entry.getAmount());
            } catch (Exception e) {
                throw new IllegalArgumentException("分录金额格式错误: " + entry.getAmount());
            }
            // 校验金额必须为正数
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("分录金额必须为正数");
            }

            if ("DEBIT".equalsIgnoreCase(entry.getDirection())) {
                totalDebit = totalDebit.add(amount);
            } else if ("CREDIT".equalsIgnoreCase(entry.getDirection())) {
                totalCredit = totalCredit.add(amount);
            } else {
                throw new IllegalArgumentException("分录方向必须为 DEBIT 或 CREDIT");
            }
        }

        // 借贷平衡校验（允许 0.01 的舍入误差）
        if (totalDebit.subtract(totalCredit).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new IllegalArgumentException("借贷不平衡：借方总额=" + totalDebit + "，贷方总额=" + totalCredit);
        }

        // 3. 创建交易主表
        FinTransactions transaction = new FinTransactions();
        transaction.setTransDate(dto.getTransDate());
        transaction.setDescription(dto.getDescription());
        transaction.setAttachmentId(dto.getAttachmentId());
        transaction.setBookId(dto.getBookId());
        transaction.setUserId(userId);
        transaction.setCreatedBy(userId);
        transaction.setAmount(totalDebit); // 单边金额（借贷平衡，取借方总额）

        boolean saved = this.save(transaction);
        if (!saved) {
            throw new RuntimeException("保存交易主表失败");
        }

        Long transId = transaction.getId();
        log.info("创建交易主表成功，transId={}", transId);

        // 4. 创建分录表
        List<FinEntries> entries = new ArrayList<>();
        for (TransactionEntryDTO.EntryRequest entryReq : dto.getEntries()) {
            FinEntries entry = new FinEntries();
            entry.setTransId(transId);
            entry.setAccountId(entryReq.getAccountId());
            entry.setDirection(entryReq.getDirection().toUpperCase());
            entry.setAmount(new BigDecimal(entryReq.getAmount()));
            entry.setMemo(entryReq.getMemo());
            entry.setBookId(dto.getBookId());
            entry.setUserId(userId);

            // 可选字段
            if (entryReq.getQuantity() != null) {
                entry.setQuantity(new BigDecimal(entryReq.getQuantity()));
            }
            if (entryReq.getPrice() != null) {
                entry.setPrice(new BigDecimal(entryReq.getPrice()));
            }
            if (entryReq.getCommodityCode() != null) {
                entry.setCommodityCode(entryReq.getCommodityCode());
            }

            entries.add(entry);
        }

        boolean entriesSaved = finEntriesService.saveBatch(entries);
        if (!entriesSaved) {
            throw new RuntimeException("保存分录表失败");
        }

        // 5. 创建标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<FinTransTags> transTagsList = new ArrayList<>();
            for (Long tagId : dto.getTagIds()) {
                FinTransTags transTag = new FinTransTags();
                transTag.setTransId(transId);
                transTag.setTagId(tagId);
                transTagsList.add(transTag);
            }

            boolean tagsSaved = finTransTagsService.saveBatch(transTagsList);
            if (!tagsSaved) {
                throw new RuntimeException("保存标签关联失败");
            }
            log.info("成功创建标签关联，transId={}, 标签数量={}", transId, transTagsList.size());
        }

        log.info("成功创建交易及分录，transId={}, 分录数量={}", transId, entries.size());
        return transId;
    }

    @Override
    public MonthlyStatisticsDTO getMonthlyStatistics(Long bookId) {
        // 获取本月开始和结束时间
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        log.info("查询本月收支统计，bookId={}, 时间范围：{} 到 {}", bookId, monthStart, monthEnd);

        // 查询本月收入
        BigDecimal totalIncome = finEntriesMapper.sumIncomeByMonth(bookId, monthStart, monthEnd);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        // 查询本月支出
        BigDecimal totalExpense = finEntriesMapper.sumExpenseByMonth(bookId, monthStart, monthEnd);
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        // 计算结余
        BigDecimal balance = totalIncome.subtract(totalExpense);

        return MonthlyStatisticsDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }

    @Override
    public MonthlyStatisticsDTO getMonthlyStatistics(Long bookId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime monthStart = ym.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = ym.atEndOfMonth().atTime(23, 59, 59);

        log.info("查询月度收支统计，bookId={}, 时间范围：{} 到 {}", bookId, monthStart, monthEnd);

        BigDecimal totalIncome = finEntriesMapper.sumIncomeByMonth(bookId, monthStart, monthEnd);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpense = finEntriesMapper.sumExpenseByMonth(bookId, monthStart, monthEnd);
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        return MonthlyStatisticsDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .build();
    }

    @Override
    public TransactionDetailDTO getTransactionDetails(Long bookId, String startDate, String endDate, int pageNum, int pageSize) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                start = LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                log.warn("开始日期格式错误: {}", startDate);
            }
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            } catch (Exception e) {
                log.warn("结束日期格式错误: {}", endDate);
            }
        }

        long offset = (long) (pageNum - 1) * pageSize;
        long total = baseMapper.countTransactionDetails(bookId, start, end);
        List<TransactionFlatVO> flatList = baseMapper.selectTransactionDetails(bookId, start, end, offset, pageSize);

        // 按 transId 分组组装
        // LinkedHashMap 保持插入顺序（SQL 已按 trans_date DESC 排序）
        java.util.LinkedHashMap<Long, List<TransactionFlatVO>> groupedByTrans = new java.util.LinkedHashMap<>();
        for (TransactionFlatVO vo : flatList) {
            groupedByTrans.computeIfAbsent(vo.getTransId(), k -> new ArrayList<>()).add(vo);
        }

        // 组装每笔交易
        List<TransactionDetailDTO.TransactionItem> items = new ArrayList<>();
        for (var entry : groupedByTrans.entrySet()) {
            List<TransactionFlatVO> rows = entry.getValue();
            TransactionFlatVO first = rows.get(0);

            // 去重分录
            java.util.LinkedHashMap<Long, TransactionFlatVO> entryMap = new java.util.LinkedHashMap<>();
            // 去重标签
            java.util.LinkedHashMap<Long, TransactionDetailDTO.TagInfo> tagMap = new java.util.LinkedHashMap<>();

            for (TransactionFlatVO row : rows) {
                if (row.getEntryId() != null) {
                    entryMap.putIfAbsent(row.getEntryId(), row);
                }
                if (row.getTagId() != null) {
                    tagMap.computeIfAbsent(row.getTagId(), k ->
                            TransactionDetailDTO.TagInfo.builder()
                                    .tagId(row.getTagId())
                                    .tagName(row.getTagName())
                                    .color(row.getTagColor())
                                    .icon(row.getTagIcon())
                                    .build()
                    );
                }
            }

            // 判断交易类型和提取显示信息
            List<TransactionFlatVO> uniqueEntries = new ArrayList<>(entryMap.values());
            String transType = resolveTransType(uniqueEntries);
            BigDecimal displayAmount = resolveDisplayAmount(uniqueEntries, transType);
            String categoryName = null;
            String categoryIcon = null;
            String targetAccountName = null;
            String targetAccountIcon = null;

            if ("EXPENSE".equals(transType)) {
                // 支出：主科目=EXPENSE类，对方=ASSET类
                for (TransactionFlatVO e : uniqueEntries) {
                    if ("EXPENSE".equals(e.getAccountType())) {
                        categoryName = e.getAccountName();
                        categoryIcon = e.getAccountIcon();
                    } else if ("ASSET".equals(e.getAccountType()) || "LIABILITY".equals(e.getAccountType())) {
                        targetAccountName = e.getAccountName();
                        targetAccountIcon = e.getAccountIcon();
                    }
                }
            } else if ("INCOME".equals(transType)) {
                // 收入：主科目=INCOME类，对方=ASSET类
                for (TransactionFlatVO e : uniqueEntries) {
                    if ("INCOME".equals(e.getAccountType())) {
                        categoryName = e.getAccountName();
                        categoryIcon = e.getAccountIcon();
                    } else if ("ASSET".equals(e.getAccountType()) || "LIABILITY".equals(e.getAccountType())) {
                        targetAccountName = e.getAccountName();
                        targetAccountIcon = e.getAccountIcon();
                    }
                }
            } else if ("TRANSFER".equals(transType)) {
                // 转账：借方=转入方，贷方=转出方
                for (TransactionFlatVO e : uniqueEntries) {
                    if ("DEBIT".equals(e.getDirection())) {
                        categoryName = e.getAccountName();
                        categoryIcon = e.getAccountIcon();
                    } else {
                        targetAccountName = e.getAccountName();
                        targetAccountIcon = e.getAccountIcon();
                    }
                }
            } else {
                // 其他：取第一条分录
                if (!uniqueEntries.isEmpty()) {
                    categoryName = uniqueEntries.get(0).getAccountName();
                    categoryIcon = uniqueEntries.get(0).getAccountIcon();
                }
            }

            items.add(TransactionDetailDTO.TransactionItem.builder()
                    .transId(first.getTransId())
                    .transDate(first.getTransDate())
                    .transType(transType)
                    .displayAmount(displayAmount)
                    .description(first.getDescription())
                    .categoryName(categoryName)
                    .categoryIcon(categoryIcon)
                    .targetAccountName(targetAccountName)
                    .targetAccountIcon(targetAccountIcon)
                    .tags(new ArrayList<>(tagMap.values()))
                    .build());
        }

        // 按日期分组
        java.util.LinkedHashMap<LocalDate, List<TransactionDetailDTO.TransactionItem>> dailyMap = new java.util.LinkedHashMap<>();
        for (TransactionDetailDTO.TransactionItem item : items) {
            LocalDate date = item.getTransDate().toLocalDate();
            dailyMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        List<TransactionDetailDTO.DailyGroup> dailyGroups = new ArrayList<>();
        for (var dailyEntry : dailyMap.entrySet()) {
            List<TransactionDetailDTO.TransactionItem> dayItems = dailyEntry.getValue();
            BigDecimal dailyIncome = BigDecimal.ZERO;
            BigDecimal dailyExpense = BigDecimal.ZERO;

            for (TransactionDetailDTO.TransactionItem item : dayItems) {
                if ("INCOME".equals(item.getTransType()) && item.getDisplayAmount() != null) {
                    dailyIncome = dailyIncome.add(item.getDisplayAmount().abs());
                } else if ("EXPENSE".equals(item.getTransType()) && item.getDisplayAmount() != null) {
                    dailyExpense = dailyExpense.add(item.getDisplayAmount().abs());
                }
            }

            dailyGroups.add(TransactionDetailDTO.DailyGroup.builder()
                    .date(dailyEntry.getKey())
                    .dailyIncome(dailyIncome)
                    .dailyExpense(dailyExpense)
                    .transactions(dayItems)
                    .build());
        }

        TransactionDetailDTO result = new TransactionDetailDTO();
        result.setDailyGroups(dailyGroups);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public YearlyTrendDTO getYearlyTrend(Long bookId, int year) {
        List<YearlyTrendDTO.MonthData> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

            BigDecimal income = finEntriesMapper.sumIncomeByMonth(bookId, start, end);
            BigDecimal expense = finEntriesMapper.sumExpenseByMonth(bookId, start, end);
            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;

            months.add(YearlyTrendDTO.MonthData.builder()
                    .month(m)
                    .income(income)
                    .expense(expense)
                    .balance(income.subtract(expense))
                    .build());
        }
        return YearlyTrendDTO.builder().year(year).months(months).build();
    }

    @Override
    public CategoryRankDTO getCategoryRank(Long bookId, String type, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> rows;
        if ("INCOME".equalsIgnoreCase(type)) {
            rows = finEntriesMapper.sumIncomeByCategory(bookId, start, end);
        } else {
            rows = finEntriesMapper.sumExpenseByCategory(bookId, start, end);
        }

        BigDecimal total = BigDecimal.ZERO;
        List<CategoryRankDTO.CategoryItem> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            BigDecimal amount = toBigDecimal(row.get("total_amount"));
            total = total.add(amount);
            items.add(CategoryRankDTO.CategoryItem.builder()
                    .accountId(toLong(row.get("account_id")))
                    .accountName((String) row.get("account_name"))
                    .accountIcon((String) row.get("account_icon"))
                    .amount(amount)
                    .build());
        }

        // 计算占比
        for (CategoryRankDTO.CategoryItem item : items) {
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentage(item.getAmount()
                        .multiply(new BigDecimal("100"))
                        .divide(total, 1, java.math.RoundingMode.HALF_UP));
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }
        }

        return CategoryRankDTO.builder()
                .type(type.toUpperCase())
                .total(total)
                .categories(items)
                .build();
    }

    @Override
    public TagStatisticsDTO getTagStatistics(Long bookId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> rows = finEntriesMapper.sumExpenseByTag(bookId, start, end);

        BigDecimal total = BigDecimal.ZERO;
        List<TagStatisticsDTO.TagItem> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            BigDecimal amount = toBigDecimal(row.get("total_amount"));
            total = total.add(amount);
            items.add(TagStatisticsDTO.TagItem.builder()
                    .tagId(toLong(row.get("tag_id")))
                    .tagName((String) row.get("tag_name"))
                    .color((String) row.get("tag_color"))
                    .icon((String) row.get("tag_icon"))
                    .amount(amount)
                    .count(((Number) row.get("trans_count")).intValue())
                    .build());
        }

        for (TagStatisticsDTO.TagItem item : items) {
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentage(item.getAmount()
                        .multiply(new BigDecimal("100"))
                        .divide(total, 1, java.math.RoundingMode.HALF_UP));
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }
        }

        return TagStatisticsDTO.builder().total(total).tags(items).build();
    }

    @Override
    public AccountBalanceDTO getAccountBalances(Long bookId, String accountType) {
        List<Map<String, Object>> rows;
        if ("LIABILITY".equalsIgnoreCase(accountType)) {
            rows = finEntriesMapper.listLiabilityAccountBalances(bookId);
        } else {
            rows = finEntriesMapper.listAssetAccountBalances(bookId);
        }

        BigDecimal total = BigDecimal.ZERO;
        List<AccountBalanceDTO.AccountItem> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            BigDecimal balance = toBigDecimal(row.get("balance"));
            total = total.add(balance);
            items.add(AccountBalanceDTO.AccountItem.builder()
                    .accountId(toLong(row.get("account_id")))
                    .accountName((String) row.get("account_name"))
                    .accountIcon((String) row.get("account_icon"))
                    .balance(balance)
                    .build());
        }

        return AccountBalanceDTO.builder()
                .accountType(accountType.toUpperCase())
                .total(total)
                .accounts(items)
                .build();
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        return new BigDecimal(obj.toString());
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        return ((Number) obj).longValue();
    }


    /**
     * 根据分录的科目类型判断交易类型
     */
    private String resolveTransType(List<TransactionFlatVO> entries) {
        boolean hasExpense = false;
        boolean hasIncome = false;
        boolean hasAsset = false;
        boolean hasLiability = false;

        for (TransactionFlatVO e : entries) {
            if (e.getAccountType() == null) continue;
            switch (e.getAccountType()) {
                case "EXPENSE" -> hasExpense = true;
                case "INCOME" -> hasIncome = true;
                case "ASSET" -> hasAsset = true;
                case "LIABILITY" -> hasLiability = true;
            }
        }

        if (hasExpense) return "EXPENSE";
        if (hasIncome) return "INCOME";
        // 资产之间或资产与负债之间的转移 = 转账
        if (hasAsset && !hasExpense && !hasIncome) return "TRANSFER";
        return "OTHER";
    }

    /**
     * 根据交易类型计算显示金额
     * 支出为负数，收入为正数，转账为正数（转入金额）
     */
    private BigDecimal resolveDisplayAmount(List<TransactionFlatVO> entries, String transType) {
        if ("EXPENSE".equals(transType)) {
            // 取 EXPENSE 科目的借方金额，显示为负数
            for (TransactionFlatVO e : entries) {
                if ("EXPENSE".equals(e.getAccountType()) && "DEBIT".equals(e.getDirection())) {
                    return e.getEntryAmount().negate();
                }
            }
        } else if ("INCOME".equals(transType)) {
            // 取 INCOME 科目的贷方金额，显示为正数
            for (TransactionFlatVO e : entries) {
                if ("INCOME".equals(e.getAccountType()) && "CREDIT".equals(e.getDirection())) {
                    return e.getEntryAmount();
                }
            }
        } else if ("TRANSFER".equals(transType)) {
            // 取借方金额
            for (TransactionFlatVO e : entries) {
                if ("DEBIT".equals(e.getDirection())) {
                    return e.getEntryAmount();
                }
            }
        }
        // fallback: 取交易主表金额
        if (!entries.isEmpty()) {
            return entries.get(0).getAmount();
        }
        return BigDecimal.ZERO;
    }
}
