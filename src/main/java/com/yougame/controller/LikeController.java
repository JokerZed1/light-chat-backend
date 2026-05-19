package com.yougame.controller;


import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.service.LikeService;
import com.yougame.vo.LikeStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /*
     * 点赞/取消点赞切换
     */

    @PostMapping
    public Result<LikeStatusVO> toggleLike(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        LikeStatusVO status = likeService.toggleLike(userId, postId);
        return Result.success(status);
    }
}
