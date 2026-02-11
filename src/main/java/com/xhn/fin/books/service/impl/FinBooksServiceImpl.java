package com.xhn.fin.books.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.fin.books.mapper.FinBooksMapper;
import com.xhn.fin.books.model.BookAssetSummaryDTO;
import com.xhn.fin.books.model.FinBooks;
import com.xhn.fin.books.service.FinBooksService;
import com.xhn.fin.entries.mapper.FinEntriesMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账本信息 服务实现类
 *
 * @author xhn
 * @date 2026-02-07
 */
@Slf4j
@Service
public class FinBooksServiceImpl extends ServiceImpl<FinBooksMapper, FinBooks> implements FinBooksService {

    @Autowired
    private FinEntriesMapper finEntriesMapper;

    @Override
    public List<FinBooks> getBooksByUserId(Long userId) {
        List<FinBooks> result = new ArrayList<>();

        // 1. 查询用户创建的账本（owner_id = userId）
        LambdaQueryWrapper<FinBooks> ownerWrapper = new LambdaQueryWrapper<>();
        ownerWrapper.eq(FinBooks::getOwnerId, userId);
        List<FinBooks> ownedBooks = this.list(ownerWrapper);
        result.addAll(ownedBooks);

        // 2. 查询用户加入的账本（通过 fin_book_members 表）
        // 这里需要自定义 SQL 查询，因为涉及到联表
        List<FinBooks> memberBooks = baseMapper.selectByUserId(userId);
        result.addAll(memberBooks);

        // 3. 去重（如果用户既是拥有者又是成员，可能会有重复）
        return result.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public BookAssetSummaryDTO getAssetSummary(Long bookId) {
        BigDecimal totalAssets = finEntriesMapper.sumAssetBalance(bookId);
        BigDecimal totalLiabilities = finEntriesMapper.sumLiabilityBalance(bookId);
        BigDecimal netAssets = totalAssets.subtract(totalLiabilities);

        return BookAssetSummaryDTO.builder()
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .netAssets(netAssets)
                .build();
    }
}
