package com.yougame.service.dto;


import lombok.Data;

import java.time.LocalDateTime;

/*
 * 专门为“我的评论”功能设计的返回对象，
 * 包含评论内容、所属帖子标题等字段，不关联用户信息
 */
@Data
public class CommentDTO {
     private Long id;
     private String content;
     private Long postId;
     private String postTitle; //关联帖子的标题
     private LocalDateTime createTime;
}
