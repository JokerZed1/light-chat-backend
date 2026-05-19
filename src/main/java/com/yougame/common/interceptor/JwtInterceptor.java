package com.yougame.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yougame.common.result.Result;
import com.yougame.common.utils.JwtUtil;
import com.yougame.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 放行所有 GET 请求（公开资源）
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            // 即使放行，也尝试从 Token 中解析用户（可选，用于展示个性化内容）
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    UserContext.setUserId(userId);
                    UserContext.setRole(role);
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);
                }
            }
            return true;   // 无论是否携带 Token，GET 请求都放行
        }

        // 2. 非 GET 请求（POST、PUT、DELETE）必须携带有效 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少有效 Token，Method：{}，URL：{}", request.getMethod(), request.getRequestURI());
            sendErrorResponse(response, 401, "未提供有效Token");
            return false;
        }

        String token = authHeader.substring(7).trim();
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 无效或已过期，URL：{}", request.getRequestURI());
            sendErrorResponse(response, 401, "Token无效或已过期");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (userId == null) {
            log.warn("Token 无法提取 userId，URL：{}", request.getRequestURI());
            sendErrorResponse(response, 401, "Token格式错误");
            return false;
        }

        UserContext.setUserId(userId);
        UserContext.setRole(role);
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        log.debug("用户认证成功，userId：{}，role：{}，Method：{}", userId, role, request.getMethod());
        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> errorResult = Result.error(code, message);
        String json = objectMapper.writeValueAsString(errorResult);
        response.getWriter().write(json);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        UserContext.clear();
    }
}