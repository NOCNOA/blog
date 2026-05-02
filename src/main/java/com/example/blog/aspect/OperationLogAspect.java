package com.example.blog.aspect;

import com.example.blog.annotation.OperationLog;
import com.example.blog.entity.OperationLogEntity;
import com.example.blog.entity.User;
import com.example.blog.mapper.OperationLogMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.util.LoginUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogMapper operationLogMapper, UserMapper userMapper, ObjectMapper objectMapper) {
        this.operationLogMapper = operationLogMapper;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.example.blog.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        OperationLogEntity logEntity = buildLogEntity(operationLog, joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            logEntity.setSuccess(1);
            safeInsert(logEntity);
            return result;
        } catch (Throwable ex) {
            logEntity.setSuccess(0);
            logEntity.setErrorMessage(truncate(ex.getMessage(), 500));
            safeInsert(logEntity);
            throw ex;
        }
    }

    private void safeInsert(OperationLogEntity logEntity) {
        try {
            operationLogMapper.insert(logEntity);
        } catch (DataAccessException ex) {
            // Do not block the main request when the log table is missing or temporarily unavailable.
        }
    }

    private OperationLogEntity buildLogEntity(OperationLog operationLog, Object[] args) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.setModule(operationLog.module());
        entity.setOperation(operationLog.operation());
        entity.setParams(toJson(sanitizeArgs(args)));

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            entity.setRequestMethod(request.getMethod());
            entity.setRequestUri(request.getRequestURI());
            entity.setIp(resolveIp(request));
        }

        Long userId = LoginUserContext.getUserId();
        entity.setOperatorId(userId);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            entity.setOperatorName(user == null ? null : user.getNickname());
        }
        return entity;
    }

    private List<Object> sanitizeArgs(Object[] args) {
        List<Object> values = new ArrayList<>();
        if (args == null) {
            return values;
        }
        for (Object arg : args) {
            if (arg == null) {
                values.add(null);
            } else if (arg instanceof MultipartFile file) {
                values.add("[file:" + file.getOriginalFilename() + "]");
            } else {
                String className = arg.getClass().getSimpleName().toLowerCase();
                if (className.contains("login") || className.contains("password")) {
                    values.add("[sensitive]");
                } else {
                    values.add(arg);
                }
            }
        }
        return values;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "[unserializable]";
        }
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
