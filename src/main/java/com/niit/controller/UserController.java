package com.niit.controller;

import com.niit.pojo.User;
import com.niit.service.UserService;
import com.niit.service.UserServiceChang;
import com.niit.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.InputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceChang userService;

    // ✅ 从 Session 获取当前登录用户 ID（增加兜底：userId 为空就按 username 再查一次补全）
    private Integer getCurrentUserId(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return null;

        if (loginUser.getUserId() != null) return loginUser.getUserId();

        // 兜底补全（防止 loginUser 没映射到 userId）
        if (loginUser.getUsername() != null) {
            User full = userService.getUserByUsername(loginUser.getUsername());
            if (full != null && full.getUserId() != null) {
                session.setAttribute("loginUser", full);
                return full.getUserId();
            }
        }
        return null;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        if (user != null) user.setPassword(null);
        model.addAttribute("user", user);
        return "profile_info";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(User user, HttpSession session, Model model) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) return "redirect:/login";

        user.setUserId(userId);

        if (userService.updateUserInfo(user)) {
            model.addAttribute("msg", "保存成功！");
            session.setAttribute("loginUser", userService.getUserById(userId));
        } else {
            model.addAttribute("msg", "保存失败，请重试。");
        }

        User updatedUser = userService.getUserById(userId);
        if (updatedUser != null) updatedUser.setPassword(null);
        model.addAttribute("user", updatedUser);
        return "profile_info";
    }

    @GetMapping("/avatar")
    public String showAvatar(Model model, HttpSession session) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "profile_avatar";
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("avatarFile") MultipartFile file,
                               HttpSession session, Model model) {
        Integer userId = getCurrentUserId(session);
        if (userId == null) return "redirect:/login";

        if (file.isEmpty()) {
            model.addAttribute("msg", "请选择图片");
            model.addAttribute("user", userService.getUserById(userId));
            return "profile_avatar";
        }

        // 检查文件大小（限制为10MB）
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            model.addAttribute("msg", "图片大小不能超过10MB");
            model.addAttribute("user", userService.getUserById(userId));
            return "profile_avatar";
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            model.addAttribute("msg", "文件名无效");
            model.addAttribute("user", userService.getUserById(userId));
            return "profile_avatar";
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
            model.addAttribute("msg", "只支持JPG、PNG、GIF、BMP、WEBP格式的图片");
            model.addAttribute("user", userService.getUserById(userId));
            return "profile_avatar";
        }

        try {
            // 上传文件到阿里云OSS
            String avatarUrl = null;
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
                avatarUrl = OssUtil.uploadAvatar(inputStream, originalFilename);
            } catch (Exception uploadException) {
                // 上传失败，记录异常
                uploadException.printStackTrace();
                model.addAttribute("msg", "头像上传失败：" + (uploadException.getMessage() != null ? uploadException.getMessage() : "未知错误"));
                model.addAttribute("user", userService.getUserById(userId));
                return "profile_avatar";
            } finally {
                // 安全关闭输入流
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // 验证上传是否成功
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                // 保存OSS的完整URL到数据库
                userService.updateUserAvatar(userId, avatarUrl);

                User currentUser = userService.getUserById(userId);
                session.setAttribute("loginUser", currentUser);

                model.addAttribute("msg", "头像上传成功！");
                model.addAttribute("user", currentUser);
            } else {
                model.addAttribute("msg", "头像上传失败：未获取到图片URL");
                model.addAttribute("user", userService.getUserById(userId));
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "上传出错：" + e.getMessage());
            model.addAttribute("user", userService.getUserById(userId));
        }

        return "profile_avatar";
    }
}
