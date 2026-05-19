package com.yougame.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yougame.common.exception.BusinessException;
import com.yougame.entity.Category;
import com.yougame.entity.Post;
import com.yougame.mapper.CategoryMapper;
import com.yougame.mapper.PostMapper;
import com.yougame.service.CategoryService;
import com.yougame.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类管理服务实现
 * 【规范说明】：
 * - 公开方法（无需登录）使用不带 userId 的签名。
 * - 管理员方法第一个参数为操作人ID，内部进行权限和业务校验。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final PostMapper postMapper;
    private final AuthService authService;

    // ==================== 公开查询方法（无需登录） ====================

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllOrderBySort();
    }

    @Override
    public Category getById(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(4004, "分类不存在");
        }
        return category;
    }

    // ==================== 管理员操作（统一校验 + 业务规则） ====================

    /**
     * 创建分类
     * @param operatorUserId 操作人ID（必须是管理员）
     * @param category 要创建的分类对象
     * @return 创建后的分类（包含自增ID）
     */
    @Override
    @Transactional
    public Category createCategory(Long operatorUserId, Category category) {
        // 1. 权限校验
        authService.requireAdmin(operatorUserId);

        // 2. 参数校验
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        // 3. 名称唯一性校验
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(4001, "分类名称已存在");
        }

        // 4. 设置默认排序值
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        // 5. 执行插入（使用手写 Mapper 方法）
        int rows = categoryMapper.insertCategory(category);
        if (rows <= 0) {
            throw new BusinessException(500, "创建分类失败");
        }

        log.info("管理员 {} 创建分类：{}", operatorUserId, category.getName());
        return category;
    }

    /**
     * 更新分类
     * @param operatorUserId 操作人ID
     * @param id 分类ID
     * @param category 包含更新字段的对象
     * @return 更新后的分类对象
     */
    @Override
    @Transactional
    public Category updateCategory(Long operatorUserId, Integer id, Category category) {
        // 1. 权限校验
        authService.requireAdmin(operatorUserId);

        // 2. 参数校验
        if (id == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException(400, "分类名称不能为空");
        }

        // 3. 检查分类是否存在
        Category exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(4004, "分类不存在");
        }

        // 4. 名称唯一性校验（排除自身）
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName())
                .ne(Category::getId, id);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(4001, "分类名称已存在");
        }

        // 5. 执行更新
        category.setId(id);
        int rows = categoryMapper.updateCategory(category);
        if (rows <= 0) {
            throw new BusinessException(500, "更新分类失败");
        }

        log.info("管理员 {} 更新分类 ID:{}，新名称：{}", operatorUserId, id, category.getName());
        return category;
    }

    /**
     * 删除分类（物理删除，但需检查关联帖子）
     * @param operatorUserId 操作人ID
     * @param id 分类ID
     */
    @Override
    @Transactional
    public void deleteCategory(Long operatorUserId, Integer id) {
        // 1. 权限校验
        authService.requireAdmin(operatorUserId);

        // 2. 检查分类是否存在
        Category exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(4004, "分类不存在");
        }

        // 3. 检查分类下是否有未删除的帖子
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(Post::getCategoryId, id)
                .eq(Post::getStatus, 1);
        if (postMapper.selectCount(postWrapper) > 0) {
            throw new BusinessException(4002, "该分类下存在帖子，无法删除");
        }

        // 4. 执行删除
        int rows = categoryMapper.deleteCategory(id);
        if (rows <= 0) {
            throw new BusinessException(500, "删除分类失败");
        }

        log.info("管理员 {} 删除分类 ID:{}，名称：{}", operatorUserId, id, exist.getName());
    }
}