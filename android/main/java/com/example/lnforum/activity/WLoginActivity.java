package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lnforum.R;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
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
 * 登录页面：对接 PC 后端，支持账号密码登录和短信验证码登录。
 */
public class WLoginActivity extends AppCompatActivity {

    // 与 main1 保持一致的后端接口地址
    private static final String LOGIN_URL = "http://192.168.172.1:8081/api/cuser/login";
    private static final String SMS_LOGIN_URL = "http://192.168.172.1:8081/api/cuser/loginByCode";

    private EditText inputAccount, inputPassword, inputPhone, inputVerificationCode;
    private TextView btnGetCode, tvSmsLogin, tvBackToAccount, tvRegister;
    private LinearLayout layoutPasswordMode, layoutSmsMode, layoutVerificationCode;
    private boolean isSmsMode = false;
    private com.example.lnforum.utils.VerificationCodeHelper verificationCodeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setupClickListeners();
        
        // 初始化验证码工具类
        verificationCodeHelper = new com.example.lnforum.utils.VerificationCodeHelper();

        // 检查是否有从注册页面传回的用户名
        String username = getIntent().getStringExtra("username");
        if (!TextUtils.isEmpty(username)) {
            inputAccount.setText(username);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String username = data.getStringExtra("username");
            if (!TextUtils.isEmpty(username)) {
                inputAccount.setText(username);
                inputPassword.requestFocus();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationCodeHelper != null) {
            verificationCodeHelper.release();
        }
    }

