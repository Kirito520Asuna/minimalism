package com.minimalism.controller;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.result.Result;
import com.minimalism.result.ResultPage;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/29 下午5:59:24
 * @Description
 */
public interface AbstractBaseController extends AbstractBean {
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
