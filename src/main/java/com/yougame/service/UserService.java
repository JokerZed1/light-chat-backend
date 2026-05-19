package com.yougame.service;

import com.yougame.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务接口
 * 【所属层级】：Service 业务层接口
 * 【设计原则】：
 *   - 方法命名遵循 find/get + 对象 + 条件 的规范
 *   - 管理员操作显式声明，参数包含操作者ID用于审计
 *   - 所有返回给前端的 User 对象均不含密码
 */
public interface UserService {

    // ==================== 认证与注册 ====================

    /**
     * 用户注册
     * @param user 用户信息（至少包含 username、password、nickname）
     * @return 注册成功返回 true，用户名已存在抛出 BusinessException
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 明文密码
     * @return 用户完整实体（含密码，仅在登录校验时使用，Controller 层不应返回密码给前端）
     */
    User login(String username, String password);

    // ==================== 用户信息查询 ====================

    /**
     * 根据用户名查询用户（含密码，仅内部调用）
     */
    User findByUsername(String username);

    /**
     * 根据 ID 查询用户基本信息（不含密码，不区分状态）
     * 用于内部关联查询、权限校验
     */
    User findById(Long id);

    /**
     * 获取当前登录用户的展示信息（不含密码，仅查正常状态用户）
     * 用于前端个人中心展示
     */
    User getCurrentUser(Long userId);

    /**
     * 查询所有正常状态的用户列表（不含密码）
     * 用于前端用户列表展示、搜索等公开场景
     */
    List<User> findAllNormalUsers();

    // ==================== 管理员操作 ====================

    /**
     * 更新用户状态（启用/禁用）
     * @param operatorId 操作者ID（管理员），用于日志审计
     * @param targetUserId 被操作用户ID
     * @param status 状态值：1=正常，0=禁用
     */
    @Transactional
    void updateUserStatus(Long operatorId, Long targetUserId, int status);

    /**
     * 查询所有用户列表（管理员视角，不含密码）
     * 包含正常和禁用用户
     */
    List<User> getAllUsers();

    /**
     * 管理员禁用用户
     * @param adminUserId  执行操作的管理员ID
     * @param targetUserId 被禁用的用户ID
     */
    void banUser(Long adminUserId, Long targetUserId);

    /**
     * 管理员解禁用户
     * @param adminUserId  执行操作的管理员ID
     * @param targetUserId 被解禁的用户ID
     */
    void unbanUser(Long adminUserId, Long targetUserId);

}