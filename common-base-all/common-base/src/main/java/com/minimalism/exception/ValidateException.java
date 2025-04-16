package com.minimalism.exception;

import cn.hutool.core.util.StrUtil;
import com.minimalism.enums.ApiCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ValidateException extends GlobalCustomException {
    public ValidateException() {
        super(ApiCode.VALIDATE_FAILED.getCode(),ApiCode.VALIDATE_FAILED.getMessage());
    }

    public ValidateException(String message) {
        super(ApiCode.VALIDATE_FAILED.getCode(),message);
    }
}
