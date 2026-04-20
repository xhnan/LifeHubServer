package com.xhn.fin.skills.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.accounts.dto.SubjectCategoriesDTO;
import com.xhn.fin.accounts.service.FinAccountsService;
import com.xhn.fin.accounts.service.SubjectCategoriesSortService;
import com.xhn.fin.books.model.FinBooks;
import com.xhn.fin.books.service.FinBooksService;
import com.xhn.fin.skills.dto.SkillBookkeepingContextDTO;
import com.xhn.response.ResponseResult;
import com.xhn.sys.userconfig.model.SysUserConfig;
import com.xhn.sys.userconfig.service.SysUserConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Skills bookkeeping aggregation endpoints.
 */
@RestController
@RequestMapping("/fin/skills")
@Tag(name = "fin", description = "Skills finance helper endpoints")
public class FinSkillsController {

    private static final String DEFAULT_BOOK_CONFIG_KEY = "fin.default_book_id";

    @Autowired
    private FinBooksService finBooksService;

    @Autowired
    private FinAccountsService finAccountsService;

    @Autowired
    private SubjectCategoriesSortService subjectCategoriesSortService;

    @Autowired
    private SysUserConfigService sysUserConfigService;

    @GetMapping("/bookkeeping-context")
    @Operation(
            summary = "Get bookkeeping context for skills",
            description = "Return the resolved default book and its subject categories for the current authenticated user"
    )
    public Mono<ResponseResult<SkillBookkeepingContextDTO>> getBookkeepingContext() {
        return SecurityUtils.getCurrentUserId()
                .map(this::buildBookkeepingContext)
                .map(ResponseResult::success)
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseResult.error(e.getMessage())))
                .switchIfEmpty(Mono.just(ResponseResult.error("Failed to resolve current user")));
    }

    private SkillBookkeepingContextDTO buildBookkeepingContext(Long userId) {
        List<FinBooks> availableBooks = finBooksService.getBooksByUserId(userId);
        if (availableBooks == null || availableBooks.isEmpty()) {
            throw new IllegalArgumentException("No accessible books found for current user");
        }

        Optional<Long> configuredBookId = findConfiguredDefaultBookId(userId);

        FinBooks defaultBook = configuredBookId
                .flatMap(bookId -> availableBooks.stream()
                        .filter(book -> bookId.equals(book.getId()))
                        .findFirst())
                .orElseGet(() -> availableBooks.stream()
                        .sorted(Comparator.comparing(FinBooks::getId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No accessible books found for current user")));

        SubjectCategoriesDTO categories = finAccountsService.getSubjectCategories(defaultBook.getId());
        categories = subjectCategoriesSortService.sortForBook(defaultBook.getId(), categories);

        return SkillBookkeepingContextDTO.builder()
                .defaultBook(SkillBookkeepingContextDTO.DefaultBookDTO.builder()
                        .id(defaultBook.getId())
                        .name(defaultBook.getName())
                        .description(defaultBook.getDescription())
                        .defaultCurrency(defaultBook.getDefaultCurrency())
                        .coverUrl(defaultBook.getCoverUrl())
                        .fromUserConfig(configuredBookId.isPresent() && configuredBookId.get().equals(defaultBook.getId()))
                        .build())
                .subjectCategories(categories)
                .build();
    }

    private Optional<Long> findConfiguredDefaultBookId(Long userId) {
        LambdaQueryWrapper<SysUserConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserConfig::getUserId, userId)
                .eq(SysUserConfig::getConfigKey, DEFAULT_BOOK_CONFIG_KEY)
                .last("limit 1");

        SysUserConfig config = sysUserConfigService.getOne(wrapper, false);
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(config.getConfigValue().trim()));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
