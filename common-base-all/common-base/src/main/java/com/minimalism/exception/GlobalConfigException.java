package com.minimalism.exception;

import com.minimalism.enums.ApiCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author yan
 * @Date 2025/3/7 19:43:00
 * @Description
 */
//@AllArgsConstructor
@Getter
public class GlobalConfigException extends GlobalException {
     Integer code;

    public GlobalConfigException() {
        super(ApiCode.SERVICE_CONFIG.getCode(),ApiCode.SERVICE_CONFIG.getMessage());
    }

    public GlobalConfigException(String message) {
        super(ApiCode.SERVICE_CONFIG.getCode(),message);
    }
}
