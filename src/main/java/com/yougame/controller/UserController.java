package com.yougame.controller;


import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.User;
import com.yougame.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
 * 用户控制器
 * 【所属层级】：Controller 表现层
 * 【注解说明】：
 *   @RestController ：组合注解，等于 @Controller + @ResponseBody
 *   @RequestMapping("/api/user") ：该 Controller 下所有接口路径前缀
 *   @RequiredArgsConstructor ：构造器注入
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /*
     * 用户注册接口
     * 请求方式：POST
     * 请求路径：/api/user/register
     * 请求体：JSON 格式的用户信息
     *
     * 测试用例：
     * {
     *   "username": "zhangsan",
     *   "password": "123456",
     *   "nickname": "张三"
     * }
     */

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        log.info("接收到注册请求，用户名：{}", user.getUsername());
        userService.register(user);
        return Result.success("注册成功");
    }

        /*
         * 根据用户名查询用户信息（测试用，后续会加 JWT 拦截）
         * 请求方式：GET
         * 请求路径：/api/user/{username}
         */

    @GetMapping("/{username}")
    public Result<User> getUserByUsername(@PathVariable String username){
        User user = userService.findByUsername(username);
        if (user == null) {
            return Result.error(4004, "用户不存在");
        }
        // 出于安全考虑，不返回密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> userList = userService.findAllNormalUsers();
        userList.forEach(user -> user.setPassword(null));  //密码不返回
        return Result.success(userList);

    }

    /**
     * 获取当前登录用户信息（需要登录）
     * 请求头需携带：Authorization: Bearer {token}
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            userId = (Long) request.getAttribute("userId");
        }
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }
}
