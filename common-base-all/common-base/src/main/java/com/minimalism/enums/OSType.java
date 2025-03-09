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

    public static String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains(win.name())) {
            os = win.name();
        } else if (os.contains(mac.name())) {
            os = mac.name();
        } else if (os.contains(linux.name())) {
            os = linux.name();
        }
        return os;
    }

    public static boolean isWindows(String os) {
        if (StrUtil.isBlank(os)) {
            os = getOS();
        }
        return os.contains(win.name());
    }

    public static boolean isMac(String os) {
        if (StrUtil.isBlank(os)) {
            os = getOS();
        }
        return os.contains(mac.name());
    }

    public static boolean isLinux(String os) {
        if (StrUtil.isBlank(os)) {
            os = getOS();
        }
        return os.contains(linux.name()) || os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static String getSeparator(String os) {
        String separator;
        if (StrUtil.isBlank(os)) {
            os = getOS();
        }
        if (isWindows(os)) {
            separator = win.getSeparator();
        } else if (isMac(os)) {
            separator = mac.getSeparator();
        } else if (isLinux(os)) {
            separator = linux.getSeparator();
        } else {
            separator = linux.getSeparator();
        }
        return separator;
    }
}
