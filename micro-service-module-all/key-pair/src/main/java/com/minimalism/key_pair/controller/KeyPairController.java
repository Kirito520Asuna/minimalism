package com.minimalism.key_pair.controller;

import com.minimalism.aop.env.InterfaceEnv;
import com.minimalism.aop.log.SysLog;
import com.minimalism.common.service.KeyPairService;
import com.minimalism.controller.AbstractBaseController;
import com.minimalism.key_pair.vo.KeyVo;
import com.minimalism.result.Result;
import com.minimalism.utils.key.KeyUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


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
    @Operation(summary = "获取基础信息")
    @InterfaceEnv
    @SysLog
    @GetMapping("/info")
    public Result<KeyUtils.KeyInfo> getInfo(@RequestParam String identity) {
        return ok(keyPairService.getInfo(identity));
    }

}
