package com.xhn.fin.accounts.advice;

import com.xhn.fin.accounts.dto.AccountSubjectDTO;
import com.xhn.fin.accounts.dto.SubjectCategoriesDTO;
import com.xhn.fin.accounts.mapper.SubjectCategorySortMapper;
import com.xhn.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class SubjectCategoriesSortAdvice implements ResponseBodyAdvice<Object> {

    private static final String TARGET_PATH = "/fin/accounts/categories";

    private final SubjectCategorySortMapper sortMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (!(body instanceof ResponseResult)) {
            return body;
        }
        ResponseResult<?> responseResult = (ResponseResult<?>) body;

        String path = request.getURI().getPath();
        if (path == null || !path.endsWith(TARGET_PATH)) {
            return body;
        }

        Object data = responseResult.getData();
        if (!(data instanceof SubjectCategoriesDTO)) {
            return body;
        }
        SubjectCategoriesDTO categories = (SubjectCategoriesDTO) data;
        if (categories.getExpense() == null) {
            return body;
        }

        Long bookId = parseBookId(request);
        if (bookId == null) {
            return body;
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
                .thenComparing(SubjectCategoriesSortAdvice::safeId)
        );
        expense.setPaymentSubjects(payment);

        List<AccountSubjectDTO> occurrenceDefault = expense.getOccurrenceSubjects() == null
                ? new ArrayList<>()
                : new ArrayList<>(expense.getOccurrenceSubjects());

        List<AccountSubjectDTO> pinned = occurrenceDefault.stream()
                .filter(SubjectCategoriesSortAdvice::isPinned)
                .sorted(Comparator.comparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                        .thenComparing(SubjectCategoriesSortAdvice::safeId))
                .collect(Collectors.toList());

        List<AccountSubjectDTO> nonPinned = occurrenceDefault.stream()
                .filter(s -> !isPinned(s))
                .sorted(Comparator.comparing((AccountSubjectDTO s) -> expenseOccurrenceUsageMap.getOrDefault(s.getId(), 0), Comparator.reverseOrder())
                        .thenComparing((AccountSubjectDTO s) -> safeSortWeight(s), Comparator.reverseOrder())
                        .thenComparing(SubjectCategoriesSortAdvice::safeId))
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

        return body;
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

    private Long parseBookId(ServerHttpRequest request) {
        String value = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("bookId");
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}