package com.yougame.service.impl;

import com.yougame.common.exception.BusinessException;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Comment;
import com.yougame.entity.Post;
import com.yougame.entity.User;
import com.yougame.mapper.FavoriteMapper;
import com.yougame.mapper.LikeMapper;
import com.yougame.mapper.PostMapper;
import com.yougame.mapper.UserMapper;
import com.yougame.service.CommentService;
import com.yougame.service.PostService;
import com.yougame.service.dto.PostPublishDTO;
import com.yougame.service.dto.PostQueryDTO;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final LikeMapper likeMapper;
    private final FavoriteMapper favoriteMapper;
    private final CommentService commentService;

    @Override
    @Transactional
    public Post publishPost(PostPublishDTO dto, Long userId) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategoryId(dto.getCategoryId());
        post.setUserId(userId);
        post.setStatus(1);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setGameId(dto.getGameId());

        int rows = postMapper.insertPost(post);
        if (rows <= 0) {
            throw new BusinessException(5001, "发布帖子失败，请稍后重试");
        }
        log.info("用户 {} 发布帖子成功，帖子ID：{}", userId, post.getId());
        return post;
    }

    @Override
    @Transactional
    public Post getPostDetail(Long id) {
        Post post = postMapper.selectPostDetailById(id);
        if (post == null) {
            throw new BusinessException(4004, "帖子不存在或已被删除");
        }
        postMapper.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        return post;
    }

    @Override
    public PageVO<Post> listPosts(PostQueryDTO queryDTO) {
        int page = queryDTO.getPage();
        int size = queryDTO.getSize();
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 10;
        int offset = (page - 1) * size;

        List<Post> posts = postMapper.selectPostsWithPage(
                queryDTO.getGameId(),
                queryDTO.getCategoryId(),
                queryDTO.getOrderBy(),
                offset,
                size
        );
        int total = postMapper.countPosts(
                queryDTO.getGameId(),
                queryDTO.getCategoryId()
        );
        return new PageVO<>(posts, (long) total, page, size);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, PostPublishDTO dto, Long userId) {
        // 1. 查询帖子（仅基本信息）
        Post exist = postMapper.selectPostById(id);
        if (exist == null) {
            throw new BusinessException(4004, "帖子不存在或已被删除");
        }

        // 2. 获取当前用户完整信息（包含角色）
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException(4004, "用户不存在");
        }

        // 3. 权限判断：作者本人 或 管理员
        boolean isAuthor = Objects.equals(exist.getUserId(), userId);
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        // 防御性日志
        log.debug("帖子作者ID: {}, 当前用户ID: {}, 当前用户角色: {}, isAuthor: {}, isAdmin: {}",
                exist.getUserId(), userId, currentUser.getRole(), isAuthor, isAdmin);

        if (!isAuthor && !isAdmin) {
            log.warn("用户 {} 试图编辑他人帖子，帖子作者：{}，用户角色：{}", userId, exist.getUserId(), currentUser.getRole());
            throw new BusinessException(4003, "无权编辑他人帖子");
        }
        // 4. 执行更新
        Post update = new Post();
        update.setId(id);
        update.setTitle(dto.getTitle());
        update.setContent(dto.getContent());
        update.setCategoryId(dto.getCategoryId());

        int rows = postMapper.updatePost(update);
        if (rows <= 0) {
            throw new BusinessException(5001, "更新帖子失败");
        }

        log.info("用户 {} 更新帖子成功，帖子ID：{}，操作角色：{}", userId, id, currentUser.getRole());
        return postMapper.selectPostDetailById(id);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long userId) {
        // 1. 查询帖子
        Post exist = postMapper.selectPostById(id);
        if (exist == null) {
            throw new BusinessException(4004, "帖子不存在或已被删除");
        }

        // 2. 查询当前用户信息（获取角色）
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new BusinessException(4004, "用户不存在");
        }

        // 3. 权限校验（作者本人或管理员）
        boolean isAuthor = Objects.equals(exist.getUserId(), userId);
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        if (!isAuthor && !isAdmin) {
            throw new BusinessException(4003, "无权删除此帖子");
        }

        // 4. 执行逻辑删除
        int rows = postMapper.deletePostById(id);
        if (rows <= 0) {
            throw new BusinessException(5001, "删除帖子失败");
        }
        log.info("用户 {} 删除帖子成功，帖子ID：{}，角色：{}", userId, id, currentUser.getRole());
    }

    @Override
    public PostDetailVO getPostDetailVO(Long postId, Long currentUserId) {

        // 1. 查询帖子详情（已过滤 status=1）
        Post post = postMapper.selectPostDetailById(postId);
        if (post == null) {
            throw new BusinessException(4004, "帖子不存在或已被删除");
        }
        // 2. 浏览量自增（已做）
        postMapper.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);

        // 3. 组装 VO
        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setUserId(post.getUserId());
        // post 实体中是否有作者昵称？我们在 XML 中映射了 nickname 到 Post 的属性上，你需要检查实体是否有对应字段。
        // 若实体中有 authorNickname 字段，直接 get；否则需要在 XML 中映射到临时字段，或在这里再查询一次用户表。
        // 简便做法：给 Post 实体增加 @TableField(exist = false) 的 transient 字段。
        vo.setAuthorNickname(post.getAuthorNickname()); // 需要实体支持
        vo.setAuthorAvatar(post.getAuthorAvatar());
        vo.setCategoryId(post.getCategoryId());
        vo.setCategoryName(post.getCategoryName());     // 同理需要实体支持
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setCreateTime(post.getCreateTime());
        vo.setUpdateTime(post.getUpdateTime());

        // 4. 查询当前用户的点赞/收藏状态
        if (currentUserId != null) {
            vo.setIsLiked(likeMapper.isLiked(currentUserId, postId) > 0);
            vo.setIsFavorited(favoriteMapper.isFavorited(currentUserId, postId) > 0);
        } else {
            vo.setIsLiked(false);
            vo.setIsFavorited(false);
        }

        // 5. 查询一级评论列表
        List<Comment> comments = commentService.getCommentByPostId(postId);
        vo.setComments(comments);

        return vo;
    }

    @Override
    public PageVO<Post> getMyPosts(Long userId, int page, int size) {
        int offset = (page -1 ) *size;
        List<Post> posts = postMapper.selectMyPosts(userId, offset, size);
        int total = postMapper.countMyPosts(userId);
        return new PageVO<>(posts, (long) total, page, size);

    }
}