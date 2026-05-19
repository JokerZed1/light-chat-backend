// PostService.java
package com.yougame.service;

import com.yougame.entity.Post;
import com.yougame.service.dto.PostPublishDTO;
import com.yougame.service.dto.PostQueryDTO;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostDetailVO;

public interface PostService {

    /**
     * 发布帖子
     */
    Post publishPost(PostPublishDTO dto, Long userId);

    /**
     * 获取帖子详情（同时增加浏览量）
     */
    Post getPostDetail(Long id);

    /**
     * 分页查询帖子列表
     */
    PageVO<Post> listPosts(PostQueryDTO queryDTO);

    /**
     * 更新帖子（仅作者可操作）
     */
    Post updatePost(Long id, PostPublishDTO dto, Long userId);

    /**
     * 删除帖子（仅作者或管理员）
     */
    void deletePost(Long id, Long userId);

    /*
     * 获取帖子详情（VO，含评论、点赞收藏状态）
     */
    PostDetailVO getPostDetailVO(Long postId, Long currentUserId);

    PageVO<Post> getMyPosts(Long userId, int page, int size);
}