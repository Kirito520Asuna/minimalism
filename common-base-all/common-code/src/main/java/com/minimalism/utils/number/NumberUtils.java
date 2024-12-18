package com.minimalism.utils.number;

import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;

/**
 * @Author yan
 * @Date 2024/10/28 下午11:58:50
 * @Description
 */
public class NumberUtils {
    /**
     * BigDecimal保留有效小数 BigDecimal==null 设置为0
     *
     * @param decimal
     * @return
     */

    public static BigDecimal keepValidDecimals(BigDecimal decimal) {
        decimal = ObjectUtil.defaultIfNull(decimal, BigDecimal.ZERO);
        String decimalStr = decimal.toString();
        if (decimalStr.contains(".")) {
            String[] split = decimalStr.split("\\.");
            String a = split[1];
            int length = a.length();
            StringBuffer reverse = new StringBuffer(a).reverse();
            String string = reverse.toString();
            char invalid = '0';
            Integer indexEnd = null;
            for (int i = 0; i < length; i++) {
                char c = string.charAt(i);
                if (!ObjectUtil.equal(c, invalid)) {
                    int index = length - i;
                    indexEnd = index;
                    break;
                }
            }
            decimal = indexEnd == null ? decimal : decimal.setScale(indexEnd, BigDecimal.ROUND_HALF_UP);
        }
        return decimal;
    }
}
