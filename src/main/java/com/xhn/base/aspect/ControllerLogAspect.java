package com.xhn.base.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller logging aspect.
 */
@Aspect
@Component
public class ControllerLogAspect {

    private final ObjectMapper objectMapper;

    public ControllerLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("execution(public * com.xhn..controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger log = LoggerFactory.getLogger(targetClass);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String params = formatParams(signature.getParameterNames(), joinPoint.getArgs());

        log.info(">>> {} params: {}", methodName, params);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        if (result instanceof Mono<?> mono) {
            return mono.doOnSuccess(data -> {
                long cost = System.currentTimeMillis() - startTime;
                log.info("<<< {} cost: {}ms result: {}", methodName, cost, toJson(data));
            }).doOnError(e -> {
                long cost = System.currentTimeMillis() - startTime;
                log.error("<<< {} cost: {}ms error: {}", methodName, cost, e.getMessage());
            });
        }

        if (result instanceof Flux<?> flux) {
            AtomicLong count = new AtomicLong();
            return flux.doOnNext(item -> count.incrementAndGet())
                    .doOnComplete(() -> {
                        long cost = System.currentTimeMillis() - startTime;
                        log.info("<<< {} cost: {}ms emitted: {}", methodName, cost, count.get());
                    })
                    .doOnError(e -> {
                        long cost = System.currentTimeMillis() - startTime;
                        log.error("<<< {} cost: {}ms error after {} items: {}", methodName, cost, count.get(), e.getMessage());
                    });
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("<<< {} cost: {}ms result: {}", methodName, cost, toJson(result));
        return result;
    }

    private String formatParams(String[] paramNames, Object[] args) {
        if (paramNames == null || paramNames.length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(paramNames[i]).append("=").append(toJson(args[i]));
        }
        sb.append("}");
        return sb.toString();
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            String json = objectMapper.writeValueAsString(obj);
            if (json.length() > 1024) {
                return json.substring(0, 1024) + "...(truncated)";
            }
            return json;
        } catch (Exception e) {
            return obj.toString();
        }
    }
}
