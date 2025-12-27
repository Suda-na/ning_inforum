package com.example.lnforum.model;

/**
 * 私信会话基础信息。
 */
public class Conversation {
    private final String id;
    private final String title;
    private String lastMessage;
    private long timestamp;
    private final String type; // follow / fan / admin
    private final String avatar;
    private final int unreadCount;

    public Conversation(String id, String title, String lastMessage, long timestamp, String type) {
        this(id, title, lastMessage, timestamp, type, null, 0);
    }

    public Conversation(String id, String title, String lastMessage, long timestamp, String type, String avatar, int unreadCount) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.type = type;
        this.avatar = avatar;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void updatePreview(String message, long time) {
        this.lastMessage = message;
        this.timestamp = time;
    }
}

