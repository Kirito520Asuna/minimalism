package com.minimalism.aop.controller;

import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.base.result.Result;
import com.minimalism.base.result.ResultPage;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/29 下午5:59:24
 * @Description
 */
@RestController
public interface AbsBaseController extends AbsBean {
    default <T> Result<T> ok() {
        return Result.ok();
    }

    default <T> Result<T> ok(T data) {
        return Result.ok(data);
    }

    default <T> Result<T> fail() {
        return Result.fail();
    }

    default <T> Result<T> fail(String message) {
        return Result.fail(message);
    }

    default <T> Result<ResultPage<T>> listToPage(List<T> list) {
        return ok(ResultPage.listToPage(list));
    }
}
