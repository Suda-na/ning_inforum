package com.niit.controller;

import com.niit.pojo.User;
import com.niit.pojo.UserPermission;
import com.niit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.niit.pojo.UserBanHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 用户管理 Controller
 * 处理用户管理相关的请求
 */
@Controller
@RequestMapping("/admin")
public class ClientController {
    @Autowired
    private UserService userService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 跳转到用户列表页面，并查询所有用户
     * @param model 模型对象，用于传递数据到前端
     * @return 视图名称，会跳转到 clients.html
     */
    @RequestMapping("/clients")
    public String clients(Model model) {
        // 查询所有用户
        List<User> users = userService.findAllUsers();
        // 将用户列表传递到前端
        model.addAttribute("users", users);
        // 返回视图名称，Spring MVC 会根据配置跳转到 /WEB-INF/templates/html/clients.html
        return "clients";
    }

    /**
     * 根据状态查询用户
     * @param status 状态：0正常, 1禁用, 2注销
     * @param model 模型对象，用于传递数据到前端
     * @return 视图名称，会跳转到 clients.html
     */
    @RequestMapping("/clients/byStatus")
    public String clientsByStatus(@RequestParam(value = "status") Integer status, Model model) {
        // 根据状态查询用户
        List<User> users = userService.findUsersByStatus(status);
        // 将用户列表传递到前端
        model.addAttribute("users", users);
        // 返回视图名称
        return "clients";
    }

