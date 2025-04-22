package com.minimalism.utils.key;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minimalism.utils.crypto.CryptoUtils;
import com.minimalism.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyUtils {

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Slf4j
    public static class KeyInfo implements Serializable {
        private String algorithm;
        @JsonIgnore
        private PublicKey publicKey;
        @JsonIgnore
        private PrivateKey privateKey;
        private String publicKeyBase64;
        private String privateKeyBase64;
        //密钥标识
        private String secretKey = CryptoUtils.generateHexKey();
        private String identity = new StringBuffer()
                .append(System.currentTimeMillis())
                .append("<#>")
                .append(UUID.randomUUID())
                .toString();

        /**
         * RSA 加密
         *
         * @param content
         * @return
         * @throws Exception
         */
        public String encrypt(String content) throws Exception {
            Charset charset = StandardCharsets.UTF_8;
            return new String(encrypt(content.getBytes(charset)), charset);
        }

        /**
         * RSA 加密
         */
        public byte[] encrypt(byte[] data) throws Exception {
            return KeyUtils.encrypt(this.publicKey, data, this.algorithm);
        }

        /**
         * RSA 解密
         *
         * @param content
         * @return
         * @throws Exception
         */
        public String decrypt(String content) throws Exception {
            Charset charset = StandardCharsets.UTF_8;
            return new String(decrypt(content.getBytes(charset)), charset);
        }

        /**
         * RSA 解密
         */
        public byte[] decrypt(byte[] data) throws Exception {
            return KeyUtils.decrypt(this.privateKey, data, this.algorithm);
        }

        /**
         * base64 构建 私钥
         *
         * @return
         * @throws Exception
         */
        public PrivateKey base64BuildPrivateKey() throws Exception {
            return KeyUtils.getPrivateKeyFromBase64(this.privateKeyBase64, this.algorithm);
        }

        /**
         * base64 构建 公钥
         *
         * @return
         * @throws Exception
         */
        public PublicKey base64BuildPublicKey() throws Exception {
            return KeyUtils.getPublicKeyFromBase64(this.publicKeyBase64, this.algorithm);
        }
    }

    /**
     * 加密
     *
     * @param key KEY
     * @param in  输入参数
     * @param out 输出加密后的密文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @Deprecated
    public static void encode(Key key, InputStream in, OutputStream out) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        // 最大的加密明文长度
        final int maxEncryptBlock = 245;
        String algorithm = ObjectUtils.defaultIfEmpty(key.getAlgorithm(), "RSA");
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] buffer = new byte[maxEncryptBlock];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            out.write(cipher.doFinal(buffer, 0, len));
        }
    }

    /**
     * 解密
     *
     * @param key KEY
     * @param in  输入参数
     * @param out 输出解密后的原文
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @Deprecated
    public static void decode(Key key, InputStream in, OutputStream out) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

        // 最大的加密明文长度
        final int maxDecryptBlock = 256;
        String algorithm = ObjectUtils.defaultIfEmpty(key.getAlgorithm(), "RSA");
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] buffer = new byte[maxDecryptBlock];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            out.write(cipher.doFinal(buffer, 0, len));
        }
    }

    /**
     * 通过 Base64 字符串构建 RSAPublicKey
     */
    public static PublicKey getPublicKeyFromBase64(String base64PublicKey, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (PublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过 Base64 字符串构建 RSAPrivateKey
     */
    public static PrivateKey getPrivateKeyFromBase64(String base64PrivateKey, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (PrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * RSA 加密
     */
    public static String encrypt(Key key, String data) throws Exception {
        String algorithm = key.getAlgorithm();
        byte[] bytes = encrypt(key, data.getBytes(StandardCharsets.UTF_8), algorithm);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * RSA 加密
     */
    public static byte[] encrypt(Key key, byte[] data, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * RSA 解密
     */
    public static String decrypt(Key key, String data) throws Exception {
        String algorithm = key.getAlgorithm();
        byte[] bytes = decrypt(key, data.getBytes(StandardCharsets.UTF_8), algorithm);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * RSA 解密
     */
    public static byte[] decrypt(Key key, byte[] data, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static KeyPairGenerator generatorKeyPair() throws NoSuchAlgorithmException {
        return generatorKeyPair(null);
    }

    public static KeyPairGenerator generatorKeyPair(String algorithm) throws NoSuchAlgorithmException {
        return generatorKeyPair(algorithm, null);
    }

    public static KeyPairGenerator generatorKeyPair(String algorithm, Integer keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ObjectUtils.defaultIfEmpty(algorithm, "RSA"));
        keyPairGenerator.initialize(ObjectUtils.defaultIfEmpty(keySize, 512));
        return keyPairGenerator;
    }

    public static KeyInfo generalKeyInfo(KeyPair keyPair, String algorithm) {
        // 公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        KeyInfo keyInfo = new KeyInfo()
                .setAlgorithm(algorithm)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .setPublicKeyBase64(publicKeyBase64)
                .setPrivateKeyBase64(privateKeyBase64);
        return keyInfo;
    }

    public static KeyInfo generalKeyInfo(String algorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = generatorKeyPair(algorithm, null);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        KeyInfo keyInfo = generalKeyInfo(keyPair, algorithm);
        return keyInfo;
    }

    public static KeyInfo generalKeyInfo() throws NoSuchAlgorithmException {
        // 生成  密钥对
        KeyPairGenerator keyPairGenerator = generatorKeyPair();
        String algorithm = keyPairGenerator.getAlgorithm();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        KeyInfo keyInfo = generalKeyInfo(keyPair, algorithm);
        return keyInfo;
    }


    /**
     * @param serverPrivateKey
     * @param clientPublicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String generateSharedSecretBase64(PrivateKey serverPrivateKey, PublicKey clientPublicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return Base64.getEncoder().encodeToString(generateSharedSecret(serverPrivateKey, clientPublicKey));
    }

    /**
     * 生成密钥
     *
     * @param serverPrivateKey
     * @param clientPublicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] generateSharedSecret(PrivateKey serverPrivateKey, PublicKey clientPublicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        // 接收到 Key 后，计算共享密钥
        String ecdh = "ECDH";
        KeyAgreement keyAgreement = KeyAgreement.getInstance(ecdh);
        keyAgreement.init(serverPrivateKey);
        keyAgreement.doPhase(clientPublicKey, true);
        byte[] generateSecret = keyAgreement.generateSecret();  // 对称密钥，通常再做 key derivation
        return generateSecret;
    }

    /**
     * 客户端公钥加密服务端公钥
     *
     * @param clientPublicKey
     * @param serverPublicKey
     * @return
     * @throws Exception
     */
    public static byte[] clientEncryptServerPublicKey(PublicKey clientPublicKey, PublicKey serverPublicKey) throws Exception {
        String clientAlgorithm = clientPublicKey.getAlgorithm();
        return clientEncryptServerPublicKey(clientPublicKey, serverPublicKey, clientAlgorithm);
    }

    /**
     * 客户端公钥加密服务端公钥
     *
     * @param clientPublicKey
     * @param serverPublicKey
     * @param clientAlgorithm
     * @return
     * @throws Exception
     */
    public static byte[] clientEncryptServerPublicKey(PublicKey clientPublicKey, PublicKey serverPublicKey, String clientAlgorithm) throws Exception {
        // 使用传入的 clientAlgorithm 获取 Cipher 实例
        Cipher cipher = Cipher.getInstance(clientAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
        // 获取服务端公钥的编码字节
        byte[] serverPublicKeyBytes = serverPublicKey.getEncoded();
        // 返回加密后的数据
        return cipher.doFinal(serverPublicKeyBytes);
    }

    public static PublicKey clientDecryptServerPublicKey(PrivateKey clientPrivateKey, byte[] serverPublicKeyBytes, String serverAlgorithm) throws Exception {
        return clientDecryptServerPublicKey(clientPrivateKey, serverPublicKeyBytes, serverAlgorithm, clientPrivateKey.getAlgorithm());
    }

    public static PublicKey clientDecryptServerPublicKey(PrivateKey clientPrivateKey, byte[] serverPublicKeyBytes, String serverAlgorithm, String clientAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(clientAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, clientPrivateKey);
        serverPublicKeyBytes = cipher.doFinal(serverPublicKeyBytes);
        // 通过 KeyFactory 构造 PublicKey 对象（这里使用 X509 格式）
        KeyFactory keyFactory = KeyFactory.getInstance(serverAlgorithm);
        return keyFactory.generatePublic(new X509EncodedKeySpec(serverPublicKeyBytes));
    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static byte[] aesEncrypt(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // IV 为 0 简化演示
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        return cipher.doFinal(data);
    }

    public static byte[] aesDecrypt(byte[] encrypted, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        return cipher.doFinal(encrypted);
    }

}
