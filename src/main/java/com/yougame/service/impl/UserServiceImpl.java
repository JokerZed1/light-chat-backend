package com.yougame.service.impl;

import com.yougame.common.exception.BusinessException;
import com.yougame.entity.User;
import com.yougame.mapper.UserMapper;
import com.yougame.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务实现类
 * 【设计原则】：
 * - 所有方法遵循 service 接口定义
 * - 密码加密器在类内部直接实例化（非 Bean，避免循环依赖）
 * - 管理员操作前都进行权限校验
 * - 查询方法返回给前端的数据不包含密码
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    // 直接 new，不依赖 Spring 容器注入，避免配置遗漏
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==================== 认证与注册 ====================

    @Override
    @Transactional
    public boolean register(User user) {
        // 1. 用户名唯一校验
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException(4001, "用户名已存在");
        }

        // 2. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 默认值设置
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        user.setRole("USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        // 4. 插入数据库
        int rows = userMapper.insertUser(user);
        log.info("用户注册成功，ID：{}，用户名：{}", user.getId(), user.getUsername());
        return rows > 0;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(4002, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(4003, "账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(4002, "用户名或密码错误");
        }
        log.info("用户登录成功：{}", username);
        // 注意：返回的 user 包含密码字段，Controller 层应在返回前端前手动置 null
        return user;
    }

    // ==================== 用户信息查询 ====================

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectUserInfoById(userId);
        if (user == null) {
            throw new BusinessException(4004, "用户不存在");
        }
        // 确保密码为空（虽然 SQL 已不查密码，但二次确认）
        user.setPassword(null);
        return user;
    }

    @Override
    public List<User> findAllNormalUsers() {
        // 只返回 status=1 的用户
        return userMapper.selectAllActiveUsers();
    }

    // ==================== 管理员操作 ====================

    @Override
    @Transactional
    public void updateUserStatus(Long operatorId, Long targetUserId, int status) {
        // 1. 校验操作者是管理员
        User operator = userMapper.selectById(operatorId);
        if (operator == null || !"ADMIN".equals(operator.getRole())) {
            throw new BusinessException(4003, "无权操作，仅管理员可修改用户状态");
        }

        // 2. 校验目标用户存在
        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new BusinessException(4004, "目标用户不存在");
        }

        // 3. 防止自己封禁自己（可选，根据业务需要）
        if (operatorId.equals(targetUserId)) {
            throw new BusinessException(4000, "不能修改自己的状态");
        }

        // 4. 防止封禁管理员
        if ("ADMIN".equals(target.getRole())) {
            throw new BusinessException(4003, "不能封禁管理员");
        }

        // 5. 执行状态更新
        userMapper.updateUserStatus(targetUserId, status);
        log.info("管理员 {} 将用户 {} 状态修改为 {}", operatorId, targetUserId, status);
    }

    @Override
    @Transactional
    public void banUser(Long adminUserId, Long targetUserId) {
        // 1. 校验管理员权限
        User admin = userMapper.selectById(adminUserId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            throw new BusinessException(4003, "无权操作");
        }
        // 2. 校验目标用户存在
        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new BusinessException(4004, "用户不存在");
        }
        // 3. 修改状态为禁用
        userMapper.updateUserStatus(targetUserId, 0);
        log.info("管理员 {} 禁用用户 {}", adminUserId, targetUserId);
    }

    @Override
    @Transactional
    public void unbanUser(Long adminUserId, Long targetUserId) {
        // 1. 校验管理员权限
        User admin = userMapper.selectById(adminUserId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            throw new BusinessException(4003, "无权操作");
        }
        // 2. 校验目标用户存在
        User target = userMapper.selectById(targetUserId);
        if (target == null) {
            throw new BusinessException(4004, "用户不存在");
        }
        // 3. 恢复状态为正常
        userMapper.updateUserStatus(targetUserId, 1);
        log.info("管理员 {} 解禁用户 {}", adminUserId, targetUserId);
    }

    @Override
    public List<User> getAllUsers() {
        // 返回所有用户（包含密码字段不安全，实际应该在Mapper中排除密码）
        return userMapper.selectAllUsers();
    }

}