package com.app.controller;

import com.app.common.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Android API 基础控制器
 * 所有 Android API 的 Controller 都应该放在 com.app.controller 包下
 * 并使用 @RestController 和统一的路径前缀 /app
 */
@RestController
@RequestMapping("/app")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppBaseController {
    
    /**
     * 测试接口
     * 用于验证 Android 连接配置是否正常
     * 
     * @return 测试响应
     */
    @RequestMapping("/test")
    public Result<String> test() {
        return Result.success("Android API 连接成功！");
    }
}

