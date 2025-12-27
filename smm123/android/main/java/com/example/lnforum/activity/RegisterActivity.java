package com.example.lnforum.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lnforum.R;
import com.example.lnforum.model.CResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.*;

public class RegisterActivity extends AppCompatActivity {

    private static final String REG_URL = "http://192.168.243.1:8080/api/cuser/register";

    private EditText inputUsername, inputRealName, inputPhone, inputPassword, inputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.input_username);
        inputRealName = findViewById(R.id.input_real_name);
        inputPhone = findViewById(R.id.input_phone);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_register).setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String u = inputUsername.getText().toString().trim();
        String r = inputRealName.getText().toString().trim();
        String p = inputPhone.getText().toString().trim();
        String pass = inputPassword.getText().toString().trim();

        if(TextUtils.isEmpty(u) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", u)
                .add("realName", r)
                .add("phone", p)
                .add("password", pass)
                .build();

        Request req = new Request.Builder().url(REG_URL).post(body).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()-> Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<Object>>(){}.getType();
                        CResult<Object> res = gson.fromJson(json, type);
                        if(res!=null && res.getCode()==200){
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch(Exception e){ e.printStackTrace(); }
                });
            }
        });
    }
}