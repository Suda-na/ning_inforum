package com.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 短信服务配置类
 * 用于存储阿里云短信服务的配置信息
 * 配置从 sms.properties 文件中读取，避免硬编码敏感信息
 */
@Component
public class SmsConfig {
    // 从配置文件读取，避免硬编码敏感信息
    @Value("${sms.accessKeyId}")
    private String accessKeyId; // 阿里云AccessKeyId
    
    @Value("${sms.accessKeySecret}")
    private String accessKeySecret; // 阿里云AccessKeySecret
    
    @Value("${sms.signName}")
    private String signName; // 短信签名
    
    @Value("${sms.templateCode}")
    private String templateCode; // 短信模板ID
    
    @Value("${sms.regionId}")
    private String regionId; // 地域ID
    
    @Value("${sms.endpoint}")
    private String endpoint; // 端点

    // Getters and Setters
    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}