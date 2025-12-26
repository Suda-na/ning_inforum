package com.app.controller;

import com.app.common.CResult;
import com.app.common.VerificationCodeManager;
import com.app.pojo.CUser;
import com.app.service.CUserService;
import com.app.service.SmsService;
import com.niit.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cuser") // 注意：安卓端的 URL 要改成这个
public class CClientController {
    
    private static final Logger logger = Logger.getLogger(CClientController.class.getName());

    @Autowired
    private CUserService cUserService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private VerificationCodeManager verificationCodeManager;
    
    @Autowired
    private com.app.service.WCircleService wCircleService;

    @PostMapping("/login")
    public CResult<CUser> login(@RequestParam("username") String username,
                                @RequestParam("password") String password) {
        CUser user = cUserService.login(username, password);
        if (user != null) {
            return CResult.success("登录成功", user);
        }
        return CResult.error("账号或密码错误");
    }

    @PostMapping("/register")
    public CResult<Object> register(@RequestParam("username") String username,
                                    @RequestParam("realName") String realName,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("password") String password) {
        CUser user = new CUser();
        user.setUsername(username);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setPassword(password);

        if (cUserService.register(user)) {
            return CResult.success("注册成功", null);
        }
        return CResult.error("注册失败，用户名或手机号已存在");
    }

