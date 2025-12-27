package com.app.service;
import com.app.pojo.CUser;

import java.util.List;
import java.util.Map;

public interface CUserService {
    CUser login(String username, String password);
    boolean register(CUser user);
    boolean updateInfo(CUser user);
    CUser findByPhone(String phone);
    CUser findByUsername(String username);
    CUser getUserInfo(Integer userId);
    List<CUser> getRelationList(Integer userId, Integer type);
    boolean followAction(Integer userId, Integer targetUserId, Integer actionType);
    Map<String, Integer> getUserStats(Integer userId);
    boolean checkFollowExists(Integer followerId, Integer followingId);
}


