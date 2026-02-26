package com.xhn.wechat.client.crypto;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 企业微信签名验证工具
 * @author xhn
 * @date 2026-02-26
 */
public class WxSignature {

    /**
     * 验证签名
     * @param signature 签名
     * @param token     Token
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 是否验证通过
     */
    public static boolean verify(String signature, String token, String timestamp, String nonce) {
        try {
            String[] arr = new String[]{token, timestamp, nonce};
            Arrays.sort(arr);

            StringBuilder sb = new StringBuilder();
            for (String s : arr) {
                sb.append(s);
            }

            String sha1 = DigestUtils.sha1Hex(sb.toString());
            return sha1.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成签名
     * @param token     Token
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 签名
     */
    public static String generate(String token, String timestamp, String nonce) {
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);

        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s);
        }

        return DigestUtils.sha1Hex(sb.toString());
    }
}
