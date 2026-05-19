package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Post;
import com.yougame.service.PostService;
import com.yougame.service.dto.PostPublishDTO;
import com.yougame.service.dto.PostQueryDTO;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 * 【权限说明】：
 *   - GET 请求公开访问
 *   - POST/PUT/DELETE 需要登录（拦截器已处理）
 *   - 业务层做具体资源权限校验
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * 发布帖子
     */
    @PostMapping
    public Result<Post> publishPost(@Valid @RequestBody PostPublishDTO dto) {
        Long userId = UserContext.getUserId();
        Post post = postService.publishPost(dto, userId);
        return Result.success("发布成功", post);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(@PathVariable Long id) {
        Long currentUserId = UserContext.getUserId();
        PostDetailVO vo = postService.getPostDetailVO(id, currentUserId);
        return Result.success(vo);
    }

    /**
     * 获取帖子列表（分页、筛选、排序）
     */
    @GetMapping("/list")
    public Result<PageVO<Post>> listPosts(@Valid PostQueryDTO queryDTO) {
        PageVO<Post> page = postService.listPosts(queryDTO);
        return Result.success(page);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id,
                                   @Valid @RequestBody PostPublishDTO dto) {
        Long userId = UserContext.getUserId();
        Post post = postService.updatePost(id, dto, userId);
        return Result.success("更新成功", post);
    }

    /**
     * 删除帖子（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        log.info("删除帖子请求，帖子ID：{}，当前用户ID：{}", id, userId);
        postService.deletePost(id, userId);
        return Result.success();
    }

    /**
     * 我的帖子（分页）
     */
    @GetMapping("/my")
    public Result<PageVO<Post>> getMyPosts(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        // 调用正确的方法名 getMyPosts
        PageVO<Post> pageVO = postService.getMyPosts(userId, page, size);
        return Result.success(pageVO);
    }
}