package com.xhn.fin.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.base.exception.ApplicationException;
import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.service.FinAccountsService;
import com.xhn.fin.app.mapper.AppFinanceMapper;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.xhn.fin.books.model.FinBooks;
import com.xhn.fin.books.service.FinBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppFinanceService {

    private final AppFinanceMapper appFinanceMapper;
    private final FinBooksService finBooksService;
    private final FinBookMembersService finBookMembersService;
    private final FinAccountsService finAccountsService;

    public Map<String, Object> overview(Long userId, Long bookId) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(23, 59, 59);

        BigDecimal totalAsset = nz(appFinanceMapper.sumBalanceByType(resolvedBookId, "ASSET", null));
        BigDecimal totalLiability = nz(appFinanceMapper.sumBalanceByType(resolvedBookId, "LIABILITY", null));
        BigDecimal prevAsset = nz(appFinanceMapper.sumBalanceByType(resolvedBookId, "ASSET", yesterdayEnd));
        BigDecimal prevLiability = nz(appFinanceMapper.sumBalanceByType(resolvedBookId, "LIABILITY", yesterdayEnd));
        BigDecimal netWorth = totalAsset.subtract(totalLiability);
        BigDecimal prevNetWorth = prevAsset.subtract(prevLiability);

        Map<String, Object> result = ordered();
        result.put("totalAsset", totalAsset);
        result.put("totalLiability", totalLiability);
        result.put("netWorth", netWorth);
        result.put("assetChange", totalAsset.subtract(prevAsset));
        result.put("liabilityChange", totalLiability.subtract(prevLiability));
        result.put("netWorthChange", netWorth.subtract(prevNetWorth));
        return result;
    }

    public List<Map<String, Object>> accounts(Long userId, Long bookId) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        return appFinanceMapper.listAccountBalances(resolvedBookId).stream()
                .map(row -> {
                    Map<String, Object> item = ordered();
                    String accountType = str(row.get("account_type"));
                    String name = str(row.get("name"));
                    item.put("id", toLong(row.get("id")));
                    item.put("name", name);
                    item.put("type", toAccountViewType(accountType, name));
                    item.put("icon", defaultIcon(str(row.get("icon")), accountType, name));
                    item.put("balance", nz(row.get("balance")));
                    return item;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> trend(Long userId, Long bookId, Integer days) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        int safeDays = days == null || days <= 0 ? 7 : Math.min(days, 366);
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(safeDays - 1L);
        Map<LocalDate, Map<String, Object>> rowsByDate = appFinanceMapper
                .selectDailyIncomeExpense(resolvedBookId, start.atStartOfDay(), end.atTime(23, 59, 59))
                .stream()
                .collect(Collectors.toMap(row -> toLocalDate(row.get("date")), Function.identity(), (a, b) -> a));

        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Map<String, Object> row = rowsByDate.get(date);
            Map<String, Object> item = ordered();
            item.put("date", date.toString());
            item.put("income", row == null ? BigDecimal.ZERO : nz(row.get("income")));
            item.put("expense", row == null ? BigDecimal.ZERO : nz(row.get("expense")));
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> expenseCategories(Long userId, Long bookId, String startDate, String endDate) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        LocalDateTime start = parseStart(startDate, null);
        LocalDateTime end = parseEnd(endDate, null);
        return appFinanceMapper.selectCategoryTotals(resolvedBookId, "EXPENSE", start, end).stream()
                .map(row -> categoryItem(row, false))
                .collect(Collectors.toList());
    }

    public Map<String, Object> records(Long userId, Long bookId, Integer page, Integer pageSize,
                                       String startDate, String endDate, String date, Long accountId,
                                       String category, BigDecimal minAmount, BigDecimal maxAmount, String keyword) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        int safePage = page == null || page <= 0 ? 1 : page;
        int safePageSize = pageSize == null || pageSize <= 0 ? 20 : Math.min(pageSize, 200);
        long offset = (long) (safePage - 1) * safePageSize;

        LocalDateTime start = parseStart(startDate, null);
        LocalDateTime end = parseEnd(endDate, null);
        LocalDateTime dateStart = parseStart(date, null);
        LocalDateTime dateEnd = parseEnd(date, null);

        long total = appFinanceMapper.countRecords(resolvedBookId, start, end, dateStart, dateEnd, accountId,
                blankToNull(category), minAmount, maxAmount, blankToNull(keyword));
        List<Map<String, Object>> rows = appFinanceMapper.selectRecords(resolvedBookId, start, end, dateStart, dateEnd,
                accountId, blankToNull(category), minAmount, maxAmount, blankToNull(keyword), offset, safePageSize);

        Map<String, Object> result = ordered();
        result.put("total", total);
        result.put("records", rows.stream().map(this::recordItem).collect(Collectors.toList()));
        return result;
    }

    public Map<String, Object> categories(Long userId, Long bookId) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        LambdaQueryWrapper<FinAccounts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinAccounts::getBookId, resolvedBookId)
                .eq(FinAccounts::getIsLeaf, true)
                .in(FinAccounts::getAccountType, "INCOME", "EXPENSE")
                .orderByAsc(FinAccounts::getSortOrder)
                .orderByAsc(FinAccounts::getId);
        List<FinAccounts> accounts = finAccountsService.list(wrapper);

        Map<String, Object> result = ordered();
        result.put("income", accounts.stream()
                .filter(a -> "INCOME".equals(a.getAccountType()))
                .map(FinAccounts::getName)
                .collect(Collectors.toList()));
        result.put("expense", accounts.stream()
                .filter(a -> "EXPENSE".equals(a.getAccountType()))
                .map(FinAccounts::getName)
                .collect(Collectors.toList()));
        return result;
    }

    public Map<String, Object> calendarSummary(Long userId, Long bookId, Integer year, Integer month) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        YearMonth ym = resolveMonth(year, month);
        Map<LocalDate, Map<String, Object>> rowsByDate = appFinanceMapper
                .selectDailyIncomeExpense(resolvedBookId, ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59))
                .stream()
                .collect(Collectors.toMap(row -> toLocalDate(row.get("date")), Function.identity(), (a, b) -> a));

        Map<String, Object> result = ordered();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate current = ym.atDay(day);
            Map<String, Object> row = rowsByDate.get(current);
            Map<String, Object> item = ordered();
            item.put("income", row == null ? BigDecimal.ZERO : nz(row.get("income")));
            item.put("expense", row == null ? BigDecimal.ZERO : nz(row.get("expense")));
            result.put(current.toString(), item);
        }
        return result;
    }

    public Map<String, Object> monthlySummary(Long userId, Long bookId, Integer year, Integer month) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        return monthlySummaryFor(resolvedBookId, resolveMonth(year, month));
    }

    public List<Map<String, Object>> monthlyRank(Long userId, Long bookId, Integer year, Integer month, String type) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        YearMonth ym = resolveMonth(year, month);
        String accountType = "income".equalsIgnoreCase(type) ? "INCOME" : "EXPENSE";
        List<Map<String, Object>> rows = appFinanceMapper.selectCategoryTotals(
                resolvedBookId, accountType, ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59));
        BigDecimal total = rows.stream().map(row -> nz(row.get("amount"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        return rows.stream().map(row -> {
            Map<String, Object> item = categoryItem(row, false);
            item.put("percent", percent(nz(row.get("amount")), total));
            return item;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> monthlyReport(Long userId, Long bookId, Integer year, Integer month) {
        Long resolvedBookId = resolveBookId(userId, bookId);
        YearMonth ym = resolveMonth(year, month);
        Map<String, Object> summary = monthlySummaryFor(resolvedBookId, ym);
        Map<String, Object> prevSummary = monthlySummaryFor(resolvedBookId, ym.minusMonths(1));

        List<Map<String, Object>> dailyTrend = trendForMonth(resolvedBookId, ym);
        List<Map<String, Object>> expenseCategories = monthlyRank(userId, resolvedBookId, ym.getYear(), ym.getMonthValue(), "expense");
        List<Map<String, Object>> incomeTop3 = appFinanceMapper
                .selectTopRecords(resolvedBookId, "income", ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59), 3)
                .stream().map(this::topRecordItem).collect(Collectors.toList());
        List<Map<String, Object>> expenseTop3 = appFinanceMapper
                .selectTopRecords(resolvedBookId, "expense", ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59), 3)
                .stream().map(this::topRecordItem).collect(Collectors.toList());

        BigDecimal expense = nz(summary.get("expense"));
        BigDecimal maxExpense = expenseTop3.stream().map(row -> nz(row.get("amount"))).reduce(BigDecimal.ZERO, BigDecimal::max);
        BigDecimal maxIncome = incomeTop3.stream().map(row -> nz(row.get("amount"))).reduce(BigDecimal.ZERO, BigDecimal::max);

        Map<String, Object> metrics = ordered();
        metrics.put("avgDailyExpense", expense.divide(BigDecimal.valueOf(ym.lengthOfMonth()), 2, RoundingMode.HALF_UP));
        metrics.put("maxExpense", maxExpense);
        metrics.put("maxIncome", maxIncome);
        metrics.put("totalDays", ym.lengthOfMonth());

        Map<String, Object> prev = ordered();
        prev.put("income", prevSummary.get("income"));
        prev.put("expense", prevSummary.get("expense"));

        Map<String, Object> result = ordered();
        result.put("summary", summary);
        result.put("prevSummary", prev);
        result.put("dailyTrend", dailyTrend);
        result.put("expenseCategories", expenseCategories);
        result.put("incomeTop3", incomeTop3);
        result.put("expenseTop3", expenseTop3);
        result.put("metrics", metrics);
        return result;
    }

    private List<Map<String, Object>> trendForMonth(Long bookId, YearMonth ym) {
        Map<LocalDate, Map<String, Object>> rowsByDate = appFinanceMapper
                .selectDailyIncomeExpense(bookId, ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59))
                .stream()
                .collect(Collectors.toMap(row -> toLocalDate(row.get("date")), Function.identity(), (a, b) -> a));
        List<Map<String, Object>> result = new ArrayList<>();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            Map<String, Object> row = rowsByDate.get(date);
            Map<String, Object> item = ordered();
            item.put("day", day);
            item.put("expense", row == null ? BigDecimal.ZERO : nz(row.get("expense")));
            result.add(item);
        }
        return result;
    }

    private Map<String, Object> monthlySummaryFor(Long bookId, YearMonth ym) {
        Map<String, Object> row = appFinanceMapper.selectMonthlySummary(bookId, ym.atDay(1).atStartOfDay(), ym.atEndOfMonth().atTime(23, 59, 59));
        Map<String, Object> result = ordered();
        result.put("income", nz(row == null ? null : row.get("income")));
        result.put("expense", nz(row == null ? null : row.get("expense")));
        result.put("incomeCount", toLong(row == null ? null : row.get("income_count")));
        result.put("expenseCount", toLong(row == null ? null : row.get("expense_count")));
        return result;
    }

    private Long resolveBookId(Long userId, Long bookId) {
        if (bookId != null) {
            if (!finBookMembersService.hasAccess(bookId, userId)) {
                throw new ApplicationException("无权限访问该账本");
            }
            return bookId;
        }
        List<FinBooks> books = finBooksService.getBooksByUserId(userId);
        if (books == null || books.isEmpty()) {
            throw new ApplicationException("当前用户没有账本");
        }
        return books.get(0).getId();
    }

    private YearMonth resolveMonth(Integer year, Integer month) {
        YearMonth now = YearMonth.now();
        return YearMonth.of(year == null ? now.getYear() : year, month == null ? now.getMonthValue() : month);
    }

    private Map<String, Object> recordItem(Map<String, Object> row) {
        Map<String, Object> item = ordered();
        item.put("id", toLong(row.get("id")));
        item.put("type", str(row.get("type")));
        item.put("category", str(row.get("category")));
        item.put("icon", defaultIcon(str(row.get("icon")), str(row.get("type")).toUpperCase(), str(row.get("category"))));
        item.put("amount", nz(row.get("amount")).abs());
        item.put("note", str(row.get("note")));
        item.put("account", str(row.get("account")));
        item.put("accountId", toLong(row.get("account_id")));
        LocalDateTime transDate = toLocalDateTime(row.get("trans_date"));
        item.put("date", transDate == null ? null : transDate.toLocalDate().toString());
        item.put("timestamp", transDate == null ? null : java.sql.Timestamp.valueOf(transDate).getTime());
        return item;
    }

    private Map<String, Object> topRecordItem(Map<String, Object> row) {
        Map<String, Object> item = ordered();
        item.put("id", toLong(row.get("id")));
        item.put("category", str(row.get("category")));
        item.put("icon", defaultIcon(str(row.get("icon")), str(row.get("type")).toUpperCase(), str(row.get("category"))));
        item.put("amount", nz(row.get("amount")).abs());
        item.put("note", str(row.get("note")));
        return item;
    }

    private Map<String, Object> categoryItem(Map<String, Object> row, boolean ignored) {
        Map<String, Object> item = ordered();
        item.put("name", str(row.get("name")));
        item.put("icon", defaultIcon(str(row.get("icon")), str(row.get("account_type")), str(row.get("name"))));
        item.put("amount", nz(row.get("amount")));
        return item;
    }

    private String toAccountViewType(String accountType, String name) {
        if ("LIABILITY".equals(accountType)) {
            return "credit";
        }
        String lowerName = name == null ? "" : name.toLowerCase();
        if (lowerName.contains("支付宝") || lowerName.contains("微信") || lowerName.contains("钱包")) {
            return "ewallet";
        }
        if (lowerName.contains("基金") || lowerName.contains("股票") || lowerName.contains("证券") || lowerName.contains("投资") || lowerName.contains("黄金")) {
            return "investment";
        }
        return "bank";
    }

    private String defaultIcon(String icon, String type, String name) {
        if (StringUtils.hasText(icon)) {
            return icon;
        }
        if ("LIABILITY".equals(type) || "credit".equalsIgnoreCase(type)) {
            return "💳";
        }
        if ("INCOME".equals(type) || "income".equalsIgnoreCase(type)) {
            return "💰";
        }
        if ("EXPENSE".equals(type) || "expense".equalsIgnoreCase(type)) {
            return "🍜";
        }
        return "🏦";
    }

    private LocalDateTime parseStart(String date, LocalDateTime fallback) {
        return StringUtils.hasText(date) ? LocalDate.parse(date).atStartOfDay() : fallback;
    }

    private LocalDateTime parseEnd(String date, LocalDateTime fallback) {
        return StringUtils.hasText(date) ? LocalDate.parse(date).atTime(23, 59, 59) : fallback;
    }

    private BigDecimal percent(BigDecimal amount, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP);
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value : null;
    }

    private BigDecimal nz(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private String str(Object value) {
        return value == null ? "" : value.toString();
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
        return LocalDate.parse(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) return ((java.sql.Timestamp) value).toLocalDateTime();
        return value == null ? null : LocalDateTime.parse(value.toString().replace(" ", "T"));
    }

    private Map<String, Object> ordered() {
        return new LinkedHashMap<>();
    }
}
