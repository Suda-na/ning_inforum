package com.example.android_java2.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.android_java2.model.CUser; // 使用 CUser
import com.google.gson.Gson;

public class CSessionManager {
    private static final String PREF_NAME = "cuser_session";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_DATA = "cuser_data";

    private static CSessionManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private CSessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized CSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new CSessionManager(context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    // ✅ 只保留这个保存 User 对象的方法，删掉了那个只有 String 的假登录
    public void saveCUser(CUser user) {
        if (user == null) return;
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USER_DATA, gson.toJson(user))
                .apply();
    }

    public CUser getCurrentCUser() {
        String json = prefs.getString(KEY_USER_DATA, null);
        if (json != null) {
            return gson.fromJson(json, CUser.class);
        }
        return null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}