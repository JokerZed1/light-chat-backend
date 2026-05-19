package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Category;
import com.yougame.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏分类控制器
 * 读操作公开，写操作需管理员权限（权限校验已在 Service 层完成）
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类（公开）
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    /**
     * 根据 ID 获取分类详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }

    /**
     * 新增分类（管理员）
     */
    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        Long userId = UserContext.getUserId();
        return Result.success("创建成功", categoryService.createCategory(userId, category));
    }

    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Integer id,
                                           @RequestBody Category category) {
        Long userId = UserContext.getUserId();
        return Result.success("更新成功", categoryService.updateCategory(userId, id, category));
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        Long userId = UserContext.getUserId();
        categoryService.deleteCategory(userId, id);
        return Result.success();
    }
}