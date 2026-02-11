package com.xhn.fin.transactions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.fin.entries.mapper.FinEntriesMapper;
import com.xhn.fin.entries.model.FinEntries;
import com.xhn.fin.entries.service.FinEntriesService;
import com.xhn.fin.transtags.model.FinTransTags;
import com.xhn.fin.transtags.service.FinTransTagsService;
import com.xhn.fin.transactions.dto.MonthlyStatisticsDTO;
import com.xhn.fin.transactions.mapper.FinTransactionsMapper;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.fin.transactions.dto.TransactionEntryDTO;
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
}
