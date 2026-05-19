package com.yougame.vo;

import com.yougame.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailVO {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String authorNickname;
    private String authorAvatar;
    private Integer categoryId;
    private String categoryName;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;      // 当前用户是否已点赞
    private Boolean isFavorited;  // 当前用户是否已收藏
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Comment> comments;  // 一级评论列表
}