package com.niit.service.impl;

import com.niit.dao.SystemSettingMapper;
import com.niit.dao.UserMapper;
import com.niit.pojo.SystemSetting;
import com.niit.pojo.User;
import com.niit.service.SystemManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SystemManageServiceImpl implements SystemManageService {

    @Autowired
    private SystemSettingMapper settingMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public SystemSetting getSetting() {
        SystemSetting setting = settingMapper.selectSetting();
        if (setting == null) {
            setting = new SystemSetting();
            setting.setId(1);
        }
        return setting;
    }

    @Override
    public boolean updateSetting(SystemSetting setting) {
        return settingMapper.updateSetting(setting) > 0;
    }

    @Override
    public List<User> getAdminList(String keyword) {
        return userMapper.selectAdminList(keyword);
    }

    @Override
    public boolean addAdmin(User user) {
        if (userMapper.selectUserByUsername(user.getUsername()) != null) {
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean deleteAdmin(Integer userId) {
        return userMapper.deleteUserById(userId) > 0;
    }

    // [新增] 实现更新逻辑
    @Override
    public boolean updateAdmin(User user) {
        // 管理员修改用户的逻辑：可能涉及权限检查等
        if (user.getPassword() != null && user.getPassword().trim().isEmpty()) {
            user.setPassword(null);
        }
        // 调用通用的 Mapper 方法
        return userMapper.updateUser(user) > 0;
    }
}