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
@AllArgsConstructor
@Getter
public class GlobalConfigException extends RuntimeException {
    private Integer code;

    public GlobalConfigException() {
        super(ApiCode.SERVICE_CONFIG.getMessage());
        this.code = ApiCode.SERVICE_CONFIG.getCode();
    }

    public GlobalConfigException(String message) {
        super(message);
        this.code = ApiCode.SERVICE_CONFIG.getCode();
    }
}
