package com.yougame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yougame.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    // ==================== 查询 ====================

    /**
     * 查询所有分类（按 sort_order 升序）
     * 实现位置：resources/mapper/CategoryMapper.xml
     */
    List<Category> selectAllOrderBySort();

    /**
     * 根据名称查询分类（用于查重）
     */
    @Select("SELECT * FROM category WHERE name = #{name}")
    Category selectByName(@Param("name") String name);

    /**
     * 统计某分类下的帖子数量（用于删除前检查）
     */
    @Select("SELECT COUNT(*) FROM post WHERE category_id = #{categoryId} AND status = 1")
    int countPostByCategoryId(@Param("categoryId") Integer categoryId);

    // ==================== 增删改 ====================

    /**
     * 插入分类
     */
    @Insert("INSERT INTO category(name, icon, sort_order) " +
            "VALUES (#{name}, #{icon}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCategory(Category category);

    /**
     * 更新分类
     */
    @Update("UPDATE category SET " +
            "name = #{name}, " +
            "icon = #{icon}, " +
            "sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int updateCategory(Category category);

    /**
     * 根据 ID 删除分类（物理删除）
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteCategory(@Param("id") Integer id);
}