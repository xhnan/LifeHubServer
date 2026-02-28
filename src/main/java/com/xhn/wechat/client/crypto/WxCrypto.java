package com.xhn.wechat.client.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * 企业微信消息加解密工具
 * 根据测试成功的demo重写
 * @author xhn
 * @date 2026-02-28
 */
@Slf4j
public class WxCrypto {

    private final byte[] aesKey;
    private final String token;

    /**
     * 构造函数
     * @param token           回调验证Token
     * @param encodingAesKey  消息加密密钥（43位字符）
     */
    public WxCrypto(String token, String encodingAesKey) {
        this.token = token;
        // 处理 EncodingAESKey：自动补齐 "="
        String aesKeyStr = encodingAesKey.endsWith("=") ? encodingAesKey : encodingAesKey + "=";
        this.aesKey = Base64.getDecoder().decode(aesKeyStr);
    }

    /**
     * 获取Token
     * @return Token
     */
    public String getToken() {
        return token;
    }

    /**
     * 对明文进行加密
     * @param text  需要加密的明文
     * @param appId 企业微信应用的CorpId
     * @return 加密后的字符串
     */
    public String encrypt(String text, String appId) {
        try {
            // 16位随机字符串
            String randomStr = getRandomStr();

            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] randomBytes = randomStr.getBytes(StandardCharsets.UTF_8);
            byte[] appIdBytes = appId.getBytes(StandardCharsets.UTF_8);

            // 计算总长度并填充
            int totalLength = randomBytes.length + 4 + textBytes.length + appIdBytes.length;
            int padLength = 32 - (totalLength % 32);

            ByteBuffer buffer = ByteBuffer.allocate(totalLength + padLength);
            buffer.put(randomBytes);
            buffer.putInt(textBytes.length);
            buffer.put(textBytes);
            buffer.put(appIdBytes);

            // PKCS7 填充
            for (int i = 0; i < padLength; i++) {
                buffer.put((byte) padLength);
            }

            byte[] unencrypted = buffer.array();

            // AES/CBC/NoPadding 加密
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte[] encrypted = cipher.doFinal(unencrypted);

            // Base64 编码
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Encrypt error", e);
            throw new RuntimeException("Encrypt error", e);
        }
    }

    /**
     * 对密文进行解密
     * @param cipherText 需要解密的密文
     * @return 解密后的明文
     */
    public String decrypt(String cipherText) {
        try {
            // 1. Base64 解码
            byte[] encrypted = Base64.getDecoder().decode(cipherText);

            // 2. AES/CBC/NoPadding 解密
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] original = cipher.doFinal(encrypted);

            // 3. 手动去除 PKCS#7 补位
            int pad = original[original.length - 1];
            if (pad < 1 || pad > 32) {
                pad = 0;
            }
            byte[] validBytes = Arrays.copyOfRange(original, 0, original.length - pad);

            // 4. 从第16字节开始读取4字节网络字节序的 msgLength
            int msgLength = ByteBuffer.wrap(validBytes, 16, 4).getInt();

            // 5. 安全边界校验
            if (msgLength < 0 || msgLength > validBytes.length - 20) {
                throw new IllegalArgumentException("解密失败：解析出的消息长度异常(msgLength=" + msgLength + ")！" +
                        "原因：EncodingAESKey 错误不匹配或密文被篡改。");
            }

            // 6. 截取 msgContent（从第20字节开始，长度为 msgLength）
            String msgContent = new String(validBytes, 20, msgLength, StandardCharsets.UTF_8);

            // 7. 截取尾部的 fromAppId
            String fromAppId = new String(validBytes, 20 + msgLength,
                                         validBytes.length - (20 + msgLength), StandardCharsets.UTF_8);

            log.debug("Decrypted message, fromAppId: {}", fromAppId);

            return msgContent;
        } catch (Exception e) {
            log.error("Decrypt error", e);
            throw new RuntimeException("Decrypt error: " + e.getMessage(), e);
        }
    }

    /**
     * 生成16位随机字符串
     * @return 随机字符串
     */
    private String getRandomStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString((int) (Math.random() * 16)));
        }
        return sb.toString();
    }
}
