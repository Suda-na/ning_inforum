package com.app.controller;

import com.app.common.CResult;
import com.app.common.VerificationCodeManager;
import com.app.pojo.CUser;
import com.app.service.CUserService;
import com.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cuser") // 注意：安卓端的 URL 要改成这个
public class CClientController {
    
    private static final Logger logger = Logger.getLogger(CClientController.class.getName());

    @Autowired
    private CUserService cUserService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private VerificationCodeManager verificationCodeManager;

    @PostMapping("/login")
    public CResult<CUser> login(@RequestParam("username") String username,
                                @RequestParam("password") String password) {
        CUser user = cUserService.login(username, password);
        if (user != null) {
            return CResult.success("登录成功", user);
        }
        return CResult.error("账号或密码错误");
    }

    @PostMapping("/register")
    public CResult<Object> register(@RequestParam("username") String username,
                                    @RequestParam("realName") String realName,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("password") String password) {
        CUser user = new CUser();
        user.setUsername(username);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setPassword(password);

        if (cUserService.register(user)) {
            return CResult.success("注册成功", null);
        }
        return CResult.error("注册失败，用户名或手机号已存在");
    }

    @PostMapping("/update")
    public CResult<CUser> update(CUser user) {
        if (cUserService.updateInfo(user)) {
            // 更新成功后，为了数据同步，这里简单直接返回修改后的对象（实际可能需要重查）
            return CResult.success("更新成功", user);
        }
        return CResult.error("更新失败");
    }
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @return 发送结果
     */
    @PostMapping("/sendCode")
    public CResult<Object> sendCode(@RequestParam("phone") String phone) {
        try {
            // 验证手机号格式
            if (phone == null || phone.trim().isEmpty()) {
                return CResult.error("手机号不能为空");
            }
            
            // 简单的手机号格式验证（11位数字）
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return CResult.error("手机号格式不正确");
            }
            
            // 使用 dypns API 发送验证码（系统自动生成验证码）
            String[] result = smsService.sendVerificationCodeWithDypns(phone);
            logger.info("发送验证码返回结果: " + (result != null ? "result[0]=" + result[0] + ", result[1]=" + result[1] : "null"));
            
            if (result != null && result.length >= 2) {
                // 存储 AccessCode/BizId（验证码由系统生成并发送到手机，响应中不包含）
                String generatedCode = result[0]; // 可能为 null，因为响应中不包含验证码
                String accessCode = result[1]; // BizId 作为 AccessCode
                
                if (accessCode != null && !accessCode.isEmpty()) {
                    verificationCodeManager.storeCodeWithAccessCode(phone, generatedCode, accessCode);
                    logger.info("验证码已存储，手机号: " + phone + ", AccessCode/BizId: " + accessCode);
                    return CResult.success("验证码发送成功", null);
                } else {
                    logger.warning("AccessCode/BizId 为空，无法存储");
                }
            } else {
                logger.warning("发送验证码返回结果异常: " + (result != null ? "长度=" + result.length : "null"));
            }
            return CResult.error("验证码发送失败，请检查服务器日志或联系管理员");
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("发送验证码时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 验证码登录
     * @param phone 手机号
     * @param code 验证码
     * @return 登录结果
     */
    @PostMapping("/loginByCode")
    public CResult<CUser> loginByCode(@RequestParam("phone") String phone,
                                     @RequestParam("code") String code) {
        try {
            // 验证手机号格式
            if (phone == null || phone.trim().isEmpty()) {
                return CResult.error("手机号不能为空");
            }
            
            if (code == null || code.trim().isEmpty()) {
                return CResult.error("验证码不能为空");
            }
            
            // 使用 dypns API 验证验证码（方法内部会自动获取 AccessCode）
            boolean isValid = smsService.verifyCodeWithDypns(phone, code);
            if (isValid) {
                // 验证码正确，根据手机号查询用户
                CUser user = cUserService.findByPhone(phone);
                if (user != null) {
                    user.setPassword(null); // 不返回密码
                    return CResult.success("登录成功", user);
                }
                return CResult.error("用户不存在");
            }
            return CResult.error("验证码错误或已过期");
        } catch (Exception e) {
            e.printStackTrace();
            return CResult.error("验证验证码时发生异常: " + e.getMessage());
        }
    }
}