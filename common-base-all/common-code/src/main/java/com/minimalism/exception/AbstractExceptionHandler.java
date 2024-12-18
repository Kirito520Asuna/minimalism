package com.minimalism.exception;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author yan
 * @Date 2024/9/27 上午1:37:08
 * @Description
 */
public interface AbstractExceptionHandler {

    default String getActive(){
        String active = "dev";
        Environment environment = SpringUtil.getBean(Environment.class);
        String property = environment.getProperty("spring.profiles.active");
        if (StrUtil.isNotBlank(property)){
            active = property;
        }
        return active;
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    default void handleIOException(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    default Result exceptionHandler(HttpServletRequest req, Exception e) {
        e.printStackTrace();
        String errMessage = "系统繁忙";
        Result result = Result.fail();
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            result.setCode(exception.getCode());
            result.setMessage(exception.getMessage());
        }else if (e instanceof GlobalCustomException) {
            GlobalCustomException exception = (GlobalCustomException) e;
            result.setCode(exception.getCode());
            result.setMessage(exception.getMessage());
        } else {
            //String env = environment.getProperty("spring.profiles.active");
            String message = e.getMessage();
            message = ObjectUtil.equals("prod", getActive()) ? errMessage : message;
            result.setMessage(message);
        }
        return result;
    }

}
