package com.xhn.fin.transactions.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.xhn.fin.transactions.dto.MonthlyStatisticsDTO;
import com.xhn.fin.transactions.dto.TransactionDetailDTO;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * App端 交易明细 控制器（流水账视图）
 *
 * @author xhn
 * @date 2026-02-12
 */
@RestController
@RequestMapping("/app/fin/transactions")
@Tag(name = "app-fin", description = "App端-交易明细")
public class AppTransactionsController {

    @Autowired
    private FinTransactionsService finTransactionsService;

    @Autowired
    private FinBookMembersService finBookMembersService;

    @GetMapping("/monthly-statistics")
    @Operation(summary = "查询指定月份收支统计",
            description = "查询账本某月的收入总额、支出总额和结余，不传年月则默认当月")
    public Mono<ResponseResult<MonthlyStatisticsDTO>> getMonthlyStatistics(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "年份，如 2026") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份 (1-12)") @RequestParam(required = false) Integer month
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<MonthlyStatisticsDTO>error("无权限访问该账本"));
                    }
                    // 默认当月
                    java.time.YearMonth ym = java.time.YearMonth.now();
                    int y = (year != null) ? year : ym.getYear();
                    int m = (month != null) ? month : ym.getMonthValue();

                    MonthlyStatisticsDTO stats = finTransactionsService.getMonthlyStatistics(bookId, y, m);
                    return Mono.just(ResponseResult.success(stats));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @GetMapping("/details")
    @Operation(summary = "查询交易流水明细",
            description = "按日期分组的流水账视图，每笔交易展示收支类型、金额、科目、标签等信息，支持按时间范围筛选")
    public Mono<ResponseResult<TransactionDetailDTO>> getTransactionDetails(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) String endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<TransactionDetailDTO>error("无权限访问该账本"));
                    }
                    TransactionDetailDTO detail = finTransactionsService.getTransactionDetails(
                            bookId, startDate, endDate, pageNum, pageSize);
                    return Mono.just(ResponseResult.success(detail));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }
}
