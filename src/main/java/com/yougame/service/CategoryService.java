package com.yougame.service;

import com.yougame.entity.Category;
import java.util.List;

public interface CategoryService {

    /** 公开：获取所有分类（按排序字段升序） */
    List<Category> getAllCategories();

    /** 公开：根据 ID 获取分类 */
    Category getById(Integer id);

    /** 管理员操作：新增分类 */
    Category createCategory(Long operatorUserId, Category category);

    /** 管理员操作：更新分类 */
    Category updateCategory(Long operatorUserId, Integer id, Category category);

    /** 管理员操作：删除分类（物理删除，只有管理员且分类下无帖子时允许） */
    void deleteCategory(Long operatorUserId, Integer id);
    // CategoryService.java

}