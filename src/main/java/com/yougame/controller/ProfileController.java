package com.yougame.controller;


import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Comment;
import com.yougame.entity.Post;
import com.yougame.service.CommentService;
import com.yougame.service.FavoriteService;
import com.yougame.service.PostService;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final PostService postService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;

    /**
     * 我的帖子
     */

    @GetMapping("/posts")
    public Result<PageVO<Post>> myPosts(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size){
        Long userId = UserContext.getUserId();
        return Result.success(postService.getMyPosts(userId, page, size));
    }

    /**
     * 我的评论
     */
    @GetMapping("comments")
    public Result<PageVO<Comment>> myComment(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        return Result.success(commentService.getMyComments(userId, page, size));

    }

    /**
     * 我的收藏
     */
    @GetMapping("/favorites")
    public Result<PageVO<PostVO>> myFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return Result.success(favoriteService.getMyFavorites(page, size));
    }
}
