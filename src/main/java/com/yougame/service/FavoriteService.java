package com.yougame.service;


import com.yougame.vo.FavoriteStatusVO;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostVO;

public interface FavoriteService {
    FavoriteStatusVO toggleFavorite(Long userId, Long postId);

    // 1. 获取当前用户的收藏（隐式用户身份）
    PageVO<PostVO> getMyFavorites(int page, int size);

}