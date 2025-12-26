package com.niit.controller;

import com.niit.pojo.PostVO;
import com.niit.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子管理 Controller
 */
@Controller
@RequestMapping("/admin")
public class PostsController {

    @Autowired
    private PostService postService;

    /**
     * 跳转到所有帖子页面
     * @param model 模型
     * @return 视图名称
     */
    @RequestMapping("/allposts")
    public String allPosts(Model model) {
        // 获取待审核帖子数量
        int pendingCount = postService.countPostsByStatus(0);
        model.addAttribute("pendingCount", pendingCount);
        return "all_posts";
    }

    /**
     * 获取帖子列表（分页）
     * @param page 页码，从1开始
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param searchType 搜索类型：user(用户), content(帖子内容)
     * @param date 日期筛选
     * @param statusFilter 状态筛选：all(全部), pending(待审核), passed(已通过), rejected(未通过)
     * @return 帖子列表
     */
    @RequestMapping("/posts/list")
    @ResponseBody
    public Map<String, Object> getPostsList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "searchType", defaultValue = "user") String searchType,
                                           @RequestParam(value = "date", required = false) String date,
                                           @RequestParam(value = "statusFilter", defaultValue = "all") String statusFilter) {
        Map<String, Object> result = new HashMap<>();
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
            params.put("searchType", searchType);
        }
        if (date != null && !date.trim().isEmpty()) {
            params.put("date", date.trim());
        }
        if (statusFilter != null && !statusFilter.equals("all")) {
            params.put("statusFilter", statusFilter);
        }
        
        // 计算分页参数
        int start = (page - 1) * pageSize;
        params.put("start", start);
        params.put("size", pageSize);
        
        // 查询数据
        List<PostVO> posts = postService.searchPosts(params);
        int total = postService.countPosts(params);
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        // 构建返回结果
        result.put("posts", posts);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @RequestMapping("/posts/detail/{postId}")
    @ResponseBody
    public PostVO getPostDetail(@PathVariable("postId") Integer postId) {
        return postService.getPostById(postId);
    }

    /**
     * 更新帖子状态（审核）
     * @param postId 帖子ID
     * @param status 状态：1已通过, 4未通过
     * @return 处理结果
     */
    @RequestMapping(value = "/posts/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updatePostStatus(@RequestParam("postId") Integer postId,
                                               @RequestParam("status") Integer status) {
        Map<String, Object> result = new HashMap<>();
        
        // 状态：0待审核, 1已通过, 2已删除, 3已结束, 4未通过
        // 只允许将状态更新为1（已通过）或4（未通过）
        if (status != 1 && status != 4) {
//            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无效的状态值");
            return result;
        }
        
        boolean success = postService.updatePostStatus(postId, status);
        
        if (success) {
            result.put("success", true);
            result.put("message", "操作成功");
        } else {
            result.put("success", false);
            result.put("message", "操作失败");
        }
        
        return result;
    }

    /**
     * 获取待审核帖子数量
     * @return 待审核帖子数量
     */
    @RequestMapping("/posts/pendingCount")
    @ResponseBody
    public Map<String, Object> getPendingCount() {
        Map<String, Object> result = new HashMap<>();
        int pendingCount = postService.countPostsByStatus(0);
        result.put("pendingCount", pendingCount);
        return result;
    }
}
