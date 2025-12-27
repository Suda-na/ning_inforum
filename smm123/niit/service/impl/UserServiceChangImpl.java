package com.niit.service.impl;

import com.niit.dao.UserMapper;
import com.niit.pojo.User;
import com.niit.service.UserService;
import com.niit.service.UserServiceChang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceChangImpl implements UserServiceChang {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public boolean updateUserInfo(User user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean updateUserAvatar(Integer userId, String avatarPath) {
        User user = new User();
        user.setUserId(userId);
        user.setAvatar(avatarPath);
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectUserByUsername(username);

        if (user == null) {
            return null;
        }
        if (user.getPassword() == null || !user.getPassword().equals(password)) {
            return null;
        }
        if (user.getStatus() != 0) {
            throw new RuntimeException("账号已被冻结，请联系超级管理员");
        }
        if (user.getRole() > 1) {
            throw new RuntimeException("非管理员禁止登录后台");
        }

        return user;
    }
}
