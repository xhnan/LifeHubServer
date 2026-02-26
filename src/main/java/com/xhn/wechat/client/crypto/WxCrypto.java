package com.xhn.wechat.client.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 企业微信消息加解密工具
 * @author xhn
 * @date 2026-02-26
 */
@Slf4j
public class WxCrypto {

    private static final Integer AES_KEY_LENGTH = 32;
    private static final String AES = "AES";
    private static final String AES_CBC_PKCS7_PADDING = "AES/CBC/PKCS5Padding";
    private final byte[] aesKey;
    private final String token;

    /**
     * 构造函数
     * @param token           回调验证Token
     * @param encodingAesKey  消息加密密钥
     */
    public WxCrypto(String token, String encodingAesKey) {
        this.token = token;
        // 验证key并生成 aesKey
        if (encodingAesKey.length() != AES_KEY_LENGTH * 2) {
            throw new IllegalArgumentException("encodingAesKey length must be 64");
        }
        this.aesKey = Base64.decodeBase64(encodingAesKey + "=");
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
     * @param appId 企业微信应用的AppId
     * @return 加密后的字符串
     */
    public String encrypt(String text, String appId) {
        try {
            // 16位随机字符串
            String randomStr = getRandomStr();

            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] randomBytes = randomStr.getBytes(StandardCharsets.UTF_8);
            byte[] appIdBytes = appId.getBytes(StandardCharsets.UTF_8);

            // 使用PKCS7Padding填充
            ByteGroup byteGroup = new ByteGroup();
            byteGroup.addBytes(randomBytes);
            byteGroup.addBytes(textBytes);
            byteGroup.addBytes(appIdBytes);

            byte[] padBytes = PKCS7Encode(byteGroup.size());
            byteGroup.addBytes(padBytes);

            byte[] unencrypted = byteGroup.toBytes();

            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS7_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, AES);
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            String result = Base64.encodeBase64String(encrypted);

            return result;
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
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(cipherText);

            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS7_PADDING);
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, AES);
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            // 解密
            byte[] original = cipher.doFinal(encrypted);

            // 去除补位字符
            byte[] bytes = PKCS7Decode(original);

            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(original, 16, 20);

            int msgLength = bytesToInt(networkOrder);

            String msgContent = new String(Arrays.copyOfRange(original, 20, 20 + msgLength), StandardCharsets.UTF_8);
            String fromAppId = new String(Arrays.copyOfRange(original, 20 + msgLength, original.length), StandardCharsets.UTF_8);

            return msgContent;
        } catch (Exception e) {
            log.error("Decrypt error", e);
            throw new RuntimeException("Decrypt error", e);
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

    /**
     * 将字节数组转换为整数
     */
    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
    }

    /**
     * PKCS7补位
     */
    private static byte[] PKCS7Encode(int count) {
        // 计算需要填充的位数
        int amountToPad = 32 - (count % 32);
        if (amountToPad == 0) {
            amountToPad = 32;
        }
        // 获得补位所用的字符
        char padChr = (char) (amountToPad & 0xFF);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < amountToPad; i++) {
            sb.append(padChr);
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 删除解密后明文的补位字符
     */
    private static byte[] PKCS7Decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    /**
     * 字节分组辅助类
     */
    private static class ByteGroup {
        final StringBuilder sb = new StringBuilder();

        void addBytes(byte[] bytes) {
            sb.append(new String(bytes, StandardCharsets.ISO_8859_1));
        }

        byte[] toBytes() {
            return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        }

        int size() {
            return sb.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        }
    }
}
