package com.yougame.service;

import com.yougame.vo.LikeStatusVO;

public interface LikeService {

    /*
     * 点赞/取消点赞切换，返回最新状态
     */
    LikeStatusVO toggleLike(Long userId, Long postId);



}
