package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.service.FavoriteService;
import com.yougame.vo.FavoriteStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/favorite")
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public Result<FavoriteStatusVO> toggleFavorite(@PathVariable Long postId) {
        Long userId = UserContext.getUserId();
        FavoriteStatusVO status = favoriteService.toggleFavorite(userId, postId);
        return Result.success(status);
    }
}