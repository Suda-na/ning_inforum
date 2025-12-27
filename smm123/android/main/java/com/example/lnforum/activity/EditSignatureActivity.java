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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditSignatureActivity extends AppCompatActivity {

    private static final String UPDATE_URL = "http://192.168.243.1:8080/api/cuser/update";

    private EditText signatureEdit;
    private CSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signature);

        sessionManager = CSessionManager.getInstance(this);

        initViews();
        loadCurrentSignature();
    }

    private void initViews() {
        ImageView backButton = findViewById(R.id.back_button);
        TextView saveButton = findViewById(R.id.save_button);
        TextView charCount = findViewById(R.id.char_count);
        signatureEdit = findViewById(R.id.signature_edit);

        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveSignature());

        signatureEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                int length = s.length();
                charCount.setText(length + "/200");
                if (length > 200) {
                    charCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
                } else {
                    charCount.setTextColor(getResources().getColor(R.color.text_hint, null));
                }
            }
        });
    }

    private void loadCurrentSignature() {
        CUser user = sessionManager.getCurrentCUser();
        if (user != null && !TextUtils.isEmpty(user.getSignature())) {
            signatureEdit.setText(user.getSignature());
        }
    }

    private void saveSignature() {
        String signature = signatureEdit.getText().toString().trim();

        if (signature.length() > 200) {
            Toast.makeText(this, "个性签名不能超过200个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        CUser user = sessionManager.getCurrentCUser();
        if (user != null) {
            updateSignatureOnServer(user, signature);
        } else {
            Toast.makeText(this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSignatureOnServer(CUser currentUser, String newSignature) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("userId", String.valueOf(currentUser.getUserId()))
                .add("signature", newSignature)
                .build();

        Request request = new Request.Builder().url(UPDATE_URL).post(body).build();

        Toast.makeText(this, "正在保存...", Toast.LENGTH_SHORT).show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EditSignatureActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
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

                            // ✅✅✅ 关键修复：手动更新本地对象的字段，而不是直接覆盖
                            // 1. 修改本地对象的签名
                            currentUser.setSignature(newSignature);

                            // 2. 保存这个包含完整信息的对象到 Session
                            sessionManager.saveCUser(currentUser);

                            Toast.makeText(EditSignatureActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = (result != null) ? result.getMessage() : "修改失败";
                            Toast.makeText(EditSignatureActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EditSignatureActivity.this, "解析错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}