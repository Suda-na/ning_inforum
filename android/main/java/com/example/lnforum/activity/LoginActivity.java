package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lnforum.R;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.*;

public class LoginActivity extends AppCompatActivity {

    // ✅ FIXED URL: Removed "/Inforum" because your Tomcat context is "/"
    private static final String LOGIN_URL = "http://192.168.172.1:8081/api/cuser/login";

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
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        btnGetCode.setOnClickListener(v -> {
            Toast.makeText(this, "演示环境：验证码功能暂不可用", Toast.LENGTH_SHORT).show();
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

    private void handleLogin(View view) {
        if (isSmsMode) {
            Toast.makeText(this, "请使用账号密码登录", Toast.LENGTH_SHORT).show();
        } else {
            handlePasswordLogin();
        }
    }

    private void handlePasswordLogin() {
        String account = inputAccount.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
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