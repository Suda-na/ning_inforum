package com.niit.controller;

import com.niit.pojo.User;
import com.niit.service.UserService;
import com.niit.service.UserServiceChang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserServiceChang userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/index";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(username, password);

            if (user != null) {
                // 登录成功：放入 Session
                session.setAttribute("loginUser", user);
                session.setMaxInactiveInterval(30 * 60);
                return "redirect:/index";
            } else {
                model.addAttribute("msg", "用户名或密码错误");
                return "login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("msg", e.getMessage());
            return "login";
        }
    }

    @GetMapping({"/", "/index"})
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
