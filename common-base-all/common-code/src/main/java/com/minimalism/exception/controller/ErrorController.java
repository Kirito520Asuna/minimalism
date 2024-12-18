package com.minimalism.exception.controller;

import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.result.Result;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;


/**
 * @Author yan
 * @Date 2023/11/1 0001 16:03
 * @Description 实现Controller接收过过滤器发来的异常
 */

@RestController
public class ErrorController extends BasicErrorController {
    public ErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    /**
     * 拦截器异常拦截
     *
     * @param request
     * @return
     */

    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        getErrorProperties().setIncludeException(true);
        getErrorProperties().setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
        getErrorProperties().setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);
        ErrorAttributeOptions errorAttributeOptions = getErrorAttributeOptions(request, MediaType.ALL);
        Map<String, Object> body = getErrorAttributes(request, errorAttributeOptions);
        //错误信息
        String obj = String.valueOf(body.get("message"));
        int errcode = ApiCode.FAIL.getCode();

        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String element = attributeNames.nextElement();
            Object attribute = request.getAttribute(element);
            if (attribute instanceof GlobalCustomException) {
                GlobalCustomException exception = (GlobalCustomException) attribute;
                Integer exceptionCode = exception.getCode();
                errcode = ObjectUtils.defaultIfEmpty(exceptionCode, errcode);
                obj = exception.getMessage();
                break;
            }
        }

        Result result = Result.result(errcode, ObjectUtils.defaultIfEmpty(obj, "系统繁忙！"));
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
