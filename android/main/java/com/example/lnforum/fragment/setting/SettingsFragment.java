package com.example.lnforum.fragment.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.lnforum.R;
import com.example.lnforum.activity.LoginActivity;
import com.example.lnforum.activity.EditPasswordActivity;
import com.example.lnforum.activity.EditSignatureActivity;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.utils.UriUtils; // 确保你有这个工具类
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.*;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    // 修改前 (错误)
// private static final String UPDATE_URL = "http://192.168.172.1:8081/api/cuser/update";
// private static final String UPLOAD_URL = "http://192.168.172.1:8081/api/cuser/upload_avatar";

    // 修改后 (正确)
    private static final String UPDATE_URL = "http://192.168.172.1:8081/api/cuser/update";
    private static final String UPLOAD_URL = "http://192.168.172.1:8081/api/cuser/upload_avatar";

    private CSessionManager sessionManager;
    private TextView usernameText, realNameText, phoneText, emailText, addressText, genderText, signatureText;
    private ImageView avatarImage;
    private View logoutButton, avatarContainer;
    private ImageView backButton;

    // 图片选择器
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化图片选择器
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadAvatar(uri);
            }
        });
    }

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

        // ✅ 绑定头像容器和图片
        avatarContainer = view.findViewById(R.id.ll_avatar_container);
        avatarImage = view.findViewById(R.id.settings_avatar);

        usernameText = view.findViewById(R.id.settings_username);
        realNameText = view.findViewById(R.id.settings_real_name);
        phoneText = view.findViewById(R.id.settings_phone);
        emailText = view.findViewById(R.id.settings_email);
        addressText = view.findViewById(R.id.settings_address);
        genderText = view.findViewById(R.id.settings_gender);
        signatureText = view.findViewById(R.id.settings_signature);
        logoutButton = view.findViewById(R.id.settings_logout);

        // 返回键逻辑
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBackToProfile());
        }

        // ✅ 核心：点击头像区域 -> 打开相册
        if (avatarContainer != null) {
            avatarContainer.setOnClickListener(v -> {
                Log.d(TAG, "点击更换头像");
                if (checkLogin()) pickImageLauncher.launch("image/*");
            });
        }

        // 绑定其他设置项点击事件
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
        setupClickListener(view, R.id.blacklist_item, () ->
                startActivity(new Intent(getContext(), com.example.lnforum.activity.BlacklistActivity.class)));

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

    // ✅ 上传头像逻辑
    private void uploadAvatar(Uri uri) {
        File file = UriUtils.getFileFromUri(getContext(), uri);
        if (file == null) {
            Toast.makeText(getContext(), "图片读取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "正在上传...", Toast.LENGTH_SHORT).show();
        CUser user = sessionManager.getCurrentCUser();

        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("userId", String.valueOf(user.getUserId()))
                .build();

        Request request = new Request.Builder().url(UPLOAD_URL).post(requestBody).build();

        // ... 前面的代码不变 ...

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                safeRunOnUiThread(() -> Toast.makeText(getContext(), "网络请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                safeRunOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<String>>(){}.getType();
                        CResult<String> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200) {
                            String newUrl = result.getData();
                            // 刷新 UI
                            Glide.with(SettingsFragment.this)
                                    .load(newUrl)
                                    .placeholder(R.drawable.ic_user_avatar)
                                    .circleCrop()
                                    .into(avatarImage);

                            // 更新本地 Session
                            user.setAvatar(newUrl);
                            sessionManager.saveCUser(user);
                            
                            // 刷新整个UI以确保数据同步
                            updateUI();

                            Toast.makeText(getContext(), "头像更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "上传失败: " + (result!=null?result.getMessage():"未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void goBackToProfile() {
        if (getActivity() instanceof com.example.lnforum.activity.WMainActivity) {
            com.example.lnforum.activity.WMainActivity mainActivity = (com.example.lnforum.activity.WMainActivity) getActivity();
            ViewPager2 viewPager = mainActivity.findViewById(R.id.view_pager);
            if (viewPager != null) {
                viewPager.setCurrentItem(2); // 跳回个人中心 Tab (根据你的实际索引修改)
            }
        } else {
            if (getActivity() != null) getActivity().finish();
        }
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        // 更新UI
        updateUI();
        // 返回个人中心页面
        goBackToProfile();
    }

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
                            // 使用后端返回的完整用户信息更新本地缓存
                            CUser updatedUser = result.getData();
                            if (updatedUser != null) {
                                sessionManager.saveCUser(updatedUser);
                                updateUI();
                                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                // 如果后端没有返回用户信息，使用本地更新方式
                                updateLocalUserField(currentUser, field, value);
                                sessionManager.saveCUser(currentUser);
                                updateUI();
                                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMsg = result != null ? result.getMessage() : "修改失败";
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
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
        boolean isLogin = sessionManager.isLoggedIn() && (user != null);
        
        if (isLogin && user != null) {
            setText(usernameText, user.getUsername());
            setText(realNameText, user.getRealName());
            setText(phoneText, user.getPhone());
            setText(emailText, user.getEmail());
            setText(addressText, user.getAddress());
            setText(signatureText, user.getSignature());
            setText(genderText, user.getGenderText());

            // ✅ 加载头像
            if (user.getAvatar() != null && !TextUtils.isEmpty(user.getAvatar())) {
                Glide.with(this).load(user.getAvatar())
                        .placeholder(R.drawable.ic_user_avatar)
                        .circleCrop().into(avatarImage);
            } else {
                if(avatarImage != null) avatarImage.setImageResource(R.drawable.ic_user_avatar);
            }
        } else {
            setText(usernameText, "未登录");
            setText(realNameText, "--");
            setText(phoneText, "--");
            setText(emailText, "--");
            setText(addressText, "--");
            setText(signatureText, "--");
            setText(genderText, "--");
            if(avatarImage != null) avatarImage.setImageResource(R.drawable.ic_user_avatar);
        }

        // 确保退出登录按钮在登录状态下显示
        if (logoutButton != null) {
            logoutButton.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        }
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