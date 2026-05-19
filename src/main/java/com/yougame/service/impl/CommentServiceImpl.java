package com.yougame.service.impl;

import com.yougame.common.exception.BusinessException;
import com.yougame.entity.Comment;
import com.yougame.entity.User;
import com.yougame.mapper.CommentMapper;
import com.yougame.mapper.PostMapper;
import com.yougame.mapper.UserMapper;
import com.yougame.service.CommentService;
import com.yougame.vo.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Comment createComment(Long userId, Long postId, String content) {
        if (postMapper.selectPostById(postId) == null) {
            throw new BusinessException(4004, "帖子不存在");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setPostId(postId);

        int rows = commentMapper.insertComment(comment);
        if (rows <= 0) {
            throw new BusinessException(5001, "评论发表失败");
        }

        postMapper.updateCommentCount(postId, 1);
        log.info("用户{}对帖子{}发表评论：{}", userId, postId, comment.getId());
        return comment;
    }

    @Override
    public List<Comment> getCommentByPostId(Long postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    @Override
    @Transactional
    public Comment replyComment(Long userId, Long postId, Long parentId, String content) {
        if (postMapper.selectPostById(postId) == null) {
            throw new BusinessException(4004, "帖子不存在");
        }

        Comment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null || !parentComment.getPostId().equals(postId)) {
            throw new BusinessException(4004, "上级评论不存在");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setParentId(parentId);
        comment.setContent(content);

        int row = commentMapper.insertComment(comment);
        if (row <= 0) {
            throw new BusinessException(5001, "回复失败");
        }
        postMapper.updateCommentCount(postId, 1);
        return comment;
    }

    @Override
    public PageVO<Comment> getMyComments(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.selectMyComments(userId, offset, size);
        int total = commentMapper.countMyComments(userId);
        return new PageVO<>(comments, (long) total, page, size);
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 1. 查询评论是否存在
        Comment comment = commentMapper.selectCommentById(commentId);
        if (comment == null) {
            throw new BusinessException(4004, "评论不存在");
        }
        // 2. 权限校验：只有作者本人或管理员可删除
        User currentUser = userMapper.selectById(userId);
        boolean isAuthor = comment.getUserId().equals(userId);
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

        if (!isAdmin && !isAuthor) {
            throw new BusinessException(4003, "你不是本人或者管理员，无权删除该帖子");
        }

        // 3. 执行删除
        int rows = commentMapper.deleteCommentById(commentId);
        if (rows <= 0) {
            throw new BusinessException(5001, "删除评论失败");
        }

        // 4. 更新帖子评论数（减少）
        postMapper.updateCommentCount(comment.getPostId(), -1);

        log.info("用户 {} 删除评论 {}，帖子ID：{}", userId, commentId, comment.getPostId());
    }
}