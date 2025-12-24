package com.niit.controller;

import com.niit.pojo.Message;
import com.niit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.niit.utils.OssUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私信用户管理 Controller
 * 处理私信用户页面的跳转和查询
 */
@Controller
@RequestMapping("/admin")
public class PrivateMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 跳转到私信用户页面
     * @return 视图名称，会跳转到 msg_send.html
     */
    @RequestMapping("/msg_send")
    public String msgSend() {
        return "msg_send";
    }

    /**
     * 查询有私信的用户列表（API接口）
     * @return 用户列表JSON
     */
    @RequestMapping("/api/msg_send/users")
    @ResponseBody
    public Map<String, Object> getUsersWithPrivateMessages() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> users = messageService.getUsersWithPrivateMessages();
            result.put("success", true);
            result.put("data", users);
            result.put("total", users.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询指定用户之间的私信记录（API接口）
     * @param userId 用户ID
     * @return 私信记录列表JSON
     */
    @RequestMapping("/api/msg_send/messages")
    @ResponseBody
    public Map<String, Object> getMessages(@RequestParam("userId") Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesBetweenAdminAndUser(userId);
            // 将该用户发给管理员的未读消息标记为已读（红点和未读数消除）
            messageService.markPrivateMessagesAsRead(userId);
            result.put("success", true);
            result.put("data", messages);
            result.put("total", messages.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 图片上传接口
     * @param file 图片文件
     * @param request HTTP请求对象，用于获取服务器路径
     * @return 操作结果JSON，包含图片URL
     */
    @RequestMapping("/api/msg_send/uploadImage")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查文件是否为空
            if (file == null || file.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要上传的图片");
                return result;
            }

            // 检查文件大小（限制为5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                result.put("success", false);
                result.put("message", "图片大小不能超过5MB");
                return result;
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件名无效");
                return result;
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
                result.put("success", false);
                result.put("message", "只支持JPG、PNG、GIF、BMP、WEBP格式的图片");
                return result;
            }

            // 上传文件到阿里云OSS
            String imageUrl = null;
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
                imageUrl = OssUtil.uploadFile(inputStream, originalFilename);
            } catch (Exception uploadException) {
                // 上传失败，记录异常
                uploadException.printStackTrace();
                result.put("success", false);
                result.put("message", "图片上传失败：" + (uploadException.getMessage() != null ? uploadException.getMessage() : "未知错误"));
                return result;
            } finally {
                // 安全关闭输入流（不影响上传结果）
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        // 关闭流失败不影响上传结果，只记录日志
                        e.printStackTrace();
                    }
                }
            }

            // 验证上传是否成功（如果到了这里，说明上传没有抛出异常）
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // 返回图片访问URL（OSS的完整URL）
                result.put("success", true);
                result.put("imageUrl", imageUrl);
                result.put("message", "图片上传成功");
            } else {
                result.put("success", false);
                result.put("message", "图片上传失败：未获取到图片URL");
            }

        } catch (Exception e) {
            // 外层异常处理（参数验证等失败的情况）
            // 如果已经有imageUrl，说明上传成功了，返回成功
            if (result.containsKey("imageUrl") && result.get("imageUrl") != null) {
                result.put("success", true);
                result.put("message", "图片上传成功");
            } else {
                result.put("success", false);
                result.put("message", "图片上传失败：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
            }
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 管理员发送私信
     * @param userId 接收的用户ID
     * @param content 消息内容（可选，图片消息时可为空）
     * @param imageUrl 图片URL（可选，文本消息时为空）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_send/add")
    @ResponseBody
    public Map<String, Object> addPrivateMessage(@RequestParam("userId") Integer userId,
                                                 @RequestParam(value = "content", required = false) String content,
                                                 @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (userId == null) {
                result.put("success", false);
                result.put("message", "用户ID不能为空");
                return result;
            }

            // 判断是文本消息还是图片消息
            boolean isImageMessage = imageUrl != null && !imageUrl.trim().isEmpty();
            String finalContent = (content != null && !content.trim().isEmpty()) ? content.trim() : "";
            
            // 至少要有内容或图片之一
            if (!isImageMessage && (finalContent.isEmpty())) {
                result.put("success", false);
                result.put("message", "消息内容或图片不能同时为空");
                return result;
            }

            // 设置消息格式：0=文本，1=图片
            int msgFormat = isImageMessage ? 1 : 0;
            String finalImageUrl = isImageMessage ? imageUrl.trim() : null;

            boolean success = messageService.addPrivateMessageByAdmin(userId, finalContent, msgFormat, finalImageUrl);
            result.put("success", success);
            result.put("message", success ? "发送成功" : "发送失败，请稍后重试");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}

