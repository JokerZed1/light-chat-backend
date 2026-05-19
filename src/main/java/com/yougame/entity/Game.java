package com.yougame.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

@Data
@TableName("game")
public class Game {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String icon;
    private Integer sortOrder;

}
