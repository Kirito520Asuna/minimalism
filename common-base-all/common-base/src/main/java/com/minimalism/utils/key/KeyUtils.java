package com.minimalism.utils.key;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
        private RSAPublicKey publicKey;
        @JsonIgnore
        private RSAPrivateKey privateKey;
        private String publicKeyBase64;
        private String privateKeyBase64;

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
        public RSAPrivateKey base64BuildPrivateKey() throws Exception {
            return KeyUtils.getPrivateKeyFromBase64(this.privateKeyBase64, this.algorithm);
        }

        /**
         * base64 构建 公钥
         *
         * @return
         * @throws Exception
         */
        public RSAPublicKey base64BuildPublicKey() throws Exception {
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
    public static RSAPublicKey getPublicKeyFromBase64(String base64PublicKey, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过 Base64 字符串构建 RSAPrivateKey
     */
    public static RSAPrivateKey getPrivateKeyFromBase64(String base64PrivateKey, String algorithm) throws Exception {
        algorithm = ObjectUtils.defaultIfEmpty(algorithm, "RSA");
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
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

    private static KeyInfo generalKeyInfo(KeyPair keyPair, String algorithm) {
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

    public static KeyInfo generalKeyInfo() throws NoSuchAlgorithmException {
        // 生成  密钥对
        KeyPairGenerator keyPairGenerator = generatorKeyPair();
        String algorithm = keyPairGenerator.getAlgorithm();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        KeyInfo keyInfo = generalKeyInfo(keyPair, algorithm);
        return keyInfo;
    }


    @SneakyThrows
    public static void main(String[] args) {
        KeyInfo keyInfo = generalKeyInfo();
        System.err.println(JSONUtil.toJsonStr(keyInfo, JSONConfig.create().setIgnoreNullValue(false)));
        byte[] bytes = "sdadawojfjid 之间".getBytes(StandardCharsets.UTF_8);
        byte[] encrypt = encrypt(keyInfo.publicKey, bytes, keyInfo.algorithm);
        System.err.println("加密后的数据 (Base64)：" + Base64.getEncoder().encodeToString(encrypt));
        // 解密数据
        byte[] decryptedData = decrypt(keyInfo.privateKey, encrypt, keyInfo.algorithm);
        System.err.println("解密后的数据：" + new String(decryptedData, StandardCharsets.UTF_8));
        //test01();
        // 生成 RSA 密钥对
        //test02();
    }

    private static void test02() throws Exception {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 将公钥/私钥转换为 Base64 字符串
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("公钥 (Base64)：" + publicKeyBase64);
        System.out.println("私钥 (Base64)：" + privateKeyBase64);

        // 通过 Base64 解析回 RSAPublicKey / RSAPrivateKey
        RSAPublicKey rsaPublicKey = getPublicKeyFromBase64(publicKeyBase64, algorithm);
        RSAPrivateKey rsaPrivateKey = getPrivateKeyFromBase64(privateKeyBase64, algorithm);

        // 加密数据
        String originalText = "你好，springdoc.cn";

        System.out.println("原文：" + originalText);
        byte[] encryptedData = encrypt(rsaPublicKey, originalText.getBytes(StandardCharsets.UTF_8), algorithm);

        System.out.println("加密后的数据 (Base64)：" + Base64.getEncoder().encodeToString(encryptedData));

        // 解密数据
        byte[] decryptedData = decrypt(rsaPrivateKey, encryptedData, algorithm);
        System.out.println("解密后的数据：" + new String(decryptedData, StandardCharsets.UTF_8));
    }


    private static void test01() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        // 生成 RSA 密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String puKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKOWy2/7kOzE5DzTbfN1nqoHrUwqfJDIiCHe33CfO85CyOjkr0FBE6s77AUUCHl7P0Hpllc3nlpB9CozjwLD+Y8CAwEAAQ==";
        String prKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAo5bLb/uQ7MTkPNNt83WeqgetTCp8kMiIId7fcJ87zkLI6OSvQUETqzvsBRQIeXs/QemWVzeeWkH0KjOPAsP5jwIDAQABAkBExVzqy8gGMVN92qhrY/P6qNWKooXRW+wWyRoHv3kl4TIilGuAZhlksIh7Xnq3WVLuSpSSlTQlJeYWofafcSnRAiEA6Dkt21waWz2xNTgRHQ9NlbRNSlckCPU12XwmQXO++BcCIQC0VqMjM+wLVPJtnnpnHm5GekdaGpQ2mu4sRhNZfrR9SQIhAKyT0cBzciLcdhVW1WEDPmVC2S2mFOGTWbGG0edSXVmBAiA9QMAgkN436x58xTtmExv5rEbX//cfpPgI6bRgzXyYoQIgRlZ9fPERaLwjUTEjryK7nrgOBZgJogsKEEgNfVat3/A=";


        // 公钥和私钥
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        System.out.println("algorithmPu: " + rsaPublicKey.getAlgorithm());
        System.out.println("algorithmPr: " + rsaPrivateKey.getAlgorithm());
        System.out.println("公钥：" + Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
        System.out.println("私钥：" + Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));

        // 要加密的原文
        byte[] content = "你好 springdoc.cn".getBytes(StandardCharsets.UTF_8);

        System.out.println("原文：" + new String(content, StandardCharsets.UTF_8));

        // 加密后的密文
        ByteArrayOutputStream encryptedout = new ByteArrayOutputStream();
        // 公钥加密
        encode(rsaPublicKey, new ByteArrayInputStream(content), encryptedout);

        System.out.println("加密后的密文：" + Base64.getEncoder().encodeToString(encryptedout.toByteArray()));

        // 解密后的原文
        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();
        // 私钥解密
        decode(rsaPrivateKey, new ByteArrayInputStream(encryptedout.toByteArray()), decryptedOut);

        System.out.println("解密后的原文：" + new String(decryptedOut.toByteArray(), StandardCharsets.UTF_8));
    }

}
