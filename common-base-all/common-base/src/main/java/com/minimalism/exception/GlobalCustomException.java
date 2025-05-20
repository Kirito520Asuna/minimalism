package com.minimalism.exception;


import com.minimalism.enums.ApiCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author yan
 * @Date 2023/10/23 0023 15:06
 * @Description
 */
//@NoArgsConstructor
//@AllArgsConstructor
@Getter
public class GlobalCustomException extends GlobalException {
    //private Integer code = ApiCode.FAIL.getCode();

    public GlobalCustomException(ApiCode apiCode) {
        super(apiCode.getCode(),apiCode.getMessage());
    }

    public GlobalCustomException(int code, String message) {
        super(code,message);
    }

    public GlobalCustomException(ApiCode apiCode, String message) {
        super(apiCode.getCode(),message);
    }

    public GlobalCustomException(String message) {
        super(ApiCode.FAIL.getCode(),message);
    }

    public GlobalCustomException(int code) {
        super(code);
    }
}
