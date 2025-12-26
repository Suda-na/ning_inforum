package com.app.controller;

import com.app.common.CResult;
import com.app.service.LMessageService;
import com.niit.pojo.Message;
import com.niit.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 消息控制器（安卓端）
 * 处理互关私信和粉丝来信的查询、发送和标记已读
 */
@RestController
@RequestMapping("/api/cuser/message")
public class LMessageController {
    
    @Autowired
    private LMessageService lMessageService;
    
    /**
     * 查询互关私信列表
     * @param userId 当前登录用户ID（从请求参数获取）
     * @return 互关私信列表
     */
    @GetMapping("/mutualFollow")
    public CResult<List<Map<String, Object>>> getMutualFollowMessages(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            List<Map<String, Object>> messages = lMessageService.getMutualFollowMessages(userId);
            if (messages != null) {
                return CResult.success("查询成功", messages);
            } else {
                return CResult.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询互关私信时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 查询粉丝来信列表
     * @param userId 当前登录用户ID（从请求参数获取）
     * @return 粉丝来信列表
     */
    @GetMapping("/fan")
    public CResult<List<Map<String, Object>>> getFanMessages(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            List<Map<String, Object>> messages = lMessageService.getFanMessages(userId);
            if (messages != null) {
                return CResult.success("查询成功", messages);
            } else {
                return CResult.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询粉丝来信时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 查询与指定用户之间的私信记录（用于查看具体会话）
     * @param userId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 私信记录列表
     */
    @GetMapping("/conversation")
    public CResult<List<Message>> getConversation(@RequestParam("userId") Integer userId,
                                                   @RequestParam("otherUserId") Integer otherUserId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (otherUserId == null) {
                return CResult.error("对方用户ID不能为空");
            }
            
            List<Message> messages = lMessageService.getMessagesBetweenUsers(userId, otherUserId);
            if (messages != null) {
                // 标记消息为已读
                lMessageService.markMessagesAsRead(userId, otherUserId);
                return CResult.success("查询成功", messages);
            } else {
                return CResult.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询会话记录时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 发送文本消息
     * @param userId 当前登录用户ID（发送者）
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @return 发送结果
     */
    @PostMapping("/send")
    public CResult<Object> sendMessage(@RequestParam("userId") Integer userId,
                                       @RequestParam("receiverId") Integer receiverId,
                                       @RequestParam("content") String content) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (receiverId == null) {
                return CResult.error("接收者ID不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return CResult.error("消息内容不能为空");
            }
            
            boolean success = lMessageService.sendMessage(userId, receiverId, content.trim(), 0, null);
            if (success) {
                return CResult.success("发送成功", null);
            } else {
                return CResult.error("发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("发送消息时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 上传图片（用于发送图片消息）
     * @param file 图片文件
     * @return 上传结果（包含图片URL）
     */
    @PostMapping("/uploadImage")
    public CResult<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file == null || file.isEmpty()) {
                return CResult.error("请选择要上传的图片");
            }
            
            // 检查文件大小（限制为10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return CResult.error("图片大小不能超过10MB");
            }
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return CResult.error("文件名无效");
            }
            
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex).toLowerCase();
            }
            
            // 允许的图片格式
            String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
            boolean isValidExtension = false;
            for (String ext : allowedExtensions) {
                if (extension.equals(ext)) {
                    isValidExtension = true;
                    break;
                }
            }
            
            if (!isValidExtension) {
                return CResult.error("只支持JPG、PNG、GIF、BMP、WEBP格式的图片");
            }
            
            // 上传文件到阿里云OSS
            String imageUrl = null;
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
                imageUrl = OssUtil.uploadFile(inputStream, originalFilename);
            } catch (Exception uploadException) {
                uploadException.printStackTrace();
                return CResult.error("图片上传失败: " + (uploadException.getMessage() != null ? uploadException.getMessage() : "未知错误"));
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                return CResult.success("图片上传成功", imageUrl);
            } else {
                return CResult.error("图片上传失败：未获取到图片URL");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("上传图片时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 发送图片消息
     * @param userId 当前登录用户ID（发送者）
     * @param receiverId 接收者ID
     * @param imageUrl 图片URL（通过uploadImage接口上传后获得）
     * @param content 可选的消息内容（图片描述）
     * @return 发送结果
     */
    @PostMapping("/sendImage")
    public CResult<Object> sendImageMessage(@RequestParam("userId") Integer userId,
                                            @RequestParam("receiverId") Integer receiverId,
                                            @RequestParam("imageUrl") String imageUrl,
                                            @RequestParam(value = "content", required = false) String content) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (receiverId == null) {
                return CResult.error("接收者ID不能为空");
            }
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return CResult.error("图片URL不能为空");
            }
            
            boolean success = lMessageService.sendMessage(
                userId, 
                receiverId, 
                content != null ? content.trim() : "", 
                1, 
                imageUrl.trim()
            );
            
            if (success) {
                return CResult.success("发送成功", null);
            } else {
                return CResult.error("发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("发送图片消息时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     * @param userId 当前登录用户ID
     * @param otherUserId 对方用户ID
     * @return 操作结果
     */
    @PostMapping("/markRead")
    public CResult<Object> markAsRead(@RequestParam("userId") Integer userId,
                                      @RequestParam("otherUserId") Integer otherUserId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            if (otherUserId == null) {
                return CResult.error("对方用户ID不能为空");
            }
            
            lMessageService.markMessagesAsRead(userId, otherUserId);
            return CResult.success("标记成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("标记已读时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取总未读消息数（所有私信，is_read=0）
     * @param userId 当前登录用户ID
     * @return 未读消息总数
     */
    @GetMapping("/totalUnreadCount")
    public CResult<Integer> getTotalUnreadCount(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Integer count = lMessageService.getTotalUnreadCount(userId);
            return CResult.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询总未读数时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取互关私信的未读消息数
     * @param userId 当前登录用户ID
     * @return 互关私信未读数
     */
    @GetMapping("/mutualFollowUnreadCount")
    public CResult<Integer> getMutualFollowUnreadCount(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Integer count = lMessageService.getMutualFollowUnreadCount(userId);
            return CResult.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询互关私信未读数时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取粉丝来信的未读消息数
     * @param userId 当前登录用户ID
     * @return 粉丝来信未读数
     */
    @GetMapping("/fanUnreadCount")
    public CResult<Integer> getFanUnreadCount(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Integer count = lMessageService.getFanUnreadCount(userId);
            return CResult.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询粉丝来信未读数时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有未读数（总未读数、互关私信未读数、粉丝来信未读数、联系管理员未读数）
     * @param userId 当前登录用户ID
     * @return 包含所有未读数的Map
     */
    @GetMapping("/allUnreadCounts")
    public CResult<Map<String, Integer>> getAllUnreadCounts(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Map<String, Integer> counts = new java.util.HashMap<>();
            counts.put("total", lMessageService.getTotalUnreadCount(userId));
            counts.put("mutualFollow", lMessageService.getMutualFollowUnreadCount(userId));
            counts.put("fan", lMessageService.getFanUnreadCount(userId));
            counts.put("admin", lMessageService.getAdminUnreadCount(userId));
            
            return CResult.success("查询成功", counts);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询未读数时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 查询管理员列表（用于联系管理员）
     * @param userId 当前登录用户ID（从请求参数获取）
     * @return 管理员列表
     */
    @GetMapping("/admin")
    public CResult<List<Map<String, Object>>> getAdminMessages(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            List<Map<String, Object>> messages = lMessageService.getAdminMessages(userId);
            if (messages != null) {
                return CResult.success("查询成功", messages);
            } else {
                return CResult.error("查询失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询管理员列表时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取联系管理员的未读消息数
     * @param userId 当前登录用户ID
     * @return 联系管理员未读数
     */
    @GetMapping("/adminUnreadCount")
    public CResult<Integer> getAdminUnreadCount(@RequestParam("userId") Integer userId) {
        try {
            if (userId == null) {
                return CResult.error("用户ID不能为空");
            }
            
            Integer count = lMessageService.getAdminUnreadCount(userId);
            return CResult.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("查询联系管理员未读数时发生异常: " + e.getMessage());
        }
    }
}



