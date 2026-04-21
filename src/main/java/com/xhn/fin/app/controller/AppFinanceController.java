package com.xhn.fin.app.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.app.service.AppFinanceService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/fin")
@RequiredArgsConstructor
@Tag(name = "app-fin", description = "App端财务首页、明细、日历、月报")
public class AppFinanceController {

    private final AppFinanceService appFinanceService;

    @GetMapping("/overview")
    public Mono<ResponseResult<Map<String, Object>>> overview(@RequestParam(required = false) Long bookId) {
        return withUser(userId -> appFinanceService.overview(userId, bookId));
    }

    @GetMapping("/accounts")
    public Mono<ResponseResult<List<Map<String, Object>>>> accounts(@RequestParam(required = false) Long bookId) {
        return withUser(userId -> appFinanceService.accounts(userId, bookId));
    }

    @GetMapping("/trend")
    public Mono<ResponseResult<List<Map<String, Object>>>> trend(@RequestParam(required = false) Long bookId,
                                                                 @RequestParam(required = false) Integer days) {
        return withUser(userId -> appFinanceService.trend(userId, bookId, days));
    }

    @GetMapping("/expense-categories")
    public Mono<ResponseResult<List<Map<String, Object>>>> expenseCategories(@RequestParam(required = false) Long bookId,
                                                                             @RequestParam(required = false) String startDate,
                                                                             @RequestParam(required = false) String endDate) {
        return withUser(userId -> appFinanceService.expenseCategories(userId, bookId, startDate, endDate));
    }

    @GetMapping("/records")
    public Mono<ResponseResult<Map<String, Object>>> records(@RequestParam(required = false) Long bookId,
                                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                             @RequestParam(required = false) String startDate,
                                                             @RequestParam(required = false) String endDate,
                                                             @RequestParam(required = false) String date,
                                                             @RequestParam(required = false) Long accountId,
                                                             @RequestParam(required = false) String category,
                                                             @RequestParam(required = false) BigDecimal minAmount,
                                                             @RequestParam(required = false) BigDecimal maxAmount,
                                                             @RequestParam(required = false) String keyword) {
        return withUser(userId -> appFinanceService.records(userId, bookId, page, pageSize, startDate, endDate,
                date, accountId, category, minAmount, maxAmount, keyword));
    }

    @GetMapping("/categories")
    public Mono<ResponseResult<Map<String, Object>>> categories(@RequestParam(required = false) Long bookId) {
        return withUser(userId -> appFinanceService.categories(userId, bookId));
    }

    @GetMapping("/calendar-summary")
    public Mono<ResponseResult<Map<String, Object>>> calendarSummary(@RequestParam(required = false) Long bookId,
                                                                     @RequestParam(required = false) Integer year,
                                                                     @RequestParam(required = false) Integer month) {
        return withUser(userId -> appFinanceService.calendarSummary(userId, bookId, year, month));
    }

    @GetMapping("/monthly-summary")
    public Mono<ResponseResult<Map<String, Object>>> monthlySummary(@RequestParam(required = false) Long bookId,
                                                                    @RequestParam(required = false) Integer year,
                                                                    @RequestParam(required = false) Integer month) {
        return withUser(userId -> appFinanceService.monthlySummary(userId, bookId, year, month));
    }

    @GetMapping("/monthly-rank")
    public Mono<ResponseResult<List<Map<String, Object>>>> monthlyRank(@RequestParam(required = false) Long bookId,
                                                                       @RequestParam(required = false) Integer year,
                                                                       @RequestParam(required = false) Integer month,
                                                                       @RequestParam(defaultValue = "expense") String type) {
        return withUser(userId -> appFinanceService.monthlyRank(userId, bookId, year, month, type));
    }

    @GetMapping("/monthly-report")
    public Mono<ResponseResult<Map<String, Object>>> monthlyReport(@RequestParam(required = false) Long bookId,
                                                                   @RequestParam(required = false) Integer year,
                                                                   @RequestParam(required = false) Integer month) {
        return withUser(userId -> appFinanceService.monthlyReport(userId, bookId, year, month));
    }

    private <T> Mono<ResponseResult<T>> withUser(java.util.function.Function<Long, T> supplier) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> ResponseResult.success(supplier.apply(userId)))
                .switchIfEmpty(Mono.just(ResponseResult.error(401, "用户未登录")));
    }
}
