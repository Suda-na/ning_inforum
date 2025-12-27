package com.app.controller;

import com.app.common.Result;
import com.app.service.WSecondHandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安卓端二手集市Controller
 */
@RestController
@RequestMapping("/app/secondhand")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WSecondHandController {

    @Autowired
    private WSecondHandService secondHandService;

    /**
     * 获取二手商品列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param tagId 标签ID（可选）
     * @return 商品列表
     */
    @GetMapping("/items")
    public Result<Map<String, Object>> getItems(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "tagId", required = false) Integer tagId,
            @RequestParam(value = "sortType", required = false) String sortType) {
        try {
            Map<String, Object> result = secondHandService.getItems(page, pageSize, tagId, sortType);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个商品详情（会自动增加浏览量）
     * @param itemId 商品ID
     * @return 商品详情
     */
    @GetMapping("/item/{itemId}")
    public Result<Map<String, Object>> getItem(@PathVariable("itemId") Integer itemId) {
        try {
            Map<String, Object> result = secondHandService.getItem(itemId);
            if (result == null) {
                return Result.error("商品不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 增加商品浏览量
     * @param itemId 商品ID
     */
    @PostMapping("/item/{itemId}/view")
    public Result<Void> incrementViewCount(@PathVariable("itemId") Integer itemId) {
        try {
            secondHandService.incrementViewCount(itemId);
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}

