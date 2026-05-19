package com.yougame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yougame.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户 Mapper 接口
 * 【所属层级】：Mapper 数据访问层
 * 【设计说明】：
 *   - 继承 BaseMapper<User> 保留 MyBatis-Plus 基础能力
 *   - 所有业务 SQL 全部手写（注解方式）
 *   - 方法按功能分组：认证注册、信息查询、用户管理
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // ==================== 认证/注册相关 ====================

    /**
     * 根据用户名查询用户完整信息（用于登录校验、注册查重）
     * 注意：此方法返回所有字段（含密码），仅在认证场景使用
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 插入新用户（注册）
     * 使用数据库自增主键，并将生成的主键回填到 user.id
     */
    @Insert("INSERT INTO user(username, password, nickname, role, status, create_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{role}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

    // ==================== 用户信息查询（不含密码） ====================

    /**
     * 根据 ID 查询用户基本信息（不含密码、不区分状态）
     * 用于内部关联查询、权限校验等场景
     */
    @Select("SELECT id, username, nickname, avatar, role, status, create_time " +
            "FROM user WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    /**
     * 根据 ID 查询用户展示信息（不含敏感字段，仅查正常状态用户）
     * 用于个人主页、作者信息展示等前端场景
     */
    @Select("SELECT id, username, nickname, avatar, bio, role, status, create_time " +
            "FROM user WHERE id = #{id} AND status = 1")
    User selectUserInfoById(@Param("id") Long id);

    // ==================== 用户状态管理（管理员） ====================

    /**
     * 更新用户状态（启用/禁用）
     * @param id     用户ID
     * @param status 状态值：1=正常，0=禁用
     */
    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateUserStatus(@Param("id") Long id, @Param("status") int status);

    /**
     * 查询所有用户列表（管理员视角，不含密码）
     */
    @Select("SELECT id, username, nickname, avatar, role, status, create_time " +
            "FROM user ORDER BY create_time DESC")
    List<User> selectAllUsers();

    /**
     * 查询所有正常状态的用户（用于前端展示、关联查询等）
     */
    @Select("SELECT id, username, nickname, avatar, role, status, create_time " +
            "FROM user WHERE status = 1 ORDER BY create_time DESC")
    List<User> selectAllActiveUsers();
}