package com.yougame.service.dto;


/*
 * 登录请求 DTO
 * 【作用】：接收前端传来的登录参数，并进行校验
 * 【注解说明】：
 *   @NotBlank：字符串不能为 null、空串、纯空格
 *   @Size：限制字符串长度
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
}
