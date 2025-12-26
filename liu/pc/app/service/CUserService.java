package com.app.service;
import com.app.pojo.CUser;

public interface CUserService {
    CUser login(String username, String password);
    boolean register(CUser user);
    boolean updateInfo(CUser user);
    CUser findByPhone(String phone);
}