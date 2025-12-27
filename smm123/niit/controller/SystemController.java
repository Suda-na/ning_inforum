package com.niit.controller;

import com.niit.pojo.SystemSetting;
import com.niit.pojo.User;
import com.niit.service.SystemManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private SystemManageService systemService;

    // ================= 系统设置 =================

    /**
     * 进入系统设置页面
     */
    @GetMapping("/settings")
    public String settingsPage(Model model) {
        SystemSetting config = systemService.getSetting();
        model.addAttribute("config", config);
        return "sys_settings";
    }

    /**
     * 更新系统设置 (Form表单提交)
     */
    @PostMapping("/setting/update")
    public String updateSetting(SystemSetting setting, Model model) {
        if (setting.getAllowRegister() == null) {
            setting.setAllowRegister(0);
        }

        boolean success = systemService.updateSetting(setting);
        model.addAttribute("msg", success ? "保存成功" : "保存失败");
        return "redirect:/system/settings";
    }

    // ================= 管理员管理 =================

    /**
     * 进入管理员列表页面
     */
    @GetMapping("/admin_list")
    public String adminListPage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<User> list = systemService.getAdminList(keyword);
        model.addAttribute("adminList", list);
        // 回显搜索关键字
        model.addAttribute("keyword", keyword);
        return "sys_admin";
    }

    /**
     * API: 添加管理员 (AJAX)
     */
    @PostMapping("/admin/add")
    @ResponseBody
    public Map<String, Object> addAdmin(User user) {
        Map<String, Object> res = new HashMap<>();
        try {
            boolean success = systemService.addAdmin(user);
            res.put("success", success);
            res.put("message", success ? "添加成功" : "用户名已存在或失败");
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "系统异常：" + e.getMessage());
        }
        return res;
    }

    /**
     * API: 删除管理员 (AJAX)
     */
    /**
     * API: 删除管理员 (AJAX)
     * 修改说明：增加了 try-catch 和 强制日志打印，用于排查 500 错误
     */
    @PostMapping("/admin/delete")
    @ResponseBody
    public Map<String, Object> deleteAdmin(@RequestParam("id") Integer id) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 2. 执行删除
            boolean success = systemService.deleteAdmin(id);
            res.put("success", success);
            res.put("message", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            // 4. 返回错误信息给前端，而不是直接报 500
            res.put("success", false);
            res.put("message", "后台报错：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        return res;
    }

    /**
     * [新增] API: 更新管理员信息 (AJAX)
     * 对应前端JS中的 url: '/system/admin/update'
     */
    @PostMapping("/admin/update")
    @ResponseBody
    public Map<String, Object> updateAdmin(User user) {

        Map<String, Object> res = new HashMap<>();
        try {
            boolean success = systemService.updateAdmin(user);
            res.put("success", success);
            res.put("message", success ? "更新成功" : "更新失败");
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "系统异常：" + e.getMessage());
        }
        return res;
    }
}