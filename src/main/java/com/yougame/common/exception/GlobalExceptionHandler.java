package com.yougame.common.exception;


import com.yougame.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.MalformedURLException;
import java.rmi.MarshalException;

/*
 * 全局异常处理器
 * 【注解说明】：
 *   @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   表示这是一个全局增强器，所有返回值都会自动转为 JSON
 *
 * 【设计思想】：AOP（面向切面编程）思想，将所有 Controller 的异常横切关注点集中处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /*
     * 处理自定义业务异常
     * 【注解说明】：
     *   @ExceptionHandler(BusinessException.class) 表示只捕获 BusinessException 及其子类
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /*
     * 处理参数校验异常（@Valid 校验失败时抛出）
     * 【异常类型】：
     *   MethodArgumentNotValidException：JSON 请求体参数校验失败
     *   BindException：表单参数校验失败
     */

    @ExceptionHandler({MalformedURLException.class, BindException.class})
    public Result<Void> handleValidationException(Exception e){
        String errorMessage;

        if (e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            // 获取第一个校验失败的字段和错误信息
            FieldError fieldError = ex.getBindingResult().getFieldError();
            errorMessage = fieldError != null
                    ? fieldError.getField() + ":" + fieldError.getDefaultMessage():"参数绑定无效";
        } else {
            errorMessage = "参数校验失败";
        }

        log.warn("参数校验异常：{}", errorMessage);
        return Result.error(400, errorMessage);
    }

    /*
     * 处理数据库唯一键冲突异常（如用户名重复）
     * 这种异常通常是底层抛出的，我们转换成友好的业务异常信息
     */

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e){
        log.warn("数据库唯一键冲突：{}", e.getMessage());
        // 根据异常信息判断是哪个字段冲突
        String message = e.getMessage();
        if (message != null && message.contains("username")) {
            return Result.error(4001, "用户名已存在");

            }
        return Result.error(4000, "数据已存在，请勿重复提交");
        }



    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handeNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(5001, "数据异常，请联系管理员");
    }


    /*
     * 处理未知异常（兜底）
     * 【重要】：必须打印完整堆栈，方便排查线上问题
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        log.error("系统未知异常", e); // 打印完整堆栈到日志文件
        return Result.error(500, "服务器内部错误，请稍后重试");
    }




}







