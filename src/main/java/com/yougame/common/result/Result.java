package com.yougame.common.result;


import lombok.Data;

import java.time.Instant;


/*
 * 统一响应结果封装类
 * 【所属层级】：common 公共模块
 * 【作用】：规范所有 Controller 返回给前端的数据格式
 */
@Data
public class Result<T> {

    private Boolean success; // 是否成功
    private Integer code;    // 状态码：200成功，其他为错误码
    private String message;  // 提示信息
    private T data;   // 返回数据，泛型保证类型安全
    private Long timestamp;  // 时间戳（毫秒）

    // 私有构造器，强制使用静态工厂方法创建对象
    private Result(){
        this.timestamp = Instant.now().toEpochMilli();
    }

    // ==================== 成功响应静态方法 ====================

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    // ==================== 失败响应静态方法 ====================

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error(String message){
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    // 快捷方法：判断是否成功（也可以直接用 success 字段，这个方法主要是后端内部使用）
    public boolean isSuccess(){
        /*
        * 后端内部判断是否成功
        * 防止空指针
        * 前端直接用 res.success 即可
         */
        return this.success != null && this.success;
    }

}
