package com.niit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户管理 Controller
 * 示例：展示如何通过 Controller 跳转到对应的页面
 */
@Controller
@RequestMapping("/admin")
public class ClientController {

    /**
     * 跳转到用户列表页面
     * @return 视图名称，会跳转到 clients.html
     */
    @RequestMapping("/clients")
    public String clients() {
        // 直接返回视图名称，Spring MVC 会根据配置跳转到 /WEB-INF/templates/html/clients.html
        return "clients";
    }
}

