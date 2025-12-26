package com.example.lnforum.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.Conversation;
import com.example.lnforum.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody;

/**
 * 消息仓库：与PC后端连接，获取互关私信和粉丝来信
 */
public class LMessageRepository {

    private static final String PREF_NAME = "message_repo";
    private static final String KEY_BLACKLIST = "blacklist_users";
    private static final String BASE_URL = "http://192.168.172.1:8081";
    private static final String FOLLOW_MESSAGES_URL = BASE_URL + "/api/cuser/message/mutualFollow";
    private static final String FAN_MESSAGES_URL = BASE_URL + "/api/cuser/message/fan";
    private static final String ADMIN_MESSAGES_URL = BASE_URL + "/api/cuser/message/admin";
    private static final String CONVERSATION_URL = BASE_URL + "/api/cuser/message/conversation";
    private static final String SEND_MESSAGE_URL = BASE_URL + "/api/cuser/message/send";
    private static final String UPLOAD_IMAGE_URL = BASE_URL + "/api/cuser/message/uploadImage";
    private static final String SEND_IMAGE_URL = BASE_URL + "/api/cuser/message/sendImage";

    /**
     * 会话响应数据模型
     * 匹配PC后端返回的Map字段
     */
    public static class ConversationResponse {
        private Integer userId;
        private String username;
        private String lastMessage;      // 原始消息内容
        private String messagePreview;   // 处理过的消息预览（包含"前缀"）
        private String lastMessageTime;
        private Integer unreadCount;
        private String avatar;           // 用户头像URL
        private Integer msgFormat;       // 消息格式0=文本1=图片

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        public String getMessagePreview() { return messagePreview; }
        public void setMessagePreview(String messagePreview) { this.messagePreview = messagePreview; }
        public String getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        public Integer getUnreadCount() { return unreadCount; }
        public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getMsgFormat() { return msgFormat; }
        public void setMsgFormat(Integer msgFormat) { this.msgFormat = msgFormat; }
    }

    /**
     * 获取互关私信列表（异步）
     */
    public static void getFollowConversations(Context context, ConversationCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(new ArrayList<>(), "用户未登录");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // PC后端使用GET方法，参数通过URL传�?
        String url = FOLLOW_MESSAGES_URL + "?userId=" + currentUser.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "网络请求失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(new ArrayList<>(), "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "FollowMessages Response: " + json);
                
