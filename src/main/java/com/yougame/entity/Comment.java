package com.yougame.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;
    private Long userId;
    private Long postId;
    private Long parentId;  // 父评论ID，null为一级评论

    private LocalDateTime createTime;

    // 非数据库字段，用于关联查询时存放用户信息
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String avatar;

    //用于展示帖子标题。
    @TableField(exist = false)
    private String postTitle;

}
