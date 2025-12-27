package com.app.controller;

import com.app.pojo.SPostVO;
import com.app.service.SPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安卓端帖子Controller
 */
@RestController
@RequestMapping("/api/android/post")
public class SPostController {

    @Autowired
    private SPostService sPostService;

    /**
     * 发布动态（分类：动态）
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/publish/circle")
    public Map<String, Object> publishCircle(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取参数
            Integer userId = getInteger(request, "userId");
            String title = getString(request, "title");
            String content = getString(request, "content");
            String tagName = getString(request, "tagName");
            String image1 = getString(request, "image1");
            String image2 = getString(request, "image2");
            String image3 = getString(request, "image3");
            
            // 验证必填字段
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return response;
            }
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "标题不能为空");
                return response;
            }
            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "内容不能为空");
                return response;
            }
            
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("categoryId", 1); // 动态分类
            params.put("title", title);
            params.put("content", content);
            params.put("tagName", tagName);
            params.put("image1", image1);
            params.put("image2", image2);
            params.put("image3", image3);
            
            // 发布帖子
            Integer postId = sPostService.publishPost(params);
            
            if (postId != null) {
                response.put("success", true);
                response.put("message", "发布成功");
                response.put("postId", postId);
            } else {
                response.put("success", false);
                response.put("message", "发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SPostController.publishCircle - 异常: " + e.getMessage());
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 发布跑腿（分类：跑腿）
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/publish/errand")
    public Map<String, Object> publishErrand(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取参数
            Integer userId = getInteger(request, "userId");
            String title = getString(request, "title");
            String description = getString(request, "description");
            String amount = getString(request, "amount");
            String remark = getString(request, "remark");
            String startPoint = getString(request, "startPoint");
            String endPoint = getString(request, "endPoint");
            String image1 = getString(request, "image1");
            String image2 = getString(request, "image2");
            String image3 = getString(request, "image3");
            
            // 验证必填字段
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return response;
            }
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "标题不能为空");
                return response;
            }
            if (description == null || description.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "描述不能为空");
                return response;
            }
            
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("categoryId", 2); // 跑腿分类
            params.put("title", title);
            params.put("content", description);
            params.put("startPoint", startPoint);
            params.put("endPoint", endPoint);
            params.put("image1", image1);
            params.put("image2", image2);
            params.put("image3", image3);
            
            // 处理金额
            if (amount != null && !amount.trim().isEmpty()) {
                try {
                    params.put("price", new BigDecimal(amount));
                } catch (Exception e) {
                    // 金额格式错误，忽略
                }
            }
            
            // 处理备注（放到itemInfo字段）
            if (remark != null && !remark.trim().isEmpty()) {
                params.put("itemInfo", remark);
            }
            
            // 发布帖子
            Integer postId = sPostService.publishPost(params);
            
            if (postId != null) {
                response.put("success", true);
                response.put("message", "发布成功");
                response.put("postId", postId);
            } else {
                response.put("success", false);
                response.put("message", "发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SPostController.publishErrand - 异常: " + e.getMessage());
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 发布失物招领（分类：失物招领）
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/publish/lostfound")
    public Map<String, Object> publishLostFound(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取参数
            Integer userId = getInteger(request, "userId");
            String title = getString(request, "title");
            String desc = getString(request, "desc");
            String tagName = getString(request, "tagName");
            String contact = getString(request, "contact");
            String location = getString(request, "location");
            String image1 = getString(request, "image1");
            String image2 = getString(request, "image2");
            String image3 = getString(request, "image3");
            
            // 验证必填字段
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return response;
            }
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "标题不能为空");
                return response;
            }
            if (desc == null || desc.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "描述不能为空");
                return response;
            }
            if (tagName == null || tagName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择分类标签");
                return response;
            }
            
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("categoryId", 4); // 失物招领分类
            params.put("title", title);
            params.put("content", desc);
            params.put("tagName", tagName);
            params.put("contactInfo", contact);
            params.put("itemInfo", location);
            params.put("image1", image1);
            params.put("image2", image2);
            params.put("image3", image3);
            
            // 发布帖子
            Integer postId = sPostService.publishPost(params);
            
            if (postId != null) {
                response.put("success", true);
                response.put("message", "发布成功");
                response.put("postId", postId);
            } else {
                response.put("success", false);
                response.put("message", "发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SPostController.publishLostFound - 异常: " + e.getMessage());
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 发布二手（分类：二手集市）
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/publish/secondhand")
    public Map<String, Object> publishSecondHand(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取参数
            Integer userId = getInteger(request, "userId");
            String title = getString(request, "title");
            String desc = getString(request, "desc");
            String price = getString(request, "price");
            String tagName = getString(request, "tagName");
            String image1 = getString(request, "image1");
            String image2 = getString(request, "image2");
            String image3 = getString(request, "image3");
            
            // 验证必填字段
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return response;
            }
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "标题不能为空");
                return response;
            }
            if (desc == null || desc.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "描述不能为空");
                return response;
            }
            if (tagName == null || tagName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择分类标签");
                return response;
            }
            
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("categoryId", 3); // 二手集市分类
            params.put("title", title);
            params.put("content", desc);
            params.put("tagName", tagName);
            params.put("image1", image1);
            params.put("image2", image2);
            params.put("image3", image3);
            
            // 处理价格
            if (price != null && !price.trim().isEmpty()) {
                try {
                    params.put("price", new BigDecimal(price));
                } catch (Exception e) {
                    params.put("price", BigDecimal.ZERO);
                }
            } else {
                params.put("price", BigDecimal.ZERO);
            }
            
            // 发布帖子
            Integer postId = sPostService.publishPost(params);
            
            if (postId != null) {
                response.put("success", true);
                response.put("message", "发布成功");
                response.put("postId", postId);
            } else {
                response.put("success", false);
                response.put("message", "发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SPostController.publishSecondHand - 异常: " + e.getMessage());
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        
        return response;
    }

    // 辅助方法：安全获取Integer
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    // 辅助方法：安全获取String
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}

