package com.minimalism.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.aviator.*;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.utils.object.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/11/12 下午3:09:40
 * @Description
 */
public interface AbstractAviatorValidAspect extends AbstractSysLog {
    /**
     *
     */
    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.aviator.AviatorValids)"
            + " || @annotation(com.minimalism.aop.aviator.AviatorValid) "
            + " || @annotation(com.minimalism.aop.aviator.AviatorNotBlank) "
            + " || @annotation(com.minimalism.aop.aviator.Aviator) "
    )
    default void SysLog() {
    }

    default void check(ProceedingJoinPoint joinPoint) throws Exception {
        Aviator aviator = getAnnotation(joinPoint, Aviator.class);
        AviatorValids aviatorValids = getAnnotation(joinPoint, AviatorValids.class);
        AviatorValid aviatorValid = getAnnotation(joinPoint, AviatorValid.class);
        AviatorNotBlank aviatorNotBlank = getAnnotation(joinPoint, AviatorNotBlank.class);

        Aviator[] aviators = null;
        AviatorValid[] valids = null;
        AviatorNotBlank[] notBlanks = null;
        List<AviatorValidInfo> validInfos = CollUtil.newArrayList();
        if (ObjectUtils.isNotEmpty(aviator)) {
            aviators = new Aviator[]{aviator};
        } else if (ObjectUtils.isNotEmpty(aviatorValids)) {
            aviators = aviatorValids.aviator();
            valids = aviatorValids.values();
            notBlanks = aviatorValids.notBlanks();
        } else if (ObjectUtils.isNotEmpty(aviatorValid)) {
            valids = new AviatorValid[]{aviatorValid};
        } else if (ObjectUtils.isNotEmpty(aviatorNotBlank)) {
            notBlanks = new AviatorNotBlank[]{aviatorNotBlank};
        }

        if (ObjectUtils.isNotEmpty(aviators)) {
            Arrays.stream(aviators)
                    .map(this::aviatorToValidInfos)
                    .forEach(validInfos::addAll);
            //List<AviatorValidInfo> infoList = aviatorToValidInfos(aviator);
            //validInfos.addAll(infoList);
        }

        if (ObjectUtils.isNotEmpty(notBlanks)) {
            List<AviatorValidInfo> infos = Arrays.stream(notBlanks)
                    .map(this::notBlankToValidInfo)
                    .collect(Collectors.toList());
            validInfos.addAll(infos);
        }

        if (ObjectUtils.isNotEmpty(valids)) {
            List<AviatorValidInfo> infos = Arrays.stream(valids).map(this::validToValidInfo).collect(Collectors.toList());
            validInfos.addAll(infos);
        }

        if (CollUtil.isNotEmpty(validInfos)) {
            Map<String, Object> variables = new LinkedHashMap<>();
            //Map<String, Class> classMaps = new LinkedHashMap<>();

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Parameter[] parameters = methodSignature.getMethod().getParameters();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < parameters.length; i++) {
                Object argValue = args[i];
                String name = parameters[i].getName();
                String argsName = String.format("args[%s]", i);
                argValue = JSONUtil.toJsonStr(argValue, jsonConfig);

                variables.put(argsName, argValue);
                variables.put(name, argValue);
                //classMaps.put(argsName, (Class) parameters[i].getParameterizedType());
            }

            Map<String, Object> variablesMap = ObjectUtils.mapKeyLengthReversed(ObjectUtils.toMap(variables));
            variablesMap = ObjectUtils.blankReplace(variablesMap);
            info("parameters参数：{}", JSONUtil.toJsonStr(parameters, jsonConfig));
            info("variables参数：{}", variables);
            info("variablesMap参数：{}", variablesMap);
            //log.info("classMaps参数：{}", classMaps);
            for (AviatorValidInfo validInfo : validInfos) {
                execute(variablesMap, validInfo);
            }
        }
    }

    default List<AviatorValidInfo> aviatorToValidInfos(Aviator aviator) {
        boolean throwException = aviator.throwException();
        String defaultErrorMessage = aviator.defaultErrorMessage();
        String defaultPrecondition = aviator.defaultPrecondition();
        boolean isStr = aviator.isStr();
        List<String> errorMessages = Arrays.stream(aviator.errorMessages()).collect(Collectors.toList());
        List<String> expressions = Arrays.stream(aviator.expressions()).collect(Collectors.toList());
        List<String> preconditions = Arrays.stream(aviator.preconditions()).collect(Collectors.toList());
        int errorSize = errorMessages.size();
        int okSize;

        if (isStr) {
            List<String> keys = Arrays.stream(aviator.keys()).collect(Collectors.toList());
            okSize = keys.size();
            expressions = keys.stream().map(key -> String.format("%s!=nil&&%s!=''", key, key)).collect(Collectors.toList());
        } else {
            okSize = expressions.size();
        }

        if (okSize > errorSize) {
            for (int i = 0; i < (okSize - errorSize); i++) {
                errorMessages.add(defaultErrorMessage);
            }
        }

        if (okSize > preconditions.size()) {
            for (int i = 0; i < (okSize - preconditions.size()); i++) {
                preconditions.add(defaultPrecondition);
            }
        }

        List<AviatorValidInfo> infoList = CollUtil.newArrayList();

        for (int i = 0; i < okSize; i++) {
            String expression = replaceNullToNil(expressions.get(i));
            String precondition = replaceNullToNil(preconditions.get(i));
            String errorMessage = errorMessages.get(i);
            infoList.add(toAviatorValidInfo(precondition, throwException, expression, errorMessage));
        }

        return infoList;
    }

    /**
     * @param notBlank
     * @return
     */
    default AviatorValidInfo notBlankToValidInfo(AviatorNotBlank notBlank) {
        String errorMessage = notBlank.errorMessage();
        String key = notBlank.key();
        boolean throwException = notBlank.throwException();
        String precondition = notBlank.precondition();

        String expression = String.format("%s!=null&&%s!=''", key, key);
        expression = replaceNullToNil(expression);
        precondition = replaceNullToNil(precondition);
        return toAviatorValidInfo(precondition, throwException, expression, errorMessage);
    }


    /**
     * @param valid
     * @return
     */
    default AviatorValidInfo validToValidInfo(AviatorValid valid) {
        boolean throwException = valid.throwException();
        String expression = valid.expression();
        String errorMessage = valid.errorMessage();
        String precondition = valid.precondition();
        // null AviatorEvaluator 无法识别  null==> nil
        expression = replaceNullToNil(expression);
        precondition = replaceNullToNil(precondition);
        return toAviatorValidInfo(precondition, throwException, expression, errorMessage);
    }

    default AviatorValidInfo toAviatorValidInfo(String precondition, boolean throwException, String expression, String errorMessage) {
        return new AviatorValidInfo()
                .setPreconditionExpression(precondition)
                .setThrowException(throwException)
                .setExpression(expression)
                .setErrorMessage(errorMessage);
    }

    /**
     * @param variablesMap
     * @param validInfo
     * @throws Exception
     */
    default void execute(Map<String, Object> variablesMap, AviatorValidInfo validInfo) throws Exception {
        Boolean validFail = null;
        boolean expressionThrowException = validInfo.isThrowException();
        String expression = validInfo.getExpression();
        String errorMessage = validInfo.getErrorMessage();
        String preconditionExpression = validInfo.getPreconditionExpression();

        /**
         * 前置条件
         */
        Boolean execute = (Boolean) AviatorEvaluator.execute(preconditionExpression, variablesMap);
        if (!ObjectUtils.defaultIfEmpty(execute,true)) {
            return;
        }
        // 使用 Aviator 进行表达式校验
        try {
            info("aviator表达式：{}", expression);
            Boolean bool = !(Boolean) AviatorEvaluator.execute(expression, variablesMap);
            validFail = ObjectUtils.defaultIfEmpty(bool, true);
        } catch (Exception e) {
            error("aviator表达式解析异常：{}", e.getMessage());
            if (expressionThrowException) {
                throw new Exception("aviator表达式解析异常:" + e.getMessage());
            }
        }

        validFail = ObjectUtils.defaultIfEmpty(validFail, true);

        if (validFail) {
            error("aviator表达式校验失败：{}", errorMessage);
            throw new GlobalCustomException(errorMessage);
        }
    }

    default String expressionReplace(String expression, Map<String, Object> variablesMap) {
        for (Map.Entry<String, Object> entry : variablesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (ObjectUtils.isNotEmpty(value)) {
                expression = expression.replace(key, JSONUtil.toJsonStr(value, jsonConfig));
            }
        }
        return expression;
    }

    @NotNull
    default String replaceNullToNil(String expression) {
        return expression.replace("null", "nil");
    }
}
