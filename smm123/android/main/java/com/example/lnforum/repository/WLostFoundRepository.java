package com.example.lnforum.repository;

import android.util.Log;

import com.example.lnforum.model.WLostFoundItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 失物招领仓库，从PC后端API获取数据
 */
public class WLostFoundRepository {
    private static final String TAG = "WLostFoundRepository";

    /**
     * 从后端获取失物招领列表
     * @param type 类型：lost(失物), found(招领)
     */
    public static List<WLostFoundItem> getItems(String type, Integer page, Integer pageSize) {
        Log.d(TAG, "========== 开始获取失物招领 ==========");
        Log.d(TAG, "参数: type=" + type + ", page=" + page + ", pageSize=" + pageSize);
        
        try {
            Map<String, String> params = new HashMap<>();
            params.put("type", type);
            params.put("page", String.valueOf(page));
            params.put("pageSize", String.valueOf(pageSize));
            
            Log.d(TAG, "发送请求: /app/lostfound/items, params=" + params);
            WApiClient.ApiResponse response = WApiClient.get("/app/lostfound/items", params);
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
                        JSONArray itemsArray = data.getJSONArray("items");
                        Log.d(TAG, "成功获取items数组，长度: " + itemsArray.length());
                        
                        List<WLostFoundItem> items = new ArrayList<>();
                        
                        for (int i = 0; i < itemsArray.length(); i++) {
                            try {
                                Log.d(TAG, "开始解析第" + (i+1) + "条记录");
                                JSONObject itemJson = itemsArray.getJSONObject(i);
                                Log.d(TAG, "第" + (i+1) + "条记录JSON: " + itemJson.toString());
                                
                                WLostFoundItem item = parseItemFromJson(itemJson);
                                if (item != null) {
                                    items.add(item);
                                    Log.d(TAG, "成功添加第" + (i+1) + "条记录: id=" + item.getId() + ", title=" + item.getTitle() + ", 当前items.size()=" + items.size());
                                } else {
                                    Log.e(TAG, "第" + (i+1) + "条记录解析后为null");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "解析第" + (i+1) + "条记录时异常", e);
                                e.printStackTrace();
                            }
                        }
                        
                        Log.d(TAG, "========== 解析完成，共 " + items.size() + " 条记录 ==========");
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
            Log.e(TAG, "获取失物招领列表异常", e);
            e.printStackTrace();
        }
        
        Log.d(TAG, "========== 返回空列表 ==========");
        return new ArrayList<>();
    }

    /**
     * 从JSON解析失物招领对象
     */
    private static WLostFoundItem parseItemFromJson(JSONObject json) throws Exception {
        String id = json.optString("id", "");
        if (id.isEmpty()) {
            id = String.valueOf(json.optInt("postId", 0));
        }
        String publisher = json.optString("publisher", "匿名");
        String time = json.optString("time", "刚刚");
        String title = json.optString("title", "");
        String desc = json.optString("desc", "");
        String tag = json.optString("tag", "失物");
        String location = json.optString("location", "");
        int views = json.optInt("views", 0);
        
        List<String> images = new ArrayList<>();
        if (json.has("images")) {
            Object imagesObj = json.get("images");
            if (imagesObj instanceof JSONArray) {
                JSONArray imagesArray = (JSONArray) imagesObj;
                for (int i = 0; i < imagesArray.length(); i++) {
                    String imageUrl = imagesArray.optString(i, "");
                    if (!imageUrl.isEmpty()) {
                        images.add(imageUrl);
                    }
                }
            }
        }
        
        Log.d(TAG, "解析失物招领: id=" + id + ", title=" + title + ", tag=" + tag + ", views=" + views);
        
        WLostFoundItem item = new WLostFoundItem(id, publisher, time, title, desc, tag, location, views, images);
        String avatar = json.optString("avatar", "");
        Log.d(TAG, "解析失物招领头像: id=" + id + ", publisher=" + publisher + ", avatar=" + avatar);
        item.setAvatar(avatar);
        return item;
    }

    /**
     * 获取失物招领详情
     */
    public static WLostFoundItem getItem(String id) {
        try {
            WApiClient.ApiResponse response = WApiClient.get("/app/lostfound/item/" + id, null);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    return parseItemFromJson(data);
                }
            } else {
                Log.e(TAG, "获取失物招领详情失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取失物招领详情异常", e);
        }
        return null;
    }
}

