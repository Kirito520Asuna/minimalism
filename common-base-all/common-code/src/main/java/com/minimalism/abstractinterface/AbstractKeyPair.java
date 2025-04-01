package com.minimalism.abstractinterface;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.utils.key.KeyUtils;

import java.security.NoSuchAlgorithmException;

/**
 * 密钥对 抽象接口
 */
public interface AbstractKeyPair {
    /**
     * 移除存储key
     *
     * @param identity
     * @return
     */
    boolean delKey(String identity);

    /**
     * 存储key
     *
     * @param keyInfo
     * @return
     */
    boolean saveKey(KeyUtils.KeyInfo keyInfo);

    /**
     * 获取存储key
     *
     * @param identity
     * @return
     */
    KeyUtils.KeyInfo getKeyByIdentity(String identity);

    /**
     * 生成密钥对
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    default KeyUtils.KeyInfo generalKey() throws NoSuchAlgorithmException {
        KeyUtils.KeyInfo keyInfo = KeyUtils.generalKeyInfo();
        SpringUtil.getBean(this.getClass()).saveKey(keyInfo);
        return keyInfo;
    }


    /**
     * RSA 加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    default String encrypt(String identity, String content) throws Exception {
        KeyUtils.KeyInfo keyInfo = getKeyByIdentity(identity);
        return keyInfo.encrypt(content);
    }


    /**
     * RSA 唯一解密(架构层面==>默认不支持,建议实现该方法使用分布式锁实现唯一性)
     *
     * @param content
     * @return
     * @throws Exception
     */
    default String soleDecrypt(String identity, String content) throws Exception {
        KeyUtils.KeyInfo keyInfo = getKeyByIdentity(identity);
        return keyInfo.decrypt(content);
    }

    /**
     * RSA 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    default String decrypt(String identity, String content) throws Exception {
        try {
            //保证aop能正常执行
            return SpringUtil.getBean(this.getClass()).soleDecrypt(identity,content);
        } finally {
            //解密完成移除密钥对
            delKey(identity);
        }
    }
}
