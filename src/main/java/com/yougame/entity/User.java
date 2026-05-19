package com.yougame.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * 用户实体类
 * 【所属层级】：Entity 层，与数据库表 user 一一对应
 * 【注解说明】：
 *   @Data ：Lombok 注解，编译时自动生成 getter/setter/toString 等方法
 *   @TableName ：MyBatis-Plus 注解，指定数据库表名
 *   @TableId ：指定主键字段及生成策略（数据库自增）
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private String username;      // 用户名，唯一
    private String password;      // 密码，存储加密后的密文
    private String nickname;      // 昵称
    private String avatar;        // 头像URL
    private String bio;           // 个人简介

    private String role;          // 角色：USER / ADMIN，默认 USER
    private Integer status;       // 状态：1正常 0禁用

    private LocalDateTime createTime;  // 创建时间

}