                try {
                    // 检查是否返回HTML（服务器错误�?
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器错误，请检查后端配置");
                        }
                        return;
                    }
                    
                    // 检查空响应
                    if (json.trim().isEmpty()) {
                        android.util.Log.e("LMessageRepository", "服务器返回空响应");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器返回空数据");
                        }
                        return;
                    }
                    
                    // 使用JsonParser手动解析，以处理时间字段可能是数组的情况
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull() 
                            ? jsonObject.get("message").getAsString() : null;
                    
                    if (code == 200) {
                        List<ConversationResponse> data = new ArrayList<>();
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonObject.getAsJsonArray("data");
                            for (JsonElement element : dataArray) {
                                JsonObject item = element.getAsJsonObject();
                                ConversationResponse resp = new ConversationResponse();
                                
                                if (item.has("userId") && !item.get("userId").isJsonNull()) {
                                    resp.setUserId(item.get("userId").getAsInt());
                                }
                                if (item.has("username") && !item.get("username").isJsonNull()) {
                                    resp.setUsername(item.get("username").getAsString());
                                }
                                if (item.has("lastMessage") && !item.get("lastMessage").isJsonNull()) {
                                    resp.setLastMessage(item.get("lastMessage").getAsString());
                                }
                                if (item.has("messagePreview") && !item.get("messagePreview").isJsonNull()) {
                                    resp.setMessagePreview(item.get("messagePreview").getAsString());
                                }
                                // 处理时间字段：可能是字符串或数组
                                if (item.has("lastMessageTime") && !item.get("lastMessageTime").isJsonNull()) {
                                    JsonElement timeElement = item.get("lastMessageTime");
                                    if (timeElement.isJsonArray()) {
                                        // 数组格式：[2025,12,9,12,25] -> "2025-12-09 12:25:00"
                                        JsonArray timeArray = timeElement.getAsJsonArray();
                                        if (timeArray.size() >= 5) {
                                            int year = timeArray.get(0).getAsInt();
                                            int month = timeArray.get(1).getAsInt();
                                            int day = timeArray.get(2).getAsInt();
                                            int hour = timeArray.get(3).getAsInt();
                                            int minute = timeArray.get(4).getAsInt();
                                            String timeStr = String.format(Locale.getDefault(), 
                                                    "%04d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);
                                            resp.setLastMessageTime(timeStr);
                                        }
                                    } else if (timeElement.isJsonPrimitive()) {
                                        resp.setLastMessageTime(timeElement.getAsString());
                                    }
                                }
                                if (item.has("unreadCount") && !item.get("unreadCount").isJsonNull()) {
                                    resp.setUnreadCount(item.get("unreadCount").getAsInt());
                                }
                                if (item.has("avatar") && !item.get("avatar").isJsonNull()) {
                                    resp.setAvatar(item.get("avatar").getAsString());
                                }
                                if (item.has("msgFormat") && !item.get("msgFormat").isJsonNull()) {
                                    JsonElement msgFormatElement = item.get("msgFormat");
                                    int msgFormat = 0;
                                    if (msgFormatElement.isJsonPrimitive()) {
                                        if (msgFormatElement.getAsJsonPrimitive().isBoolean()) {
                                            // 布尔值：true=1(图片)，false=0(文本)
                                            msgFormat = msgFormatElement.getAsBoolean() ? 1 : 0;
                                        } else if (msgFormatElement.getAsJsonPrimitive().isNumber()) {
                                            msgFormat = msgFormatElement.getAsInt();
                                        }
                                    }
                                    resp.setMsgFormat(msgFormat);
                                }
                                
                                data.add(resp);
                            }
                        }
                        List<Conversation> conversations = convertToConversations(data, "follow");
                        if (callback != null) {
                            callback.onResult(conversations, null);
                        }
                    } else {
                        String msg = message != null ? message : "获取互关私信失败";
                        android.util.Log.e("LMessageRepository", "获取互关私信失败: " + msg);
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), msg);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    android.util.Log.e("LMessageRepository", "JSON解析错误: " + e.getMessage() + ", JSON: " + json);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "数据解析失败，请检查后端返回格式");
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "未知错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "处理数据时出错: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获取粉丝来信列表（异步）
     */
    public static void getFanConversations(Context context, ConversationCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(new ArrayList<>(), "用户未登录");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // PC后端使用GET方法，参数通过URL传�?
        String url = FAN_MESSAGES_URL + "?userId=" + currentUser.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "网络请求失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(new ArrayList<>(), "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "FanMessages Response: " + json);
                
                try {
                    // 检查是否返回HTML（服务器错误�?
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器错误，请检查后端配置");
                        }
                        return;
                    }
                    
                    // 检查空响应
                    if (json.trim().isEmpty()) {
                        android.util.Log.e("LMessageRepository", "服务器返回空响应");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器返回空数据");
                        }
                        return;
                    }
                    
                    // 使用JsonParser手动解析，以处理时间字段可能是数组的情况
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull() 
                            ? jsonObject.get("message").getAsString() : null;
                    
                    if (code == 200) {
                        List<ConversationResponse> data = new ArrayList<>();
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonObject.getAsJsonArray("data");
                            for (JsonElement element : dataArray) {
                                JsonObject item = element.getAsJsonObject();
                                ConversationResponse resp = new ConversationResponse();
                                
                                if (item.has("userId") && !item.get("userId").isJsonNull()) {
                                    resp.setUserId(item.get("userId").getAsInt());
                                }
                                if (item.has("username") && !item.get("username").isJsonNull()) {
                                    resp.setUsername(item.get("username").getAsString());
                                }
                                if (item.has("lastMessage") && !item.get("lastMessage").isJsonNull()) {
                                    resp.setLastMessage(item.get("lastMessage").getAsString());
                                }
                                if (item.has("messagePreview") && !item.get("messagePreview").isJsonNull()) {
                                    resp.setMessagePreview(item.get("messagePreview").getAsString());
                                }
                                // 处理时间字段：可能是字符串或数组
                                if (item.has("lastMessageTime") && !item.get("lastMessageTime").isJsonNull()) {
                                    JsonElement timeElement = item.get("lastMessageTime");
                                    if (timeElement.isJsonArray()) {
                                        // 数组格式：[2025,12,9,12,25] -> "2025-12-09 12:25:00"
                                        JsonArray timeArray = timeElement.getAsJsonArray();
                                        if (timeArray.size() >= 5) {
                                            int year = timeArray.get(0).getAsInt();
                                            int month = timeArray.get(1).getAsInt();
                                            int day = timeArray.get(2).getAsInt();
                                            int hour = timeArray.get(3).getAsInt();
                                            int minute = timeArray.get(4).getAsInt();
                                            String timeStr = String.format(Locale.getDefault(), 
                                                    "%04d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);
                                            resp.setLastMessageTime(timeStr);
                                        }
                                    } else if (timeElement.isJsonPrimitive()) {
                                        resp.setLastMessageTime(timeElement.getAsString());
                                    }
                                }
                                if (item.has("unreadCount") && !item.get("unreadCount").isJsonNull()) {
                                    resp.setUnreadCount(item.get("unreadCount").getAsInt());
                                }
                                if (item.has("avatar") && !item.get("avatar").isJsonNull()) {
                                    resp.setAvatar(item.get("avatar").getAsString());
                                }
                                if (item.has("msgFormat") && !item.get("msgFormat").isJsonNull()) {
                                    JsonElement msgFormatElement = item.get("msgFormat");
                                    int msgFormat = 0;
                                    if (msgFormatElement.isJsonPrimitive()) {
                                        if (msgFormatElement.getAsJsonPrimitive().isBoolean()) {
                                            // 布尔值：true=1(图片)，false=0(文本)
                                            msgFormat = msgFormatElement.getAsBoolean() ? 1 : 0;
                                        } else if (msgFormatElement.getAsJsonPrimitive().isNumber()) {
                                            msgFormat = msgFormatElement.getAsInt();
                                        }
                                    }
                                    resp.setMsgFormat(msgFormat);
                                }
                                
                                data.add(resp);
                            }
                        }
                        List<Conversation> conversations = convertToConversations(data, "fan");
                        if (callback != null) {
                            callback.onResult(conversations, null);
                        }
                    } else {
                        String msg = message != null ? message : "获取粉丝来信失败";
                        android.util.Log.e("LMessageRepository", "获取粉丝来信失败: " + msg);
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), msg);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    android.util.Log.e("LMessageRepository", "JSON解析错误: " + e.getMessage() + ", JSON: " + json);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "数据解析失败，请检查后端返回格式");
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "未知错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "处理数据时出错: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获取管理员列表（异步�?
     */
    public static void getAdminConversations(Context context, ConversationCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(new ArrayList<>(), "用户未登录");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // PC后端使用GET方法，参数通过URL传�?
        String url = ADMIN_MESSAGES_URL + "?userId=" + currentUser.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "网络请求失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(new ArrayList<>(), "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "AdminMessages Response: " + json);
                
                try {
                    // 检查是否返回HTML（服务器错误�?
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器错误，请检查后端配置");
                        }
                        return;
                    }
                    
                    // 检查空响应
                    if (json.trim().isEmpty()) {
                        android.util.Log.e("LMessageRepository", "服务器返回空响应");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器返回空数据");
                        }
                        return;
                    }
                    
                    // 使用JsonParser手动解析，以处理时间字段可能是数组的情况
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull() 
                            ? jsonObject.get("message").getAsString() : null;
                    
                    if (code == 200) {
                        List<ConversationResponse> data = new ArrayList<>();
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonObject.getAsJsonArray("data");
                            for (JsonElement element : dataArray) {
                                JsonObject item = element.getAsJsonObject();
                                ConversationResponse resp = new ConversationResponse();
                                
                                if (item.has("userId") && !item.get("userId").isJsonNull()) {
                                    resp.setUserId(item.get("userId").getAsInt());
                                }
                                if (item.has("username") && !item.get("username").isJsonNull()) {
                                    resp.setUsername(item.get("username").getAsString());
                                }
                                if (item.has("lastMessage") && !item.get("lastMessage").isJsonNull()) {
                                    resp.setLastMessage(item.get("lastMessage").getAsString());
                                }
                                if (item.has("messagePreview") && !item.get("messagePreview").isJsonNull()) {
                                    resp.setMessagePreview(item.get("messagePreview").getAsString());
                                }
                                // 处理时间字段：可能是字符串或数组
                                if (item.has("lastMessageTime") && !item.get("lastMessageTime").isJsonNull()) {
                                    JsonElement timeElement = item.get("lastMessageTime");
                                    if (timeElement.isJsonArray()) {
                                        // 数组格式：[2025,12,9,12,25] -> "2025-12-09 12:25:00"
                                        JsonArray timeArray = timeElement.getAsJsonArray();
                                        if (timeArray.size() >= 5) {
                                            int year = timeArray.get(0).getAsInt();
                                            int month = timeArray.get(1).getAsInt();
                                            int day = timeArray.get(2).getAsInt();
                                            int hour = timeArray.get(3).getAsInt();
                                            int minute = timeArray.get(4).getAsInt();
                                            String timeStr = String.format(Locale.getDefault(), 
                                                    "%04d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);
                                            resp.setLastMessageTime(timeStr);
                                        }
                                    } else if (timeElement.isJsonPrimitive()) {
                                        resp.setLastMessageTime(timeElement.getAsString());
                                    }
                                }
                                if (item.has("unreadCount") && !item.get("unreadCount").isJsonNull()) {
                                    resp.setUnreadCount(item.get("unreadCount").getAsInt());
                                }
                                if (item.has("avatar") && !item.get("avatar").isJsonNull()) {
                                    resp.setAvatar(item.get("avatar").getAsString());
                                }
                                if (item.has("msgFormat") && !item.get("msgFormat").isJsonNull()) {
                                    JsonElement msgFormatElement = item.get("msgFormat");
                                    int msgFormat = 0;
                                    if (msgFormatElement.isJsonPrimitive()) {
                                        if (msgFormatElement.getAsJsonPrimitive().isBoolean()) {
                                            // 布尔值：true=1(图片)，false=0(文本)
                                            msgFormat = msgFormatElement.getAsBoolean() ? 1 : 0;
                                        } else if (msgFormatElement.getAsJsonPrimitive().isNumber()) {
                                            msgFormat = msgFormatElement.getAsInt();
                                        }
                                    }
                                    resp.setMsgFormat(msgFormat);
                                }
                                
                                data.add(resp);
                            }
                        }
                        List<Conversation> conversations = convertToConversations(data, "admin");
                        if (callback != null) {
                            callback.onResult(conversations, null);
                        }
                    } else {
                        String msg = message != null ? message : "获取管理员列表失败";
                        android.util.Log.e("LMessageRepository", "获取管理员列表失败: " + msg);
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), msg);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    android.util.Log.e("LMessageRepository", "JSON解析错误: " + e.getMessage() + ", JSON: " + json);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "数据解析失败，请检查后端返回格式");
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "未知错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "处理数据时出�? " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 转换响应数据为Conversation列表
     */
    private static List<Conversation> convertToConversations(List<ConversationResponse> responses, String type) {
        List<Conversation> conversations = new ArrayList<>();
        for (ConversationResponse resp : responses) {
            if (resp == null || resp.getUserId() == null) continue;
            
            String conversationId = type + "_" + resp.getUserId();
            String title = resp.getUsername() != null ? resp.getUsername() : "用户" + resp.getUserId();
            
            // 处理最后一条消息预览
            String lastMessage;
            if (resp.getMsgFormat() != null && resp.getMsgFormat() == 1) {
                // 图片消息，显示"[图片]"
                lastMessage = "[图片]";
            } else {
                // 文本消息，优先使用messagePreview（已处理"我:"前缀），否则使用lastMessage
                lastMessage = (resp.getMessagePreview() != null && !resp.getMessagePreview().isEmpty()) 
                        ? resp.getMessagePreview() 
                        : (resp.getLastMessage() != null ? resp.getLastMessage() : "");
            }
            
            long timestamp = parseTime(resp.getLastMessageTime());
            String avatar = resp.getAvatar();
            int unreadCount = resp.getUnreadCount() != null ? resp.getUnreadCount() : 0;
            
            conversations.add(new Conversation(conversationId, title, lastMessage, timestamp, type, avatar, unreadCount));
        }
        // 按时间倒序排序
        conversations.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return conversations;
    }

    /**
     * 解析时间字符串为时间�?
     */
    private static long parseTime(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return System.currentTimeMillis();
        }
        try {
            // 尝试解析 "yyyy-MM-dd HH:mm:ss" 格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            try {
                // 尝试解析 "MM-dd HH:mm" 格式
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                Date date = sdf.parse(timeStr);
                return date != null ? date.getTime() : System.currentTimeMillis();
            } catch (ParseException e2) {
                return System.currentTimeMillis();
            }
        }
    }

    /**
     * 格式化时间戳为字符串
     */
    public static String formatTime(long time) {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(time);
    }

    /**
     * 回调接口
     */
    public interface ConversationCallback {
        void onResult(List<Conversation> conversations, String error);
    }

    // 黑名单相关方法（保留原有功能�?
    public static List<String> getBlacklist(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet(KEY_BLACKLIST, new HashSet<String>());
        return new ArrayList<>(set);
    }

    public static void addToBlacklist(Context context, String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(sp.getStringSet(KEY_BLACKLIST, new HashSet<String>()));
        set.add(username.trim());
        sp.edit().putStringSet(KEY_BLACKLIST, set).apply();
    }

    public static void removeFromBlacklist(Context context, String username) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(sp.getStringSet(KEY_BLACKLIST, new HashSet<String>()));
        set.remove(username);
        sp.edit().putStringSet(KEY_BLACKLIST, set).apply();
    }

    public static boolean isBlacklisted(Context context, String username) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet(KEY_BLACKLIST, new HashSet<String>());
        return set.contains(username);
    }

    /**
     * 消息响应数据模型
     */
    public static class MessageResponse {
        private Integer messageId;
        private Integer senderId;
        private Integer receiverId;
        private String content;
        private Integer msgFormat;
        private String imageUrl;
        private String createTime;

        public Integer getMessageId() { return messageId; }
        public void setMessageId(Integer messageId) { this.messageId = messageId; }
        public Integer getSenderId() { return senderId; }
        public void setSenderId(Integer senderId) { this.senderId = senderId; }
        public Integer getReceiverId() { return receiverId; }
        public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getMsgFormat() { return msgFormat; }
        public void setMsgFormat(Integer msgFormat) { this.msgFormat = msgFormat; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
    }

    /**
     * 获取聊天记录（异步）
     */
    public static void getChatMessages(Context context, Integer otherUserId, ChatMessageCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(new ArrayList<>(), "用户未登录");
            }
            return;
        }

        if (otherUserId == null) {
            if (callback != null) {
                callback.onResult(new ArrayList<>(), "对方用户ID不能为空");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = CONVERSATION_URL + "?userId=" + currentUser.getUserId() + "&otherUserId=" + otherUserId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "获取聊天记录失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(new ArrayList<>(), "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "ChatMessages Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    if (json.trim().isEmpty()) {
                        android.util.Log.e("LMessageRepository", "服务器返回空响应");
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), "服务器返回空数据");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();

                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        List<ChatMessage> messages = new ArrayList<>();
                        String otherUserAvatar = null;
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonArray dataArray = jsonObject.getAsJsonArray("data");
                            for (JsonElement element : dataArray) {
                                JsonObject item = element.getAsJsonObject();
                                
                                Integer senderId = item.has("senderId") && !item.get("senderId").isJsonNull()
                                        ? item.get("senderId").getAsInt() : null;
                                String content = item.has("content") && !item.get("content").isJsonNull()
                                        ? item.get("content").getAsString() : "";
                                // 处理msgFormat字段：可能是布尔值或整数
                                Integer msgFormat = 0;
                                if (item.has("msgFormat") && !item.get("msgFormat").isJsonNull()) {
                                    JsonElement msgFormatElement = item.get("msgFormat");
                                    if (msgFormatElement.isJsonPrimitive()) {
                                        if (msgFormatElement.getAsJsonPrimitive().isBoolean()) {
                                            // 布尔值：true=1(图片)，false=0(文本)
                                            msgFormat = msgFormatElement.getAsBoolean() ? 1 : 0;
                                        } else if (msgFormatElement.getAsJsonPrimitive().isNumber()) {
                                            msgFormat = msgFormatElement.getAsInt();
                                        }
                                    }
                                }
                                String imageUrl = item.has("imageUrl") && !item.get("imageUrl").isJsonNull()
                                        ? item.get("imageUrl").getAsString() : null;
                                
                                // 获取对方用户头像
                                boolean fromMe = (senderId != null && senderId.equals(currentUser.getUserId()));
                                if (!fromMe && otherUserAvatar == null) {
                                    // 获取发送者头像（对方用户�?
                                    if (item.has("senderAvatar") && !item.get("senderAvatar").isJsonNull()) {
                                        otherUserAvatar = item.get("senderAvatar").getAsString();
                                    }
                                } else if (fromMe && otherUserAvatar == null) {
                                    // 获取接收者头像（对方用户�?
                                    if (item.has("receiverAvatar") && !item.get("receiverAvatar").isJsonNull()) {
                                        otherUserAvatar = item.get("receiverAvatar").getAsString();
                                    }
                                }
                                
                                // 处理时间字段
                                String createTime = null;
                                if (item.has("createTime") && !item.get("createTime").isJsonNull()) {
                                    JsonElement timeElement = item.get("createTime");
                                    if (timeElement.isJsonArray()) {
                                        JsonArray timeArray = timeElement.getAsJsonArray();
                                        if (timeArray.size() >= 5) {
                                            int year = timeArray.get(0).getAsInt();
                                            int month = timeArray.get(1).getAsInt();
                                            int day = timeArray.get(2).getAsInt();
                                            int hour = timeArray.get(3).getAsInt();
                                            int minute = timeArray.get(4).getAsInt();
                                            createTime = String.format(Locale.getDefault(),
                                                    "%04d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);
                                        }
                                    } else if (timeElement.isJsonPrimitive()) {
                                        createTime = timeElement.getAsString();
                                    }
                                }

                                long timestamp = parseTime(createTime);
                                String conversationId = "chat_" + otherUserId;

                                messages.add(new ChatMessage(conversationId, fromMe, content, timestamp, msgFormat, imageUrl));
                            }
                        }
                        if (callback != null) {
                            callback.onResult(messages, null);
                            if (otherUserAvatar != null) {
                                callback.onOtherUserAvatar(otherUserAvatar);
                            }
                        }
                    } else {
                        String msg = message != null ? message : "获取聊天记录失败";
                        android.util.Log.e("LMessageRepository", "获取聊天记录失败: " + msg);
                        if (callback != null) {
                            callback.onResult(new ArrayList<>(), msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(new ArrayList<>(), "数据解析失败: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 发送文本消息（异步�?
     */
    public static void sendTextMessage(Context context, Integer receiverId, String content, SendMessageCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(false, "用户未登录");
            }
            return;
        }

        if (receiverId == null) {
            if (callback != null) {
                callback.onResult(false, "接收者ID不能为空");
            }
            return;
        }

        if (TextUtils.isEmpty(content)) {
            if (callback != null) {
                callback.onResult(false, "消息内容不能为空");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userId", String.valueOf(currentUser.getUserId()))
                .add("receiverId", String.valueOf(receiverId))
                .add("content", content)
                .build();

        Request request = new Request.Builder()
                .url(SEND_MESSAGE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "发送消息失�? " + e.getMessage());
                if (callback != null) {
                    callback.onResult(false, "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "SendMessage Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        if (callback != null) {
                            callback.onResult(false, "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        if (callback != null) {
                            callback.onResult(true, null);
                        }
                    } else {
                        String msg = message != null ? message : "发送消息失败";
                        if (callback != null) {
                            callback.onResult(false, msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage());
                    if (callback != null) {
                        callback.onResult(false, "数据解析失败");
                    }
                }
            }
        });
    }

    /**
     * 上传图片（异步）
     */
    public static void uploadImage(Context context, java.io.File imageFile, UploadImageCallback callback) {
        if (imageFile == null || !imageFile.exists()) {
            if (callback != null) {
                callback.onResult(null, "图片文件不存在");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        
        RequestBody fileBody = RequestBody.create(
                okhttp3.MediaType.parse("image/*"),
                imageFile
        );
        
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_IMAGE_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "上传图片失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(null, "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "UploadImage Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        if (callback != null) {
                            callback.onResult(null, "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        String imageUrl = jsonObject.has("data") && !jsonObject.get("data").isJsonNull()
                                ? jsonObject.get("data").getAsString() : null;
                        if (callback != null) {
                            callback.onResult(imageUrl, null);
                        }
                    } else {
                        String msg = message != null ? message : "上传失败";
                        if (callback != null) {
                            callback.onResult(null, msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage());
                    if (callback != null) {
                        callback.onResult(null, "数据解析失败");
                    }
                }
            }
        });
    }

    /**
     * 发送图片消息（异步�?
     */
    public static void sendImageMessage(Context context, Integer receiverId, String imageUrl, String content, SendMessageCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(false, "用户未登录");
            }
            return;
        }

        if (receiverId == null) {
            if (callback != null) {
                callback.onResult(false, "接收者ID不能为空");
            }
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            if (callback != null) {
                callback.onResult(false, "图片URL不能为空");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("userId", String.valueOf(currentUser.getUserId()))
                .add("receiverId", String.valueOf(receiverId))
                .add("imageUrl", imageUrl);
        
        if (!TextUtils.isEmpty(content)) {
            builder.add("content", content);
        }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(SEND_IMAGE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "发送图片消息失�? " + e.getMessage());
                if (callback != null) {
                    callback.onResult(false, "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "SendImageMessage Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        if (callback != null) {
                            callback.onResult(false, "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        if (callback != null) {
                            callback.onResult(true, null);
                        }
                    } else {
                        String msg = message != null ? message : "发送消息失败";
                        if (callback != null) {
                            callback.onResult(false, msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage());
                    if (callback != null) {
                        callback.onResult(false, "数据解析失败");
                    }
                }
            }
        });
    }

    /**
     * 从conversationId中提取otherUserId
     * 格式: "follow_3" �?"fan_4" -> 返回 3 �?4
     */
    public static Integer extractOtherUserId(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            return null;
        }
        try {
            int underscoreIndex = conversationId.indexOf('_');
            if (underscoreIndex >= 0 && underscoreIndex < conversationId.length() - 1) {
                String userIdStr = conversationId.substring(underscoreIndex + 1);
                return Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            android.util.Log.e("LMessageRepository", "解析conversationId失败: " + conversationId);
        }
        return null;
    }

    /**
     * 聊天消息回调接口
     */
    public abstract static class ChatMessageCallback {
        public abstract void onResult(List<ChatMessage> messages, String error);
        public void onOtherUserAvatar(String avatarUrl) {
            // 默认实现，子类可以重�?
        }
    }

    /**
     * 发送消息回调接�?
     */
    public interface SendMessageCallback {
        void onResult(boolean success, String error);
    }

    /**
     * 上传图片回调接口
     */
    public interface UploadImageCallback {
        void onResult(String imageUrl, String error);
    }

    /**
     * 未读数回调接�?
     */
    public interface UnreadCountCallback {
        void onResult(Integer count, String error);
    }

    /**
     * 所有未读数回调接口
     */
    public interface AllUnreadCountsCallback {
        void onResult(Integer total, Integer mutualFollow, Integer fan, Integer admin, String error);
    }

    /**
     * 获取总未读消息数（异步）
     */
    public static void getTotalUnreadCount(Context context, UnreadCountCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(0, "用户未登录");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/api/cuser/message/totalUnreadCount?userId=" + currentUser.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "获取总未读数失败: " + e.getMessage());
                if (callback != null) {
                    callback.onResult(0, "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "TotalUnreadCount Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(0, "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        Integer count = 0;
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonElement dataElement = jsonObject.get("data");
                            if (dataElement.isJsonPrimitive() && dataElement.getAsJsonPrimitive().isNumber()) {
                                count = dataElement.getAsInt();
                            }
                        }
                        if (callback != null) {
                            callback.onResult(count, null);
                        }
                    } else {
                        String msg = message != null ? message : "获取总未读数失败";
                        if (callback != null) {
                            callback.onResult(0, msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(0, "数据解析失败: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获取所有未读数（总未读数、互关私信未读数、粉丝来信未读数、联系管理员未读数）（异步）
     */
    public static void getAllUnreadCounts(Context context, AllUnreadCountsCallback callback) {
        CUser currentUser = CSessionManager.getInstance(context).getCurrentCUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            if (callback != null) {
                callback.onResult(0, 0, 0, 0, "用户未登录");
            }
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/api/cuser/message/allUnreadCounts?userId=" + currentUser.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("LMessageRepository", "获取未读数失�? " + e.getMessage());
                if (callback != null) {
                    callback.onResult(0, 0, 0, 0, "网络请求失败: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.d("LMessageRepository", "AllUnreadCounts Response: " + json);

                try {
                    if (json.trim().startsWith("<")) {
                        android.util.Log.e("LMessageRepository", "服务器返回HTML而不是JSON");
                        if (callback != null) {
                            callback.onResult(0, 0, 0, 0, "服务器错误，请检查后端接口");
                        }
                        return;
                    }

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(json).getAsJsonObject();
                    Integer code = jsonObject.get("code").getAsInt();
                    String message = jsonObject.has("message") && !jsonObject.get("message").isJsonNull()
                            ? jsonObject.get("message").getAsString() : null;

                    if (code == 200) {
                        Integer total = 0;
                        Integer mutualFollow = 0;
                        Integer fan = 0;
                        Integer admin = 0;
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            JsonObject dataObject = jsonObject.getAsJsonObject("data");
                            android.util.Log.d("LMessageRepository", "AllUnreadCounts data: " + dataObject.toString());
                            if (dataObject.has("total") && !dataObject.get("total").isJsonNull()) {
                                total = dataObject.get("total").getAsInt();
                            }
                            if (dataObject.has("mutualFollow") && !dataObject.get("mutualFollow").isJsonNull()) {
                                mutualFollow = dataObject.get("mutualFollow").getAsInt();
                            }
                            if (dataObject.has("fan") && !dataObject.get("fan").isJsonNull()) {
                                fan = dataObject.get("fan").getAsInt();
                            }
                            if (dataObject.has("admin") && !dataObject.get("admin").isJsonNull()) {
                                admin = dataObject.get("admin").getAsInt();
                            }
                        }
                        android.util.Log.d("LMessageRepository", "解析后的未读�?- total:" + total + ", mutualFollow:" + mutualFollow + ", fan:" + fan + ", admin:" + admin);
                        if (callback != null) {
                            callback.onResult(total, mutualFollow, fan, admin, null);
                        }
                    } else {
                        String msg = message != null ? message : "获取未读数失败";
                        if (callback != null) {
                            callback.onResult(0, 0, 0, 0, msg);
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("LMessageRepository", "解析错误: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onResult(0, 0, 0, 0, "数据解析失败: " + e.getMessage());
                    }
                }
            }
        });
    }
}
