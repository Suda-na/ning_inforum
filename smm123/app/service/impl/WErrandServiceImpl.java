package com.app.service.impl;

import com.app.dao.WErrandMapper;
import com.app.service.WErrandService;
import com.niit.pojo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.niit.dao.UserMapper;
import com.niit.pojo.User;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 安卓端跑腿Service实现类
 */
@Service
public class WErrandServiceImpl implements WErrandService {

    @Autowired
    private WErrandMapper errandMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getOrders(String status, Integer page, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        // 构建查询参数
        List<Integer> statusList = new ArrayList<>();
        if ("unaccepted".equals(status)) {
            // 未接单：status=0或1（待审核或已通过但未接单）
            statusList.add(0);
            statusList.add(1);
        } else if ("completed".equals(status)) {
            // 已完成：status=3
            statusList.add(3);
        } else {
            // 默认查询未接单
            statusList.add(0);
            statusList.add(1);
        }
        
        int start = (page - 1) * pageSize;
        System.out.println("========== 跑腿查询开始 ==========");
        System.out.println("查询参数: status=" + status + ", statusList=" + statusList + ", start=" + start + ", size=" + pageSize);
        
        List<PostVO> posts = errandMapper.selectErrandOrders(statusList, start, pageSize);
        int total = errandMapper.countErrandOrders(statusList);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        System.out.println("数据库查询结果: 找到 " + posts.size() + " 条记录, 总数=" + total);
        
        // 转换为安卓端需要的格式
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            PostVO post = posts.get(i);
            try {
                System.out.println("开始转换第" + (i+1) + "条记录: postId=" + post.getPostId() + ", title=" + post.getTitle());
                Map<String, Object> orderMap = convertPostToOrderMap(post);
                orderList.add(orderMap);
                System.out.println("成功转换第" + (i+1) + "条记录: id=" + orderMap.get("id") + ", title=" + orderMap.get("title"));
            } catch (Exception e) {
                System.err.println("转换第" + (i+1) + "条记录失败: postId=" + post.getPostId() + ", error=" + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("转换完成，共转换 " + orderList.size() + " 条记录");
        System.out.println("返回结果: orders.size()=" + orderList.size() + ", total=" + total);
        System.out.println("========== 跑腿查询结束 ==========");
        
        result.put("orders", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }

    /**
     * 将PostVO转换为订单Map格式（适配安卓端）
     */
    private Map<String, Object> convertPostToOrderMap(PostVO post) {
        System.out.println("转换订单: postId=" + post.getPostId() + ", title=" + post.getTitle());
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(post.getPostId()));
        map.put("postId", post.getPostId());
        map.put("title", post.getTitle() != null ? post.getTitle() : "");
        map.put("desc", post.getContent() != null ? post.getContent() : "");
        map.put("from", post.getStartPoint() != null ? post.getStartPoint() : "");
        map.put("to", post.getEndPoint() != null ? post.getEndPoint() : "");
        map.put("price", post.getPrice() != null ? post.getPrice().toString() : "0.00");
        map.put("status", convertStatus(post.getStatus()));
        // tag字段存储发布者名称，用于安卓端显示
        map.put("tag", post.getUsername() != null ? post.getUsername() : "匿名");
        map.put("publisher", post.getUsername() != null ? post.getUsername() : "匿名");
        map.put("publisherId", post.getUserId()); // 添加发布者ID
        map.put("time", formatTime(post.getCreateTime()));
        map.put("remark", post.getItemInfo() != null ? post.getItemInfo() : "");
        
        // 添加图片列表
        List<String> images = new ArrayList<>();
        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            images.add(post.getImage1());
            System.out.println("添加图片1: " + post.getImage1());
        }
        if (post.getImage2() != null && !post.getImage2().isEmpty()) {
            images.add(post.getImage2());
            System.out.println("添加图片2: " + post.getImage2());
        }
        if (post.getImage3() != null && !post.getImage3().isEmpty()) {
            images.add(post.getImage3());
            System.out.println("添加图片3: " + post.getImage3());
        }
        map.put("images", images);
        System.out.println("订单转换完成，图片数量: " + images.size());
        
        return map;
    }

    /**
     * 转换状态
     */
    private String convertStatus(Integer status) {
        if (status == null) {
            return "waiting";
        }
        if (status == 0 || status == 1) {
            return "waiting"; // 未接单
        } else if (status == 3) {
            return "completed"; // 已完成
        } else {
            return "delivering"; // 其他状态视为配送中
        }
    }

    /**
     * 格式化时间
     */
    private String formatTime(Date date) {
        if (date == null) {
            return "刚刚";
        }
        
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (seconds < 60) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 7) {
            return days + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            return sdf.format(date);
        }
    }

