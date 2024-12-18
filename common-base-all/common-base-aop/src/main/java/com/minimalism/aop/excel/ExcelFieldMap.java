package com.minimalism.aop.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2023/4/23 0023 16:52
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelFieldMap {
    /**
     * excel名称
     */
    @Schema(description = "excel名称")
    private String name;
    /**
     * java类字段映射名
     */
    @Schema(description = "java类字段映射名")
    private String fieldName;

    /**
     * 获取一个类里所有的@ExcelField 的映射关系
     *
     * @param clazz
     * @return
     */
    public static List<ExcelFieldMap> getAllFieldMaps(Class<?> clazz) {
        //Class<Demo> clazz = Demo.class; // 获取Demo类的Class对象
        Field[] fields = clazz.getDeclaredFields(); // 获取Demo类中所有定义的字段
        List<ExcelFieldMap> excelFieldMaps = new ArrayList<>(fields.length);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(ExcelField.class); // 获取当前字段上的ExcelField注解
            if (annotation instanceof ExcelField) { // 判断当前字段是否有ExcelField注解
                ExcelField excelField = (ExcelField) annotation; // 将注解强制转换为ExcelField类型
                //System.out.println(field.getName() + ": " + excelField.name()); // 输出字段名和对应的Excel表格列名
                String[] values = excelField.values();
                List<String> list = Stream.of(values).collect(Collectors.toList());
                list.stream().forEach(value -> {
                    ExcelFieldMap excelFieldMap = new ExcelFieldMap(value, field.getName());
                    excelFieldMaps.add(excelFieldMap);
                });

            }
        }
        return excelFieldMaps;
    }

    /**
     * 获取一个类里所有必传字段的@ExcelField 的映射关系
     *
     * @param tClass
     * @return
     */
    public static List<ExcelFieldMap> getAllMustFieldMaps(Class<?> tClass) {
        //Class<Demo> tClass = Demo.class; // 获取Demo类的Class对象
        Field[] fields = tClass.getDeclaredFields(); // 获取Demo类中所有定义的字段
        List<ExcelFieldMap> excelFieldMaps = new ArrayList<>(fields.length);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(ExcelField.class); // 获取当前字段上的ExcelField注解
            if (annotation instanceof ExcelField) { // 判断当前字段是否有ExcelField注解
                ExcelField excelField = (ExcelField) annotation; // 将注解强制转换为ExcelField类型
                //System.out.println(field.getName() + ": " + excelField.name()); // 输出字段名和对应的Excel表格列名
                if (excelField.bool()) {
                    String[] values = excelField.values();
                    List<String> list = Stream.of(values).collect(Collectors.toList());
                    list.stream().forEach(value -> {
                        ExcelFieldMap excelFieldMap = new ExcelFieldMap(value, field.getName());
                        excelFieldMaps.add(excelFieldMap);
                    });
                }
            }
        }
        return excelFieldMaps;
    }

    /**
     * 获取 读取映射
     *
     * @param clazz
     * @return
     */
    public static Map<String, String> getHeaderReadMap(Class<?> clazz) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<ExcelFieldMap> fieldMaps = getAllFieldMaps(clazz);
        fieldMaps.stream().forEach(one -> {
            String name = one.getName();
            String fieldName = one.getFieldName();
            map.put(name, fieldName);
        });
        return map;
    }

    /**
     * 获取 导出映射
     *
     * @param clazz
     * @return
     */
    public static Map<String, String> getHeaderOutMap(Class<?> clazz) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<ExcelFieldMap> fieldMaps = getAllFieldMaps(clazz);
        fieldMaps.stream().forEach(one -> {
            String name = one.getName();
            String fieldName = one.getFieldName();
            map.put(fieldName, name);
        });
        return map;
    }
}
