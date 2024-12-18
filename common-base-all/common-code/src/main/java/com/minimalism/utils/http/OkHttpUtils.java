package com.minimalism.utils.http;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import com.minimalism.utils.date.DateUtils;
import com.minimalism.utils.api.ApiUtil;
import com.minimalism.utils.api.SingleSignature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 基于okhttp and hutool and lombok
 * 不使用hutool 发起请求的原因 ：高并发下 发起过多会出现 发起请求会断开之前发起过的请求连接
 *
 * @author yan
 * @date 2024/4/6 7:56
 */
@Slf4j
public class OkHttpUtils extends HttpUtils {

    @SneakyThrows
    public static void main(String[] args) {
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inclusive = DateUtils.longToLocalDateTime(currentTimeMillis).minusMonths(20);
        Duration between = Duration.between(inclusive, now);
        long a = between.toMinutes();
        long abs = Math.abs(a);
        //currentTimeMillis = DateUtils.LocalDateTimeTolong(inclusive);
        System.err.println(abs);
        String apiSalt = "API_SALT";
        String timestamp = "timestamp";
        String sign = "sign";
        String url = "http://127.0.0.1:9500/im/api/user/getUser";
        String method = "GET";
        Map<String, String[]> map1 = Maps.newLinkedHashMap();
        map1.put("userId", new String[]{"5"});
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.putAll(map1);

        SingleSignature singleSignature = new SingleSignature()
                .setSalt(apiSalt)
                .setMethod(method)
                .setUrl(url)
                .setParams(map1)
                .setBody(Maps.newLinkedHashMap())
                .setExCollection(CollUtil.newArrayList(sign, timestamp));

        String generalSign = ApiUtil.generalSign(singleSignature);
        Map<String, String> hashMap = Maps.newLinkedHashMap();
        hashMap.put(sign, generalSign);
        hashMap.put(timestamp, currentTimeMillis + "");
        String s = get(url, map, hashMap);
        System.err.println(s);

        //String url  = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=a9e460fa-d9fb-4d30-999a-994d952e8cf3";
        //
        //String json = "{\n" +
        //        "    \"touser\":\"13817709339\",\n" +
        //        "    \"msgtype\": \"template_card\",\n" +
        //        "    \"template_card\": {\n" +
        //        "      \"card_type\":\"text_notice\",\n" +
        //        "              \"source\":{\n" +
        //        "            \"icon_url\":\"https://patient-png-bucket.oss-cn-shanghai.aliyuncs.com/linx_logo2.jpg\",\n" +
        //        "            \"desc\":\"急诊手术\",\n" +
        //        "            \"desc_color\":2\n" +
        //        "        },\n" +
        //        "        \"main_title\": {\n" +
        //        "            \"title\": \"复旦大学附属华山医院\",\n" +
        //        "            \"desc\": \"第一导管室\"\n" +
        //        "        },\n" +
        //        "      \n" +
        //        "        \"emphasis_content\": {\n" +
        //        "            \"title\": \"待接单\",\n" +
        //        "            \"desc\": \"请关注接单情况\"\n" +
        //        "        },\n" +
        //        "        \"horizontal_content_list\": [\n" +
        //        "            {\n" +
        //        "                \"keyname\": \"手术时间\",\n" +
        //        "                \"value\": \"2024.8.1 12:00\"\n" +
        //        "            },\n" +
        //        "            {\n" +
        //        "                \"keyname\": \"主诉\",\n" +
        //        "                \"value\": \"患者10岁，头痛11年\"\n" +
        //        "            },\n" +
        //        "            {\n" +
        //        "                \"keyname\": \"邀请专家\",\n" +
        //        "                \"value\": \"赵xx，钱xx，孙xx，李xx\"\n" +
        //        "            },\n" +
        //        "            {\n" +
        //        "                \"keyname\": \"接诊专家\",\n" +
        //        "                \"value\": \"李xx\"\n" +
        //        "            }        \n" +
        //        "        ],\n" +
        //        "              \"card_action\":{\n" +
        //        "            \"type\":1,\n" +
        //        "            \"url\":\"https://work.weixin.qq.com/?from=openApi\",\n" +
        //        "            \"appid\":\"APPID\",\n" +
        //        "            \"pagepath\":\"PAGEPATH\"\n" +
        //        "        }\n" +
        //        "    }\n" +
        //        "}";
        //String post = post(url, json);
        //System.out.println(post);
        //String url = "http://localhost:8800/seata/api/operateLog/page";
        //String s = get(url, new Object());
        //System.out.println(s);

        //String SALT = "JvzMQSh5rg3MmkKQF+S9dKGWz3Fqsrkhhmyc65dPr6C0nY3DLd2BGVZN38zmiL8kHPM8qYJ3n583yKmNJnPgq/uCa+rnbK5UiJzbBVaEcGwq343p5NJ+ynVzYqWtJpk+R0ndhC0CmMcxyEXEfsRJSrCJDD9eLle+pA8pw6Ob7Js=";
        //
        //String httpMethod = "GET";
        //String originalUrl = "/getAllProvinceCityInfo.do";
        //originalUrl = "https://dev.medtion.com" + originalUrl;
        //StringBuilder stringBuilder = new StringBuilder(httpMethod + originalUrl);
        //
        //LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        //hashMap.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
        //hashMap.forEach((key, value) -> stringBuilder.append(key).append("=").append(value));
        //stringBuilder.append(SALT);
        //String sign = DigestUtils.md5DigestAsHex(URLEncoder.encode(stringBuilder.toString(), StandardCharsets.UTF_8.name())
        //        .replace("+", "%20").replace("*", "%2A").getBytes(StandardCharsets.UTF_8));
        //hashMap.put("sign", sign);
        //Map<String, Collection<String>> queries = new HashMap<>();
        //hashMap.forEach((key, value) -> queries.put(key, Collections.singleton(value)));
        //
        //String s = get(originalUrl, queries);
        //System.out.println(s);
    }
}
