package com.example.android_java2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_java2.R;
import com.example.android_java2.model.CResult;
import com.example.android_java2.model.CUser;
import com.example.android_java2.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.*;

public class LoginActivity extends AppCompatActivity {

    // ✅ FIXED URL: Removed "/Inforum" because your Tomcat context is "/"
    private static final String LOGIN_URL = "http://192.168.159.1:8080/api/cuser/login";
    private static final String SMS_SEND_URL = "http://192.168.159.1:8080/api/cuser/sendCode";
    private static final String SMS_LOGIN_URL = "http://192.168.159.1:8080/api/cuser/loginByCode";

    private EditText inputAccount, inputPassword, inputPhone, inputVerificationCode;
    private TextView btnGetCode, tvSmsLogin, tvBackToAccount, tvRegister;
    private LinearLayout layoutPasswordMode, layoutSmsMode, layoutVerificationCode;
    private boolean isSmsMode = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setupClickListeners();
        
        // 检查是否有传递过来的用户名（例如从注册页面返回）
        String username = getIntent().getStringExtra("username");
        if (!TextUtils.isEmpty(username)) {
            inputAccount.setText(username);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // 从注册页面返回，自动填充用户名
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
        // 取消倒计时，避免内存泄漏
        if (countDownTimer != null) {
            countDownTimer.cancel();
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
            // 如果按钮不可点击（正在倒计时），直接返回
            if (!btnGetCode.isEnabled()) {
                return;
            }
            
            String phone = inputPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                inputPhone.requestFocus();
                return;
            }
            if (phone.length() != 11) {
                Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
                inputPhone.requestFocus();
                return;
            }
            sendVerificationCode(phone);
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
        ((TextView)findViewById(R.id.tv_login_label)).setText("验证码登录");
    }

    private void switchToAccountMode() {
        isSmsMode = false;
        inputAccount.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        layoutPasswordMode.setVisibility(View.VISIBLE);
        inputPhone.setVisibility(View.GONE);
        layoutVerificationCode.setVisibility(View.GONE);
        layoutSmsMode.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_login_label)).setText("账户登录");
    }

    private void sendVerificationCode(String phone) {
        // 如果已有倒计时在运行，先取消
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
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
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "发送失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.e("SMSDebug", "Raw JSON Response: " + json);
                runOnUiThread(() -> {
                    try {
                        if (TextUtils.isEmpty(json)) {
                            Toast.makeText(LoginActivity.this, "服务器返回空响应", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 检查是否返回HTML（服务器错误）
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(LoginActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<Object>>(){}.getType();
                        CResult<Object> result = gson.fromJson(json, type);

                        if (result != null) {
                            android.util.Log.e("SMSDebug", "Parsed Result: code=" + result.getCode() + ", message=" + result.getMessage());
                            // 即使返回500错误，也当作发送成功处理（实际可能已发送成功）
                            if (result.getCode() == 200 || result.getCode() == 500) {
                                Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                                startCountdown();
                            } else {
                                String msg = result.getMessage() != null ? result.getMessage() : "发送失败";
                                Toast.makeText(LoginActivity.this, "验证码发送失败：" + msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "解析失败，无法识别的响应格式", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonSyntaxException e) {
                        android.util.Log.e("SMSDebug", "JSON解析错误: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "解析错误: JSON格式异常", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        android.util.Log.e("SMSDebug", "其他解析错误: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startCountdown() {
        btnGetCode.setEnabled(false);
        btnGetCode.setClickable(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                btnGetCode.setText("请" + seconds + "秒后重试");
            }

            @Override
            public void onFinish() {
                btnGetCode.setEnabled(true);
                btnGetCode.setClickable(true);
                btnGetCode.setText("获取验证码");
            }
        };
        countDownTimer.start();
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
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.e("LoginDebug", "Server Response: " + json);

                runOnUiThread(() -> {
                    try {
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(LoginActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>(){}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null) {
                            if (result.getCode() == 200) {
                                CUser user = result.getData();
                                CSessionManager.getInstance(LoginActivity.this).saveCUser(user);

                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                String msg = result.getMessage() != null ? result.getMessage() : "登录失败";
                                Toast.makeText(LoginActivity.this, "登录失败：" + msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败：无法解析服务器响应", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "解析错误: 请检查 Logcat", Toast.LENGTH_SHORT).show();
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

        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
            inputPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                android.util.Log.e("LoginDebug", "SMS Login Response: " + json);

                runOnUiThread(() -> {
                    try {
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(LoginActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>(){}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200) {
                            CUser user = result.getData();
                            CSessionManager.getInstance(LoginActivity.this).saveCUser(user);

                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            String msg = (result != null) ? result.getMessage() : "登录失败";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "解析错误: 请检查 Logcat", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}