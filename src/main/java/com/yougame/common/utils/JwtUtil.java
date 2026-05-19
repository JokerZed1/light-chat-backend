package com.yougame.common.utils;


/*
 * JWT 工具类
 * 【作用】：生成 Token、解析 Token、校验 Token 有效性
 * 【设计模式】：单例（由 Spring 管理，默认单例）
 */


import com.yougame.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Component
public class JwtUtil {

    // 从配置文件读取密钥和过期时间
    @Value("${jwt.secret:yougame-jwt-secret-key-must-be-at-least-256-bits-long}")
    private String secret;

    // 默认 24 小时（毫秒）
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /*
     * 生成 SecretKey 对象
     * 要求密钥长度至少 256 位（32 字节），否则 JJWT 会抛异常
     */

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
     * 生成 JWT Token
     * @param userId   用户ID
     * @param username 用户名
     * @return Token 字符串
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", "USER"); //默认角色

        Date now = new Date();
        Date expiryData = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)             // 设置自定义载荷
                .subject(username)          // 主题（通常是用户名）
                .issuedAt(now)              // 签发时间
                .expiration(expiryData)     // 过期时间
                .signWith(getSigningKey())  // 签名算法和密钥
                .compact();                 // 构建字符串
    }

    // ====================== 新增：带角色的Token（企业必须） ======================
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role",role); // ✅ 存储角色

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * 从 Token 中解析 Claims（载荷）
     * @param token JWT Token
     * @return Claims 对象，解析失败返回 null
     */
    public Claims parseToken(String token){
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * 校验 Token 是否有效
     * @param token JWT Token
     * @return true=有效，false=无效或已过期
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        // 检查是否过期
        return !claims.getExpiration().before(new Date());
    }

    /**
     * 从 Token 中获取用户 ID（企业级安全转换）
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object userIdObj = claims.get("userId");
        if (userIdObj == null) {
            return null;
        }
        // 统一转为 Long
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /*
     * 从 Token 中获取用户名
     */

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    // ====================== 新增：获取角色（企业必须） ======================
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.get("role", String.class);
    }


    /*
     * Token 续期
     * @param oldToken 旧令牌
     * @return 新令牌
     */

    public String refreshToken(String oldToken) {
        // 1.校验旧token有效性
        if (!validateToken(oldToken)){
            throw new BusinessException(4001, "令牌无效或已过期，无法续期");
        }
        // 2.解析用户信息
        Long userId = getUserIdFromToken(oldToken);
        String username = getUsernameFromToken(oldToken);
        String role = getRoleFromToken(oldToken);

        // 3.颁发新token（全新过期时间）

        return generateToken(userId,username,role);




    }



}
