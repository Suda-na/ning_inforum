package com.niit.controller;

import com.niit.pojo.Message;
import com.niit.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统通知管理 Controller
 * 处理系统通知页面的跳转和查询
 */
@Controller
@RequestMapping("/admin")
public class SystemNotificationController {

    @Autowired
    private MessageService messageService;

    /**
     * 跳转到系统通知页面
     * @return 视图名称，会跳转到 msg_system.html
     */
    @RequestMapping("/msg_system")
    public String msgSystem() {
        return "msg_system";
    }

    /**
     * 查询系统通知列表（API接口）
     * @return 系统通知列表JSON
     */
    @RequestMapping("/api/msg_system/list")
    @ResponseBody
    public Map<String, Object> getSystemNotifications() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Message> notifications = messageService.getSystemNotifications();
            result.put("success", true);
            result.put("data", notifications);
            result.put("total", notifications.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 添加系统通知（API接口）
     * @param title 通知标题
     * @param content 通知内容
     * @param receiverId 接收者ID（如果为null或0，表示发送给所有用户，需要特殊处理）
     * @param imageUrl 图片地址（可选）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/add")
    @ResponseBody
    public Map<String, Object> addSystemNotification(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "receiverId", required = false) Integer receiverId,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 参数验证
            if (title == null || title.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知标题不能为空");
                return result;
            }
            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知内容不能为空");
                return result;
            }
            
            Message message = new Message();
            message.setTitle(title.trim());
            message.setContent(content.trim());
            // 如果receiverId为null或0，Service层会处理为发送给所有用户
            message.setReceiverId(receiverId != null && receiverId > 0 ? receiverId : null);
            message.setImageUrl(imageUrl);
            message.setMsgFormat(imageUrl != null && !imageUrl.isEmpty() ? 1 : 0); // 有图片则为1，否则为0
            
