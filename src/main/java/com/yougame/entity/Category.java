package com.yougame.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String icon;
    private Integer sortOrder;  // 注意：数据库字段是 sort_order，MyBatis-Plus 自动映射
}