    @Override
    public boolean isErrandRunner(Integer userId) {
        if (userId == null) {
            return false;
        }
        // 查询用户表的role字段，role=2表示跑腿员
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return false;
        }
        Integer role = user.getRole();
        // role: 0超级管理员, 1管理员, 2跑腿员, 3普通用户
        return role != null && role == 2;
    }

    @Override
    public Map<String, Object> getOrderDetail(Integer orderId) {
        PostVO post = errandMapper.selectErrandOrderById(orderId);
        if (post == null) {
            return null;
        }
        Map<String, Object> orderMap = convertPostToOrderMap(post);
        // 添加更多详情字段
        orderMap.put("contactInfo", post.getContactInfo() != null ? post.getContactInfo() : "");
        orderMap.put("itemInfo", post.getItemInfo() != null ? post.getItemInfo() : "");
        orderMap.put("status", post.getStatus());
        // 判断是否显示"联系管理员"按钮：status为0或1时显示
        boolean showContactAdmin = (post.getStatus() != null && (post.getStatus() == 0 || post.getStatus() == 1));
        orderMap.put("showContactAdmin", showContactAdmin);
        return orderMap;
    }

    @Override
    public boolean acceptOrder(Integer postId, Integer acceptorId) {
        try {
            // 1. 查询帖子信息
            PostVO post = errandMapper.selectErrandOrderById(postId);
            if (post == null) {
                System.err.println("接单失败：帖子不存在，postId=" + postId);
                return false;
            }
            
            // 2. 检查帖子状态是否为待接单（0或1）
            if (post.getStatus() == null || (post.getStatus() != 0 && post.getStatus() != 1)) {
                System.err.println("接单失败：帖子状态不允许接单，status=" + post.getStatus());
                return false;
            }
            
            // 3. 更新帖子状态为进行中（status=5）
            int updateResult = errandMapper.updatePostStatusToInProgress(postId);
            if (updateResult <= 0) {
                System.err.println("接单失败：更新帖子状态失败，postId=" + postId);
                return false;
            }
            
            // 4. 生成任务编号
            String taskNo = "ER" + System.currentTimeMillis();
            
            // 5. 创建trade_task记录
            int insertResult = errandMapper.insertTradeTask(
                taskNo,
                postId,
                post.getUserId(), // creatorId
                acceptorId,
                post.getTitle() != null ? post.getTitle() : "跑腿任务",
                post.getContent() != null ? post.getContent() : "",
                post.getPrice() != null ? post.getPrice() : java.math.BigDecimal.ZERO,
                post.getStartPoint(),
                post.getEndPoint()
            );
            
            if (insertResult <= 0) {
                System.err.println("接单失败：创建trade_task失败，postId=" + postId);
                // 回滚帖子状态
                // 这里可以添加回滚逻辑，但为了简化，我们只记录错误
                return false;
            }
            
            System.out.println("接单成功：postId=" + postId + ", acceptorId=" + acceptorId + ", taskNo=" + taskNo);
            return true;
        } catch (Exception e) {
            System.err.println("接单异常：postId=" + postId + ", acceptorId=" + acceptorId);
            e.printStackTrace();
            return false;
        }
    }
}

