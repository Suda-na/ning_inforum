package com.app.controller;

import com.app.common.Result;
import com.app.service.WTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 安卓端标签Controller
 */
@RestController
@RequestMapping("/app/tag")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WTagController {

    @Autowired
    private WTagService tagService;

    /**
     * 获取标签列表（按分类筛选）
     * @param categoryId 分类ID（可选，1=动态，2=跑腿，3=二手集市，4=失物招领）
     * @return 标签列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getTags(
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        try {
            List<Map<String, Object>> tags = tagService.getTagsByCategory(categoryId);
            return Result.success(tags);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}

