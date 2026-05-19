package com.yougame.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;              // 帖子标题
    private String content;            // 帖子内容（富文本/Markdown）
    private Long userId;               // 发布帖子的用户ID（对应用 user 表的 id）
    private Integer gameId;            // 新增：关联 game 表
    private Integer categoryId;        // 分类ID（对应 category 表的 id）
    private Integer viewCount;         // 浏览量 / 查看次数
    private Integer likeCount;         // 点赞数量
    private Integer commentCount;      // 评论数量
    private Integer status;            // 1正常 0删除
    private LocalDateTime createTime;  // 创建时间（发布时间）
    private LocalDateTime updateTime;  // 更新时间（最后修改时间）

    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private String username;        // 作者用户名
    @TableField(exist = false)
    private String nickname;        // 作者昵称
    @TableField(exist = false)
    private String avatar;          // 作者头像
    @TableField(exist = false)
    private String categoryName;   // 帖子类型名称
    @TableField(exist = false)
    private String gameName;       // 游戏名称

    @TableField(exist = false)
    private String authorNickname;
    @TableField(exist = false)
    private String authorAvatar;

}
