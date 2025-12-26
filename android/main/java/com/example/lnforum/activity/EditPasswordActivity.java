package com.example.lnforum.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

public class EditPasswordActivity extends AppCompatActivity {

    // ✅ 使用和 SettingsFragment 一样的更新接口
    private static final String UPDATE_URL = "http://192.168.172.1:8081/api/cuser/update";

    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;
    private EditText confirmPasswordEdit;
    private CSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        sessionManager = CSessionManager.getInstance(this);
        initViews();
    }

    private void initViews() {
        ImageView backButton = findViewById(R.id.back_button);
        TextView saveButton = findViewById(R.id.save_button);
        oldPasswordEdit = findViewById(R.id.old_password);
        newPasswordEdit = findViewById(R.id.new_password);
        confirmPasswordEdit = findViewById(R.id.confirm_password);

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> savePassword());
    }

    private void savePassword() {
        String oldPwd = oldPasswordEdit.getText().toString().trim();
        String newPwd = newPasswordEdit.getText().toString().trim();
        String confirmPwd = confirmPasswordEdit.getText().toString().trim();

        if (TextUtils.isEmpty(oldPwd)) {
            Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 简单本地校验：如果本地 Session 有存密码，可以先比对一下（可选）
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser != null && currentUser.getPassword() != null && !currentUser.getPassword().isEmpty()) {
            if (!currentUser.getPassword().equals(oldPwd)) {
                Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6) {
            Toast.makeText(this, "新密码至少6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 发起网络请求更新密码
        if (currentUser != null) {
            updatePasswordOnServer(currentUser, newPwd);
        } else {
            Toast.makeText(this, "用户信息失效，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePasswordOnServer(CUser user, String newPassword) {
        OkHttpClient client = new OkHttpClient();

        // 提交 userId 和新的 password
        RequestBody body = new FormBody.Builder()
                .add("userId", String.valueOf(user.getUserId()))
                .add("password", newPassword)
                .build();

        Request request = new Request.Builder().url(UPDATE_URL).post(body).build();

        Toast.makeText(this, "正在保存...", Toast.LENGTH_SHORT).show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EditPasswordActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>(){}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200) {
                            // 更新成功：更新本地缓存的密码
                            user.setPassword(newPassword);
                            sessionManager.saveCUser(user);

                            Toast.makeText(EditPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = result != null ? result.getMessage() : "修改失败";
                            Toast.makeText(EditPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}