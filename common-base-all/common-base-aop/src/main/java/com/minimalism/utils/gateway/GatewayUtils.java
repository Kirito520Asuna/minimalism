package com.minimalism.utils.gateway;

import cn.hutool.core.util.StrUtil;
import com.minimalism.constant.gateway.GatewayConstants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yan
 * @Date 2025/2/9 3:57:52
 * @Description
 */
@Slf4j
public class GatewayUtils {
    public static String replaceUrl(HttpServletRequest request, String url) throws Exception {
        String gatewayDomainsAllPaths = request.getHeader(GatewayConstants.GATEWAY_DOMAINS_ALL_PATHS);
        if (StrUtil.isNotBlank(gatewayDomainsAllPaths)) {
            String gatewayDomainsName = request.getHeader(GatewayConstants.GATEWAY_DOMAINS_NAME);
            if (StrUtil.isBlank(gatewayDomainsAllPaths)) {
                log.error("请求头缺少参数 {} or 值为空", GatewayConstants.GATEWAY_DOMAINS_NAME);
                throw new Exception();
            }
            if (url.contains(gatewayDomainsName) && !url.contains(gatewayDomainsAllPaths)) {
                url = url.replace(gatewayDomainsName, gatewayDomainsAllPaths);
            }
        }
        return url;
    }

}
