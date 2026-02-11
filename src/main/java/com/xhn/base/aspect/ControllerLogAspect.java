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

import java.util.Arrays;

/**
 * Controller 层统一日志切面
 * <p>
 * 自动记录：方法名、入参、返回值、执行耗时
 * 通过 LoggerFactory.getLogger(targetClass) 动态获取 logger，
 * controller 类无需添加 @Slf4j 注解
 *
 * @author xhn
 */
@Aspect
@Component
public class ControllerLogAspect {

    private final ObjectMapper objectMapper;

    public ControllerLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 切入所有 controller 包下的 public 方法
     */
    @Pointcut("execution(public * com.xhn..controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 动态获取目标类的 logger
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger log = LoggerFactory.getLogger(targetClass);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String params = formatParams(signature.getParameterNames(), joinPoint.getArgs());

        log.info(">>> {} 入参: {}", methodName, params);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        // 处理 Mono 返回值
        if (result instanceof Mono<?> mono) {
            return mono.doOnSuccess(data -> {
                long cost = System.currentTimeMillis() - startTime;
                log.info("<<< {} 耗时: {}ms 返回: {}", methodName, cost, toJson(data));
            }).doOnError(e -> {
                long cost = System.currentTimeMillis() - startTime;
                log.error("<<< {} 耗时: {}ms 异常: {}", methodName, cost, e.getMessage());
            });
        }

        // 处理 Flux 返回值
        if (result instanceof Flux<?> flux) {
            return flux.collectList().flatMapMany(list -> {
                long cost = System.currentTimeMillis() - startTime;
                log.info("<<< {} 耗时: {}ms 返回元素数: {}", methodName, cost, list.size());
                return Flux.fromIterable(list);
            }).doOnError(e -> {
                long cost = System.currentTimeMillis() - startTime;
                log.error("<<< {} 耗时: {}ms 异常: {}", methodName, cost, e.getMessage());
            });
        }

        // 普通同步返回值
        long cost = System.currentTimeMillis() - startTime;
        log.info("<<< {} 耗时: {}ms 返回: {}", methodName, cost, toJson(result));
        return result;
    }

    private String formatParams(String[] paramNames, Object[] args) {
        if (paramNames == null || paramNames.length == 0) {
            return "无";
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(paramNames[i]).append("=").append(toJson(args[i]));
        }
        sb.append("}");
        return sb.toString();
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        try {
            String json = objectMapper.writeValueAsString(obj);
            // 截断过长的日志
            if (json.length() > 1024) {
                return json.substring(0, 1024) + "...(truncated)";
            }
            return json;
        } catch (Exception e) {
            return obj.toString();
        }
    }
}
