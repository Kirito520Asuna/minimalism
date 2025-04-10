package com.minimalism.utils.crypto;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * 对称加密工具类（基于 HuTool）
 */
public class CryptoUtils {

    // 默认算法 AES（可改为 DES、3DES、SM4 等）
    private static final String ALGORITHM = SymmetricAlgorithm.AES.getValue();

    // 默认密钥（从配置读取或生成）
    // 示例：在配置文件中读取 hex 密钥并赋值，或使用 generateHexKey() 生成一次性写死
    private static final String DEFAULT_HEX_KEY = "3fbe87e4ad3b66c68bcda51e8cc6f56a";

    // 加密器
    private static SymmetricCrypto CRYPTO;

    static {
        CRYPTO = initSymmetricCrypto(DEFAULT_HEX_KEY);
    }

    public static SymmetricCrypto initSymmetricCrypto(String hexKey) {
        return initSymmetricCrypto(ALGORITHM, hexKey);
    }

    public static SymmetricCrypto initSymmetricCrypto(String algorithm, String hexKey) {
        return new SymmetricCrypto(algorithm, HexUtil.decodeHex(hexKey));
    }

    /**
     * 加密为十六进制字符串
     */
    public static String encrypt(SymmetricCrypto crypto, String content) {
        return crypto.encryptHex(content);
    }

    /**
     * 解密十六进制字符串
     */
    public static String decrypt(SymmetricCrypto crypto, String encryptedHex) {
        return crypto.decryptStr(encryptedHex);
    }

    /**
     * 加密为十六进制字符串
     */
    public static String encrypt(String content) {
        return encrypt(CRYPTO, content);
    }

    /**
     * 解密十六进制字符串
     */
    public static String decrypt(String encryptedHex) {
        return decrypt(CRYPTO, encryptedHex);
    }

    /**
     * 生成一个新的随机密钥（十六进制字符串）
     */
    public static String generateHexKey() {
        return generateHexKey(ALGORITHM);
    }

    /**
     * 生成一个新的随机密钥（十六进制字符串）
     */
    public static String generateHexKey(String algorithm) {
        byte[] key = SecureUtil.generateKey(algorithm).getEncoded();
        return HexUtil.encodeHexStr(key);
    }
}