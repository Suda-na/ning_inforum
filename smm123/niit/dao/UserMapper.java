package com.niit.dao;

import com.niit.pojo.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    // 1. 通用查询：根据ID查用户 (管理员列表回显、个人中心回显 都用这个)
    User selectUserById(@Param("userId") Integer userId);

    // 2. 通用更新：更新用户信息 (管理员改人、用户自改 都用这个)
    int updateUser(User user);

    // 3. 你的其他现有方法 (保持不变)
    List<User> selectAdminList(@Param("keyword") String keyword);
    int insertUser(User user);
    int deleteUserById(@Param("userId") Integer userId);
    User selectUserByUsername(String username);
}