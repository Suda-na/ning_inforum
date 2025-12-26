package com.app.service.impl;

import com.app.dao.CUserMapper;
import com.app.pojo.CUser;
import com.app.service.CUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    @Override
    public CUser getUserInfo(Integer userId) {
        CUser user = cUserMapper.findById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }
    
    @Override
    public List<CUser> getRelationList(Integer userId, Integer type) {
        List<CUser> users = cUserMapper.selectRelationList(userId, type);
        // 不返回密码
        for (CUser user : users) {
            user.setPassword(null);
        }
        return users;
    }
    
    @Override
    public boolean followAction(Integer userId, Integer targetUserId, Integer actionType) {
        try {
            if (actionType == 0) {
                // 关注
                return cUserMapper.insertFollow(userId, targetUserId) > 0;
            } else if (actionType == 1) {
                // 取消关注
                return cUserMapper.deleteFollow(userId, targetUserId) > 0;
            } else if (actionType == 2) {
                // 移除粉丝（删除对方关注我的记录）
                return cUserMapper.deleteFollow(targetUserId, userId) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Map<String, Integer> getUserStats(Integer userId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("followingCount", cUserMapper.countFollowing(userId));
        stats.put("fansCount", cUserMapper.countFans(userId));
        stats.put("likeCount", cUserMapper.countLikes(userId));
        return stats;
    }
}


