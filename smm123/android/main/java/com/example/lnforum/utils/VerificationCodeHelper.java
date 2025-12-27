package com.example.lnforum.utils;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lnforum.model.CResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 验证码工具类
 * 封装验证码发送和校验功能
 */
public class VerificationCodeHelper {
    private static final String TAG = "VerificationCodeHelper";
    private static final String BASE_URL = "http://192.168.243.1:8080";
    private static final String SMS_SEND_URL = BASE_URL + "/api/cuser/sendCode";
    // 注意：后端没有单独的验证码验证接口，验证码验证在登录时完成（/api/cuser/loginByCode）
    
    // 倒计时时长（毫秒）
    private static final long COUNTDOWN_INTERVAL = 60000; // 60秒
    private static final long COUNTDOWN_TICK = 1000; // 1秒更新一次
    
    private CountDownTimer countDownTimer;
    private TextView codeButton;
    private String currentPhone;
    
    /**
     * 验证码发送回调接口
     */
    public interface SendCodeCallback {
        /**
         * 发送成功
         */
        void onSuccess();
        
        /**
         * 发送失败
         * @param message 错误信息
         */
        void onError(String message);
    }
    
    /**
     * 验证码校验回调接口
     */
    public interface VerifyCodeCallback {
        /**
         * 校验成功
         */
        void onSuccess();
        