    private void initViews() {
        inputAccount = findViewById(R.id.input_account);
        inputPassword = findViewById(R.id.input_password);
        inputPhone = findViewById(R.id.input_phone);
        inputVerificationCode = findViewById(R.id.input_verification_code);
        btnGetCode = findViewById(R.id.btn_get_code);
        tvSmsLogin = findViewById(R.id.tv_sms_login);
        tvBackToAccount = findViewById(R.id.tv_back_to_account);
        tvRegister = findViewById(R.id.tv_register);
        layoutPasswordMode = findViewById(R.id.layout_password_mode);
        layoutSmsMode = findViewById(R.id.layout_sms_mode);
        layoutVerificationCode = findViewById(R.id.layout_verification_code);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_login).setOnClickListener(this::handleLogin);
    }

    private void setupClickListeners() {
        tvSmsLogin.setOnClickListener(v -> switchToSmsMode());
        tvBackToAccount.setOnClickListener(v -> switchToAccountMode());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, 100);
        });

        btnGetCode.setOnClickListener(v -> {
            String phone = inputPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                inputPhone.requestFocus();
                return;
            }
            // 使用验证码工具类发送验证码
            if (verificationCodeHelper != null) {
                verificationCodeHelper.sendVerificationCode(phone, btnGetCode, 
                    new com.example.lnforum.utils.VerificationCodeHelper.SendCodeCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WLoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                            inputVerificationCode.setText("");
                        }
                        
                        @Override
                        public void onError(String message) {
                            Toast.makeText(WLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }

    private void switchToSmsMode() {
        isSmsMode = true;
        inputAccount.setVisibility(View.GONE);
        inputPassword.setVisibility(View.GONE);
        layoutPasswordMode.setVisibility(View.GONE);
        inputPhone.setVisibility(View.VISIBLE);
        layoutVerificationCode.setVisibility(View.VISIBLE);
        layoutSmsMode.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_login_label)).setText("验证码登录");
        // 清空验证码输入框，聚焦手机号输入框
        inputVerificationCode.setText("");
        inputPhone.requestFocus();
    }

    private void switchToAccountMode() {
        isSmsMode = false;
        inputAccount.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        layoutPasswordMode.setVisibility(View.VISIBLE);
        inputPhone.setVisibility(View.GONE);
        layoutVerificationCode.setVisibility(View.GONE);
        layoutSmsMode.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_login_label)).setText("账户登录");
        // 清空手机号和验证码输入框
        inputPhone.setText("");
        inputVerificationCode.setText("");
        inputAccount.requestFocus();
    }


    private void handleLogin(View view) {
        if (isSmsMode) {
            handleSmsLogin();
        } else {
            handlePasswordLogin();
        }
    }

    private void handlePasswordLogin() {
        String account = inputAccount.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            inputAccount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", account)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WLoginActivity.this, "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(WLoginActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>() {}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null) {
                            if (result.getCode() == 200) {
                                CUser user = result.getData();
                                CSessionManager.getInstance(WLoginActivity.this).saveCUser(user);

                                Toast.makeText(WLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                String msg = result.getMessage() != null ? result.getMessage() : "登录失败";
                                Toast.makeText(WLoginActivity.this, "登录失败：" + msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WLoginActivity.this, "登录失败：无法解析服务器响应", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(WLoginActivity.this, "解析错误: 请检查 Logcat", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void handleSmsLogin() {
        String phone = inputPhone.getText().toString().trim();
        String code = inputVerificationCode.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            inputPhone.requestFocus();
            return;
        }

        // 使用验证码工具类验证格式
        if (!com.example.lnforum.utils.VerificationCodeHelper.isValidPhone(phone)) {
            Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
            inputPhone.requestFocus();
            return;
        }
        
        phone = com.example.lnforum.utils.VerificationCodeHelper.formatPhone(phone);

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            inputVerificationCode.requestFocus();
            return;
        }
        
        // 验证验证码格式
        if (!com.example.lnforum.utils.VerificationCodeHelper.isValidCode(code)) {
            Toast.makeText(this, "验证码格式不正确，请输入4-6位数字", Toast.LENGTH_SHORT).show();
            inputVerificationCode.requestFocus();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("phone", phone)
                .add("code", code)
                .build();

        Request request = new Request.Builder()
                .url(SMS_LOGIN_URL)
                .post(body)
                .build();

        Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();

        android.util.Log.d("WLoginActivity", "验证码登录请求 - 手机号: " + phone + ", 验证码: " + code);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("WLoginActivity", "验证码登录失败", e);
                runOnUiThread(() -> Toast.makeText(WLoginActivity.this, "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final int httpCode = response.code();
                final String json = response.body() != null ? response.body().string() : null;
                android.util.Log.d("WLoginActivity", "验证码登录响应 - HTTP状态码: " + httpCode);
                android.util.Log.d("WLoginActivity", "验证码登录响应 - 响应内容: " + (json != null ? json : "null"));
                
                runOnUiThread(() -> {
                    try {
                        // 检查HTTP状态码
                        if (httpCode != 200) {
                            Toast.makeText(WLoginActivity.this, "服务器错误，HTTP状态码: " + httpCode, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        if (TextUtils.isEmpty(json) || "null".equals(json)) {
                            Toast.makeText(WLoginActivity.this, "服务器返回空响应，请检查验证码是否正确", Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(WLoginActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        
                        // 首先尝试解析为标准CResult格式
                        try {
                            Type type = new TypeToken<CResult<CUser>>() {}.getType();
                            CResult<CUser> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() != null) {
                                if (result.getCode() == 200) {
                                    CUser user = result.getData();
                                    if (user != null) {
                                        CSessionManager.getInstance(WLoginActivity.this).saveCUser(user);
                                        Toast.makeText(WLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        Toast.makeText(WLoginActivity.this, "登录失败：用户信息为空", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String msg = result.getMessage() != null ? result.getMessage() : "登录失败";
                                    // 根据错误码提供更友好的提示
                                    if (msg.contains("验证码") || msg.contains("code") || msg.contains("验证")) {
                                        Toast.makeText(WLoginActivity.this, "验证码错误或已过期，请重新获取", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(WLoginActivity.this, "登录失败：" + msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                return;
                            }
                        } catch (Exception e) {
                            android.util.Log.d("WLoginActivity", "尝试解析CResult格式失败，尝试SMS服务错误格式", e);
                        }

                        // 如果CResult解析失败，尝试解析SMS服务错误格式
                        try {
                            com.google.gson.JsonObject jsonObj = gson.fromJson(json, com.google.gson.JsonObject.class);
                            if (jsonObj.has("Code") && jsonObj.has("Message")) {
                                String errorCode = jsonObj.get("Code").getAsString();
                                String errorMessage = jsonObj.get("Message").getAsString();
                                
                                android.util.Log.w("WLoginActivity", "SMS服务错误 - Code: " + errorCode + ", Message: " + errorMessage);
                                
                                String userMessage;
                                if ("isv.INVALID_PARAMETERS".equals(errorCode)) {
                                    userMessage = "参数错误，请检查手机号和验证码";
                                } else if (errorMessage.contains("验证码") || errorMessage.contains("code") || errorMessage.contains("验证")) {
                                    userMessage = "验证码错误或已过期，请重新获取";
                                } else {
                                    userMessage = "登录失败：" + errorMessage;
                                }
                                Toast.makeText(WLoginActivity.this, userMessage, Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (Exception e) {
                            android.util.Log.e("WLoginActivity", "解析SMS服务错误格式失败", e);
                        }

                        // 如果都解析失败，显示原始响应
                        Toast.makeText(WLoginActivity.this, "登录失败：无法解析服务器响应", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("WLoginActivity", "无法解析的响应: " + json);
                        
                    } catch (JsonSyntaxException e) {
                        android.util.Log.e("WLoginActivity", "JSON解析错误", e);
                        Toast.makeText(WLoginActivity.this, "解析错误: JSON格式异常", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        android.util.Log.e("WLoginActivity", "处理响应时发生错误", e);
                        Toast.makeText(WLoginActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

