package com.xhn.fin.transactions.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.xhn.fin.transactions.dto.*;
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

    @GetMapping("/yearly-trend")
    @Operation(summary = "查询年度收支趋势",
            description = "按月统计一年12个月的收入、支出、结余趋势")
    public Mono<ResponseResult<YearlyTrendDTO>> getYearlyTrend(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "年份，如 2026，不传默认当年") @RequestParam(required = false) Integer year
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<YearlyTrendDTO>error("无权限访问该账本"));
                    }
                    int y = (year != null) ? year : java.time.Year.now().getValue();
                    YearlyTrendDTO trend = finTransactionsService.getYearlyTrend(bookId, y);
                    return Mono.just(ResponseResult.success(trend));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @GetMapping("/category-rank")
    @Operation(summary = "查询分类排行",
            description = "按科目分类统计支出或收入排行，含金额和占比")
    public Mono<ResponseResult<CategoryRankDTO>> getCategoryRank(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "类型：EXPENSE(支出)/INCOME(收入)") @RequestParam(defaultValue = "EXPENSE") String type,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份 (1-12)") @RequestParam(required = false) Integer month
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<CategoryRankDTO>error("无权限访问该账本"));
                    }
                    java.time.YearMonth ym = java.time.YearMonth.now();
                    int y = (year != null) ? year : ym.getYear();
                    int m = (month != null) ? month : ym.getMonthValue();
                    CategoryRankDTO rank = finTransactionsService.getCategoryRank(bookId, type, y, m);
                    return Mono.just(ResponseResult.success(rank));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @GetMapping("/tag-statistics")
    @Operation(summary = "查询标签统计",
            description = "按标签分组统计支出金额、笔数和占比")
    public Mono<ResponseResult<TagStatisticsDTO>> getTagStatistics(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份 (1-12)") @RequestParam(required = false) Integer month
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<TagStatisticsDTO>error("无权限访问该账本"));
                    }
                    java.time.YearMonth ym = java.time.YearMonth.now();
                    int y = (year != null) ? year : ym.getYear();
                    int m = (month != null) ? month : ym.getMonthValue();
                    TagStatisticsDTO stats = finTransactionsService.getTagStatistics(bookId, y, m);
                    return Mono.just(ResponseResult.success(stats));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @GetMapping("/account-balances")
    @Operation(summary = "查询资产/负债各科目余额明细",
            description = "查询指定类型（ASSET/LIABILITY）下每个叶子科目的当前余额")
    public Mono<ResponseResult<AccountBalanceDTO>> getAccountBalances(
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "科目类型：ASSET(资产)/LIABILITY(负债)") @RequestParam(defaultValue = "ASSET") String accountType
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    if (!finBookMembersService.hasAccess(bookId, userId)) {
                        return Mono.just(ResponseResult.<AccountBalanceDTO>error("无权限访问该账本"));
                    }
                    AccountBalanceDTO balances = finTransactionsService.getAccountBalances(bookId, accountType);
                    return Mono.just(ResponseResult.success(balances));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }
}
