package com.example.lnforum.model;

/**
 * 单条聊天消息。
 */
public class ChatMessage {
    private final String conversationId;
    private final boolean fromMe;
    private final String content;
    private final long timestamp;
    private final Integer msgFormat; // 0=text,1=image
    private final String imageUrl;

    public ChatMessage(String conversationId, boolean fromMe, String content, long timestamp) {
        this(conversationId, fromMe, content, timestamp, null, null);
    }

    public ChatMessage(String conversationId, boolean fromMe, String content, long timestamp, Integer msgFormat, String imageUrl) {
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

    public Integer getMsgFormat() {
        return msgFormat;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


