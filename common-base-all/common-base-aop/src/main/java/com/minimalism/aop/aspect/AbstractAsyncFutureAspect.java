package com.minimalism.aop.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.aop.AbstractAop;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.AopConstants;
import com.minimalism.aop.async.AsyncFuture;
import com.minimalism.utils.object.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author yan
 * @Date 2024/11/3 上午10:48:52
 * @Description
 */
public interface AbstractAsyncFutureAspect extends AbstractAop {
    @Override
    default int getOrder() {
        return AopConstants.AsyncOrder;
    }

    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.async.AsyncFuture)")
    default void Aop() {
    }

    @Nullable
    default <T> T asyncAround(ProceedingJoinPoint joinPoint, AtomicReference<Throwable> throwable, Class<T> returnTypeClass) {
        T t = null;
        Object futureObj = null;
        try {
            futureObj = AbstractAop.super.around(joinPoint);
        } catch (Throwable e) {
            throwable.set(e);
        } finally {
            if (!ObjectUtils.equal(Void.class, returnTypeClass)) {
                //T newInstance = returnTypeClass.newInstance();
            }
            return t;
        }
    }

    @Nullable
    default Object asyncAround(ProceedingJoinPoint joinPoint, AtomicReference<Throwable> throwable) {
        Object futureObj = null;
        try {
            futureObj = AbstractAop.super.around(joinPoint);
        } catch (Throwable e) {
            throwable.set(e);
        } finally {
            return futureObj;
        }
    }

    /**
     * 异步执行
     * @param joinPoint
     * @param throwable
     * @param executorBean
     * @return
     */
    default CompletableFuture<Object> supplyAsyncCompletableFuture(ProceedingJoinPoint joinPoint, AtomicReference<Throwable> throwable, Executor executorBean) {
        return ObjectUtils.isEmpty(executorBean) ?
                CompletableFuture.supplyAsync(() -> asyncAround(joinPoint, throwable)) :
                CompletableFuture.supplyAsync((() -> asyncAround(joinPoint, throwable)), executorBean);
    }

    /**
     * 异步执行
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    default Object async(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        AsyncFuture asyncFuture = getAnnotation(joinPoint, AsyncFuture.class);

        Object o;
        boolean asyncUse = ObjectUtil.isNotEmpty(asyncFuture) && asyncFuture.enable();
        if (asyncUse) {
            CompletableFuture<Object> future;
            Executor executorBean = null;
            if (asyncFuture.useExecutor()) {
                Class<? extends Executor> aClass = asyncFuture.executorClass();
                String beanName = asyncFuture.executorBeanName();
                try {
                    // 获取bean
                    executorBean = StrUtil.isBlank(beanName) ? SpringUtil.getBean(aClass) : SpringUtil.getBean(beanName, aClass);
                } catch (Exception e) {
                    // 获取bean fail to next 反射创建 Executor
                    try {
                        executorBean = aClass.newInstance();
                    } catch (Exception e1) {
                        getLogger().error("create executorBean fail e:{}", e1);
                    }
                }
                future = CompletableFuture.supplyAsync((() -> asyncAround(joinPoint, throwable)), executorBean);
            } else {
                future = CompletableFuture.supplyAsync(() -> asyncAround(joinPoint, throwable));
            }
            future.join();
            o = future.get();
        } else {
            o = AbstractAop.super.around(joinPoint);
        }

        if (ObjectUtil.isNotEmpty(throwable.get())) {
            throw throwable.get();
        }

        return o;
    }

    /**
     * 异步执行
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    default Object aroundAsync(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        AsyncFuture asyncFuture = getAnnotation(joinPoint, AsyncFuture.class);

        Object o;
        boolean asyncUse = ObjectUtil.isNotEmpty(asyncFuture) && asyncFuture.enable();
        if (asyncUse) {
            CompletableFuture<Object> future;
            Executor executorBean = null;
            if (asyncFuture.useExecutor()) {
                Class<? extends Executor> aClass = asyncFuture.executorClass();
                String beanName = asyncFuture.executorBeanName();
                try {
                    // 获取bean
                    executorBean = StrUtil.isBlank(beanName) ? SpringUtil.getBean(aClass) : SpringUtil.getBean(beanName, aClass);
                } catch (Exception e) {
                    // 获取bean fail to next 反射创建 Executor
                    try {
                        executorBean = aClass.newInstance();
                    } catch (Exception e1) {
                        getLogger().error("create executorBean fail e:{}", e1);
                    }
                }
            }
            future = supplyAsyncCompletableFuture(joinPoint, throwable, executorBean);
            future.join();
            o = future.get();
        } else {
            o = AbstractAop.super.around(joinPoint);
        }

        if (ObjectUtil.isNotEmpty(throwable.get())) {
            throw throwable.get();
        }

        return o;
    }

    @Override
    @Around(value = "Aop()")
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return aroundAsync(joinPoint);
    }


}
