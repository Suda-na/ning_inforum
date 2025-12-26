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
            
            WApiClient.ApiResponse response = WApiClient.get("/app/secondhand/items", params);
            if (response.success && response.getCode() == 200) {
                Object dataObj = response.getData();
                if (dataObj instanceof JSONObject) {
                    JSONObject data = (JSONObject) dataObj;
                    JSONArray itemsArray = data.getJSONArray("items");
                    List<WSecondHandItem> items = new ArrayList<>();
                    
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemJson = itemsArray.getJSONObject(i);
                        WSecondHandItem item = parseItemFromJson(itemJson);
                        items.add(item);
                    }
                    return items;
                }
            } else {
                Log.e(TAG, "获取二手商品列表失败: " + response.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取二手商品列表异常", e);
        }
        
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
        item.setSellerAvatar(avatar);
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

