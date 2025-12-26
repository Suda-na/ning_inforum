package com.example.android_java2.fragment.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2; // ✅ 引入 ViewPager2

import com.example.android_java2.R;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.activity.EditPasswordActivity;
import com.example.android_java2.activity.EditSignatureActivity;
import com.example.android_java2.activity.MainActivity; // ✅ 引入 MainActivity
import com.example.android_java2.model.CResult;
import com.example.android_java2.model.CUser;
import com.example.android_java2.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.*;

public class SettingsFragment extends Fragment {

    private static final String UPDATE_URL = "http://10.152.184.173:80/api/cuser/update";

    private CSessionManager sessionManager;
    private TextView usernameText, realNameText, phoneText, emailText, addressText, genderText, signatureText;
    private View logoutButton;
    private ImageView backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sessionManager = CSessionManager.getInstance(requireContext());
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void initViews(View view) {
        backButton = view.findViewById(R.id.back_button);
        usernameText = view.findViewById(R.id.settings_username);
        realNameText = view.findViewById(R.id.settings_real_name);
        phoneText = view.findViewById(R.id.settings_phone);
        emailText = view.findViewById(R.id.settings_email);
        addressText = view.findViewById(R.id.settings_address);
        genderText = view.findViewById(R.id.settings_gender);
        signatureText = view.findViewById(R.id.settings_signature);
        logoutButton = view.findViewById(R.id.settings_logout);

        // ✅ 1. 返回键逻辑：跳回个人中心 (ProfileFragment)
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBackToProfile());
        }

        // --- 绑定设置项点击事件 ---
        // ✅ 添加用户名的点击事件
        setupClickListener(view, R.id.settings_username_item, () ->
                showEditDialog("username", "修改用户名", usernameText.getText().toString()));
        setupClickListener(view, R.id.settings_real_name_item, () ->
                showEditDialog("realName", "修改真实姓名", realNameText.getText().toString()));

        setupClickListener(view, R.id.settings_phone_item, () ->
                showEditDialog("phone", "修改手机号", phoneText.getText().toString()));

        setupClickListener(view, R.id.settings_email_item, () ->
                showEditDialog("email", "修改邮箱", emailText.getText().toString()));

        setupClickListener(view, R.id.settings_address_item, () ->
                showEditDialog("address", "修改地址", addressText.getText().toString()));

        setupClickListener(view, R.id.settings_gender_item, this::showGenderDialog);

        setupClickListener(view, R.id.settings_signature_item, () ->
                startActivity(new Intent(getContext(), EditSignatureActivity.class)));

        setupClickListener(view, R.id.settings_password_item, () ->
                startActivity(new Intent(getContext(), EditPasswordActivity.class)));

        // 退出登录逻辑
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("确定要退出登录吗？")
                        .setPositiveButton("退出", (dialog, which) -> performLogout())
                        .setNegativeButton("取消", null)
                        .show();
            });
        }
    }

    /**
     * ✅ 核心逻辑：返回到个人中心
     * 假设你的 SettingsFragment 是 ViewPager 的第 4 页 (index 3)
     * 而 ProfileFragment 是第 3 页 (index 2) —— 请根据实际情况修改 index
     */
    private void goBackToProfile() {
        if (getActivity() instanceof com.example.android_java2.activity.MainActivity) {
            com.example.android_java2.activity.MainActivity mainActivity = (com.example.android_java2.activity.MainActivity) getActivity();
            ViewPager2 viewPager = mainActivity.findViewById(R.id.view_pager);
            if (viewPager != null) {
                // 🔴 这里的数字必须是 2！
                viewPager.setCurrentItem(2);
            }
        } else {
            // 如果不是在 MainActivity 里，可能是单独 Activity，直接 finish
            if (getActivity() != null) getActivity().finish();
        }
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();

        // ✅ 2. 退出后，直接执行返回操作，回到个人中心
        goBackToProfile();
    }

    // ... (下面的 updateUser, showEditDialog 等代码保持不变，跟之前给你的一样) ...
    // ... 为了节省篇幅，请保留之前我给你的 updateLocalUserField 等方法 ...

    private void setupClickListener(View root, int id, Runnable action) {
        View v = root.findViewById(id);
        if (v != null) {
            v.setOnClickListener(view -> {
                if (checkLogin()) action.run();
            });
        }
    }

    private void showEditDialog(String fieldName, String title, String currentVal) {
        EditText input = new EditText(getContext());
        input.setText(isEmptyPlaceholder(currentVal) ? "" : currentVal);
        input.setPadding(50, 50, 50, 50);
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> updateUser(fieldName, input.getText().toString().trim()))
                .setNegativeButton("取消", null)
                .show();
    }

    private void showGenderDialog() {
        String[] items = {"未知", "男", "女"};
        new AlertDialog.Builder(getContext())
                .setTitle("选择性别")
                .setItems(items, (dialog, which) -> updateUser("gender", String.valueOf(which)))
                .show();
    }

    private void updateUser(String field, String value) {
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null) return;

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("userId", String.valueOf(currentUser.getUserId()))
                .add(field, value);

        Request request = new Request.Builder().url(UPDATE_URL).post(builder.build()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                safeRunOnUiThread(() -> Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                safeRunOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>(){}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200) {
                            // ✅ 手动更新本地对象，防止数据丢失
                            updateLocalUserField(currentUser, field, value);
                            sessionManager.saveCUser(currentUser);
                            updateUI();
                            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void updateLocalUserField(CUser user, String field, String value) {
        switch (field) {
            case "username": user.setUsername(value); break;
            case "realName": user.setRealName(value); break;
            case "phone": user.setPhone(value); break;
            case "email": user.setEmail(value); break;
            case "address": user.setAddress(value); break;
            case "gender":
                try { user.setGender(Integer.parseInt(value)); } catch (Exception e) {}
                break;
        }
    }

    private void updateUI() {
        CUser user = sessionManager.getCurrentCUser();
        boolean isLogin = (user != null);
        setText(usernameText, isLogin ? user.getUsername() : "未登录");
        setText(realNameText, isLogin ? user.getRealName() : "--");
        setText(phoneText, isLogin ? user.getPhone() : "--");
        setText(emailText, isLogin ? user.getEmail() : "--");
        setText(addressText, isLogin ? user.getAddress() : "--");
        setText(signatureText, isLogin ? user.getSignature() : "--");
        setText(genderText, isLogin ? user.getGenderText() : "--");
        if (logoutButton != null) logoutButton.setVisibility(isLogin ? View.VISIBLE : View.GONE);
    }

    private void setText(TextView tv, String text) {
        if (tv != null) tv.setText(TextUtils.isEmpty(text) ? "未设置" : text);
    }

    private boolean isEmptyPlaceholder(String text) {
        return text == null || text.equals("--") || text.equals("未设置");
    }

    private void safeRunOnUiThread(Runnable action) {
        if (getActivity() != null && !isDetached()) getActivity().runOnUiThread(action);
    }

    private boolean checkLogin() {
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return false;
        }
        return true;
    }
}