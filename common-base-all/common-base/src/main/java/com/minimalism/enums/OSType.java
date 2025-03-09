package com.minimalism.enums;

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

    private static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static boolean isWindows() {
        return getOS().contains(win.name());
    }

    public static boolean isMac() {
        return getOS().contains(mac.name());
    }

    public static boolean isLinux() {
        String os = getOS();
        return os.contains(linux.name()) || os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static String getSeparator() {
        String separator;
        if (isWindows()) {
            separator = win.getSeparator();
        } else if (isMac()) {
            separator = mac.getSeparator();
        } else if (isLinux()) {
            separator = linux.getSeparator();
        } else {
            separator = linux.getSeparator();
        }
        return separator;
    }
}
