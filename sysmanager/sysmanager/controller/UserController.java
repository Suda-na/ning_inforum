package com.niit.controller;

import com.niit.pojo.User;
import com.niit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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

    private static final String UPLOAD_DIR = "D:/niit_uploads/";

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

        try {
            File fileDir = new File(UPLOAD_DIR);
            if (!fileDir.exists()) fileDir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String suffix = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + suffix;

            file.transferTo(new File(UPLOAD_DIR + fileName));

            userService.updateUserAvatar(userId, fileName);

            User currentUser = userService.getUserById(userId);
            session.setAttribute("loginUser", currentUser);

            model.addAttribute("msg", "头像上传成功！");
            model.addAttribute("user", currentUser);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("msg", "上传出错：" + e.getMessage());
            model.addAttribute("user", userService.getUserById(userId));
        }

        return "profile_avatar";
    }
}
