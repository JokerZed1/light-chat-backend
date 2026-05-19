package com.yougame.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostVO {
    private Long id;
    private String title;
    private String content;      // 列表页可能只展示摘要，但也可以返回全文由前端截断
    private Long userId;
    private String authorNickname;
    private String authorAvatar;
    private Integer categoryId;
    private String categoryName;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}