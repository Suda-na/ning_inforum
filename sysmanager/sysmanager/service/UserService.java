package com.niit.service;

import com.niit.pojo.User;

public interface UserService {

    User getUserById(Integer userId);

    User getUserByUsername(String username);

    boolean updateUserInfo(User user);

    boolean updateUserAvatar(Integer userId, String avatarPath);

    User login(String username, String password);
}
