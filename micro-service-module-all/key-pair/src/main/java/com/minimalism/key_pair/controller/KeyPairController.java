package com.minimalism.key_pair.controller;

import cn.hutool.core.util.StrUtil;
import com.minimalism.aop.env.InterfaceEnv;
import com.minimalism.aop.log.SysLog;
import com.minimalism.common.service.KeyPairService;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.key_pair.vo.KeyVo;
import com.minimalism.result.Result;
import com.minimalism.utils.key.KeyUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.HttpResource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;


/**
 * @Author yan
 * @Date 2025/3/6 14:59:55
 * @Description
 */
@Tag(name = "密钥对接口")
@RequestMapping({"/key-pair", "/api/key-pair", "/jwt/key-pair"})
@RestController
public class KeyPairController implements AbstractBaseController {
    @Resource
    private KeyPairService keyPairService;

    @SneakyThrows
    @Operation(summary = "获取公钥")
    @SysLog
    @GetMapping("/public-key")
    public Result<KeyVo> getPublicKey() {
        KeyUtils.KeyInfo keyInfo = keyPairService.generalKey();
        String algorithm = keyInfo.getAlgorithm();
        String publicKeyBase64 = keyInfo.getPublicKeyBase64();
        String identity = keyInfo.getIdentity();
        return ok(KeyVo.builder()
                .publicKey(publicKeyBase64)
                .identity(identity)
                .algorithm(algorithm).build());
    }

    @SneakyThrows
    @Operation(summary = "双端加密获取公钥")
    @SysLog
    @GetMapping("/encrypt/public-key")
    public Result<KeyVo> getPublicKey(
            @RequestParam(required = false) String clientPublicKeyBase64,
            @RequestParam(required = false) String clientAlgorithm,
            HttpServletRequest request, HttpServletResponse resource
    ) {

        if (StrUtil.isBlank(clientPublicKeyBase64)) {
            clientPublicKeyBase64 = request.getHeader("X-Client-PublicKeyBase64");
        }
        if (StrUtil.isBlank(clientAlgorithm)) {
            clientAlgorithm = request.getHeader("X-Client-Algorithm");
        }

        if (StrUtil.isBlank(clientPublicKeyBase64) || StrUtil.isBlank(clientAlgorithm)) {
            throw new GlobalCustomException("缺少参数");
        }

        KeyUtils.KeyInfo keyInfo = keyPairService.generalKey();
        String algorithm = keyInfo.getAlgorithm();
        String publicKeyBase64 = keyInfo.getPublicKeyBase64();
        String identity = keyInfo.getIdentity();
        RSAPublicKey clientPublicKey = KeyUtils.getPublicKeyFromBase64(clientPublicKeyBase64, clientAlgorithm);
        String encryptPublicKeyBase64 = KeyUtils.encrypt(clientPublicKey, publicKeyBase64);
        String encryptSecretKey = KeyUtils.encrypt(clientPublicKey, keyInfo.getSecretKey());

        resource.setHeader("X-Service-KeyPair-EncryptPublicKeyBase64",encryptPublicKeyBase64);
        resource.setHeader("X-Service-KeyPair-Algorithm",algorithm);
        resource.setHeader("X-Service-KeyPair-EncryptSecretKey",encryptSecretKey);
        resource.setHeader("X-Service-KeyPair-identity",identity);

        return ok(KeyVo.builder()
                //.publicKey(publicKeyBase64)
                .encryptPublicKey(encryptPublicKeyBase64)
                .encryptSecretKey(encryptSecretKey)
                .identity(identity)
                .algorithm(algorithm).build());
    }



    @SneakyThrows
    @Operation(summary = "获取基础信息")
    @InterfaceEnv(values = {"dev", "test"})
    @SysLog
    @GetMapping("/info")
    public Result<KeyUtils.KeyInfo> getInfo(@RequestParam String identity) {
        return ok(keyPairService.getInfo(identity));
    }

}
