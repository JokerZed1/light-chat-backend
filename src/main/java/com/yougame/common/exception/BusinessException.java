package com.yougame.common.exception;


/*
 * 自定义业务异常类
 * 【设计目的】：将业务校验失败的情况统一用异常抛出，由全局异常处理器捕获
 * 【继承说明】：继承 RuntimeException，无需显式 try-catch
 * 【使用场景】：
 *   - 用户名已存在：throw new BusinessException(4001, "用户名已存在");
 *   - 密码错误：throw new BusinessException(4002, "用户名或密码错误");
 *   - 权限不足：throw new BusinessException(4003, "无权限访问");
 */

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final Integer code;  //业务错误码

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message){
        super(message);
        this.code = 500;
    }

}
