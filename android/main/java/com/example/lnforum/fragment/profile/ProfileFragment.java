package com.example.lnforum.fragment.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.lnforum.R;
import com.example.lnforum.activity.FollowListActivity;
import com.example.lnforum.activity.WMainActivity;
import com.example.lnforum.activity.LoginActivity;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.utils.UriUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    // Backend APIs
    private static final String API_STATS = "http://192.168.172.1:8081/api/cuser/user_stats?userId=";
    private static final String API_UPLOAD = "http://192.168.172.1:8081/api/cuser/upload_avatar";

    // UI Components
    private ViewPager2 profileViewPager;
    private TabLayout tabLayout; // ✅ 新增：使用 Google 的 TabLayout
    private ImageView profileGender;
    private ImageView profileAvatar;

    // ❌ 已删除旧的 Tab 控件 (postsTab, commentsTab 等)

    private List<Fragment> fragmentList;
    private LinearLayout settingsButton;
    private LinearLayout loginButton;
    private TextView profileName;
    private TextView profileIp;
    private TextView profileDays; // Signature
    private TextView profileFollowCount;
    private TextView profileFansCount;
    private TextView profileLikeCount;

    private CSessionManager sessionManager;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Image Picker
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadImage(uri);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = CSessionManager.getInstance(requireContext());

        // Bind Views
        settingsButton = view.findViewById(R.id.settings_button);
        loginButton = view.findViewById(R.id.login_button);
        profileName = view.findViewById(R.id.profile_name);
        profileGender = view.findViewById(R.id.profile_gender);
        profileAvatar = view.findViewById(R.id.profile_avatar);
        profileIp = view.findViewById(R.id.profile_ip);
        profileDays = view.findViewById(R.id.profile_days);
        profileFollowCount = view.findViewById(R.id.profile_follow_count);
        profileFansCount = view.findViewById(R.id.profile_fans_count);
        profileLikeCount = view.findViewById(R.id.profile_like_count);

        // Click Listeners
        view.findViewById(R.id.profile_follow_container).setOnClickListener(v -> openFollowList(FollowListActivity.TYPE_FOLLOWING));
        view.findViewById(R.id.profile_fans_container).setOnClickListener(v -> openFollowList(FollowListActivity.TYPE_FANS));

        view.findViewById(R.id.profile_like_container).setOnClickListener(v -> {
            if (sessionManager.isLoggedIn() && profileViewPager != null) {
                profileViewPager.setCurrentItem(2, true); // Jump to "Eye/Collection" tab
            } else {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        settingsButton.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(getContext(), LoginActivity.class));
                return;
            }
             WMainActivity activity = (WMainActivity) getActivity();
            if (activity != null) {
                ViewPager2 viewPager = activity.findViewById(R.id.view_pager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(3); // Navigate to Settings tab if applicable
                }
            }
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));

        // Avatar Click -> Upload
        if (profileAvatar != null) {
            profileAvatar.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    pickImageLauncher.launch("image/*");
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ✅ 初始化新组件
        initFragments();
        initViewPager(view); // 包含 ViewPager 和 TabLayout 的绑定

        // Initial UI Update
        updateLoginUi();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginUi();
        loadUserStats();
    }

    // --- Upload Logic ---
    private void uploadImage(Uri uri) {
        File file = UriUtils.getFileFromUri(getContext(), uri);
        if (file == null) {
            Toast.makeText(getContext(), "图片读取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        CUser user = sessionManager.getCurrentCUser();
        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("userId", String.valueOf(user.getUserId()))
                .build();

        Request request = new Request.Builder().url(API_UPLOAD).post(requestBody).build();

        Toast.makeText(getContext(), "正在上传...", Toast.LENGTH_SHORT).show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() != null)
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "上传失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<String>>(){}.getType();
                            CResult<String> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200) {
                                String newUrl = result.getData();
                                Toast.makeText(getContext(), "头像更新成功", Toast.LENGTH_SHORT).show();

                                // Update UI immediately
                                if (profileAvatar != null) {
                                    // 🔴 关键修复：清除可能存在的 tint
                                    profileAvatar.setImageTintList(null);
                                    Glide.with(getContext())
                                            .load(newUrl)
                                            .circleCrop()
                                            .into(profileAvatar);
                                }

                                // Update local session
                                user.setAvatar(newUrl);
                                sessionManager.saveCUser(user);
                            } else {
                                Toast.makeText(getContext(), "失败: " + (result!=null?result.getMessage():"未知"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    // --- Refresh User Info from Server ---
    private void refreshUserInfo(Integer userId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.172.1:8081/api/cuser/userInfo?userId=" + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 失败时使用本地缓存的数据
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateUIFromLocalUser());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<CUser>>(){}.getType();
                            CResult<CUser> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200 && result.getData() != null) {
                                CUser user = result.getData();
                                // 更新本地session
                                sessionManager.saveCUser(user);
                                // 更新UI
                                updateUIFromUser(user);
                            } else {
                                // 使用本地缓存
                                updateUIFromLocalUser();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            updateUIFromLocalUser();
                        }
                    });
                }
            }
        });
    }

    private void updateUIFromLocalUser() {
        CUser user = sessionManager.getCurrentCUser();
        if (user != null) {
            updateUIFromUser(user);
        }
    }

    private void updateUIFromUser(CUser user) {
        if (user == null) return;
        
        if (profileName != null) {
            profileName.setText(user.getUsername() != null ? user.getUsername() : "用户");
        }

        // Gender Icon Logic
        if (profileGender != null) {
            if (user.getGender() != null && user.getGender() == 1) { // Male
                profileGender.setVisibility(View.VISIBLE);
                profileGender.setImageResource(R.drawable.ic_gender_male);
            } else if (user.getGender() != null && user.getGender() == 2) { // Female
                profileGender.setVisibility(View.VISIBLE);
                profileGender.setImageResource(R.drawable.ic_gender_female);
            } else {
                profileGender.setVisibility(View.GONE);
            }
        }

        // Avatar
        if (profileAvatar != null) {
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                profileAvatar.setImageTintList(null);
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(R.drawable.ic_user_avatar)
                        .error(R.drawable.ic_user_avatar)
                        .circleCrop()
                        .into(profileAvatar);
            } else {
                profileAvatar.setImageResource(R.drawable.ic_user_avatar);
            }
        }

        // IP Address
        if (profileIp != null) {
            String address = (!TextUtils.isEmpty(user.getAddress())) ? user.getAddress() : "未知";
            profileIp.setText("IP属地：" + address);
        }

        // Signature
        if (profileDays != null) {
            String signature = (!TextUtils.isEmpty(user.getSignature())) ? user.getSignature() : "这个人很懒，什么都没有留下";
            profileDays.setText(signature);
        }
    }

    // --- Stats Logic ---
    private void loadUserStats() {
        if (!sessionManager.isLoggedIn()) return;
        CUser user = sessionManager.getCurrentCUser();
        if (user == null) return;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_STATS + user.getUserId()).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<Map<String, Object>>>(){}.getType();
                            CResult<Map<String, Object>> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200 && result.getData() != null) {
                                Map<String, Object> stats = result.getData();
                                Object following = stats.get("followingCount");
                                Object fans = stats.get("fansCount");
                                Object likes = stats.get("likeCount");

                                int followingCount = following instanceof Number ? ((Number) following).intValue() : 0;
                                int fansCount = fans instanceof Number ? ((Number) fans).intValue() : 0;
                                int likeCount = likes instanceof Number ? ((Number) likes).intValue() : 0;

                                profileFollowCount.setText(String.valueOf(followingCount));
                                profileFansCount.setText(String.valueOf(fansCount));
                                profileLikeCount.setText(String.valueOf(likeCount));

                                user.setFollowingCount(followingCount);
                                user.setFansCount(fansCount);
                                user.setLikeCount(likeCount);
                                sessionManager.saveCUser(user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    // --- UI Update Logic ---
    private void updateLoginUi() {
        boolean loggedIn = sessionManager.isLoggedIn();
        settingsButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);

        if (loggedIn) {
            CUser user = sessionManager.getCurrentCUser();
            if (user != null && user.getUserId() != null) {
                // 刷新用户信息（从服务器获取最新数据）
                refreshUserInfo(user.getUserId());
            } else {
                // 如果没有用户ID，使用本地缓存数据
                updateUIFromLocalUser();
            }

            // Show cached stats
            if (user != null) {
                profileFollowCount.setText(String.valueOf(user.getFollowingCount()));
                profileFansCount.setText(String.valueOf(user.getFansCount()));
                profileLikeCount.setText(String.valueOf(user.getLikeCount()));
            }
        } else {
            profileName.setText("未登录");
            profileIp.setText("登录后展示IP属地");
            profileDays.setText("未登录，欢迎加入校园论坛");
            profileFollowCount.setText("0");
            profileFansCount.setText("0");
            profileLikeCount.setText("0");
            if (profileGender != null) profileGender.setVisibility(View.GONE);
            if (profileAvatar != null) profileAvatar.setImageResource(R.drawable.ic_user_avatar);
        }
    }

    private void openFollowList(String type) {
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
        CUser user = sessionManager.getCurrentCUser();
        String username = (user != null) ? user.getUsername() : "";
        FollowListActivity.open(requireContext(), type, username);
    }

    // ❌ 删除了 initTabs 方法

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ProfilePostsFragment());
        fragmentList.add(new ProfileCommentsFragment());
        fragmentList.add(new ProfileEyeFragment());
        fragmentList.add(new ProfileOrdersFragment());
    }

    private void initViewPager(View view) {
        profileViewPager = view.findViewById(R.id.profile_viewpager);
        tabLayout = view.findViewById(R.id.profile_tab_layout); // 绑定 TabLayout

        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getActivity());
        profileViewPager.setAdapter(adapter);

        // ✅ 使用 TabLayoutMediator 自动关联 Tab 和 ViewPager
        String[] titles = new String[]{"动态", "评论", "插眼", "订单"};
        new TabLayoutMediator(tabLayout, profileViewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();
    }

    // ❌ 删除了 setTabClickListeners 和 updateTabStatus 方法

    private class ProfileViewPagerAdapter extends FragmentStateAdapter {
        public ProfileViewPagerAdapter(FragmentActivity fa) { super(fa); }
        @Override public Fragment createFragment(int position) { return fragmentList.get(position); }
        @Override public int getItemCount() { return fragmentList.size(); }
    }
}