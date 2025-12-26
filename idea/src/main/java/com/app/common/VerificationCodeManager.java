package com.app.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码管理类
 * 用于存储和验证验证码，支持验证码过期时间设置
 */
@Component
public class VerificationCodeManager {
    // 存储验证码：key=手机号，value=验证码信息
    private final Map<String, VerificationCodeInfo> codeMap = new HashMap<>();
    // 验证码过期时间（秒）
    private static final long EXPIRE_TIME = 300; // 5分钟

    /**
     * 存储验证码
     * @param phone 手机号
     * @param code 验证码
     */
    public void storeCode(String phone, String code) {
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(EXPIRE_TIME);
        codeMap.put(phone, new VerificationCodeInfo(code, expireAt));
    }

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 输入的验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        VerificationCodeInfo info = codeMap.get(phone);
        if (info == null) {
            return false; // 验证码不存在
        }
        if (System.currentTimeMillis() > info.expireAt) {
            codeMap.remove(phone); // 验证码已过期，移除
            return false;
        }
        boolean isValid = info.code.equals(code);
        if (isValid) {
            codeMap.remove(phone); // 验证成功，移除验证码
        }
        return isValid;
    }

    /**
     * 存储验证码和 AccessCode
     * @param phone 手机号
     * @param code 验证码
     * @param accessCode AccessCode
     */
    public void storeCodeWithAccessCode(String phone, String code, String accessCode) {
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(EXPIRE_TIME);
        codeMap.put(phone, new VerificationCodeInfo(code, expireAt, accessCode));
    }

    /**
     * 获取 AccessCode
     * @param phone 手机号
     * @return AccessCode，如果不存在返回 null
     */
    public String getAccessCode(String phone) {
        VerificationCodeInfo info = codeMap.get(phone);
        if (info == null) {
            return null;
        }
        // 检查是否过期
        if (System.currentTimeMillis() > info.expireAt) {
            codeMap.remove(phone); // 验证码已过期，移除
            return null;
        }
        return info.accessCode;
    }

    /**
     * 验证码信息内部类
     */
    private static class VerificationCodeInfo {
        private final String code;
        private final long expireAt;
        private final String accessCode;

        private VerificationCodeInfo(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
            this.accessCode = null;
        }

        private VerificationCodeInfo(String code, long expireAt, String accessCode) {
            this.code = code;
            this.expireAt = expireAt;
            this.accessCode = accessCode;
        }
    }
}


