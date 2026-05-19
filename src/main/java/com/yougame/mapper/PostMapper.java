package com.yougame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yougame.entity.Post;
import com.yougame.vo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    // ==================== 单表操作（注解） ====================
    /**
     * 增加浏览量（原子操作，手写 SQL）
     */
    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 更新评论数
     */
    @Update("UPDATE post SET comment_count = comment_count + #{delta} WHERE id = #{postId}")
    int updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);

    // ==================== 复杂操作（XML） ====================
    int insertPost(Post post);
    Post selectPostById(@Param("id") Long id);
    int updatePost(Post post);
    int deletePostById(@Param("id") Long id);
    Post selectPostDetailById(@Param("id") Long id);
    List<Post> selectPostsWithPage(@Param("gameId") Integer gameId,
                                   @Param("categoryId") Integer categoryId,
                                   @Param("orderBy") String orderBy,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    int countPosts(@Param("gameId") Integer gameId,
                   @Param("categoryId") Integer categoryId);
    /**
     * 我的帖子列表（分页）
     */
    List<Post> selectMyPosts(@Param("userId") Long userId,
                             @Param("offset") int offset,
                             @Param("limit") int limit);
    int countMyPosts(@Param("userId") Long userId);

    List<PostVO> selectPostsByGameId(@Param("gameId") Integer gameId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
}