package com.app.controller;

import com.app.common.Result;
import com.app.service.WErrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 安卓端跑腿Controller
 */
@RestController
@RequestMapping("/app/errand")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WErrandController {

    @Autowired
    private WErrandService errandService;

    /**
     * 获取跑腿订单列表
     * @param status 状态：unaccepted(未接单，status=0或1), completed(已完成，status=3)
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    @GetMapping("/orders")
    public Result<Map<String, Object>> getOrders(
            @RequestParam(value = "status", defaultValue = "unaccepted") String status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            Map<String, Object> result = errandService.getOrders(status, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否是跑腿员
     * @param userId 用户ID
     * @return 是否是跑腿员
     */
    @GetMapping("/check-runner")
    public Result<Map<String, Object>> checkRunner(@RequestParam("userId") Integer userId) {
        try {
            boolean isRunner = errandService.isErrandRunner(userId);
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("isRunner", isRunner);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取跑腿订单详情
     * @param orderId 订单ID（帖子ID）
     * @return 订单详情
     */
    @GetMapping("/order/{orderId}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable("orderId") Integer orderId) {
        try {
            Map<String, Object> result = errandService.getOrderDetail(orderId);
            if (result == null) {
                return Result.error("订单不存在");
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}

