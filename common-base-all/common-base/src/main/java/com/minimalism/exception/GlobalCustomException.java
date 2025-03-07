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
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GlobalCustomException extends RuntimeException {
    private Integer code = ApiCode.FAIL.getCode();

    public GlobalCustomException(ApiCode apiCode) {
        super(apiCode.getMessage());
        this.code = apiCode.getCode();
    }

    public GlobalCustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalCustomException(ApiCode apiCode, String message) {
        super(message);
        this.code = apiCode.getCode();
    }

    public GlobalCustomException(String message) {
        super(message);
        this.code = ApiCode.FAIL.getCode();
    }
}
