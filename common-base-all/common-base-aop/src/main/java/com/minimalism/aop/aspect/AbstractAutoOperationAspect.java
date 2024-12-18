package com.minimalism.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.security.AutoOperation;
import com.minimalism.enums.AutoOperationEnum;
import com.minimalism.utils.bean.CustomBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author yan
 * @Date 2024/10/3 下午4:15:55
 * @Description
 */
public interface AbstractAutoOperationAspect extends AbstractSysLog {
    JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);

    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.security.AutoOperation)")
    default void SysLog() {
    }

    default void setArgs(Object[] args, int index, String argName, String value) {
        // 空指针检查
        if (args[index] == null) {
            throw new IllegalArgumentException("Argument at index " + index + " is null.");
        }
        args[index] = recursion(args[index], argName, value);
    }

    default Object recursion(Object o, String key, Object value) {
        try {
            String strTemp = JSONUtil.toJsonStr(o, JSON_CONFIG);
            boolean typeJSONArray = JSONUtil.isTypeJSONArray(strTemp);
            boolean typeJSON = JSONUtil.isTypeJSON(strTemp);

            if (typeJSONArray) {
                List<Object> list = JSONUtil.toList(strTemp, Object.class);
                // 流操作结果赋值
                list.stream().forEach(b -> recursion(b, key, value));
            } else if (typeJSON) {
                Map<String, Object> bean = JSONUtil.toBean(strTemp, JSON_CONFIG, Map.class);
                if (ObjectUtil.isNotEmpty(bean)) {
                    Set<String> setList = bean.keySet();
                    if (setList.contains(key)) {
                        bean.put(key, value);
                        CustomBeanUtils.copyPropertiesIgnoreNull(bean, o);
                    }

                    for (String setKey : setList) {
                        if (!ObjectUtil.equals(setKey, key)) {
                            recursion(bean.get(setKey), key, value);
                        }
                    }

                }

            }
        } catch (Exception e) {
            // 异常处理
            //System.err.println("Error during JSON conversion: " + e.getMessage());
            return o;  // 返回原始对象
        }

        return o;
    }


    default void setArgumentValue(ProceedingJoinPoint joinPoint, String argName, String value) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(argName)) {
                args[i] = value;
            } else {
                setArgs(args, i, argName, value);
            }
        }
        // 重新设置参数
        // 使用反射修改参数
        try {
            Field field = joinPoint.getClass().getDeclaredField("args");
            field.setAccessible(true);
            field.set(joinPoint, args);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().error("Failed to set argument value: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set argument value", e);
        }
    }

    @Override
    @Around(value = "SysLog()")
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AutoOperation annotation = method.getAnnotation(AutoOperation.class);
        AutoOperationEnum operation = AutoOperationEnum.OTHER;
        if (ObjectUtil.isNotEmpty(annotation)) {
            operation = annotation.operation();
        }
        String operator = getOperator();
        //String operatorName = StrUtil.EMPTY;
        List<String> operatorNameList = CollUtil.newArrayList();
        //将更新人的信息设置到参数中
        switch (operation) {
            case ADD:
                // 新增操作
                String createByName = annotation.createByName();
                operatorNameList.add(createByName);
            case UPDATE:
                // 更新操作
                String updateByName = annotation.updateByName();
                operatorNameList.add(updateByName);
                break;
            case OTHER:
            default:
                break;
        }

        if (CollUtil.isNotEmpty(operatorNameList)) {
            for (String nameByOperator : operatorNameList) {
                // 设置操作人信息
                setArgumentValue(joinPoint, nameByOperator, operator);
            }
        }

        try {
            // 执行原有逻辑
            return AbstractSysLog.super.around(joinPoint);
        } catch (Throwable e) {
            throw e;
        }
    }

    default String getOperator() {
        return null;
    }

}
