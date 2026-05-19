package com.yougame.controller;




/*
 * 认证控制器（登录、注册）
 * 【设计说明】：将认证相关接口独立出来，符合单一职责原则
 */

import com.yougame.common.result.Result;
import com.yougame.common.utils.JwtUtil;
import com.yougame.entity.User;
import com.yougame.service.UserService;
import com.yougame.service.dto.LoginDTO;
import com.yougame.service.dto.RegisterDTO;
import com.yougame.vo.LoginVO;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /*
     * 用户注册
     * 【注解说明】：@Valid 触发 DTO 中的校验注解
     */

    @PostMapping("register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("注册请求：{}", registerDTO.getUsername());

        // DTO 转 Entity
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setNickname(registerDTO.getNickname());

        userService.register(user);
        return Result.success("注册成功");
    }

    /*
     * 用户登录
     * @return 包含 Token 和用户信息的 LoginVO
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("登录请求：{}", loginDTO.getUsername());

        // 1. 验证用户名密码
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 2. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 3. 构建返回对象
        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();

        log.info("用户 {} 登陆成功", user.getUsername());
        return Result.success("登录成功",loginVO);
    }
}
