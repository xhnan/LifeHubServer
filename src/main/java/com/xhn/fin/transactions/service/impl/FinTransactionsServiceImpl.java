package com.xhn.fin.transactions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.fin.transactions.mapper.FinTransactionsMapper;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务交易服务实现类
 *
 * @author xhn
 * @date 2026-02-04
 */
@Slf4j
@Service
public class FinTransactionsServiceImpl extends ServiceImpl<FinTransactionsMapper, FinTransactions> implements FinTransactionsService {

    @Override
    public Page<FinTransactions> pageByUserIdAndDateRange(Page<FinTransactions> page, Long userId, String startDate, String endDate) {
        LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinTransactions::getUserId, userId);

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
}