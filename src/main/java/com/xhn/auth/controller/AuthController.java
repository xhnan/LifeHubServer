package com.xhn.auth.controller;


import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;
import com.xhn.auth.model.QrCodeLoginInfo;
import com.xhn.auth.model.AuthLoginPayload;
import com.xhn.auth.model.AuthUserProfile;
import com.xhn.auth.model.WxLoginRequest;
import com.xhn.base.exception.ApplicationException;
import com.xhn.auth.service.AuthService;
import com.xhn.auth.service.QrCodeService;
import com.xhn.base.utils.JwtUtil;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.response.ResponseResult;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import com.xhn.wechat.binding.model.BaseWeChatUserBinding;
import com.xhn.wechat.binding.service.WeChatUserBindingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    private final SysUserService sysUserService;
    private final WeChatUserBindingService weChatUserBindingService;
    private final JwtUtil jwtUtil;
    private final WebClient webClient;

    @Value("${wechat.miniapp.app-id:}")
    private String miniAppId;

    @Value("${wechat.miniapp.secret:}")
    private String miniAppSecret;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数（会自动验证）
     * @return 返回 token
     */
    @PostMapping("/login")
    public ResponseResult<AuthLoginPayload> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, loginRequest.getUsername())
                .one();
        if (user == null) {
            throw new ApplicationException("用户不存在");
        }
        return ResponseResult.success(new AuthLoginPayload(response.getToken(), toProfile(user)));

    }

    @PostMapping("/wx-login")
    public Mono<ResponseResult<AuthLoginPayload>> wxLogin(@RequestBody WxLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            return Mono.just(ResponseResult.error(400, "code不能为空"));
        }

        return resolveMiniProgramOpenId(request.getCode())
                .onErrorResume(e -> Mono.just(request.getCode()))
                .publishOn(Schedulers.boundedElastic())
                .map(openId -> {
                    BaseWeChatUserBinding binding = findWxBinding(openId);
                    if (binding == null || binding.getUserId() == null) {
                        return ResponseResult.<AuthLoginPayload>error(401, "微信账号未绑定系统用户");
                    }
                    SysUser user = sysUserService.getById(binding.getUserId());
                    if (user == null) {
                        return ResponseResult.<AuthLoginPayload>error(401, "绑定用户不存在");
                    }
                    String token = jwtUtil.generateToken(user.getUserId());
                    return ResponseResult.success(new AuthLoginPayload(token, toProfile(user)));
                });
    }

    @GetMapping("/profile")
    public Mono<ResponseResult<AuthUserProfile>> profile() {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    SysUser user = sysUserService.getById(userId);
                    if (user == null) {
                        return ResponseResult.<AuthUserProfile>error(404, "用户不存在");
                    }
                    return ResponseResult.success(toProfile(user));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error(401, "用户未登录")));
    }

    //根据刷新token获取新的访问token
    @PostMapping("/refresh")
    public ResponseResult<LoginResponse> refreshToken(@RequestBody String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseResult.success(response);
    }

    private Mono<String> resolveMiniProgramOpenId(String code) {
        if (!StringUtils.hasText(miniAppId) || !StringUtils.hasText(miniAppSecret)) {
            return Mono.just(code);
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.weixin.qq.com")
                        .path("/sns/jscode2session")
                        .queryParam("appid", miniAppId)
                        .queryParam("secret", miniAppSecret)
                        .queryParam("js_code", code)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    if (json.hasNonNull("openid")) {
                        return json.get("openid").asText();
                    }
                    String message = json.hasNonNull("errmsg") ? json.get("errmsg").asText() : "微信登录失败";
                    throw new ApplicationException(message);
                });
    }

    private BaseWeChatUserBinding findWxBinding(String openIdOrCode) {
        LambdaQueryWrapper<BaseWeChatUserBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatUserBinding::getWxOpenid, openIdOrCode)
                .or()
                .eq(BaseWeChatUserBinding::getWxUserId, openIdOrCode)
                .last("LIMIT 1");
        return weChatUserBindingService.getOne(wrapper, false);
    }

    private AuthUserProfile toProfile(SysUser user) {
        String nickname = StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getUsername();
        String avatar = user.getAvatarUrl() == null ? "" : user.getAvatarUrl();
        return new AuthUserProfile(user.getUserId(), user.getUsername(), nickname, avatar);
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
