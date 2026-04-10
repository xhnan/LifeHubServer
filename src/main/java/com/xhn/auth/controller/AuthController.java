package com.xhn.auth.controller;


import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;
import com.xhn.auth.model.QrCodeLoginInfo;
import com.xhn.auth.service.AuthService;
import com.xhn.auth.service.QrCodeService;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


/**
 * 认证控制器
 * @author xhn
 * @date 2026-01-24
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final QrCodeService qrCodeService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数（会自动验证）
     * @return 返回 token
     */
    @PostMapping("/login")
    public ResponseResult<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseResult.success(response);

    }

    //根据刷新token获取新的访问token
    @PostMapping("/refresh")
    public ResponseResult<LoginResponse> refreshToken(@RequestBody String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseResult.success(response);
    }

    // ==================== 二维码登录 ====================

    /**
     * 生成二维码
     *
     * @return qrCodeId 和过期时间
     */
    @PostMapping("/qrcode/generate")
    public Mono<ResponseResult<Map<String, Object>>> generateQrCode() {
        return qrCodeService.generateQrCode()
                .map(qrCodeId -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("qrCodeId", qrCodeId);
                    data.put("expiresIn", 300);
                    return ResponseResult.success(data);
                });
    }

    /**
     * 查询二维码状态（Web前端轮询）
     *
     * @param qrCodeId 二维码ID
     * @return 二维码状态信息，确认后包含 token
     */
    @GetMapping("/qrcode/status/{qrCodeId}")
    public Mono<ResponseResult<QrCodeLoginInfo>> getQrCodeStatus(@PathVariable String qrCodeId) {
        return qrCodeService.getQrCodeStatus(qrCodeId)
                .map(ResponseResult::success);
    }

    /**
     * 扫码（移动端调用，需要JWT认证）
     *
     * @param qrCodeId 二维码ID
     * @return 操作结果
     */
    @PostMapping("/qrcode/scan/{qrCodeId}")
    public Mono<ResponseResult<Void>> scanQrCode(@PathVariable String qrCodeId) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> qrCodeService.scanQrCode(qrCodeId, userId)
                        .thenReturn(ResponseResult.<Void>success()))
                .switchIfEmpty(Mono.just(ResponseResult.error(401, "未登录，请先登录")));
    }

    /**
     * 确认登录（移动端调用，需要JWT认证）
     *
     * @param qrCodeId 二维码ID
     * @return 操作结果
     */
    @PostMapping("/qrcode/confirm/{qrCodeId}")
    public Mono<ResponseResult<Void>> confirmQrCode(@PathVariable String qrCodeId) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> qrCodeService.confirmQrCode(qrCodeId, userId)
                        .thenReturn(ResponseResult.<Void>success()))
                .switchIfEmpty(Mono.just(ResponseResult.error(401, "未登录，请先登录")));
    }

    /**
     * 取消登录（移动端调用，需要JWT认证）
     *
     * @param qrCodeId 二维码ID
     * @return 操作结果
     */
    @PostMapping("/qrcode/cancel/{qrCodeId}")
    public Mono<ResponseResult<Void>> cancelQrCode(@PathVariable String qrCodeId) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> qrCodeService.cancelQrCode(qrCodeId, userId)
                        .thenReturn(ResponseResult.<Void>success()))
                .switchIfEmpty(Mono.just(ResponseResult.error(401, "未登录，请先登录")));
    }
}