    /**
     * 封禁用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @RequestMapping("/clients/ban")
    public String banUser(@RequestParam(value = "userId") Integer userId) {
        userService.banUser(userId);
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 恢复用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @RequestMapping("/clients/unban")
    public String unbanUser(@RequestParam(value = "userId") Integer userId) {
        userService.unbanUser(userId);
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 操作结果
     */
    @RequestMapping("/clients/resetPassword")
    public String resetPassword(@RequestParam(value = "userId") Integer userId) {
        userService.resetPassword(userId);
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 修改用户用户名
     * @param userId 用户ID
     * @param username 新用户名
     * @return 操作结果
     */
    @RequestMapping("/clients/editUsername")
    public String editUsername(@RequestParam(value = "userId") Integer userId,
                              @RequestParam(value = "username") String username) {
        // 根据用户ID查询用户
        User user = userService.findUserById(userId);
        if (user != null) {
            // 设置新用户名
            user.setUsername(username);
            // 更新用户信息
            userService.updateUser(user);
        }
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @RequestMapping("/clients/delete")
    public String deleteUser(@RequestParam(value = "userId") Integer userId) {
        userService.deleteUser(userId);
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 修改用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @RequestMapping("/clients/editRole")
    public String editRole(@RequestParam(value = "userId") Integer userId,
                          @RequestParam(value = "role") Integer role) {
        // 根据用户ID查询用户
        User user = userService.findUserById(userId);
        if (user != null) {
            // 设置新角色
            user.setRole(role);
            // 更新用户信息
            userService.updateUser(user);
        }
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 添加用户
     * @param username 用户名
     * @param realName 真实姓名
     * @param password 密码
     * @param phone 手机号
     * @param email 邮箱
     * @param gender 性别：1男，2女
     * @param role 角色：2跑腿员，3普通用户
     * @param address 地址
     * @param signature 个性签名
     * @param avatar 头像URL
     * @return 操作结果
     */
    @RequestMapping("/clients/add")
    public String addUser(@RequestParam(value = "username") String username,
                         @RequestParam(value = "realName") String realName,
                         @RequestParam(value = "password") String password,
                         @RequestParam(value = "phone") String phone,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "gender") Integer gender,
                         @RequestParam(value = "role") Integer role,
                         @RequestParam(value = "address", required = false) String address,
                         @RequestParam(value = "signature", required = false) String signature,
                         @RequestParam(value = "avatar", required = false) String avatar) {
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setPassword(password);
        user.setPhone(phone);
        user.setEmail(email);
        user.setGender(gender);
        user.setRole(role);
        user.setAddress(address);
        user.setSignature(signature);
        user.setAvatar(avatar != null ? avatar : "default_avatar.png");
        user.setStatus(0); // 0表示正常状态
        user.setWarningCount(0);
        user.setCreateTime(new Date()); // 设置注册时间为当前时间
        
        // 添加用户
        userService.addUser(user);
        // 重定向到用户列表页面
        return "redirect:/admin/clients";
    }

    /**
     * 跳转到权限管理页面，并查询所有用户及其权限信息和封禁历史
     * @param model 模型对象，用于传递数据到前端
     * @return 视图名称，会跳转到 permissions.html
     */
    @RequestMapping("/permissions")
    public String permissions(Model model) {
        // 查询所有用户
        List<User> users = userService.findAllUsers();
        // 查询每个用户的权限信息和封禁历史
        for (User user : users) {
            UserPermission permission = userService.findUserPermissionByUserId(user.getUserId());
            user.setPermission(permission);
            
            List<UserBanHistory> banHistories = userService.findBanHistoryByUserId(user.getUserId());
            user.setBanHistories(banHistories);
            
            // 分析封禁历史，生成当前有效的封禁状态描述
            Map<String, String> activeBans = new HashMap<>();
            
            // 遍历封禁历史，找出每个权限对应的最新激活封禁记录
            // 注意：查询结果已经按start_time DESC排序，所以最新的记录在前面
            // 由于Service层在插入新封禁记录前会将同一权限的旧记录的is_active设为0，
            // 所以理论上每个权限只有一个is_active=1的记录，这个记录就是最新的激活封禁记录
            Map<String, UserBanHistory> latestActiveBans = new HashMap<>();
            
            for (UserBanHistory banHistory : banHistories) {
                // 只处理激活状态的封禁记录（查询已过滤is_active=1，这里只需要判断action_type）
                // 封禁状态的天数由is_active=1的记录决定
                if ("封禁".equals(banHistory.getActionType())) {
                    String restrictionsBefore = banHistory.getRestrictionsBefore();
                    String restrictionsAfter = banHistory.getRestrictionsAfter();
                    
                    if (restrictionsBefore != null && restrictionsAfter != null) {
                        try {
                            // 使用JSON解析来准确判断权限变化
                            JsonNode beforeNode = objectMapper.readTree(restrictionsBefore);
                            JsonNode afterNode = objectMapper.readTree(restrictionsAfter);
                            
                            // 检查发帖权限是否被封禁（支持两种JSON格式：can_post和canPost）
                            // 修复：只要restrictions_after中该权限为0，就认为这条记录封禁了该权限
                            // 这样可以处理用户已经被封禁时再次封禁的情况
                            JsonNode canPostAfter = afterNode.has("can_post") ? afterNode.get("can_post") : afterNode.get("canPost");
                            if (canPostAfter != null && canPostAfter.asInt() == 0) {
                                if (!latestActiveBans.containsKey("post")) {
                                    latestActiveBans.put("post", banHistory);
                                }
                            }
                            
                            // 检查评论权限是否被封禁
                            JsonNode canCommentAfter = afterNode.has("can_comment") ? afterNode.get("can_comment") : afterNode.get("canComment");
                            if (canCommentAfter != null && canCommentAfter.asInt() == 0) {
                                if (!latestActiveBans.containsKey("comment")) {
                                    latestActiveBans.put("comment", banHistory);
                                }
                            }
                            
                            // 检查私信权限是否被封禁
                            JsonNode canMessageAfter = afterNode.has("can_message") ? afterNode.get("can_message") : afterNode.get("canMessage");
                            if (canMessageAfter != null && canMessageAfter.asInt() == 0) {
                                if (!latestActiveBans.containsKey("message")) {
                                    latestActiveBans.put("message", banHistory);
                                }
                            }
                        } catch (Exception e) {
                            // JSON解析失败时，回退到字符串匹配方式
                            // 修复：只要restrictions_after中该权限为0，就认为这条记录封禁了该权限
                            // 检查发帖权限是否被封禁（支持两种JSON格式：can_post和canPost）
                            if (restrictionsAfter.contains("\"can_post\":0") || restrictionsAfter.contains("\"canPost\":0")) {
                                if (!latestActiveBans.containsKey("post")) {
                                    latestActiveBans.put("post", banHistory);
                                }
                            }
                            // 检查评论权限是否被封禁
                            if (restrictionsAfter.contains("\"can_comment\":0") || restrictionsAfter.contains("\"canComment\":0")) {
                                if (!latestActiveBans.containsKey("comment")) {
                                    latestActiveBans.put("comment", banHistory);
                                }
                            }
                            // 检查私信权限是否被封禁
                            if (restrictionsAfter.contains("\"can_message\":0") || restrictionsAfter.contains("\"canMessage\":0")) {
                                if (!latestActiveBans.containsKey("message")) {
                                    latestActiveBans.put("message", banHistory);
                                }
                            }
                        }
                    }
                }
            }
            
            // 根据durationDays生成封禁描述文本，并标记永久封禁
            Map<String, Boolean> isPermanentBans = new HashMap<>();
            for (Map.Entry<String, UserBanHistory> entry : latestActiveBans.entrySet()) {
                String permissionType = entry.getKey();
                UserBanHistory banHistory = entry.getValue();
                Integer durationDays = banHistory.getDurationDays();
                
                String banDesc;
                boolean isPermanent = false;
                if (durationDays == null || durationDays == 0) {
                    banDesc = "永久封禁";
                    isPermanent = true;
                } else if (durationDays == 1) {
                    banDesc = "封禁一天";
                } else if (durationDays == 3) {
                    banDesc = "封禁三天";
                } else {
                    banDesc = "封禁" + durationDays + "天";
                }
                
                activeBans.put(permissionType, banDesc);
                isPermanentBans.put(permissionType, isPermanent);
            }
            
            // 只对当前被封禁的权限显示封禁描述
            Map<String, String> finalActiveBans = new HashMap<>();
            Map<String, Boolean> finalIsPermanentBans = new HashMap<>();
            if (permission.getCanPost() == 0) {
                finalActiveBans.put("post", activeBans.getOrDefault("post", "禁止发布"));
                finalIsPermanentBans.put("post", isPermanentBans.getOrDefault("post", false));
            }
            if (permission.getCanComment() == 0) {
                finalActiveBans.put("comment", activeBans.getOrDefault("comment", "禁止评论"));
                finalIsPermanentBans.put("comment", isPermanentBans.getOrDefault("comment", false));
            }
            if (permission.getCanMessage() == 0) {
                finalActiveBans.put("message", activeBans.getOrDefault("message", "禁止私信"));
                finalIsPermanentBans.put("message", isPermanentBans.getOrDefault("message", false));
            }
            
            user.setActiveBans(finalActiveBans);
            user.setIsPermanentBans(finalIsPermanentBans);
        }
        // 将用户列表传递到前端
        model.addAttribute("users", users);
        // 返回视图名称，Spring MVC 会根据配置跳转到 /WEB-INF/templates/html/permissions.html
        return "permissions";
    }

    /**
     * 更新用户发帖权限
     * @param userId 用户ID
     * @param canPost 是否允许发帖：1允许，0禁止
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 操作结果
     */
    @ResponseBody
    @RequestMapping("/permissions/updatePostPermission")
    public Map<String, Object> updatePostPermission(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "canPost") Integer canPost,
            @RequestParam(value = "reason", required = false, defaultValue = "") String reason,
            @RequestParam(value = "durationDays", required = false, defaultValue = "0") Integer durationDays) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 管理员ID暂时固定为1，后续可以从登录信息中获取
            int adminId = 1;
            int rows = userService.updateUserPostPermission(userId, canPost, adminId, reason, durationDays);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "权限更新成功");
            } else {
                result.put("success", false);
                result.put("message", "权限更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "权限更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户评论权限
     * @param userId 用户ID
     * @param canComment 是否允许评论：1允许，0禁止
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 操作结果
     */
    @ResponseBody
    @RequestMapping("/permissions/updateCommentPermission")
    public Map<String, Object> updateCommentPermission(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "canComment") Integer canComment,
            @RequestParam(value = "reason", required = false, defaultValue = "") String reason,
            @RequestParam(value = "durationDays", required = false, defaultValue = "0") Integer durationDays) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 管理员ID暂时固定为1，后续可以从登录信息中获取
            int adminId = 1;
            int rows = userService.updateUserCommentPermission(userId, canComment, adminId, reason, durationDays);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "权限更新成功");
            } else {
                result.put("success", false);
                result.put("message", "权限更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "权限更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户私信权限
     * @param userId 用户ID
     * @param canMessage 是否允许私信：1允许，0禁止
     * @param reason 操作原因
     * @param durationDays 封禁天数
     * @return 操作结果
     */
    @ResponseBody
    @RequestMapping("/permissions/updateMessagePermission")
    public Map<String, Object> updateMessagePermission(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "canMessage") Integer canMessage,
            @RequestParam(value = "reason", required = false, defaultValue = "") String reason,
            @RequestParam(value = "durationDays", required = false, defaultValue = "0") Integer durationDays) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 管理员ID暂时固定为1，后续可以从登录信息中获取
            int adminId = 1;
            int rows = userService.updateUserMessagePermission(userId, canMessage, adminId, reason, durationDays);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "权限更新成功");
            } else {
                result.put("success", false);
                result.put("message", "权限更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "权限更新失败：" + e.getMessage());
        }
        return result;
    }
}

