package com.example.android_java2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_java2.R;
import com.example.android_java2.model.CResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;
import okhttp3.*;

public class RegisterActivity extends AppCompatActivity {

    private static final String REG_URL = "http://192.168.159.1:8080/api/cuser/register";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private EditText inputUsername, inputRealName, inputPhone, inputPassword, inputConfirmPassword;
    private TextView btnRegister;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.input_username);
        inputRealName = findViewById(R.id.input_real_name);
        inputPhone = findViewById(R.id.input_phone);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        if (isRegistering) {
            return; // 防止重复提交
        }

        String username = inputUsername.getText().toString().trim();
        String realName = inputRealName.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }

        if (username.length() < 3 || username.length() > 20) {
            Toast.makeText(this, "用户名长度应在3-20个字符之间", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
            return;
        }

        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "密码长度应在6-20个字符之间", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            inputConfirmPassword.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            inputPhone.requestFocus();
            return;
        }

        isRegistering = true;
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("realName", realName)
                .add("phone", phone)
                .add("password", password)
                .build();

        Request req = new Request.Builder().url(REG_URL).post(body).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    isRegistering = false;
                    btnRegister.setEnabled(true);
                    btnRegister.setText("立即注册");
                    Toast.makeText(RegisterActivity.this, "网络错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    isRegistering = false;
                    btnRegister.setEnabled(true);
                    btnRegister.setText("立即注册");
                    
                    try {
                        if (json.trim().startsWith("<")) {
                            Toast.makeText(RegisterActivity.this, "服务器错误 (返回了HTML)", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<Object>>(){}.getType();
                        CResult<Object> res = gson.fromJson(json, type);
                        
                        if (res != null && res.getCode() == 200) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            // 返回登录页面，并传递用户名
                            Intent intent = new Intent();
                            intent.putExtra("username", username);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            String msg = (res != null && !TextUtils.isEmpty(res.getMessage())) 
                                    ? res.getMessage() : "注册失败，请重试";
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}