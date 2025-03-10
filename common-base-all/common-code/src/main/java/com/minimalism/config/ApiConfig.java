package com.minimalism.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.io.ByteSource;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.exception.GlobalCustomException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/5/15 0015 17:13
 * @Description
 */
@Slf4j
@Configuration
@Data @NoArgsConstructor
@AllArgsConstructor
public class ApiConfig implements AbstractBean {
    public static JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);
    @Resource
    @Lazy
    private ObjectMapper objectMapper;
    @Value("${spring.application.name: }")
    public String springApplicationName;
    @Value("${salt.api:API_SALT}")
    public String apiSalt;
    @Value("${asName.sign:sign}")
    public String signAsName;
    @Value("${asName.timestamp:timestamp}")
    public String timestampAsName;
    @Value("${api.path:/api/}")
    private String apiPath;
    @Value("${ip.whitelist:127.0.0.1}")
    private String ipWhitelist;
    @Value("${ip.blacklist: }")
    private String ipBlackList;
    @Value("${server.servlet.context-path: }")
    private String contextPath;
    @Value("${sign.enable:true}")
    private Boolean signEnable;
    @Value("${sign.multiple.enable:false}")
    private Boolean signMultipleEnable;
    @Value("${sign.timeOut:10}")
    private Long signTimeOut;
    @Value("${server.port:8080}")
    private String serverPort;

    public Boolean getSignMultipleEnable() {
        if (ObjectUtil.isEmpty(signMultipleEnable)){
            signMultipleEnable = false;
        }
        return signMultipleEnable;
    }

    public String getApiPath() {
        String apiPath = this.apiPath;
        if (StrUtil.isBlank(apiPath)) {
            apiPath = "/api/";
        }
        if (!apiPath.startsWith("/")) {
            apiPath = new StringBuffer("/").append(apiPath).toString();
        }
        if (!apiPath.endsWith("/")) {
            apiPath = new StringBuffer(apiPath).append("/").toString();
        }
        return apiPath;
    }

    /**
     * 验证ip是否在白名单中
     *
     * @param ip
     * @return
     */
    public boolean verifyIpWhiteList(String ip) {
        String temp = ipWhitelist;
        List<String> list = CollUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(temp)) {
            list.addAll(Arrays.stream(temp.replace(" ", "").split(",")).collect(Collectors.toList()));
        }
        return list.contains(ip);
    }

    public String getPath() {
        String path = contextPath;
        path = ObjectUtils.isEmpty(path) ? "" : path;
        return path;
    }

    /**
     * 获取url中的路径
     *
     * @param url
     * @return
     */
    public String getUrl(String url) {
        String path = getPath();
        if (ObjectUtils.isEmpty(path)) {
            path = getServerPort();
        } else {
            path = path.endsWith("/") ? path : path + "/";
        }
        int startIndex = url.indexOf(path);
        startIndex = startIndex == -1 ? 0 : startIndex;
        String substring = url.substring(startIndex, url.length());
        String s = substring;
        if (!s.startsWith("/")) {
            s = new StringBuffer("/").append(s).toString();
        }
        return s;
    }

    public String verifyTimestamp(HttpServletRequest request) {
        // 使用 CachedBodyHttpServletRequest 包装原始请求，以支持多次读取请求体
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
        //String timestampHeader = request.getHeader(timestampAsName);
        String timestampHeader = wrapper.getHeader(timestampAsName);
        if (StrUtil.isBlank(timestampHeader) ||
                Math.abs(Duration.between(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(timestampHeader)), ZoneId.systemDefault()), LocalDateTime.now()).toMinutes()) >= signTimeOut) {
            throw new GlobalCustomException("请求时间戳不合法");
        }
        return timestampHeader;
    }

    /**
     * 将 Map 转换为 TreeMap，并递归处理值为 JSON 对象的情况。
     *
     * @param o 要转换的原始 Object
     * @return 转换后的 TreeMap
     */
    public static <T> TreeMap<String, Object> tree(T o) {
        // 将原始 Object 转换为 JSONObject
        JSONObject entries = new JSONObject(o, JSON_CONFIG);
        // 创建原始 TreeMap 和临时 TreeMap
        TreeMap<String, Object> treeMap = new TreeMap<>(entries);
        TreeMap<String, Object> tempTreeMap = new TreeMap<>();
        if (o instanceof Collection) {
            System.err.println("list");
        }

        Iterator<Map.Entry<String, Object>> iterator = treeMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            String valueOf = String.valueOf(value);
            try {
                JSON parse = JSONUtil.parse(valueOf, JSON_CONFIG);
                Map<String, Object> map = parse.toBean(Map.class);
                value = tree(map);
                // 使用 iterator 的 remove 方法来安全删除当前条目
                iterator.remove();
                tempTreeMap.put(key, value);
            } catch (Exception e) {
                //非json对象
            }
        }
        // 将临时 TreeMap 中的更新后的条目放回原始 TreeMap
        treeMap.putAll(tempTreeMap);
        // 返回最终的 TreeMap
        return new TreeMap<>(treeMap);
    }

    /**
     * 参数转JsonNode
     *
     * @param request      请求
     * @param objectMapper jackson
     * @return JsonNode
     * @throws IOException 异常
     */
    public static JsonNode parseToJsonNode(@Nonnull HttpServletRequest request, @Validated @Nonnull ObjectMapper objectMapper) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        InputStream inputStream = request.getInputStream();
        return parseToJsonNode(objectMapper, parameterMap, inputStream);
    }

    /**
     * 参数转JsonNode
     *
     * @param objectMapper
     * @param parameterMap
     * @param inputStream
     * @return
     * @throws IOException
     */
    @SneakyThrows
    public static JsonNode parseToJsonNode(ObjectMapper objectMapper, Map<String, String[]> parameterMap, InputStream inputStream) {
        Map<String, Collection<String>> queries = new HashMap<>();
        if (CollUtil.isNotEmpty(parameterMap)) {
            parameterMap.keySet().forEach(key -> {
                List<String> strings = Arrays.asList(parameterMap.get(key));
                queries.put(key, strings);
            });
        }
        byte[] body = ObjectUtil.isEmpty(inputStream) ? null : IOUtils.toByteArray(inputStream);

        return parseToJsonNode(queries, body, objectMapper);
    }

    /**
     * 参数转JsonNode
     *
     * @param queries
     * @param body
     * @param objectMapper
     * @return
     * @throws IOException
     */
    public static JsonNode parseToJsonNode(Map<String, Collection<String>> queries, byte[] body, @Validated @NonNull ObjectMapper objectMapper) throws IOException {
        if (CollUtil.isEmpty(queries)) {
            if (body == null) {
                body = ByteSource.empty().read();
            }
            return objectMapper.readTree(body);
        } else {
            JsonNode jsonNode = objectMapper.valueToTree(queries);
            ArrayNode arrayNode = new ArrayNode(objectMapper.getNodeFactory());
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                for (JsonNode itemNode : next.getValue()) {
                    ObjectNode objectNode = new ObjectNode(objectMapper.getNodeFactory());
                    objectNode.set(next.getKey(), itemNode);
                    arrayNode.add(objectNode);
                }
            }
            return arrayNode;
        }
    }


    // 定义一个方法 fieldForSort，从 JSON 节点中提取字段，并将它们存储在一个映射中。
    public static Map<String, Object> fieldForSort(JsonNode jsonNode, Map<String, Object> fieldMap) {
        // 判断 jsonNode 是否为 ArrayNode 类型
        if (jsonNode instanceof ArrayNode) {
            // 遍历数组节点下的所有元素，并对每个元素递归调用 fieldForSort 方法
            for (JsonNode item : jsonNode) {
                fieldForSort(item, fieldMap);
            }
        } else if (jsonNode instanceof ObjectNode) {
            // 遍历对象节点下的所有字段
            Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                // 如果字段的值是 ValueNode 类型
                if (fieldValue instanceof ValueNode) {
                    // 将该字段名和字段值添加到 fieldMap 中
                    fieldMap.put(fieldName, fieldValue.asText());
                } else if (fieldValue instanceof ObjectNode) {
                    // 如果字段的值是 ObjectNode 类型，则递归调用 fieldForSort 方法
                    fieldForSort(fieldValue, fieldMap);
                } else if (fieldValue instanceof ArrayNode) {
                    // 如果字段的值是 ArrayNode 类型，则遍历该数组节点下的所有元素，并对每个元素递归调用 fieldForSort 方法
                    for (JsonNode arrayItem : fieldValue) {
                        fieldForSort(arrayItem, fieldMap);
                    }
                }
            }
        }
        // 返回 fieldMap
        return fieldMap;
    }

    public static void main(String[] args) {

        BigDecimal decimal = new BigDecimal("0.000045000");
        System.out.println(decimal);
        decimal = keepValidDecimals(decimal);

        System.out.println(decimal);


//        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> hashMap1 = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> hashMap2 = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> hashMap3 = new LinkedHashMap<>();
//        hashMap3.put("b", "2");
//        hashMap3.put("a", "1");
//        hashMap3.put("c", "3");
//        hashMap3.put("e", 1);
//        hashMap3.put("d", "4");
//
//
//        hashMap2.put("a", "1");
//        hashMap2.put("b", "2");
//        hashMap2.put("e", 1);
//        hashMap2.put("hashMap3", hashMap3);
//        hashMap2.put("c", "3");
//        hashMap2.put("d", "4");
//
//        hashMap1.put("a", "1");
//        hashMap1.put("b", "2");
//        hashMap1.put("c", "3");
//        hashMap1.put("d", "4");
//        hashMap1.put("e", 1);
//        hashMap1.put("hashMap2", hashMap2);
//
//        hashMap.put("c", "3");
//        hashMap.put("d", "4");
//        hashMap.put("e", 1);
//        hashMap.put("a", "1");
//        hashMap.put("b", "2");
//        hashMap.put("hashMap1", hashMap1);
//
//        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//        BeanUtil.copyProperties(hashMap3, map);
//        hashMap.put("hashMapList", CollUtil.newArrayList(map, map, map));
//        System.out.println(hashMap);
//        System.err.println(tree(hashMap));
//        LinkedHashMap<String, Object> fieldMap = Maps.newLinkedHashMap();
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        Map<String, String[]> objectObjectLinkedHashMap = Maps.newLinkedHashMap();
//        Set<Map.Entry<String, Object>> entries = hashMap.entrySet();
//        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> next = iterator.next();
//            Object value = next.getValue();
//            if (value instanceof Map) {
//                System.err.println("sssssssss");
//            }
//
//            objectObjectLinkedHashMap.put(next.getKey(), new String[]{String.valueOf(value)});
//        }
//        System.err.println("'999/'" + JSONUtil.parse(objectObjectLinkedHashMap));
//        JsonNode jsonNode = parseToJsonNode(objectMapper1, objectObjectLinkedHashMap, null);
//        System.err.println(jsonNode);
//        System.err.println(fieldForSort(jsonNode, fieldMap));


    }

    public static BigDecimal keepValidDecimals(BigDecimal decimal) {
        if (decimal == null) {
            decimal = BigDecimal.ZERO;
        } else {
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
        }
        return decimal;
    }


    /**
     * @param keySign 签名密钥
     * @param method  HTTP方法（GET、POST等）
     * @param url     请求的URL
     * @param map     请求参数的Map
     * @return 生成的签名字符串
     * @throws UnsupportedEncodingException 不支持的字符编码异常
     */
    public String generalSign(String keySign, String method, String url, Map<String, Object> map) throws UnsupportedEncodingException {
        // 构建用于生成签名的字符串
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method);
        stringBuilder.append(url);

        // 使用TreeMap对请求参数进行排序，保证签名的一致性
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        for (String key : treeMap.keySet()) {
            // 排除签名参数本身，避免循环引用
            if (ObjectUtil.equal(key, signAsName) || ObjectUtil.equal(key, timestampAsName)) {
                continue;
            }
            // 将键值对追加到签名字符串
            stringBuilder.append(key).append("=");
            stringBuilder.append(treeMap.get(key));
        }

        // 追加签名密钥的属性值
        stringBuilder.append(keySign);

        // 对签名字符串进行URL编码，并计算MD5摘要，返回十六进制字符串形式的摘要
        return DigestUtils.md5DigestAsHex(
                URLEncoder.encode(stringBuilder.toString(), StandardCharsets.UTF_8.name())
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .getBytes()
        );
    }

    // 定义一个方法 fieldForSort，从 JSON 节点中提取字段，并将它们存储在一个映射中。
    public static Map<String, Object> fieldForSort(JsonNode jsonNode, Map<String, Object> fieldMap, List<String> duplicateKey) {
        // 判断 jsonNode 是否为 ArrayNode 类型
        if (jsonNode instanceof ArrayNode) {
            // 遍历数组节点下的所有元素，并对每个元素递归调用 fieldForSort 方法
            for (JsonNode item : jsonNode) {
                fieldForSort(item, fieldMap, duplicateKey);
            }
        } else if (jsonNode instanceof ObjectNode) {
            // 遍历对象节点下的所有字段
            Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();

                // 如果字段的值是 ValueNode 类型
                if (fieldValue instanceof ValueNode) {
                    // 检查 fieldMap 中是否已经包含该字段名
                    if (fieldMap.containsKey(fieldName)) {
                        // 将重复的字段名添加到 duplicateKey 列表中
                        duplicateKey.add(fieldName);
                    }
                    // 将该字段名和字段值添加到 fieldMap 中
                    fieldMap.put(fieldName, fieldValue.asText());
                } else if (fieldValue instanceof ObjectNode) {
                    // 如果字段的值是 ObjectNode 类型，则递归调用 fieldForSort 方法
                    fieldForSort(fieldValue, fieldMap, duplicateKey);
                } else if (fieldValue instanceof ArrayNode) {
                    // 如果字段的值是 ArrayNode 类型，则遍历该数组节点下的所有元素，并对每个元素递归调用 fieldForSort 方法
                    for (JsonNode arrayItem : fieldValue) {
                        fieldForSort(arrayItem, fieldMap, duplicateKey);
                    }
                }
            }
        }

        // 在遍历完所有字段后，将 duplicateKey 列表中的所有字段名从 fieldMap 中移除
        for (String key : duplicateKey) {
            fieldMap.remove(key);
        }

        // 返回 fieldMap
        return fieldMap;
    }

}
