package com.yougame.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteStatusVO {
    private Boolean isFavorited;
    private Integer favoriteCount;   // 收藏总数，可选
}
