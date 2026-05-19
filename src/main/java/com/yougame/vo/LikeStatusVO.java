package com.yougame.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeStatusVO {
    private Boolean isLiked;
    private Integer likeCount;
}