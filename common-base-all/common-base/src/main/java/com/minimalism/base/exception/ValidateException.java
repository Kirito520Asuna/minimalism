package com.minimalism.base.exception;

import com.minimalism.base.enums.ApiCode;
import lombok.Getter;
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
