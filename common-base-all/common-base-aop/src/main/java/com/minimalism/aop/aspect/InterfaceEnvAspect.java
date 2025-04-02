package com.minimalism.aop.aspect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@Getter
public class InterfaceEnvAspect implements AbstractInterfaceEnvAspect {
}
