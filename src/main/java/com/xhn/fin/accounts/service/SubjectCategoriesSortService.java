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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        SubjectCategoriesDTO.ExpenseCategories expense = categories.getExpense();

        List<AccountSubjectDTO> payment = expense.getPaymentSubjects() == null
                ? new ArrayList<>()
                : new ArrayList<>(expense.getPaymentSubjects());
        payment.sort(Comparator
                .comparing((AccountSubjectDTO s) -> isPinned(s)).reversed()
                .thenComparing((AccountSubjectDTO s) -> expensePaymentUsageMap.getOrDefault(s.getId(), 0), Comparator.reverseOrder())
                .thenComparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                .thenComparing(SubjectCategoriesSortService::safeId)
        );
        expense.setPaymentSubjects(payment);

        List<AccountSubjectDTO> occurrenceDefault = expense.getOccurrenceSubjects() == null
                ? new ArrayList<>()
                : new ArrayList<>(expense.getOccurrenceSubjects());

        List<AccountSubjectDTO> pinned = occurrenceDefault.stream()
                .filter(SubjectCategoriesSortService::isPinned)
                .sorted(Comparator.comparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                        .thenComparing(SubjectCategoriesSortService::safeId))
                .collect(Collectors.toList());

        List<AccountSubjectDTO> nonPinned = occurrenceDefault.stream()
                .filter(s -> !isPinned(s))
                .sorted(Comparator.comparing((AccountSubjectDTO s) -> expenseOccurrenceUsageMap.getOrDefault(s.getId(), 0), Comparator.reverseOrder())
                        .thenComparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                        .thenComparing(SubjectCategoriesSortService::safeId))
                .collect(Collectors.toList());

        List<AccountSubjectDTO> top4 = new ArrayList<>();
        appendUntil(top4, pinned, 4);
        appendUntil(top4, nonPinned, 4);

        Set<Long> topIds = top4.stream().map(AccountSubjectDTO::getId).collect(Collectors.toCollection(HashSet::new));
        List<AccountSubjectDTO> occurrenceResult = new ArrayList<>(top4);
        for (AccountSubjectDTO item : occurrenceDefault) {
            if (!topIds.contains(item.getId())) {
                occurrenceResult.add(item);
            }
        }
        expense.setOccurrenceSubjects(occurrenceResult);

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

    private static void appendUntil(List<AccountSubjectDTO> target, List<AccountSubjectDTO> source, int maxSize) {
        for (AccountSubjectDTO item : source) {
            if (target.size() >= maxSize) {
                break;
            }
            target.add(item);
        }
    }

    private static boolean isPinned(AccountSubjectDTO subject) {
        return safeSortWeight(subject) > 0;
    }

    private static int safeSortWeight(AccountSubjectDTO subject) {
        return subject == null || subject.getSortWeight() == null ? 0 : subject.getSortWeight();
    }

    private static long safeId(AccountSubjectDTO subject) {
        return subject == null || subject.getId() == null ? Long.MAX_VALUE : subject.getId();
    }
}