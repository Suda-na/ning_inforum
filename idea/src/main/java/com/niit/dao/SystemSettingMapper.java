package com.niit.dao;

import com.niit.pojo.SystemSetting;

public interface SystemSettingMapper {
    // 获取唯一配置 (ID=1)
    SystemSetting selectSetting();

    // 更新配置
    int updateSetting(SystemSetting setting);
}