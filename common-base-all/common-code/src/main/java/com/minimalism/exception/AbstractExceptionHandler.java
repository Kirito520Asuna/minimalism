package com.minimalism.exception;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.enums.ApiCode;
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
public interface AbstractExceptionHandler extends AbstractBean {

    default String getActive(){
        String active = SpringUtil.getBean(Environment.class).getProperty("spring.profiles.active",String.class,"dev");
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

        Integer code = ApiCode.FAIL.getCode();
        String message = ApiCode.FAIL.getMessage();
        if (e instanceof GlobalConfigException) {
            GlobalConfigException exception = (GlobalConfigException) e;
            code = exception.getCode();
            message = exception.getMessage();
        }else if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            code = exception.getCode();
            message = exception.getMessage();
        }else if (e instanceof GlobalCustomException) {
            GlobalCustomException exception = (GlobalCustomException) e;
            code = exception.getCode();
            message = exception.getMessage();
        } else {
            message = ObjectUtil.equals("prod", getActive()) ? errMessage : e.getMessage();
        }
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}
