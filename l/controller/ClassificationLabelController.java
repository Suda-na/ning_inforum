package com.niit.controller;

import com.niit.pojo.Tag;
import com.niit.service.CategoryService;
import com.niit.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类标签管理 Controller
 * 处理分类标签管理页面的跳转和查询
 */
@Controller
@RequestMapping("/admin")
public class ClassificationLabelController {

    @Autowired
    private TagService tagService;
    
    @Autowired
    private CategoryService categoryService;

    /**
     * 跳转到分类标签管理页面
     * @return 视图名称，会跳转到 classification_label.html
     */
    @RequestMapping("/classification_label")
    public String classificationLabel() {
        return "classification_label";
    }

    /**
     * 查询分类标签列表（API接口）
     * @param tagName 标签名称（可选，用于搜索）
     * @param module 所属模块（可选，用于筛选，需要转换为categoryId）
     * @return 分类标签列表JSON
     */
    @RequestMapping("/api/classification_label/list")
    @ResponseBody
    public Map<String, Object> getClassificationLabels(
            @RequestParam(value = "tagName", required = false) String tagName,
            @RequestParam(value = "module", required = false) String module) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 将模块名称转换为categoryId（如果需要）
            Integer categoryId = null;
            if (module != null && !module.isEmpty() && !"all".equals(module)) {
                categoryId = convertModuleToCategoryId(module);
            }
            
            List<Tag> tags = tagService.getAllTags(tagName, categoryId);
            result.put("success", true);
            result.put("data", tags);
            result.put("total", tags != null ? tags.size() : 0);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 将模块名称转换为categoryId
     * @param module 模块名称（circle/errand/second-hand/lost-found）
     * @return categoryId
     */
    private Integer convertModuleToCategoryId(String module) {
        // 模块名称到分类名称的映射
        Map<String, String> moduleToCategoryMap = new HashMap<>();
        moduleToCategoryMap.put("circle", "动态");
        moduleToCategoryMap.put("errand", "跑腿");
        moduleToCategoryMap.put("second-hand", "二手集市");
        moduleToCategoryMap.put("lost-found", "失物招领");
        
        String categoryName = moduleToCategoryMap.get(module);
        if (categoryName == null) {
            return null;
        }
        
        // 通过CategoryService查询categoryId
        com.niit.pojo.Category category = categoryService.getCategoryByName(categoryName);
        return category != null ? category.getCategoryId() : null;
    }

    /**
     * 添加分类标签（API接口）
     * @param tagName 标签名称
     * @param module 所属模块（circle/errand/second-hand/lost-found）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/classification_label/add")
    @ResponseBody
    public Map<String, Object> addClassificationLabel(
            @RequestParam("tagName") String tagName,
            @RequestParam(value = "module", required = false) String module) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 参数验证
            if (tagName == null || tagName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "标签名称不能为空");
                return result;
            }
            
            // 将模块名称转换为categoryId
            Integer categoryId = null;
            if (module != null && !module.isEmpty() && !"all".equals(module)) {
                categoryId = convertModuleToCategoryId(module);
            }
            
            boolean success = tagService.addTag(tagName.trim(), categoryId);
            if (success) {
                result.put("success", true);
                result.put("message", "分类标签添加成功");
            } else {
                result.put("success", false);
                result.put("message", "添加失败，标签名称可能已存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新分类标签（API接口）
     * @param tagId 标签ID
     * @param tagName 标签名称
     * @param module 所属模块（可选）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/classification_label/update")
    @ResponseBody
    public Map<String, Object> updateClassificationLabel(
            @RequestParam("tagId") Integer tagId,
            @RequestParam("tagName") String tagName,
            @RequestParam(value = "module", required = false) String module) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (tagId == null) {
                result.put("success", false);
                result.put("message", "标签ID不能为空");
                return result;
            }
            if (tagName == null || tagName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "标签名称不能为空");
                return result;
            }

            // 将模块名称转换为categoryId
            Integer categoryId = null;
            if (module != null && !module.isEmpty() && !"all".equals(module)) {
                categoryId = convertModuleToCategoryId(module);
            }

            boolean success = tagService.updateTag(tagId, tagName.trim(), categoryId);
            if (success) {
                result.put("success", true);
                result.put("message", "分类标签修改成功");
            } else {
                result.put("success", false);
                result.put("message", "修改失败，标签不存在或名称已存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修改失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除分类标签（API接口）
     * @param tagId 标签ID
     * @return 操作结果JSON
     */
    @RequestMapping("/api/classification_label/delete")
    @ResponseBody
    public Map<String, Object> deleteClassificationLabel(@RequestParam("tagId") Integer tagId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (tagId == null) {
                result.put("success", false);
                result.put("message", "标签ID不能为空");
                return result;
            }

            boolean success = tagService.deleteTag(tagId);
            if (success) {
                result.put("success", true);
                result.put("message", "分类标签删除成功");
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
     * 批量删除分类标签（API接口）
     * @param tagIds 标签ID列表（逗号分隔的字符串）
     * @return 操作结果JSON
     */
    @RequestMapping("/api/classification_label/batch_delete")
    @ResponseBody
    public Map<String, Object> batchDeleteClassificationLabels(@RequestParam("tagIds") String tagIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (tagIds == null || tagIds.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "标签ID列表不能为空");
                return result;
            }

            // 解析标签ID列表
            String[] idArray = tagIds.split(",");
            java.util.List<Integer> idList = new java.util.ArrayList<>();
            for (String idStr : idArray) {
                try {
                    idList.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // 忽略无效的ID
                }
            }

            if (idList.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有有效的标签ID");
                return result;
            }

            boolean success = tagService.batchDeleteTags(idList);
            if (success) {
                result.put("success", true);
                result.put("message", "批量删除成功，共删除 " + idList.size() + " 个标签");
            } else {
                result.put("success", false);
                result.put("message", "批量删除失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}

