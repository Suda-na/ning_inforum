package com.example.android_java2.model;

/**
 * 单条聊天消息。
 */
public class ChatMessage {
    private final String conversationId;
    private final boolean fromMe;
    private final String content;
    private final long timestamp;
    private final int msgFormat; // 0文本，1图片
    private final String imageUrl; // 图片URL

    public ChatMessage(String conversationId, boolean fromMe, String content, long timestamp) {
        this(conversationId, fromMe, content, timestamp, 0, null);
    }

    public ChatMessage(String conversationId, boolean fromMe, String content, long timestamp, int msgFormat, String imageUrl) {
        this.conversationId = conversationId;
        this.fromMe = fromMe;
        this.content = content;
        this.timestamp = timestamp;
        this.msgFormat = msgFormat;
        this.imageUrl = imageUrl;
    }

    public String getConversationId() {
        return conversationId;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMsgFormat() {
        return msgFormat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isImage() {
        return msgFormat == 1;
    }
}


