package com.example.lnforum.repository;

import android.util.Log;

import com.example.lnforum.model.WSecondHandItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二手集市仓库，从PC后端API获取数据
 */
public class WSecondHandRepository {
    private static final String TAG = "WSecondHandRepository";

    /**
     * 从后端获取二手商品列表
     */
    public static List<WSecondHandItem> getItems(Integer page, Integer pageSize, Integer tagId, String sortType) {
        Log.d(TAG, "========== 开始获取二手商品 ==========");
        Log.d(TAG, "参数: page=" + page + ", pageSize=" + pageSize + ", tagId=" + tagId + ", sortType=" + sortType);
        
        try {
            Map<String, String> params = new HashMap<>();
            params.put("page", String.valueOf(page));
            params.put("pageSize", String.valueOf(pageSize));
            if (tagId != null) {
                params.put("tagId", String.valueOf(tagId));
            }
            if (sortType != null && !sortType.isEmpty()) {
                params.put("sortType", sortType);
            }
            
            Log.d(TAG, "发送请求: /app/secondhand/items, params=" + params);
            WApiClient.ApiResponse response = WApiClient.get("/app/secondhand/items", params);
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
                    
                    try {
                        JSONArray itemsArray = data.getJSONArray("items");
                        Log.d(TAG, "成功获取items数组，长度: " + itemsArray.length());
                        
                        List<WSecondHandItem> items = new ArrayList<>();
                        
                        for (int i = 0; i < itemsArray.length(); i++) {
                            try {
                                Log.d(TAG, "开始解析第" + (i+1) + "条商品");
                                JSONObject itemJson = itemsArray.getJSONObject(i);
                                Log.d(TAG, "第" + (i+1) + "条商品JSON: " + itemJson.toString());
                                
                                WSecondHandItem item = parseItemFromJson(itemJson);
                                if (item != null) {
                                    items.add(item);
                                    Log.d(TAG, "成功添加第" + (i+1) + "条商品: id=" + item.getId() + ", title=" + item.getTitle());
                                } else {
                                    Log.e(TAG, "第" + (i+1) + "条商品解析后为null");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "解析第" + (i+1) + "条商品时异常", e);
                                e.printStackTrace();
                            }
                        }
                        
                        Log.d(TAG, "========== 解析完成，共 " + items.size() + " 条商品 ==========");
                        return items;
                    } catch (Exception e) {
                        Log.e(TAG, "获取items数组失败", e);
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
            Log.e(TAG, "获取二手商品列表异常", e);
            e.printStackTrace();
        }
        
        Log.d(TAG, "========== 返回空列表 ==========");
        // 失败时返回空列表
        return new ArrayList<>();
    }

    /**
     * 从JSON解析商品对象
     */
    private static WSecondHandItem parseItemFromJson(JSONObject json) throws Exception {
        String id = json.getString("id");
        String publisher = json.optString("publisher", "匿名");
        String time = json.optString("time", "刚刚");
        String title = json.optString("title", "");
        String desc = json.optString("desc", "");
        String tag = json.optString("tag", "二手");
        String price = json.optString("price", "0.00");
        int views = json.optInt("views", 0);
        
        List<String> images = new ArrayList<>();
        if (json.has("images")) {
            JSONArray imagesArray = json.getJSONArray("images");
            for (int i = 0; i < imagesArray.length(); i++) {
                images.add(imagesArray.getString(i));
            }
        }
        
        WSecondHandItem item = new WSecondHandItem(id, publisher, time, title, desc, tag, price, "宁夏大学", views, images);
        String avatar = json.optString("avatar", "");
        Log.d(TAG, "解析二手商品头像: id=" + id + ", publisher=" + publisher + ", avatar=" + avatar);
        item.setSellerAvatar(avatar);
        
        // 解析卖家ID
        Integer sellerId = null;
        if (json.has("sellerId")) {
            sellerId = json.optInt("sellerId", 0);
            if (sellerId == 0) sellerId = null;
        } else if (json.has("userId")) {
            sellerId = json.optInt("userId", 0);
            if (sellerId == 0) sellerId = null;
        }
        item.setSellerId(sellerId);
        Log.d(TAG, "解析二手商品卖家ID: id=" + id + ", sellerId=" + sellerId);
        
        return item;
    }

    /**
     * 获取商品详情
     */
    public static WSecondHandItem getItem(String id) {
        try {
            WApiClient.ApiResponse response = WApiClient.get("/app/secondhand/item/" + id, null);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    return parseItemFromJson(data);
                }
            } else {
                Log.e(TAG, "获取商品详情失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取商品详情异常", e);
        }
        return null;
    }
}

