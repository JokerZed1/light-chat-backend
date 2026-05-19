package com.yougame.service.impl;

import com.yougame.common.exception.BusinessException;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Post;
import com.yougame.mapper.FavoriteMapper;
import com.yougame.mapper.PostMapper;
import com.yougame.service.FavoriteService;
import com.yougame.vo.FavoriteStatusVO;
import com.yougame.vo.PageVO;
import com.yougame.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public FavoriteStatusVO toggleFavorite(Long userId, Long postId) {
        // 校验帖子存在
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new BusinessException(4004, "帖子不存在");
        }

        int exists = favoriteMapper.isFavorited(userId, postId);

        if (exists == 0) {
            // 收藏
            try {
                favoriteMapper.insertFavorite(userId, postId);
            } catch (DuplicateKeyException e) {
                throw new BusinessException(4000, "请勿重复收藏");
            }
            log.info("用户 {} 收藏帖子 {}", userId, postId);
        } else {
            // 取消收藏
            favoriteMapper.deleteFavorite(userId, postId);
            log.info("用户 {} 取消收藏帖子 {}", userId, postId);
        }

        int count = favoriteMapper.getFavoriteCount(postId);
        return FavoriteStatusVO.builder()
                .isFavorited(exists == 0)   // 之前不存在则为已收藏
                .favoriteCount(count)
                .build();
    }

    @Override
    public PageVO<PostVO> getMyFavorites(int page, int size) {
        Long userId = UserContext.getUserId();
        int offset = (page - 1) * size;
        List<PostVO> posts = favoriteMapper.selectMyFavorites(userId, offset, size);
        int total = favoriteMapper.countMyFavorites(userId);
        return new PageVO<>(posts, (long) total, page, size);
    }
}