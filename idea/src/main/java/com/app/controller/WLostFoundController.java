package com.app.controller;

import com.app.common.Result;
import com.app.service.WLostFoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安卓端失物招领Controller
 */
@RestController
@RequestMapping("/app/lostfound")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WLostFoundController {

    @Autowired
    private WLostFoundService lostFoundService;

    /**
     * 获取失物招领列表
     * @param type 类型：lost(失物), found(招领)
     * @param page 页码
     * @param pageSize 每页数量
     * @return 列表
     */
    @GetMapping("/items")
    public Result<Map<String, Object>> getItems(
            @RequestParam(value = "type", defaultValue = "lost") String type,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = lostFoundService.getItems(type, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取失物招领详情
     * @param itemId 物品ID（帖子ID）
     * @return 详情
     */
    @GetMapping("/item/{itemId}")
    public Result<Map<String, Object>> getItemDetail(@PathVariable("itemId") Integer itemId) {
        try {
            Map<String, Object> result = lostFoundService.getItemDetail(itemId);
            if (result == null) {
                return Result.error("物品不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 增加失物招领浏览量
     * @param itemId 物品ID（帖子ID）
     */
    @PostMapping("/item/{itemId}/view")
    public Result<Void> incrementViewCount(@PathVariable("itemId") Integer itemId) {
        try {
            lostFoundService.incrementViewCount(itemId);
            return Result.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}

