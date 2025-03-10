package com.minimalism.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/3/9 16:12:44
 * @Description
 */
@AllArgsConstructor
@Getter
public enum OSType {
    win("\\"), linux("/"), mac("/");
    private String separator;

}
