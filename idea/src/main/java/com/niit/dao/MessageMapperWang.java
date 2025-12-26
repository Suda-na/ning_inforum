package com.niit.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface MessageMapperWang {

    /**
     * 插入消息
     */
    @Insert("INSERT INTO message (sender_id, receiver_id, message_type, title, content, msg_format, image_url, related_type, is_read, create_time) " +
            "VALUES (#{senderId}, #{receiverId}, 0, NULL, #{content}, 0, NULL, '举报', 0, #{createTime})")
    int insertMessage(@Param("senderId") Integer senderId,
                      @Param("receiverId") Integer receiverId,
                      @Param("content") String content,
                      @Param("createTime") Date createTime);
}

