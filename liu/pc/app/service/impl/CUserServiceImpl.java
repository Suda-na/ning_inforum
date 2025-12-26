package com.app.service.impl;

import com.app.dao.CUserMapper;
import com.app.pojo.CUser;
import com.app.service.CUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CUserServiceImpl implements CUserService {

    @Autowired
    private CUserMapper cUserMapper;

    @Override
    public CUser login(String username, String password) {
        CUser user = cUserMapper.findByUsername(username);
        // 简单明文比对
        if (user != null && user.getPassword().equals(password)) {
            user.setPassword(null); // 不返回密码给前端
            return user;
        }
        return null;
    }

    @Override
    public boolean register(CUser user) {
        if (cUserMapper.findByPhone(user.getPhone()) != null) return false; // 手机号重复
        if (cUserMapper.findByUsername(user.getUsername()) != null) return false; // 用户名重复
        return cUserMapper.insertCUser(user) > 0;
    }

    @Override
    public boolean updateInfo(CUser user) {
        return cUserMapper.updateCUser(user) > 0;
    }
    
    @Override
    public CUser findByPhone(String phone) {
        CUser user = cUserMapper.findByPhone(phone);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }
}