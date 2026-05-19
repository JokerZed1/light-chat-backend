package com.yougame.mapper;

import com.yougame.vo.PostVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO user_favorite(user_id, post_id, create_time) VALUES(#{userId}, #{postId}, NOW())")
    int insertFavorite(@Param("userId") Long userId, @Param("postId") Long postId);

    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteFavorite(@Param("userId") Long userId, @Param("postId") Long postId);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND post_id = #{postId}")
    int isFavorited(@Param("userId") Long userId, @Param("postId") Long postId);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE post_id = #{postId}")
    int getFavoriteCount(@Param("postId") Long postId);

    /**
     * 查询用户收藏的帖子（分页，返回 PostVO）
     * 实现位置：resources/mapper/FavoriteMapper.xml
     */
    List<PostVO> selectMyFavorites(@Param("userId") Long userId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    /**
     * 统计用户收藏总数
     */
    int countMyFavorites(@Param("userId") Long userId);
}