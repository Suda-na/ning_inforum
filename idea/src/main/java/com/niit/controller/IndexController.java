package com.niit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页 Controller
 * 处理根路径请求，跳转到首页
 */
@Controller
public class IndexController {

    /**
     * 处理根路径请求，跳转到首页
     * @return 视图名称，会跳转到 index.html
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}

