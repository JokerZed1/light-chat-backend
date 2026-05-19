package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import com.yougame.entity.Category;
import com.yougame.entity.User;
import com.yougame.service.CategoryService;
import com.yougame.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 * 【权限说明】：
 *   所有接口仅管理员可访问，具体权限校验在 Service 层完成。
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        Long adminId = UserContext.getUserId();
        log.info("管理员 {} 查询用户列表", adminId);
        return Result.success(userService.getAllUsers());
    }

    /**
     * 禁用用户
     */
    @PutMapping("/users/{id}/ban")
    public Result<Void> banUser(@PathVariable Long id) {
        Long adminId = UserContext.getUserId();
        userService.banUser(adminId, id);
        log.info("管理员 {} 禁用用户 {}", adminId, id);
        return Result.success();
    }

    /**
     * 解禁用户
     */
    @PutMapping("/users/{id}/unban")
    public Result<Void> unbanUser(@PathVariable Long id) {
        Long adminId = UserContext.getUserId();
        userService.unbanUser(adminId, id);
        log.info("管理员 {} 解禁用户 {}", adminId, id);
        return Result.success();
    }

    // ==================== 分类管理 ====================

    /**
     * 创建新分类
     */
    @PostMapping("/categories")
    public Result<Category> createCategory(@RequestBody Category category) {
        Long adminId = UserContext.getUserId();
        // 调用优化后的 Service 方法（参数顺序：operatorUserId, category）
        Category savedCategory = categoryService.createCategory(adminId, category);
        log.info("管理员 {} 创建分类：{}", adminId, savedCategory.getName());
        return Result.success("创建成功", savedCategory);
    }

    /**
     * 更新分类
     */
    @PutMapping("/categories/{id}")
    public Result<Category> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        Long adminId = UserContext.getUserId();
        // 调用优化后的 Service 方法（参数顺序：operatorUserId, id, category）
        Category updatedCategory = categoryService.updateCategory(adminId, id, category);
        log.info("管理员 {} 更新分类 ID:{}，新名称：{}", adminId, id, updatedCategory.getName());
        return Result.success("更新成功", updatedCategory);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        Long adminId = UserContext.getUserId();
        // 调用优化后的 Service 方法（参数顺序：operatorUserId, id）
        categoryService.deleteCategory(adminId, id);
        log.info("管理员 {} 删除分类 ID:{}", adminId, id);
        return Result.success();
    }
}