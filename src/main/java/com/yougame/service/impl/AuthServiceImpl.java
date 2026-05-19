package com.yougame.service.impl;

import com.yougame.common.exception.BusinessException;
import com.yougame.entity.User;
import com.yougame.mapper.UserMapper;
import com.yougame.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    @Override
    public void requireAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || !"ADMIN".equals(user.getRole())){
            throw new BusinessException(4003,"需要管理员权限");
        }
    }
}
