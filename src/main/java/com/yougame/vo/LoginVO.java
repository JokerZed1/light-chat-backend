package com.yougame.vo;


/*
 * 登录成功返回给前端的视图对象
 * 【作用】：只返回前端需要的信息，不暴露敏感字段
 */

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {

    private String token;         // JWT Token
    private Long userId;          // 用户ID
    private String username;      // 用户名
    private String nickname;      // 昵称
    private String avatar;        // 头像
    private String role;          // 角色
}
