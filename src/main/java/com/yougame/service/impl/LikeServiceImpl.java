package com.yougame.service.impl;


import com.yougame.common.exception.BusinessException;
import com.yougame.entity.Post;
import com.yougame.mapper.LikeMapper;
import com.yougame.mapper.PostMapper;
import com.yougame.service.LikeService;
import com.yougame.vo.LikeStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public LikeStatusVO toggleLike(Long userId, Long postId) {
        // 1. 校验帖子是否存在（此查询会过滤 status=1，所以被逻辑删除的帖子无法点赞）
        Post existPost = postMapper.selectPostById(postId);
        if (existPost == null) {
            throw new BusinessException(4004, "帖子不存在");
        }
        int exists = likeMapper.isLiked(userId, postId);

        if (exists == 0) {
            // 未点赞 → 添加点赞
            try {
                likeMapper.insertLike(userId, postId);
            } catch (DuplicateKeyException e) {
                throw new BusinessException(4000, "切勿重复点赞");
            }
            likeMapper.updatePostLikeCount(postId, 1);
            log.info("用户{}点赞帖子{}", userId, postId);
        } else {
            // 已点赞 → 取消点赞
            likeMapper.deleteLike(userId, postId);
            likeMapper.updatePostLikeCount(postId, -1);
            log.info("用户{}点赞帖子{}", userId, postId);
        }

        // 2. 重新查询帖子，获取最新的 like_count（不要再手动 +1/-1！）
        Post updaredPost = postMapper.selectPostById(postId);
        int latestLikeCount = updaredPost != null ? updaredPost.getLikeCount() : 0;

        return LikeStatusVO.builder()
                .isLiked(exists == 0) // 之前不存在就是已点赞，否则是取消
                .likeCount(latestLikeCount)
                .build();
    }

}
