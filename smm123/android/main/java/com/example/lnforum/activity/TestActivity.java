package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lnforum.R;
import com.example.lnforum.model.CResult; // 导入刚刚新建的 Result 类
import com.example.lnforum.model.CUser;
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

public class TestActivity extends AppCompatActivity {

    private TextView tvLog;
    private Button btnTest;

    // TODO: 确认你的 IP 和 端口 (Tomcat默认是8080)
    private static final String BASE_URL = "http://192.168.243.1:8080";

    // 登录接口地址
    private static final String LOGIN_URL = BASE_URL + "/api/user/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvLog = findViewById(R.id.tv_log);
        btnTest = findViewById(R.id.btn_test_connect);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLogin();
            }
        });
    }

    private void testLogin() {
        printLog("正在连接: " + LOGIN_URL);

        OkHttpClient client = new OkHttpClient();

        // 构建 POST 表单数据
        RequestBody formBody = new FormBody.Builder()
                .add("username", "admin") // 替换为真实用户名
                .add("password", "admin123")  // 替换为真实密码
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                printLog("【连接失败】: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonString = response.body().string();

                // 切换回主线程打印日志
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        printLog("【原始数据】: " + jsonString);
                        parseLoginData(jsonString);
                    } else {
                        printLog("服务器错误代码: " + response.code());
                    }
                });
            }
        });
    }

    private void parseLoginData(String json) {
        try {
            Gson gson = new Gson();

            // 使用 Result<User> 进行解析
            Type type = new TypeToken<CResult<CUser>>(){}.getType();
            CResult<CUser> result = gson.fromJson(json, type);

            if (result != null) {
                // 判断 code (根据你的 Result 类，200是成功)
                if (result.getCode() != null && result.getCode() == 200) {
                    CUser user = result.getData();
                    printLog("-----------------");
                    printLog("🎉 登录成功！");
                    printLog("消息: " + result.getMessage()); // 这里现在能正确获取 message 了
                    if (user != null) {
                        printLog("用户ID: " + user.getUserId());
                        printLog("用户名: " + user.getUsername());
                    }
                } else {
                    printLog("⚠️ 登录失败: " + result.getMessage());
                }
            }

        } catch (Exception e) {
            printLog("解析错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printLog(String msg) {
        // 确保在主线程更新 UI (这是另一种写法，也可以用 runOnUiThread)
        if (Thread.currentThread() == getMainLooper().getThread()) {
            tvLog.append("\n\n" + msg);
        } else {
            runOnUiThread(() -> tvLog.append("\n\n" + msg));
        }
    }
}