package com.xhn.health.psychology.chatmemories.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.psychology.chatmemories.model.HealthPsyChatMemories;
import com.xhn.health.psychology.chatmemories.service.HealthPsyChatMemoriesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * 聊天记录控制器
 *
 * @author xhn
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/health/psychology/chat-memories")
@Tag(name = "health-psychology", description = "心理健康管理")
public class HealthPsyChatMemoriesController {

    @Autowired
    private HealthPsyChatMemoriesService healthPsyChatMemoriesService;

    @PostMapping
    @Operation(summary = "新增聊天记录")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthPsyChatMemories healthPsyChatMemories
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthPsyChatMemories.setUserId(userId);
                    boolean result = healthPsyChatMemoriesService.save(healthPsyChatMemories);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除聊天记录")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthPsyChatMemories chatMemory = healthPsyChatMemoriesService.getById(id);
                    if (chatMemory == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(chatMemory.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthPsyChatMemoriesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改聊天记录")
    public ResponseResult<Boolean> update(
            @RequestBody HealthPsyChatMemories healthPsyChatMemories
    ) {
        boolean result = healthPsyChatMemoriesService.updateById(healthPsyChatMemories);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询聊天记录")
    public ResponseResult<HealthPsyChatMemories> getById(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        HealthPsyChatMemories healthPsyChatMemories = healthPsyChatMemoriesService.getById(id);
        return healthPsyChatMemories != null ? ResponseResult.success(healthPsyChatMemories) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有聊天记录列表")
    public ResponseResult<List<HealthPsyChatMemories>> list() {
        List<HealthPsyChatMemories> list = healthPsyChatMemoriesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的聊天记录列表")
    public Mono<ResponseResult<List<HealthPsyChatMemories>>> getMyChatMemories(
            @Parameter(description = "角色") @RequestParam(required = false) String role
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthPsyChatMemories> chatMemories;
                    if (role != null && !role.isEmpty()) {
                        chatMemories = healthPsyChatMemoriesService.getChatMemoriesByUserIdAndRole(userId, role);
                    } else {
                        chatMemories = healthPsyChatMemoriesService.getChatMemoriesByUserId(userId);
                    }
                    return ResponseResult.success(chatMemories);
                });
    }

    @GetMapping("/recent")
    @Operation(summary = "获取我的最近聊天记录")
    public Mono<ResponseResult<List<HealthPsyChatMemories>>> getRecentChatMemories(
            @Parameter(description = "记录数量") @RequestParam(defaultValue = "10") int limit
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthPsyChatMemories> chatMemories = healthPsyChatMemoriesService.getRecentChatMemoriesByUserId(userId, limit);
                    return ResponseResult.success(chatMemories);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询聊天记录")
    public ResponseResult<Page<HealthPsyChatMemories>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthPsyChatMemories> page = new Page<>(pageNum, pageSize);
        Page<HealthPsyChatMemories> resultPage = healthPsyChatMemoriesService.page(page);
        return ResponseResult.success(resultPage);
    }
}
