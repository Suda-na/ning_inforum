package com.example.lnforum.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lnforum.model.ChatMessage;
import com.example.lnforum.model.Conversation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 简易本地数据仓库：会话列表、聊天记录、黑名单。
 * 后续对接后端时可替换为真实请求。
 */
public class MessageRepository {

    private static final String PREF_NAME = "message_repo";
    private static final String KEY_BLACKLIST = "blacklist_users";

    private static final List<Conversation> conversations = new ArrayList<>();
    private static final Map<String, List<ChatMessage>> chatMessages = new HashMap<>();

    static {
        long now = System.currentTimeMillis();
        // 互关私信
        conversations.add(new Conversation("c_follow_1", "小王", "周末一起自习吗？", now - 15 * 60_000, "follow"));
        conversations.add(new Conversation("c_follow_2", "阿云", "收到你的资料啦，谢谢！", now - 2 * 60 * 60_000, "follow"));

        // 粉丝来信
        conversations.add(new Conversation("c_fan_1", "新粉丝-暖阳", "可以互相关注吗？", now - 5 * 60_000, "fan"));
        conversations.add(new Conversation("c_fan_2", "新粉丝-远山", "喜欢你的分享！", now - 6 * 60 * 60_000, "fan"));

        // 管理员
        conversations.add(new Conversation("c_admin", "管理员", "欢迎反馈问题，我们随时在线。", now - 30 * 60_000, "admin"));

        // 聊天示例消息
        addInitialMessages("c_follow_1", "小王", "周末一起自习吗？");
        addInitialMessages("c_follow_2", "阿云", "收到你的资料啦，谢谢！");
        addInitialMessages("c_fan_1", "新粉丝-暖阳", "可以互相关注吗？");
        addInitialMessages("c_fan_2", "新粉丝-远山", "喜欢你的分享！");
        addInitialMessages("c_admin", "管理员", "欢迎反馈问题，我们随时在线。");
    }

    private static void addInitialMessages(String conversationId, String peer, String hello) {
        long now = System.currentTimeMillis();
        chatMessages.put(conversationId, new ArrayList<ChatMessage>());
        addMessage(conversationId, false, hello);
        addMessage(conversationId, true, "好的，稍后回复你~");
    }

    public static List<Conversation> getConversations(String type) {
        List<Conversation> result = new ArrayList<>();
        for (Conversation c : conversations) {
            if (type == null || type.equals(c.getType())) {
                result.add(c);
            }
        }
        // 时间倒序
        Collections.sort(result, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return result;
    }

    public static Conversation getConversationById(String id) {
        for (Conversation c : conversations) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public static List<ChatMessage> getMessages(String conversationId) {
        List<ChatMessage> list = chatMessages.get(conversationId);
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list);
    }

    public static void addMessage(String conversationId, boolean fromMe, String content) {
        long now = System.currentTimeMillis();
        List<ChatMessage> list = chatMessages.get(conversationId);
        if (list == null) {
            list = new ArrayList<>();
            chatMessages.put(conversationId, list);
        }
        list.add(new ChatMessage(conversationId, fromMe, content, now));

        Conversation conversation = getConversationById(conversationId);
        if (conversation != null) {
            String preview = fromMe ? "我：" + content : content;
            conversation.updatePreview(preview, now);
        }
    }

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

    public static String formatTime(long time) {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(time);
    }
}

