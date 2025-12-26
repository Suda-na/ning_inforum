package com.app.controller;

import com.app.common.CResult;
import com.app.config.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信配置检查控制器
 * 用于检查短信服务配置是否正确
 */
@RestController
@RequestMapping("/api/sms")
public class SmsConfigCheckController {

    @Autowired
    private SmsConfig smsConfig;

    /**
     * 检查短信配置
     * 用于调试和验证配置是否正确
     */
    @GetMapping("/checkConfig")
    public CResult<Map<String, Object>> checkConfig() {
        Map<String, Object> configInfo = new HashMap<>();
        
        // 检查各项配置
        boolean accessKeyIdValid = smsConfig.getAccessKeyId() != null && !smsConfig.getAccessKeyId().isEmpty();
        boolean accessKeySecretValid = smsConfig.getAccessKeySecret() != null && !smsConfig.getAccessKeySecret().isEmpty();
        boolean signNameValid = smsConfig.getSignName() != null && !smsConfig.getSignName().trim().isEmpty();
        boolean templateCodeValid = smsConfig.getTemplateCode() != null && !smsConfig.getTemplateCode().trim().isEmpty();
        
        configInfo.put("accessKeyId", accessKeyIdValid ? "已配置" : "未配置");
        configInfo.put("accessKeySecret", accessKeySecretValid ? "已配置" : "未配置");
        configInfo.put("signName", signNameValid ? smsConfig.getSignName() : "未配置");
        configInfo.put("signNameLength", signNameValid ? smsConfig.getSignName().length() : 0);
        configInfo.put("signNameBytes", signNameValid ? 
            java.util.Arrays.toString(smsConfig.getSignName().getBytes(StandardCharsets.UTF_8)) : "N/A");
        configInfo.put("templateCode", templateCodeValid ? smsConfig.getTemplateCode() : "未配置");
        configInfo.put("endpoint", smsConfig.getEndpoint() != null ? smsConfig.getEndpoint() : "未配置");
        configInfo.put("regionId", smsConfig.getRegionId() != null ? smsConfig.getRegionId() : "未配置");
        
        // 检查编码
        if (signNameValid) {
            String signName = smsConfig.getSignName();
            byte[] bytes = signName.getBytes(StandardCharsets.UTF_8);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            configInfo.put("signNameEncodingCheck", signName.equals(decoded) ? "正常" : "异常");
        }
        
        boolean allValid = accessKeyIdValid && accessKeySecretValid && signNameValid && templateCodeValid;
        configInfo.put("allConfigValid", allValid);
        
        if (allValid) {
            return CResult.success("配置检查完成", configInfo);
        } else {
            return CResult.error("配置不完整，请检查 sms.properties 文件");
        }
    }
}