    @PostMapping("/update")
    public CResult<CUser> update(@RequestParam(value = "userId", required = true) Integer userId,
                                 @RequestParam(value = "username", required = false) String username,
                                 @RequestParam(value = "realName", required = false) String realName,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "gender", required = false) Integer gender,
                                 @RequestParam(value = "signature", required = false) String signature,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "password", required = false) String password,
                                 @RequestParam(value = "avatar", required = false) String avatar) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            // 构建用户对象，只设置非空字段
            CUser user = new CUser();
            user.setUserId(userId);
            if (username != null && !username.trim().isEmpty()) {
                user.setUsername(username.trim());
            }
            if (realName != null && !realName.trim().isEmpty()) {
                user.setRealName(realName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email.trim());
            }
            if (gender != null) {
                user.setGender(gender);
            }
            if (signature != null) {
                user.setSignature(signature.trim());
            }
            if (address != null && !address.trim().isEmpty()) {
                user.setAddress(address.trim());
            }
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password.trim());
            }
            if (avatar != null && !avatar.trim().isEmpty()) {
                user.setAvatar(avatar.trim());
            }
            
            if (cUserService.updateInfo(user)) {
                // 更新成功后，重新查询用户信息以确保数据同步
                CUser updatedUser = cUserService.getUserInfo(userId);
                if (updatedUser != null) {
                    return CResult.success("更新成功", updatedUser);
                }
                return CResult.error("更新成功但获取用户信息失败");
            }
            return CResult.error("更新失败");
        } catch (Exception e) {
            logger.severe("更新用户信息异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传结果，返回头像URL
     */
    @PostMapping("/upload_avatar")
    public CResult<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                        @RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            if (file == null || file.isEmpty()) {
                return CResult.error("请选择图片");
            }
            
            // 检查文件大小（限制为10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return CResult.error("图片大小不能超过10MB");
            }
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return CResult.error("文件名无效");
            }
            
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex).toLowerCase();
            }
            
            // 允许的图片格式
            String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
            boolean isValidExtension = false;
            for (String ext : allowedExtensions) {
                if (extension.equals(ext)) {
                    isValidExtension = true;
                    break;
                }
            }
            
            if (!isValidExtension) {
                return CResult.error("只支持JPG、PNG、GIF、BMP、WEBP格式的图片");
            }
            
            // 上传文件到阿里云OSS
            InputStream inputStream = file.getInputStream();
            String avatarUrl = OssUtil.uploadAvatar(inputStream, originalFilename);
            inputStream.close();
            
            // 更新用户头像URL
            CUser user = new CUser();
            user.setUserId(userId);
            user.setAvatar(avatarUrl);
            if (cUserService.updateInfo(user)) {
                return CResult.success("头像上传成功", avatarUrl);
            } else {
                return CResult.error("头像上传成功但更新用户信息失败");
            }
            
        } catch (Exception e) {
            logger.severe("上传头像异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @return 发送结果
     */
    @PostMapping("/sendCode")
    public CResult<Object> sendCode(@RequestParam("phone") String phone) {
        try {
            // 验证手机号格式
            if (phone == null || phone.trim().isEmpty()) {
                return CResult.error("手机号不能为空");
            }
            
            // 简单的手机号格式验证（11位数字）
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return CResult.error("手机号格式不正确");
            }
            
            // 使用 dypns API 发送验证码（系统自动生成验证码）
            String[] result = smsService.sendVerificationCodeWithDypns(phone);
            logger.info("发送验证码返回结果: " + (result != null ? "result[0]=" + result[0] + ", result[1]=" + result[1] : "null"));
            
            if (result != null && result.length >= 2) {
                // 存储 AccessCode/BizId（验证码由系统生成并发送到手机，响应中不包含）
                String generatedCode = result[0]; // 可能为 null，因为响应中不包含验证码
                String accessCode = result[1]; // BizId 作为 AccessCode
                
                if (accessCode != null && !accessCode.isEmpty()) {
                    verificationCodeManager.storeCodeWithAccessCode(phone, generatedCode, accessCode);
                    logger.info("验证码已存储，手机号: " + phone + ", AccessCode/BizId: " + accessCode);
                    return CResult.success("验证码发送成功", null);
                } else {
                    logger.warning("AccessCode/BizId 为空，无法存储");
                }
            } else {
                logger.warning("发送验证码返回结果异常: " + (result != null ? "长度=" + result.length : "null"));
            }
            return CResult.error("验证码发送失败，请检查服务器日志或联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("发送验证码时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 验证码登录
     * @param phone 手机号
     * @param code 验证码
     * @return 登录结果
     */
    @PostMapping("/loginByCode")
    public CResult<CUser> loginByCode(@RequestParam("phone") String phone,
                                     @RequestParam("code") String code) {
        try {
            // 验证手机号格式
            if (phone == null || phone.trim().isEmpty()) {
                return CResult.error("手机号不能为空");
            }
            
            if (code == null || code.trim().isEmpty()) {
                return CResult.error("验证码不能为空");
            }
            
            // 使用 dypns API 验证验证码（方法内部会自动获取 AccessCode）
            boolean isValid = smsService.verifyCodeWithDypns(phone, code);
            if (isValid) {
                // 验证码正确，根据手机号查询用户
                CUser user = cUserService.findByPhone(phone);
                if (user != null) {
                    user.setPassword(null); // 不返回密码
                    return CResult.success("登录成功", user);
                }
                return CResult.error("用户不存在");
            }
            return CResult.error("验证码错误或已过期");
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("验证验证码时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/userInfo")
    public CResult<CUser> getUserInfo(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            CUser user = cUserService.getUserInfo(userId);
            if (user != null) {
                return CResult.success("获取成功", user);
            }
            return CResult.error("用户不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("获取用户信息时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取关注/粉丝列表
     * @param userId 用户ID
     * @param type 类型：0=关注列表，1=粉丝列表
     * @return 用户列表
     */
    @GetMapping("/relation_list")
    public CResult<List<CUser>> getRelationList(@RequestParam("userId") Integer userId,
                                                @RequestParam("type") Integer type) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (type == null || (type != 0 && type != 1)) {
                return CResult.error("类型参数错误，0=关注列表，1=粉丝列表");
            }
            
            List<CUser> users = cUserService.getRelationList(userId, type);
            return CResult.success("查询成功", users);
        } catch (Exception e) {
            logger.severe("获取关注/粉丝列表异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 关注操作（关注/取消关注/移除粉丝）
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @param actionType 操作类型：0=关注，1=取消关注，2=移除粉丝
     * @return 操作结果
     */
    @PostMapping("/follow_action")
    public CResult<Object> followAction(@RequestParam("userId") Integer userId,
                                       @RequestParam("targetUserId") Integer targetUserId,
                                       @RequestParam("actionType") Integer actionType) {
        try {
            if (userId == null || targetUserId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (actionType == null || actionType < 0 || actionType > 2) {
                return CResult.error("操作类型错误，0=关注，1=取消关注，2=移除粉丝");
            }
            
            boolean success = cUserService.followAction(userId, targetUserId, actionType);
            if (success) {
                String message = actionType == 0 ? "关注成功" : (actionType == 1 ? "取消关注成功" : "移除粉丝成功");
                return CResult.success(message, null);
            }
            return CResult.error("操作失败");
        } catch (Exception e) {
            logger.severe("关注操作异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户统计信息（关注数、粉丝数、获赞数）
     * @param userId 用户ID
     * @return 统计信息
     */
    @GetMapping("/user_stats")
    public CResult<Map<String, Integer>> getUserStats(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Map<String, Integer> stats = cUserService.getUserStats(userId);
            return CResult.success("查询成功", stats);
        } catch (Exception e) {
            logger.severe("获取用户统计异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表（返回符合CPost格式的数据）
     */
    @GetMapping("/my_watches")
    public CResult<List<Map<String, Object>>> getMyWatches(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            // 调用WCircleService获取收藏列表
            Map<String, Object> result = wCircleService.getFavoritePosts(userId, 1, 100); // 获取前100条
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> posts = (List<Map<String, Object>>) result.get("posts");
            if (posts == null) {
                posts = new java.util.ArrayList<>();
            }
            
            // 转换为符合CPost格式的数据
            List<Map<String, Object>> cPostList = new java.util.ArrayList<>();
            for (Map<String, Object> post : posts) {
                Map<String, Object> cPost = new java.util.HashMap<>();
                cPost.put("postId", post.get("postId"));
                cPost.put("userId", post.get("userId"));
                cPost.put("title", post.get("title"));
                cPost.put("content", post.get("content"));
                cPost.put("image1", post.get("image1")); // 如果有图片列表，取第一张
                cPost.put("createTime", post.get("time")); // 时间字段
                cPost.put("authorName", post.get("author"));
                cPost.put("authorAvatar", post.get("avatar"));
                cPost.put("views", post.get("views"));
                cPost.put("likes", post.get("likes"));
                cPost.put("comments", post.get("comments"));
                cPostList.add(cPost);
            }
            
            return CResult.success("查询成功", cPostList);
        } catch (Exception e) {
            logger.severe("获取收藏列表异常: " + e.getMessage());
            e.printStackTrace();
            return CResult.error("查询失败: " + e.getMessage());
        }
    }
}


