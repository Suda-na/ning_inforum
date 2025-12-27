package com.example.lnforum.repository;

import android.util.Log;

import com.example.lnforum.model.WErrandOrder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跑腿订单仓库，从PC后端API获取数据
 */
public class WErrandRepository {
    private static final String TAG = "WErrandRepository";

    /**
     * 从后端获取跑腿订单列表
     */
    public static List<WErrandOrder> getOrders(String status, Integer page, Integer pageSize) {
        Log.d(TAG, "========== 开始获取跑腿订单 ==========");
        Log.d(TAG, "参数: status=" + status + ", page=" + page + ", pageSize=" + pageSize);
        
        try {
            Map<String, String> params = new HashMap<>();
            params.put("status", status);
            params.put("page", String.valueOf(page));
            params.put("pageSize", String.valueOf(pageSize));
            
            Log.d(TAG, "发送请求: /app/errand/orders, params=" + params);
            WApiClient.ApiResponse response = WApiClient.get("/app/errand/orders", params);
            Log.d(TAG, "收到响应: code=" + response.getCode() + ", success=" + response.success);
            Log.d(TAG, "响应message: " + response.getMessage());
            Log.d(TAG, "响应data是否为null: " + (response.getData() == null));
            
            if (response.getData() != null) {
                Log.d(TAG, "响应data类型: " + response.getData().getClass().getName());
                Log.d(TAG, "响应data内容: " + response.getData().toString());
            }
            
            if (response.success && response.getCode() == 200) {
                Log.d(TAG, "响应成功，开始解析数据");
                Object dataObj = response.getData();
                
                if (dataObj == null) {
                    Log.e(TAG, "dataObj为null，无法解析");
                    return new ArrayList<>();
                }
                
                Log.d(TAG, "dataObj类型: " + dataObj.getClass().getName());
                
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    Log.d(TAG, "data是JSONObject，keys: " + data.keys().toString());
                    
                    // 参考二手集市的解析方式，直接使用getJSONArray
                    try {
                        JSONArray ordersArray = data.getJSONArray("orders");
                        Log.d(TAG, "成功获取orders数组，长度: " + ordersArray.length());
                        
                        List<WErrandOrder> orders = new ArrayList<>();
                        
                        for (int i = 0; i < ordersArray.length(); i++) {
                            try {
                                Log.d(TAG, "开始解析第" + (i+1) + "条订单");
                                JSONObject orderJson = ordersArray.getJSONObject(i);
                                Log.d(TAG, "第" + (i+1) + "条订单JSON: " + orderJson.toString());
                                
                                WErrandOrder order = parseOrderFromJson(orderJson);
                                if (order != null) {
                                    orders.add(order);
                                    Log.d(TAG, "成功添加第" + (i+1) + "条订单: id=" + order.getId() + ", title=" + order.getTitle());
                                } else {
                                    Log.e(TAG, "第" + (i+1) + "条订单解析后为null");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "解析第" + (i+1) + "条订单时异常", e);
                                e.printStackTrace();
                            }
                        }
                        
                        Log.d(TAG, "========== 解析完成，共 " + orders.size() + " 条订单 ==========");
                        return orders;
                    } catch (Exception e) {
                        Log.e(TAG, "获取orders数组失败", e);
                        e.printStackTrace();
                        Log.e(TAG, "data完整内容: " + data.toString());
                    }
                } else {
                    Log.e(TAG, "dataObj不是JSONObject类型: " + dataObj.getClass().getName());
                }
            } else {
                Log.e(TAG, "响应失败: code=" + response.getCode() + ", message=" + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取跑腿订单列表异常", e);
            e.printStackTrace();
        }
        
        Log.d(TAG, "========== 返回空列表 ==========");
        return new ArrayList<>();
    }

    /**
     * 从JSON解析订单对象
     */
    private static WErrandOrder parseOrderFromJson(JSONObject json) throws Exception {
        Log.d(TAG, "开始解析订单JSON: " + json.toString());
        
        String id = json.optString("id", "");
        if (id.isEmpty()) {
            id = String.valueOf(json.optInt("postId", 0));
        }
        Log.d(TAG, "订单id: " + id);
        
        String title = json.optString("title", "");
        Log.d(TAG, "订单title: " + title);
        
        String desc = json.optString("desc", "");
        Log.d(TAG, "订单desc: " + desc);
        
        String from = json.optString("from", "");
        Log.d(TAG, "订单from: " + from);
        
        String to = json.optString("to", "");
        Log.d(TAG, "订单to: " + to);
        
        String price = json.optString("price", "0.00");
        Log.d(TAG, "订单price: " + price);
        
        String status = json.optString("status", "waiting");
        Log.d(TAG, "订单status: " + status);
        
        String tag = json.optString("tag", "匿名");
        if (tag.isEmpty()) {
            tag = json.optString("publisher", "匿名");
        }
        Log.d(TAG, "订单tag: " + tag);
        
        // 解析图片列表
        List<String> images = new ArrayList<>();
        if (json.has("images")) {
            try {
                Object imagesObj = json.get("images");
                Log.d(TAG, "订单images类型: " + (imagesObj != null ? imagesObj.getClass().getName() : "null"));
                if (imagesObj instanceof JSONArray) {
                    JSONArray imagesArray = (JSONArray) imagesObj;
                    for (int i = 0; i < imagesArray.length(); i++) {
                        String imageUrl = imagesArray.optString(i, "");
                        if (!imageUrl.isEmpty()) {
                            images.add(imageUrl);
                            Log.d(TAG, "添加图片: " + imageUrl);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "解析图片列表失败", e);
            }
        }
        Log.d(TAG, "订单图片数量: " + images.size());
        
        WErrandOrder order = new WErrandOrder(id, tag, title, desc, from, to, price, status, images);
        Log.d(TAG, "订单解析完成: id=" + order.getId() + ", title=" + order.getTitle());
        
        return order;
    }

    /**
     * 获取订单详情
     */
    public static WErrandOrder getOrder(String id) {
        try {
            WApiClient.ApiResponse response = WApiClient.get("/app/errand/order/" + id, null);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    return parseOrderFromJson(data);
                }
            } else {
                Log.e(TAG, "获取订单详情失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取订单详情异常", e);
        }
        return null;
    }

    public List<WErrandOrder> getSampleOrders() {
        List<WErrandOrder> list = new ArrayList<>();
        list.add(new WErrandOrder(
                "1",
                "帮寄快递",
                "怀远一教开一张成绩单，送到A区文贺楼盖章",
                "需要跑腿顺丰寄出，邮费另算",
                "怀远一教413",
                "菜鸟驿站",
                "25.00",
                "completed"
        ));
        list.add(new WErrandOrder(
                "2",
                "帮取外卖",
                "一份过桥米线，尾号8775",
                "带上一次性手套，注意防洒",
                "宁夏大学文萃校区南门",
                "宁夏大学文萃校区北门",
                "5.00",
                "waiting"
        ));
        list.add(new WErrandOrder(
                "3",
                "帮取快递",
                "代取快递送到图书馆一楼",
                "轻拿轻放，有易碎标识",
                "文萃校区菜鸟驿站",
                "宁夏大学文萃校区图书馆",
                "15.00",
                "delivering"
        ));
        list.add(new WErrandOrder(
                "4",
                "帮送物品",
                "帮忙送羽毛球到文萃北门，很近",
                "尽快送达，联系收件人后放置门口",
                "文萃校区六号楼412",
                "文萃北门",
                "15.00",
                "completed"
        ));
        return list;
    }
}

