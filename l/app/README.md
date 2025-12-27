# Android API 配置说明

## 已完成的配置

### 1. Spring 配置 (`springmvc.xml`)
- ✅ 添加了 `com.app.*` 包的组件扫描
- ✅ 添加了 `com.app.dao` 的 MyBatis Mapper 扫描
- ✅ 添加了 `appmapper` 目录的 XML 映射文件扫描

### 2. CORS 跨域配置 (`CorsConfig.java`)
- ✅ 支持所有源的跨域请求
- ✅ 允许常用 HTTP 方法（GET, POST, PUT, DELETE 等）
- ✅ 允许携带凭证（Cookie 等）
- ✅ 配置了 CORS 过滤器

### 3. 统一响应格式 (`Result.java`)
- ✅ 统一的 API 响应封装类
- ✅ 包含成功、失败、错误等多种响应方法
- ✅ 支持泛型，可返回任意类型数据

### 4. 全局异常处理 (`GlobalExceptionHandler.java`)
- ✅ 统一处理所有异常
- ✅ 自动转换为统一的响应格式
- ✅ 记录异常日志

### 5. 示例控制器 (`AppBaseController.java`)
- ✅ 基础的 Controller 示例
- ✅ 包含测试接口 `/app/test`

## 使用示例

### 创建 Android API Controller

```java
package com.app.controller;

import com.app.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/user")
public class UserController {
    
    // GET 请求示例
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Integer id) {
        // 业务逻辑...
        return Result.success(user);
    }
    
    // POST 请求示例
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 业务逻辑...
        return Result.success(loginResponse);
    }
    
    // 错误响应示例
    @GetMapping("/error")
    public Result<?> testError() {
        return Result.error("这是一个错误示例");
    }
}
```

### Android 端调用示例（Kotlin）

```kotlin
// Retrofit 接口定义
interface ApiService {
    @GET("app/test")
    suspend fun test(): Result<String>
    
    @POST("app/user/login")
    suspend fun login(@Body request: LoginRequest): Result<LoginResponse>
}

// 使用示例
val response = apiService.test()
if (response.code == 200) {
    println("成功: ${response.data}")
} else {
    println("失败: ${response.message}")
}
```

## API 路径规范

所有 Android API 的路径都应该以 `/app` 开头，例如：
- `/app/test` - 测试接口
- `/app/user/login` - 用户登录
- `/app/post/list` - 帖子列表
- 等等...

## 响应格式说明

所有 API 响应都遵循以下格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": { ... }
}
```

- `code`: 状态码（200=成功，400=客户端错误，500=服务器错误）
- `message`: 响应消息
- `data`: 响应数据（可为 null）

## 注意事项

1. **CORS 配置**: 生产环境建议将 `allowedOriginPatterns("*")` 改为具体的 Android 应用域名
2. **异常处理**: 所有异常都会被全局异常处理器捕获并转换为统一格式
3. **MyBatis Mapper**: 如果需要在 `com.app.dao` 下创建 Mapper 接口，对应的 XML 文件应放在 `src/main/resources/appmapper/` 目录下
4. **日志记录**: 所有异常都会记录到日志中，便于排查问题

## 下一步

1. 在 `com.app.controller` 包下创建具体的业务 Controller
2. 在 `com.app.service` 包下创建业务逻辑 Service
3. 在 `com.app.dao` 包下创建数据访问接口
4. 在 `src/main/resources/appmapper/` 目录下创建对应的 MyBatis XML 映射文件