        /**
         * 校验失败
         * @param message 错误信息
         */
        void onError(String message);
    }
    
    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        // 移除可能的空格、横线等字符
        phone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        // 验证手机号格式（中国大陆手机号：1开头，第二位为3-9，共11位）
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 格式化手机号（移除空格、横线等）
     * @param phone 原始手机号
     * @return 格式化后的手机号
     */
    public static String formatPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone.replaceAll("[\\s\\-\\(\\)]", "");
    }
    
    /**
     * 验证验证码格式
     * @param code 验证码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        // 验证码通常是4-6位数字
        return code.matches("^\\d{4,6}$");
    }
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @param codeButton 获取验证码按钮（用于显示倒计时）
     * @param callback 回调接口
     */
    public void sendVerificationCode(String phone, TextView codeButton, SendCodeCallback callback) {
        // 验证手机号格式
        if (!isValidPhone(phone)) {
            if (callback != null) {
                callback.onError("请输入正确的11位手机号");
            }
            return;
        }
        
        // 格式化手机号
        phone = formatPhone(phone);
        this.currentPhone = phone;
        this.codeButton = codeButton;
        
        // 如果按钮不为空，开始倒计时
        if (codeButton != null) {
            startCountdown();
        }
        
        Log.d(TAG, "发送验证码请求 - 手机号: " + phone);
        
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("phone", phone)
                .build();
        
        Request request = new Request.Builder()
                .url(SMS_SEND_URL)
                .post(body)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "发送验证码失败", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) {
                        callback.onError("发送失败: " + e.getMessage());
                    }
                    // 发送失败时取消倒计时
                    if (codeButton != null) {
                        cancelCountdown();
                    }
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final int httpCode = response.code();
                final String json = response.body() != null ? response.body().string() : null;
                Log.d(TAG, "发送验证码响应 - HTTP状态码: " + httpCode);
                Log.d(TAG, "发送验证码响应 - 响应内容: " + (json != null ? json : "null"));
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        // 检查HTTP状态码
                        if (httpCode != 200) {
                            if (callback != null) {
                                callback.onError("服务器错误，HTTP状态码: " + httpCode);
                            }
                            cancelCountdown();
                            return;
                        }
                        
                        if (TextUtils.isEmpty(json) || "null".equals(json)) {
                            if (callback != null) {
                                callback.onError("服务器返回空响应，可能是频率限制或服务异常");
                            }
                            // 空响应时也启动倒计时，防止频繁请求
                            return;
                        }
                        
                        if (json.trim().startsWith("<")) {
                            if (callback != null) {
                                callback.onError("服务器错误 (返回了HTML)");
                            }
                            cancelCountdown();
                            return;
                        }
                        
                        Gson gson = new Gson();
                        
                        // 首先尝试解析为标准CResult格式
                        try {
                            Type type = new TypeToken<CResult<Object>>() {}.getType();
                            CResult<Object> result = gson.fromJson(json, type);
                            
                            if (result != null && result.getCode() != null) {
                                if (result.getCode() == 200) {
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                } else {
                                    String msg = result.getMessage() != null ? result.getMessage() : "发送失败";
                                    if (callback != null) {
                                        callback.onError("验证码发送失败：" + msg);
                                    }
                                    cancelCountdown();
                                }
                                return;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "尝试解析CResult格式失败，尝试SMS服务错误格式", e);
                        }
                        
                        // 如果CResult解析失败，尝试解析SMS服务错误格式
                        try {
                            com.google.gson.JsonObject jsonObj = gson.fromJson(json, com.google.gson.JsonObject.class);
                            if (jsonObj.has("Code") && jsonObj.has("Message")) {
                                String errorCode = jsonObj.get("Code").getAsString();
                                String errorMessage = jsonObj.get("Message").getAsString();
                                boolean success = jsonObj.has("Success") && 
                                    (jsonObj.get("Success").isJsonPrimitive() && 
                                     jsonObj.get("Success").getAsJsonPrimitive().isBoolean() ? 
                                     jsonObj.get("Success").getAsBoolean() : 
                                     "true".equalsIgnoreCase(jsonObj.get("Success").getAsString()));
                                
                                Log.w(TAG, "SMS服务错误 - Code: " + errorCode + ", Message: " + errorMessage + ", Success: " + success);
                                
                                if (!success) {
                                    // 根据错误码提供更友好的错误提示
                                    String userMessage;
                                    if ("isv.INVALID_PARAMETERS".equals(errorCode)) {
                                        userMessage = "手机号格式不正确，请检查是否为11位有效手机号";
                                    } else if ("biz.FREQUENCY".equals(errorCode) || errorMessage.contains("frequency")) {
                                        userMessage = "发送过于频繁，请稍后再试（通常需要等待60秒）";
                                    } else if ("isv.MOBILE_NUMBER_ILLEGAL".equals(errorCode)) {
                                        userMessage = "手机号格式不正确";
                                    } else if ("isv.BUSINESS_LIMIT_CONTROL".equals(errorCode)) {
                                        userMessage = "发送频率超限，请稍后再试";
                                    } else {
                                        userMessage = "发送失败：" + errorMessage;
                                    }
                                    if (callback != null) {
                                        callback.onError(userMessage);
                                    }
                                    // 频率限制时也保持倒计时
                                    if (!errorMessage.contains("frequency") && !"isv.BUSINESS_LIMIT_CONTROL".equals(errorCode)) {
                                        cancelCountdown();
                                    }
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "解析SMS服务错误格式失败", e);
                        }
                        
                        // 如果都解析失败，显示原始响应
                        if (callback != null) {
                            callback.onError("解析失败，无法识别的响应格式");
                        }
                        Log.e(TAG, "无法解析的响应: " + json);
                        cancelCountdown();
                        
                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "JSON解析错误", e);
                        if (callback != null) {
                            callback.onError("解析错误: JSON格式异常");
                        }
                        cancelCountdown();
                    } catch (Exception e) {
                        Log.e(TAG, "处理响应时发生错误", e);
                        if (callback != null) {
                            callback.onError("解析错误: " + e.getMessage());
                        }
                        cancelCountdown();
                    }
                });
            }
        });
    }
    
    /**
     * 验证验证码格式（仅前端验证，不调用后端）
     * 注意：后端没有单独的验证码验证接口，验证码验证在登录时完成（/api/cuser/loginByCode）
     * @param code 验证码
     * @return 是否格式正确
     */
    public static boolean validateCodeFormat(String code) {
        return isValidCode(code);
    }
    
    /**
     * 开始倒计时
     */
    private void startCountdown() {
        if (codeButton == null) {
            return;
        }
        
        cancelCountdown();
        
        codeButton.setEnabled(false);
        codeButton.setClickable(false);
        
        countDownTimer = new CountDownTimer(COUNTDOWN_INTERVAL, COUNTDOWN_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                if (codeButton != null) {
                    codeButton.setText("请" + seconds + "秒后重试");
                }
            }
            
            @Override
            public void onFinish() {
                if (codeButton != null) {
                    codeButton.setEnabled(true);
                    codeButton.setClickable(true);
                    codeButton.setText("获取验证码");
                }
            }
        };
        countDownTimer.start();
    }
    
    /**
     * 取消倒计时
     */
    public void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (codeButton != null) {
            codeButton.setEnabled(true);
            codeButton.setClickable(true);
            codeButton.setText("获取验证码");
        }
    }
    
    /**
     * 释放资源
     */
    public void release() {
        cancelCountdown();
        codeButton = null;
        currentPhone = null;
    }
}

