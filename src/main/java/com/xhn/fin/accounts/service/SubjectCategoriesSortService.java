package com.xhn.fin.accounts.service;

import com.xhn.fin.accounts.dto.AccountSubjectDTO;
import com.xhn.fin.accounts.dto.SubjectCategoriesDTO;
import com.xhn.fin.accounts.mapper.SubjectCategorySortMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectCategoriesSortService {

    private final SubjectCategorySortMapper sortMapper;

    public SubjectCategoriesDTO sortForBook(Long bookId, SubjectCategoriesDTO categories) {
        if (bookId == null || categories == null || categories.getExpense() == null) {
            return categories;
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        Map<Long, Integer> expensePaymentUsageMap = toUsageMap(sortMapper.countExpensePaymentUsage(bookId, startDate, endDate));
        Map<Long, Integer> expenseOccurrenceUsageMap = toUsageMap(sortMapper.countExpenseOccurrenceUsage(bookId, startDate, endDate));
        Map<Long, Integer> expensePaymentTop3RankMap = toTopUsageRankMap(expensePaymentUsageMap);
        Map<Long, Integer> expenseOccurrenceTop3RankMap = toTopUsageRankMap(expenseOccurrenceUsageMap);

        SubjectCategoriesDTO.ExpenseCategories expense = categories.getExpense();

        List<AccountSubjectDTO> payment = expense.getPaymentSubjects() == null
                ? new ArrayList<>()
                : new ArrayList<>(expense.getPaymentSubjects());
        payment.sort(Comparator
                .comparing((AccountSubjectDTO s) -> isPinned(s)).reversed()
                .thenComparing((AccountSubjectDTO s) -> isTopUsage(s, expensePaymentTop3RankMap)).reversed()
                .thenComparing((AccountSubjectDTO s) -> topUsageRank(s, expensePaymentTop3RankMap))
                .thenComparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                .thenComparing(SubjectCategoriesSortService::safeSortOrder)
                .thenComparing(SubjectCategoriesSortService::safeId)
        );
        expense.setPaymentSubjects(payment);

        List<AccountSubjectDTO> occurrence = expense.getOccurrenceSubjects() == null
                ? new ArrayList<>()
                : new ArrayList<>(expense.getOccurrenceSubjects());
        occurrence.sort(Comparator
                .comparing((AccountSubjectDTO s) -> isPinned(s)).reversed()
                .thenComparing((AccountSubjectDTO s) -> isTopUsage(s, expenseOccurrenceTop3RankMap)).reversed()
                .thenComparing((AccountSubjectDTO s) -> topUsageRank(s, expenseOccurrenceTop3RankMap))
                .thenComparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                .thenComparing(SubjectCategoriesSortService::safeSortOrder)
                .thenComparing(SubjectCategoriesSortService::safeId)
        );
        expense.setOccurrenceSubjects(occurrence);

        return categories;
    }

    private static Map<Long, Integer> toUsageMap(List<Map<String, Object>> rows) {
        Map<Long, Integer> usageMap = new HashMap<>();
        if (rows == null) {
            return usageMap;
        }
        for (Map<String, Object> row : rows) {
            Object accountId = row.get("account_id");
            Object usageCount = row.get("usage_count");
            if (accountId instanceof Number && usageCount instanceof Number) {
                usageMap.put(((Number) accountId).longValue(), ((Number) usageCount).intValue());
            }
        }
        return usageMap;
    }

    private static boolean isPinned(AccountSubjectDTO subject) {
        // Convention: sortWeight >= 1000 means pinned.
        return safeSortWeight(subject) >= 1000;
    }

    private static int safeSortWeight(AccountSubjectDTO subject) {
        return subject == null || subject.getSortWeight() == null ? 0 : subject.getSortWeight();
    }

    private static Map<Long, Integer> toTopUsageRankMap(Map<Long, Integer> usageMap) {
        if (usageMap == null || usageMap.isEmpty()) {
            return new HashMap<>();
        }

        List<Map.Entry<Long, Integer>> topEntries = usageMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
                .sorted(Map.Entry.<Long, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .collect(Collectors.toList());

        Map<Long, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < topEntries.size(); i++) {
            rankMap.put(topEntries.get(i).getKey(), i);
        }
        return rankMap;
    }

    private static boolean isTopUsage(AccountSubjectDTO subject, Map<Long, Integer> topUsageRankMap) {
        return subject != null && subject.getId() != null && topUsageRankMap.containsKey(subject.getId());
    }

    private static int topUsageRank(AccountSubjectDTO subject, Map<Long, Integer> topUsageRankMap) {
        if (subject == null || subject.getId() == null) {
            return Integer.MAX_VALUE;
        }
        return topUsageRankMap.getOrDefault(subject.getId(), Integer.MAX_VALUE);
    }

    private static long safeSortOrder(AccountSubjectDTO subject) {
        return subject == null || subject.getSortOrder() == null ? Long.MAX_VALUE : subject.getSortOrder();
    }

    private static long safeId(AccountSubjectDTO subject) {
        return subject == null || subject.getId() == null ? Long.MAX_VALUE : subject.getId();
    }
}
