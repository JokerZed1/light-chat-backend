package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Comment;
import com.yougame.service.CommentService;
import com.yougame.vo.PageVO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 发表评论（需要登录）
     */
    @PostMapping
    public Result<Comment> createComment(@RequestParam Long postId,
                                         @RequestParam @NotBlank String content) {
        Long userId = UserContext.getUserId();
        Comment comment = commentService.createComment(userId, postId, content);
        return Result.success("评论成功", comment);   // 原为“评论失败”，已修正
    }

    /**
     * 获取帖子的评论列表（公开）
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public Result<Comment> replyComment(@RequestParam Long postId,
                                        @RequestParam Long parentId,
                                        @RequestParam @NotBlank String content) {
        Long userId = UserContext.getUserId();
        Comment comment = commentService.replyComment(userId, postId, parentId, content);
        return Result.success("回复成功", comment);    // 原为“恢复成功”，已修正
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id){
        Long userId = UserContext.getUserId();
        commentService.deleteComment(id, userId);
        return Result.success();
    }

    /**
     * 我的评论（分页）
     */
    @GetMapping("/my")
    public Result<PageVO<Comment>> getMyComments(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        // 调用正确的方法：getMyComments
        PageVO<Comment> pageVO = commentService.getMyComments(userId, page, size);
        return Result.success(pageVO);
    }
}
