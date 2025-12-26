package com.app.service;

import com.niit.pojo.Message;
import java.util.List;
import java.util.Map;

/**
 * 消息服务接口（安卓端）
 * 用于处理互关私信和粉丝来信查询
 */
public interface LMessageService {
    
    /**
     * 查询互关私信列表
     * 返回与当前用户互相关注的用户之间的私信列表
     * @param currentUserId 当前登录用户ID
     * @return 私信列表（包含对方用户信息和最新消息内容）
     */
    List<Map<String, Object>> getMutualFollowMessages(Integer currentUserId);
    
    /**
     * 查询粉丝来信列表
     * 返回关注了当前用户但当前用户未关注对方的用户发送的私信列表
     * @param currentUserId 当前登录用户ID
     * @return 私信列表（包含对方用户信息和最新消息内容）
     */
    List<Map<String, Object>> getFanMessages(Integer currentUserId);
    
    /**
     * 查询与指定用户之间的私信记录（用于查看具体会话）
     * @param currentUserId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 私信记录列表（按时间升序）
     */
    List<Message> getMessagesBetweenUsers(Integer currentUserId, Integer otherUserId);
    
    /**
     * 发送私信消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容（文本消息时必填，图片消息时可为空）
     * @param msgFormat 消息格式：0文本，1图片
     * @param imageUrl 图片地址（图片消息时必填，文本消息时为空）
     * @return 是否发送成功
     */
    boolean sendMessage(Integer senderId, Integer receiverId, String content, Integer msgFormat, String imageUrl);
    
    /**
     * 将当前登录用户与指定用户之间的未读私信标记为已读
     * @param currentUserId 当前登录用户ID
     * @param otherUserId 对方用户ID
     */
    void markMessagesAsRead(Integer currentUserId, Integer otherUserId);
    
    /**
     * 获取总未读消息数（所有私信，is_read=0）
     * @param currentUserId 当前登录用户ID
     * @return 未读消息总数
     */
    Integer getTotalUnreadCount(Integer currentUserId);
    
    /**
     * 获取互关私信的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 互关私信未读数
     */
    Integer getMutualFollowUnreadCount(Integer currentUserId);
    
    /**
     * 获取粉丝来信的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 粉丝来信未读数
     */
    Integer getFanUnreadCount(Integer currentUserId);
    
    /**
     * 查询管理员列表（用于联系管理员）
     * 返回所有管理员（role IN (0,1)）的列表，包含最新消息和未读数
     * @param currentUserId 当前登录用户ID
     * @return 管理员列表（包含管理员信息和最新消息内容）
     */
    List<Map<String, Object>> getAdminMessages(Integer currentUserId);
    
    /**
     * 获取联系管理员的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 联系管理员未读数
     */
    Integer getAdminUnreadCount(Integer currentUserId);
}


