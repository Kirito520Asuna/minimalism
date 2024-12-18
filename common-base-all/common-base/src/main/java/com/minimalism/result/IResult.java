package com.minimalism.result;

/**
 * 结果代码接口
 *
 * @author yan
 */
public interface IResult {
    /**
     * 获取返回代码
     *
     * @return 返回代码
     */
    Integer getCode();

    /**
     * 获取返回信息
     *
     * @return 返回信息
     */
    String getMessage();
}
