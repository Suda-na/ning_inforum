package com.niit.service.impl;

import com.niit.dao.MessageMapper;
import com.niit.pojo.Message;
import com.niit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Override
    public List<Message> getSystemNotifications() {
        return messageMapper.selectSystemNotifications();
    }
    
    @Override
    public List<Map<String, Object>> getUsersWithPrivateMessages() {
        return messageMapper.selectUsersWithPrivateMessages();
    }
    
    @Override
    public List<Map<String, Object>> getUsersWithPrivateMessagesByCurrentUser(Integer currentUserId) {
        if (currentUserId == null) {
            return null;
        }
        return messageMapper.selectUsersWithPrivateMessagesByCurrentUser(currentUserId);
    }
    
    @Override
    public List<Message> getMessagesBetweenAdminAndUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        return messageMapper.selectMessagesBetweenAdminAndUser(userId);
    }
    
    @Override
    public List<Message> getMessagesBetweenUsers(Integer currentUserId, Integer otherUserId) {
        if (currentUserId == null || otherUserId == null) {
            return null;
        }
        return messageMapper.selectMessagesBetweenUsers(currentUserId, otherUserId);
    }

    @Override
    public boolean addPrivateMessageByAdmin(Integer adminId, Integer receiverId, String content, Integer msgFormat, String imageUrl) {
        try {
            if (adminId == null) {
                // 如果没有传入adminId，使用默认管理员（向后兼容）
                adminId = messageMapper.selectDefaultAdminId();
                if (adminId == null) {
                    return false;
                }
            }
            int result = messageMapper.insertPrivateMessageFromAdmin(adminId, receiverId, content, msgFormat, imageUrl);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean addPrivateMessage(Integer senderId, Integer receiverId, String content, Integer msgFormat, String imageUrl) {
        try {
            if (senderId == null || receiverId == null) {
                return false;
            }
            // 使用相同的插入方法，因为私信的结构是一样的
            int result = messageMapper.insertPrivateMessageFromAdmin(senderId, receiverId, content, msgFormat, imageUrl);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void markPrivateMessagesAsRead(Integer adminId, Integer userId) {
        if (userId == null) {
            return;
        }
        try {
            if (adminId == null) {
                // 如果没有传入adminId，使用默认管理员（向后兼容）
                adminId = messageMapper.selectDefaultAdminId();
            }
            if (adminId != null) {
                messageMapper.updatePrivateMessagesRead(adminId, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void markPrivateMessagesAsReadBetweenUsers(Integer currentUserId, Integer otherUserId) {
        if (currentUserId == null || otherUserId == null) {
            return;
        }
        try {
            messageMapper.updatePrivateMessagesReadBetweenUsers(currentUserId, otherUserId);
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
            return messageMapper.countUnreadPrivateMessages(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public boolean addSystemNotification(Message message) {
        try {
            // 设置系统通知的固定属性
            message.setMessageType(1); // 系统通知类型
            message.setSenderId(null); // 系统消息，发送者为NULL
            message.setIsRead(0); // 默认未读
            // 直接插入一条记录：
            // receiverId 为 null 表示广播给所有用户；
            // receiverId 为具体用户ID 表示只发给该用户。
            int result = messageMapper.insertSystemNotification(message);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = messageMapper.selectAllUsers();
        // 将性别字段转换为汉字（0未知，1男，2女）
        if (users != null) {
            for (Map<String, Object> user : users) {
                user.put("gender", convertGenderToText(user.get("gender")));
            }
        }
        return users;
    }

    @Override
    public boolean updateSystemNotification(Message message) {
        try {
            int result = messageMapper.updateSystemNotification(message);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSystemNotification(Integer messageId) {
        try {
            int result = messageMapper.deleteSystemNotification(messageId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getNotificationReceivers(Integer receiverId) {
        List<Map<String, Object>> receivers = messageMapper.selectNotificationReceivers(receiverId);
        // 将性别字段转换为汉字（0未知，1男，2女）
        if (receivers != null) {
            for (Map<String, Object> user : receivers) {
                user.put("gender", convertGenderToText(user.get("gender")));
            }
        }
        return receivers;
    }

    /**
     * 将性别数值/字符串转换为汉字描述
     * @param genderObj 0/1/2 或对应字符串
     * @return 性别汉字
     */
    private String convertGenderToText(Object genderObj) {
        if (genderObj == null) {
            return "未知";
        }
        try {
            int gender = Integer.parseInt(genderObj.toString());
            if (gender == 1) {
                return "男";
            } else if (gender == 2) {
                return "女";
            }
        } catch (NumberFormatException ignored) {
            // 如果不是数字，继续后续判断
        }
        // 兜底处理：已是汉字时原样返回，否则未知
        String text = genderObj.toString().trim();
        if ("男".equals(text) || "女".equals(text)) {
            return text;
        }
        return "未知";
    }
    
    // ========== 多接收者支持方法实现 ==========
    
    @Override
    public boolean addSystemNotificationWithReceivers(Message message, List<Integer> receiverIds) {
        try {
            // 设置系统通知的固定属性
            message.setMessageType(1); // 系统通知类型
            message.setSenderId(null); // 系统消息，发送者为NULL
            message.setIsRead(0); // 默认未读
            // receiverId设为null，表示通过关联表管理
            message.setReceiverId(null);
            
            // 插入消息主记录
            int result = messageMapper.insertSystemNotification(message);
            if (result <= 0) {
                return false;
            }
            
            // 如果有接收者列表，插入关联记录
            if (receiverIds != null && !receiverIds.isEmpty()) {
                int receiverResult = messageMapper.insertMessageReceivers(message.getMessageId(), receiverIds);
                return receiverResult > 0;
            }
            // 如果没有接收者列表，表示发送给所有人（不插入关联记录，通过receiverId为null判断）
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getMessageReceivers(Integer messageId) {
        if (messageId == null) {
            return null;
        }
        try {
            return messageMapper.selectMessageReceivers(messageId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean updateSystemNotificationReceivers(Integer messageId, List<Integer> receiverIds) {
        try {
            // 先删除所有旧的接收者关联
            messageMapper.deleteAllMessageReceivers(messageId);
            
            // 如果有新的接收者列表，插入新的关联记录
            if (receiverIds != null && !receiverIds.isEmpty()) {
                int result = messageMapper.insertMessageReceivers(messageId, receiverIds);
                return result > 0;
            }
            // 如果没有接收者列表，表示发送给所有人（不插入关联记录）
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean addReceiversToNotification(Integer messageId, List<Integer> receiverIds) {
        if (messageId == null || receiverIds == null || receiverIds.isEmpty()) {
            return false;
        }
        try {
            int result = messageMapper.addMessageReceivers(messageId, receiverIds);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean removeReceiversFromNotification(Integer messageId, List<Integer> receiverIds) {
        if (messageId == null || receiverIds == null || receiverIds.isEmpty()) {
            return false;
        }
        try {
            int result = messageMapper.deleteMessageReceivers(messageId, receiverIds);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

