package com.minimalism.exception.handler;

import cn.hutool.core.util.ObjectUtil;
import com.minimalism.exception.AbstractExceptionHandler;
import com.minimalism.exception.BusinessException;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author yan
 * @Date 2023/9/1 0001 13:12
 * @Description 通用异常拦截响应处理
 */
@RestControllerAdvice
public class CommonExceptionHandler implements AbstractExceptionHandler {


}