            boolean success = messageService.addSystemNotification(message);
            if (success) {
                result.put("success", true);
                result.put("message", "系统通知添加成功");
                result.put("data", message);
            } else {
                result.put("success", false);
                result.put("message", "添加失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询所有用户列表（API接口，用于选择接收者）
     * @return 用户列表JSON
     */
    @RequestMapping("/api/msg_system/users")
    @ResponseBody
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> users = messageService.getAllUsers();
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
     * 更新系统通知（API接口）
     * @param messageId 通知ID
     * @param title 通知标题
     * @param content 通知内容
     * @param receiverId 接收者ID（可选，不传则保持不变，由前端传原值）
     * @param imageUrl 图片地址（可选）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/update")
    @ResponseBody
    public Map<String, Object> updateSystemNotification(
            @RequestParam("messageId") Integer messageId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "receiverId", required = false) Integer receiverId,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            if (title == null || title.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知标题不能为空");
                return result;
            }
            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知内容不能为空");
                return result;
            }

            Message message = new Message();
            message.setMessageId(messageId);
            message.setTitle(title.trim());
            message.setContent(content.trim());
            message.setReceiverId(receiverId);
            message.setImageUrl(imageUrl);
            message.setMsgFormat(imageUrl != null && !imageUrl.isEmpty() ? 1 : 0);

            boolean success = messageService.updateSystemNotification(message);
            if (success) {
                result.put("success", true);
                result.put("message", "系统通知修改成功");
            } else {
                result.put("success", false);
                result.put("message", "修改失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修改失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除系统通知（API接口）
     * @param messageId 通知ID
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/delete")
    @ResponseBody
    public Map<String, Object> deleteSystemNotification(@RequestParam("messageId") Integer messageId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }

            boolean success = messageService.deleteSystemNotification(messageId);
            if (success) {
                result.put("success", true);
                result.put("message", "系统通知删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询系统通知的接收用户列表（API接口）
     * @param messageId 通知ID
     * @return 接收用户列表JSON
     */
    @RequestMapping("/api/msg_system/receivers")
    @ResponseBody
    public Map<String, Object> getNotificationReceivers(@RequestParam("messageId") Integer messageId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            
            // 先查询通知信息获取receiverId
            List<Message> notifications = messageService.getSystemNotifications();
            Message notification = null;
            for (Message msg : notifications) {
                if (msg.getMessageId() != null && msg.getMessageId().equals(messageId)) {
                    notification = msg;
                    break;
                }
            }
            
            if (notification == null) {
                result.put("success", false);
                result.put("message", "通知不存在");
                return result;
            }
            
            // 查询接收用户列表
            List<Map<String, Object>> receivers = messageService.getNotificationReceivers(notification.getReceiverId());
            result.put("success", true);
            result.put("data", receivers);
            result.put("total", receivers.size());
            result.put("isAllUsers", notification.getReceiverId() == null);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    // ========== 多接收者支持接口 ==========
    
    /**
     * 添加系统通知（支持多接收者）
     * @param title 通知标题
     * @param content 通知内容
     * @param receiverIds 接收者ID列表（JSON数组字符串，如 "[1,2,3]" 或 "all" 表示所有人）
     * @param imageUrl 图片地址（可选）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/add_multiple")
    @ResponseBody
    public Map<String, Object> addSystemNotificationWithReceivers(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "receiverIds", required = false) String receiverIds,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (title == null || title.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知标题不能为空");
                return result;
            }
            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "通知内容不能为空");
                return result;
            }
            
            Message message = new Message();
            message.setTitle(title.trim());
            message.setContent(content.trim());
            message.setImageUrl(imageUrl);
            message.setMsgFormat(imageUrl != null && !imageUrl.isEmpty() ? 1 : 0);
            
            List<Integer> receiverIdList = null;
            if (receiverIds != null && !receiverIds.trim().isEmpty() && !"all".equalsIgnoreCase(receiverIds.trim())) {
                // 解析JSON数组字符串
                try {
                    receiverIdList = parseReceiverIds(receiverIds);
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "接收者ID格式错误");
                    return result;
                }
            }
            
            boolean success = messageService.addSystemNotificationWithReceivers(message, receiverIdList);
            if (success) {
                result.put("success", true);
                result.put("message", "系统通知添加成功");
                result.put("data", message);
            } else {
                result.put("success", false);
                result.put("message", "添加失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 查询某个通知的所有接收者（从关联表查询）
     * @param messageId 通知ID
     * @return 接收者列表JSON
     */
    @RequestMapping("/api/msg_system/receivers_list")
    @ResponseBody
    public Map<String, Object> getMessageReceivers(@RequestParam("messageId") Integer messageId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            
            List<Map<String, Object>> receivers = messageService.getMessageReceivers(messageId);
            result.put("success", true);
            result.put("data", receivers);
            result.put("total", receivers != null ? receivers.size() : 0);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 更新系统通知的接收者列表
     * @param messageId 通知ID
     * @param receiverIds 新的接收者ID列表（JSON数组字符串，如 "[1,2,3]" 或 "all" 表示所有人）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/update_receivers")
    @ResponseBody
    public Map<String, Object> updateSystemNotificationReceivers(
            @RequestParam("messageId") Integer messageId,
            @RequestParam(value = "receiverIds", required = false) String receiverIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            
            List<Integer> receiverIdList = null;
            if (receiverIds != null && !receiverIds.trim().isEmpty() && !"all".equalsIgnoreCase(receiverIds.trim())) {
                try {
                    receiverIdList = parseReceiverIds(receiverIds);
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("message", "接收者ID格式错误");
                    return result;
                }
            }
            
            boolean success = messageService.updateSystemNotificationReceivers(messageId, receiverIdList);
            if (success) {
                result.put("success", true);
                result.put("message", "接收者更新成功");
            } else {
                result.put("success", false);
                result.put("message", "更新失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 为通知添加新的接收者
     * @param messageId 通知ID
     * @param receiverIds 要添加的接收者ID列表（JSON数组字符串，如 "[1,2,3]"）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/add_receivers")
    @ResponseBody
    public Map<String, Object> addReceiversToNotification(
            @RequestParam("messageId") Integer messageId,
            @RequestParam("receiverIds") String receiverIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            if (receiverIds == null || receiverIds.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "接收者ID列表不能为空");
                return result;
            }
            
            List<Integer> receiverIdList = parseReceiverIds(receiverIds);
            if (receiverIdList == null || receiverIdList.isEmpty()) {
                result.put("success", false);
                result.put("message", "接收者ID列表不能为空");
                return result;
            }
            
            boolean success = messageService.addReceiversToNotification(messageId, receiverIdList);
            if (success) {
                result.put("success", true);
                result.put("message", "接收者添加成功");
            } else {
                result.put("success", false);
                result.put("message", "添加失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 从通知中删除接收者
     * @param messageId 通知ID
     * @param receiverIds 要删除的接收者ID列表（JSON数组字符串，如 "[1,2,3]"）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/msg_system/remove_receivers")
    @ResponseBody
    public Map<String, Object> removeReceiversFromNotification(
            @RequestParam("messageId") Integer messageId,
            @RequestParam("receiverIds") String receiverIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (messageId == null) {
                result.put("success", false);
                result.put("message", "通知ID不能为空");
                return result;
            }
            if (receiverIds == null || receiverIds.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "接收者ID列表不能为空");
                return result;
            }
            
            List<Integer> receiverIdList = parseReceiverIds(receiverIds);
            if (receiverIdList == null || receiverIdList.isEmpty()) {
                result.put("success", false);
                result.put("message", "接收者ID列表不能为空");
                return result;
            }
            
            boolean success = messageService.removeReceiversFromNotification(messageId, receiverIdList);
            if (success) {
                result.put("success", true);
                result.put("message", "接收者删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 解析接收者ID列表（支持JSON数组字符串或逗号分隔的字符串）
     * @param receiverIdsStr 接收者ID字符串
     * @return 接收者ID列表
     */
    private List<Integer> parseReceiverIds(String receiverIdsStr) {
        List<Integer> receiverIds = new java.util.ArrayList<>();
        if (receiverIdsStr == null || receiverIdsStr.trim().isEmpty()) {
            return receiverIds;
        }
        
        String trimmed = receiverIdsStr.trim();
        // 如果是JSON数组格式 [1,2,3]
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        
        // 按逗号分割
        String[] parts = trimmed.split(",");
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                try {
                    receiverIds.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    // 忽略无效的数字
                }
            }
        }
        return receiverIds;
    }
}

