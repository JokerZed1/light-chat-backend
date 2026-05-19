package com.yougame.mapper;

import lombok.Data;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {

    /*
     * 插入点赞记录（防重由数据库唯一键保证，业务层捕获异常）
     */
    @Insert("INSERT INTO user_like(user_id, post_id, create_time)VALUES (#{userId}, #{postId}, NOW())")
    int insertLike(@Param("userId")Long userId, @Param("postId") Long postId);

    /*
     * 取消点赞
     */

    @Delete("Delete FROM user_like WHERE user_id=#{userId} AND post_id = #{postId}")
    int deleteLike(@Param("userId") Long userId, @Param("postId") Long postId);

    /*
     * 检查用户是否已点赞
     */

    @Select("SELECT COUNT(*) FROM user_like WHERE user_id = #{userId} AND post_id = #{postId}")
    int isLiked(@Param("userId") Long userId, @Param("postId") Long postId);

    /*
     * 更新帖子的点赞总数（+1 或 -1）
     */

    @Update("UPDATE post SET like_count = like_count + #{delta} WHERE id = #{postId}")
    int updatePostLikeCount(@Param("postId") Long postId, @Param("delta") int delta);
}
