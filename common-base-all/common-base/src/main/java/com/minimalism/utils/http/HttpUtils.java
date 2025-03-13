package com.minimalism.utils.http;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.minimalism.utils.object.ObjectUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 基于okhttp and hutool and lombok
 * 不使用hutool 发起请求的原因 ：高并发下 发起过多会出现 发起请求会断开之前发起过的请求连接
 *
 * @author yan
 * @date 2024/4/6 7:56
 */
@Slf4j
public class HttpUtils {
   public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    /**
     * url 拼接
     *
     * @param url
     * @param params
     * @return
     */
    @SneakyThrows
    private static String urlJoin(String url, Map<String, Object> params) {
        if (ObjectUtil.isNotEmpty(params)) {
            StringBuffer append = new StringBuffer(url);
            // 检查字符串的最后一个字符是否是"?"
            if (!url.endsWith("?")) {
                append.append("?");
            }
            params.keySet().forEach(key -> {
                Object obj = params.get(key);
                if (ObjectUtil.isNotEmpty(obj)) {
                    if (obj instanceof String[]) {
                        log.info("ok");
                        String[] temp = (String[]) obj;
                        obj = temp[0];
                    }
                    append.append(key).append("=").append(obj).append("&");
                }
            });
            url = append.toString();
        }
        // 检查字符串的最后一个字符是否是"&"||"?"
        if (url.endsWith("&") || url.endsWith("?")) {
            // 使用substring方法移除最后一个字符
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * @param builder
     * @param headers
     * @return
     */
    @SneakyThrows
    private static Request.Builder addHeader(Request.Builder builder, Map<String, String> headers) {
        if (ObjectUtil.isNotEmpty(headers)) {
            headers.keySet().forEach(key -> {
                String obj = headers.get(key);
                if (ObjectUtil.isNotEmpty(obj)) {
                    builder.addHeader(key, obj);
                }
            });
        }
        return builder;
    }

    @SneakyThrows
    public static Map<String, Object> objectToMap(Object o) {
        Map<String, Object> map = ObjectUtils.defaultIfEmpty(BeanUtil.beanToMap(o),null);
        return map;
    }

    @SneakyThrows
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = ObjectUtils.defaultIfEmpty(JSONUtil.toBean(json, Map.class),null);
        return map;
    }

    @SneakyThrows

    public static Map<String, String> jsonHeadersToMap(String json) {
        Map<String, String> map = ObjectUtils.defaultIfEmpty(JSONUtil.toBean(json, Map.class),null);
        return map;
    }

    @SneakyThrows
    public static String get(String url, String paramsJson) {
        Map<String, Object> beanToMap = jsonToMap(paramsJson);
        return get(url, beanToMap);
    }

    @SneakyThrows
    public static String post(String url, String bodyJson) {
        Map<String, Object> beanToMap = jsonToMap(bodyJson);
        return post(url, beanToMap);
    }

    @SneakyThrows
    public static String put(String url, String bodyJson) {
        Map<String, Object> beanToMap = jsonToMap(bodyJson);
        return put(url, beanToMap);
    }

    @SneakyThrows
    public static String delete(String url, String paramsJson) {
        Map<String, Object> beanToMap = jsonToMap(paramsJson);
        return delete(url, beanToMap);
    }


    @SneakyThrows
    public static String get(String url, Object params) {
        Map<String, Object> beanToMap = objectToMap(params);
        return get(url, beanToMap);
    }

    @SneakyThrows
    public static String post(String url, Object body) {
        Map<String, Object> beanToMap = objectToMap(body);
        return post(url, beanToMap);
    }

    @SneakyThrows
    public static String put(String url, Object body) {
        Map<String, Object> beanToMap = objectToMap(body);
        return put(url, beanToMap);
    }

    @SneakyThrows
    public static String delete(String url, Object params) {
        Map<String, Object> beanToMap = objectToMap(params);
        return delete(url, beanToMap);
    }

    @SneakyThrows
    public static String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    @SneakyThrows
    public static String post(String url, Map<String, Object> body) {
        return post(url, null, body, null, null);
    }

    @SneakyThrows
    public static String put(String url, Map<String, Object> body) {
        return put(url, null, body, null, null);
    }

    @SneakyThrows
    public static String delete(String url, Map<String, Object> params) {
        return delete(url, params, null);
    }

    @SneakyThrows
    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        HttpMethod get = HttpMethod.GET;
        return sendHttpRequest(get, url, params, null, headers, null);
    }

