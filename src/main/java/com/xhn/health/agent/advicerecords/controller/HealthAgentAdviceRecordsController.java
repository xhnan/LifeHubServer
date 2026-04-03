package com.xhn.health.agent.advicerecords.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.agent.advicerecords.model.HealthAgentAdviceRecords;
import com.xhn.health.agent.advicerecords.service.HealthAgentAdviceRecordsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/health/agent/advice-records")
@Tag(name = "health-agent", description = "Health agent data")
public class HealthAgentAdviceRecordsController {

    @Autowired
    private HealthAgentAdviceRecordsService healthAgentAdviceRecordsService;

    @PostMapping
    @Operation(summary = "Create advice record")
    public Mono<ResponseResult<Boolean>> add(@RequestBody HealthAgentAdviceRecords adviceRecord) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    adviceRecord.setUserId(userId);
                    boolean result = healthAgentAdviceRecordsService.save(adviceRecord);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete advice record")
    public Mono<ResponseResult<Boolean>> delete(@Parameter(description = "ID") @PathVariable Long id) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthAgentAdviceRecords adviceRecord = healthAgentAdviceRecordsService.getById(id);
                    if (adviceRecord == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(adviceRecord.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("无权删除该记录"));
                    }
                    boolean result = healthAgentAdviceRecordsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "Update advice record")
    public ResponseResult<Boolean> update(@RequestBody HealthAgentAdviceRecords adviceRecord) {
        boolean result = healthAgentAdviceRecordsService.updateById(adviceRecord);
        return result ? ResponseResult.success(true) : ResponseResult.error("更新失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get advice record by id")
    public ResponseResult<HealthAgentAdviceRecords> getById(@Parameter(description = "ID") @PathVariable Long id) {
        HealthAgentAdviceRecords adviceRecord = healthAgentAdviceRecordsService.getById(id);
        return adviceRecord != null ? ResponseResult.success(adviceRecord) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "List advice records")
    public ResponseResult<List<HealthAgentAdviceRecords>> list() {
        return ResponseResult.success(healthAgentAdviceRecordsService.list());
    }

    @GetMapping("/my")
    @Operation(summary = "Get my advice records")
    public Mono<ResponseResult<List<HealthAgentAdviceRecords>>> my(
            @RequestParam(required = false) String agentType,
            @RequestParam(required = false) Boolean activeOnly) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthAgentAdviceRecords> list;
                    if (Boolean.TRUE.equals(activeOnly)) {
                        list = healthAgentAdviceRecordsService.getActiveAdviceRecordsByUserId(userId);
                    } else if (agentType != null && !agentType.isBlank()) {
                        list = healthAgentAdviceRecordsService.getAdviceRecordsByUserIdAndAgentType(userId, agentType);
                    } else {
                        list = healthAgentAdviceRecordsService.getAdviceRecordsByUserId(userId);
                    }
                    return ResponseResult.success(list);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "Page advice records")
    public ResponseResult<Page<HealthAgentAdviceRecords>> page(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<HealthAgentAdviceRecords> page = new Page<>(pageNum, pageSize);
        return ResponseResult.success(healthAgentAdviceRecordsService.page(page));
    }
}
