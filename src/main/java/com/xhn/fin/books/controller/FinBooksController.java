package com.xhn.fin.books.controller;

import com.xhn.fin.books.model.FinBooks;
import com.xhn.fin.books.service.FinBooksService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinBooks 控制器
 *
 * @author xhn
 * @date 2026-02-07
 */
@RestController
@RequestMapping("/fin/books")
@Tag(name = "fin", description = "账簿管理")
public class FinBooksController {

    @Autowired
    private FinBooksService finBooksService;

    @PostMapping
    @Operation(summary = "新增账簿")
    public ResponseResult<Boolean> add(
            @RequestBody FinBooks finBooks
    ) {
        boolean result = finBooksService.save(finBooks);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除账簿")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "账簿ID") @PathVariable Long id
    ) {
        boolean result = finBooksService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改账簿")
    public ResponseResult<Boolean> update(
            @RequestBody FinBooks finBooks
    ) {
        boolean result = finBooksService.updateById(finBooks);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询账簿")
    public ResponseResult<FinBooks> getById(
            @Parameter(description = "账簿ID") @PathVariable Long id
    ) {
        FinBooks finBooks = finBooksService.getById(id);
        return finBooks != null ? ResponseResult.success(finBooks) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有账簿列表")
    public ResponseResult<List<FinBooks>> list() {
        List<FinBooks> list = finBooksService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账簿")
    public ResponseResult<Page<FinBooks>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinBooks> page = new Page<>(pageNum, pageSize);
        Page<FinBooks> resultPage = finBooksService.page(page);
        return ResponseResult.success(resultPage);
    }
}