    @SneakyThrows
    public static String post(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        HttpMethod post = HttpMethod.POST;
        return sendHttpRequest(post, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static String put(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        HttpMethod put = HttpMethod.PUT;
        return sendHttpRequest(put, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static String delete(String url, Map<String, Object> params, Map<String, String> headers) {
        HttpMethod delete = HttpMethod.DELETE;
        return sendHttpRequest(delete, url, params, null, headers, null);
    }


    @SneakyThrows
    public static String sendHttpRequest(HttpMethod httpMethod, String url, String paramsJson, String bodyJson, String headersJson, String mediaType) {
        Map<String, Object> paramsToMap = jsonToMap(paramsJson);
        Map<String, Object> bodyToMap = jsonToMap(bodyJson);
        Map<String, String> headersToMap = jsonHeadersToMap(headersJson);
        return sendHttpRequest(httpMethod, url, paramsToMap, bodyToMap, headersToMap, mediaType);
    }

    @SneakyThrows
    public static String sendHttpRequest(HttpMethod httpMethod, String url, Object params, Object body, Object headers, String mediaType) {
        Map<String, Object> paramsToMap = objectToMap(params);
        Map<String, Object> bodyToMap = objectToMap(body);
        Map<String, Object> headersToMapTemp = objectToMap(headers);
        Map<String, String> headersToMap = ObjectUtil.isEmpty(headers) ? null : new LinkedHashMap<>();
        if (ObjectUtil.isNotEmpty(headers)) {
            headersToMapTemp.keySet().stream().forEach(key -> headersToMap.put(key, String.valueOf(headersToMapTemp.get(key))));
        }
        return sendHttpRequest(httpMethod, url, paramsToMap, bodyToMap, headersToMap, mediaType);
    }

    /**
     * 发送请求(该类最底层)
     *
     * @param httpMethod  请求方式
     * @param url         请求地址
     * @param params      请求行
     * @param body 请求体
     * @param headers     请求头
     * @param mediaType   请求格式
     * @return 响应
     */
    @SneakyThrows
    public static String sendHttpRequest(HttpMethod httpMethod, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        Map<String, Object> sendMap = Maps.newLinkedHashMap();
        sendMap.put("httpMethod", httpMethod);
        sendMap.put("url", url);
        sendMap.put("headers", headers);
        sendMap.put("params", params);
        sendMap.put("body", body);
        log.info("[SendHttp]::[START]==>[info]:[{}]<==[END]",JSONUtil.toJsonStr(sendMap));
        url = urlJoin(url, params);
        log.info("[SendHttp]::[splicingTogether]==>url:{}<==[END]",url);
        Request.Builder builder = addHeader(new Request.Builder(), headers).url(url);
        if (ObjectUtils.equals(HttpMethod.GET, httpMethod)) {
            builder.get();
        } else if (ObjectUtils.equals(HttpMethod.POST, httpMethod) || ObjectUtils.equals(HttpMethod.PUT, httpMethod)) {
            String jsonStr = JSONUtil.toJsonStr(ObjectUtils.defaultIfEmpty(body,new LinkedHashMap<>()));
            //ObjectUtil.isEmpty(mediaType) ? "application/json" : mediaType
            RequestBody requestBody = RequestBody.create(MediaType.parse(ObjectUtils.defaultIfEmpty(mediaType,"application/json")), jsonStr);
            if (ObjectUtils.equals(HttpMethod.POST, httpMethod)) {
                builder.post(requestBody);
            } else if (ObjectUtils.equals(HttpMethod.PUT, httpMethod)) {
                builder.put(requestBody);
            } else {
                throw new Exception("");
            }
        } else if (ObjectUtils.equals(HttpMethod.DELETE, httpMethod)) {
            builder.delete();
        } else {
            throw new Exception("请求方法不存在!");
        }
        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            log.info("[SendHttp]::[Success]");
        }else {
            log.error("[SendHttp]::[Error]:[code={},message={}]", response.code(), response.message());
        }
        String responseData = response.body().string();
        return responseData;
    }
}
