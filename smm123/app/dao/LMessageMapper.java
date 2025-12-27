package com.app.dao;

import com.niit.pojo.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 消息Mapper接口（安卓端）
 * 用于处理互关私信和粉丝来信查询
 */
public interface LMessageMapper {
    
    /**
     * 查询互关私信列表
     * 返回与当前用户互相关注的用户之间的私信列表（按会话分组，返回最新一条消息）
     * @param currentUserId 当前登录用户ID
     * @return 私信列表（包含对方用户信息和最新消息内容）
     */
    List<Map<String, Object>> selectMutualFollowMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询粉丝来信列表
     * 返回关注了当前用户但当前用户未关注对方的用户发送的私信列表（按会话分组，返回最新一条消息）
     * @param currentUserId 当前登录用户ID
     * @return 私信列表（包含对方用户信息和最新消息内容）
     */
    List<Map<String, Object>> selectFanMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询与指定用户之间的私信记录（用于查看具体会话）
     * @param currentUserId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 私信记录列表（按时间升序）
     */
    List<Message> selectMessagesBetweenUsers(@Param("currentUserId") Integer currentUserId, 
                                             @Param("otherUserId") Integer otherUserId);
    
    /**
     * 插入私信消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param msgFormat 消息格式：0文本，1图片
     * @param imageUrl 图片地址（可选）
     * @return 影响的行数
     */
    int insertPrivateMessage(@Param("senderId") Integer senderId,
                            @Param("receiverId") Integer receiverId,
                            @Param("content") String content,
                            @Param("msgFormat") Integer msgFormat,
                            @Param("imageUrl") String imageUrl);
    
    /**
     * 将当前登录用户与指定用户之间的未读私信标记为已读
     * @param currentUserId 当前登录用户ID（作为接收者）
     * @param otherUserId 对方用户ID（作为发送者）
     * @return 影响行数
     */
    int updateMessagesReadBetweenUsers(@Param("currentUserId") Integer currentUserId,
                                      @Param("otherUserId") Integer otherUserId);
    
    /**
     * 统计当前用户的总未读私信数（所有is_read=0的私信）
     * @param currentUserId 当前登录用户ID
     * @return 未读消息总数
     */
    Integer countTotalUnreadMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 统计互关私信的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 互关私信未读数
     */
    Integer countMutualFollowUnreadMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 统计粉丝来信的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 粉丝来信未读数
     */
    Integer countFanUnreadMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询管理员列表（用于联系管理员）
     * 返回所有管理员（role IN (0,1)）的列表，包含最新消息和未读数
     * @param currentUserId 当前登录用户ID
     * @return 管理员列表（包含管理员信息和最新消息内容）
     */
    List<Map<String, Object>> selectAdminMessages(@Param("currentUserId") Integer currentUserId);
    
    /**
     * 统计联系管理员的未读消息数
     * @param currentUserId 当前登录用户ID
     * @return 联系管理员未读数
     */
    Integer countAdminUnreadMessages(@Param("currentUserId") Integer currentUserId);
}


