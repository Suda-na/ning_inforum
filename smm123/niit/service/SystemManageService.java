package com.niit.service;

import com.niit.pojo.SystemSetting;
import com.niit.pojo.User;
import java.util.List;

public interface SystemManageService {
    // 系统设置
    SystemSetting getSetting();
    boolean updateSetting(SystemSetting setting);

    // 管理员管理
    List<User> getAdminList(String keyword);
    boolean addAdmin(User user);
    boolean deleteAdmin(Integer userId);

    // [新增] 更新管理员
    boolean updateAdmin(User user);
}