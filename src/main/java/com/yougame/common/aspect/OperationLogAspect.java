package com.yougame.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yougame.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.yougame.controller..*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String userId = UserContext.getUserId() != null ? UserContext.getUserId().toString() : "GUEST";
        String args = Arrays.toString(joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[{}] {} {} | User:{} | Args:{} | Time:{}ms | Status:SUCCESS",
                    method, uri, joinPoint.getSignature().toShortString(), userId, args, elapsed);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[{}] {} {} | User:{} | Args:{} | Time:{}ms | Status:ERROR | Error:{}",
                    method, uri, joinPoint.getSignature().toShortString(), userId, args, elapsed, e.getMessage());
            throw e;
        }
    }
}