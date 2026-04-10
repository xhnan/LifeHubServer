package com.xhn.auth.service;

import com.xhn.auth.model.QrCodeLoginInfo;
import reactor.core.publisher.Mono;

/**
 * 二维码登录服务接口
 *
 * @author xhn
 */
public interface QrCodeService {

    /**
     * 生成二维码
     *
     * @return qrCodeId
     */
    Mono<String> generateQrCode();

    /**
     * 查询二维码状态
     *
     * @param qrCodeId 二维码ID
     * @return 二维码登录信息
     */
    Mono<QrCodeLoginInfo> getQrCodeStatus(String qrCodeId);

    /**
     * 扫码
     *
     * @param qrCodeId 二维码ID
     * @param userId   扫码用户ID
     */
    Mono<Void> scanQrCode(String qrCodeId, Long userId);

    /**
     * 确认登录（生成JWT token）
     *
     * @param qrCodeId 二维码ID
     * @param userId   确认用户ID
     * @return 包含token的登录信息
     */
    Mono<QrCodeLoginInfo> confirmQrCode(String qrCodeId, Long userId);

    /**
     * 取消登录
     *
     * @param qrCodeId 二维码ID
     * @param userId   取消用户ID
     */
    Mono<Void> cancelQrCode(String qrCodeId, Long userId);
}
