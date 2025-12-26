package com.example.lnforum.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lnforum.model.WUser;

/**
 * 用户会话管理工具类
 */
public class WSessionManager {
    private static final String PREF_NAME = "LnForumSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static WSessionManager instance;

    private WSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized WSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new WSessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 设置登录状态
     */
    public void setLogin(boolean isLoggedIn, WUser user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        if (user != null) {
            editor.putInt(KEY_USER_ID, user.getUserId());
            editor.putString(KEY_USERNAME, user.getUsername());
        }
        editor.apply();
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 获取当前用户
     */
    public WUser getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }
        int userId = pref.getInt(KEY_USER_ID, -1);
        String username = pref.getString(KEY_USERNAME, "");
        if (userId == -1 || username.isEmpty()) {
            return null;
        }
        WUser user = new WUser();
        user.setUserId(userId);
        user.setUsername(username);
        return user;
    }

    /**
     * 保存用户信息（更新当前登录用户的信息）
     * 注意：密码和签名等敏感信息应该通过API更新到服务器，这里只保存基本用户信息
     */
    public void saveUser(WUser user) {
        if (user != null && isLoggedIn()) {
            // 只更新已保存的字段
            if (user.getUserId() != null) {
                editor.putInt(KEY_USER_ID, user.getUserId());
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                editor.putString(KEY_USERNAME, user.getUsername());
            }
            // 注意：密码和签名等敏感信息不应该保存在本地SharedPreferences中
            // 这些信息应该通过API更新到服务器
            editor.apply();
        }
    }

    /**
     * 登出
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

