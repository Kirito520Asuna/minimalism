package com.minimalism.exception.handler.security;

import com.minimalism.enums.ApiCode;
import com.minimalism.exception.handler.CommonExceptionHandler;
import com.minimalism.result.Result;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yan
 * @Date 2023/9/1 0001 13:12
 * @Description 通用异常拦截响应处理
 */
@Primary
@RestControllerAdvice
public class SecurityExceptionHandler extends CommonExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, Exception e) {
        Result result = super.exceptionHandler(req, e);
        if (e instanceof AccessDeniedException){
            result = Result.result(ApiCode.FORBIDDEN);
        }
        return result;
    }

}
