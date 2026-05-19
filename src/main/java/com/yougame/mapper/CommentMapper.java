package com.yougame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yougame.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 插入评论（手写 SQL）
     */
    @Insert("INSERT INTO comment(content, user_id, post_id, parent_id, create_time) " +
            "VALUES (#{content}, #{userId}, #{postId}, #{parentId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(Comment comment);

    /**
     * 查询某帖子的所有一级评论（关联用户信息）
     */
    List<Comment> selectCommentsByPostId(@Param("postId") Long postId);

    /**
     * 我的评论列表（分页，关联帖子标题）
     */
    List<Comment> selectMyComments(@Param("userId") Long userId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    /**
     * 统计我的评论总数
     */
    int countMyComments(@Param("userId") Long userId);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteCommentById(@Param("id") Long id);

    // 按ID查询评论（不含关联，只查基本信息）
    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment selectCommentById(@Param("id") Long id);



}