package com.yougame.service;

import com.yougame.entity.Comment;
import com.yougame.service.dto.CommentDTO;
import com.yougame.vo.PageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentService {

    /*
     * 发布评论（一级）
     */

    Comment createComment(Long userId, Long postId, String content);

    /*
     * 获取帖子的一级评论列表
     */

    List<Comment> getCommentByPostId(Long postId);

    Comment replyComment(Long userId, Long postId, Long parentId, String content);

    // CommentService.java
    PageVO<Comment> getMyComments(Long userId, int page, int size);

    //删除帖子
    void deleteComment(Long commentId, Long userId);

}
