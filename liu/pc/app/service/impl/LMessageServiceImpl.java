package com.app.service.impl;

import com.app.dao.LMessageMapper;
import com.app.service.LMessageService;
import com.niit.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 消息服务实现类（安卓端）
 */
@Service
public class LMessageServiceImpl implements LMessageService {
    
    @Autowired
    private LMessageMapper lMessageMapper;
    
    @Override
    public List<Map<String, Object>> getMutualFollowMessages(Integer currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        try {
            return lMessageMapper.selectMutualFollowMessages(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Map<String, Object>> getFanMessages(Integer currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        try {
            return lMessageMapper.selectFanMessages(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Message> getMessagesBetweenUsers(Integer currentUserId, Integer otherUserId) {
        if (currentUserId == null || otherUserId == null) {
            return null;
        }
        try {
            return lMessageMapper.selectMessagesBetweenUsers(currentUserId, otherUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean sendMessage(Integer senderId, Integer receiverId, String content, Integer msgFormat, String imageUrl) {
        if (senderId == null || receiverId == null) {
            return false;
        }
        try {
            // 验证消息格式和内容
            if (msgFormat == null) {
                msgFormat = 0; // 默认为文本消息
            }
            
            // 文本消息需要内容，图片消息需要图片URL
            boolean isImageMessage = (msgFormat != null && msgFormat == 1);
            if (isImageMessage) {
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    return false;
                }
            } else {
                if (content == null || content.trim().isEmpty()) {
                    return false;
                }
            }
            
            int result = lMessageMapper.insertPrivateMessage(
                senderId, 
                receiverId, 
                content != null ? content.trim() : "", 
                msgFormat, 
                imageUrl != null ? imageUrl.trim() : null
            );
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void markMessagesAsRead(Integer currentUserId, Integer otherUserId) {
        if (currentUserId == null || otherUserId == null) {
            return;
        }
        try {
            lMessageMapper.updateMessagesReadBetweenUsers(currentUserId, otherUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Integer getTotalUnreadCount(Integer currentUserId) {
        if (currentUserId == null) {
            return 0;
        }
        try {
            Integer count = lMessageMapper.countTotalUnreadMessages(currentUserId);
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public Integer getMutualFollowUnreadCount(Integer currentUserId) {
        if (currentUserId == null) {
            return 0;
        }
        try {
            Integer count = lMessageMapper.countMutualFollowUnreadMessages(currentUserId);
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public Integer getFanUnreadCount(Integer currentUserId) {
        if (currentUserId == null) {
            return 0;
        }
        try {
            Integer count = lMessageMapper.countFanUnreadMessages(currentUserId);
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public List<Map<String, Object>> getAdminMessages(Integer currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        try {
            return lMessageMapper.selectAdminMessages(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Integer getAdminUnreadCount(Integer currentUserId) {
        if (currentUserId == null) {
            return 0;
        }
        try {
            Integer count = lMessageMapper.countAdminUnreadMessages(currentUserId);
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}



