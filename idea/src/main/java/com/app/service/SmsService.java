package com.app.service;

/**
 * 短信服务接口
 * 用于发送短信验证码
 */
public interface SmsService {
    /**
     * 发送短信验证码（使用 dypns API，系统自动生成验证码）
     * @param phone 手机号
     * @return 包含验证码和 AccessCode 的数组，[0]=验证码, [1]=AccessCode，如果发送失败返回 null
     */
    String[] sendVerificationCodeWithDypns(String phone);
    
    /**
     * 发送短信验证码（兼容旧接口，使用手动生成的验证码）
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String code);
    
    /**
     * 生成随机验证码
     * @param length 验证码长度
     * @return 随机验证码
     */
    String generateVerificationCode(int length);
    
    /**
     * 验证验证码（使用 dypns API）
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyCodeWithDypns(String phone, String code);
}


