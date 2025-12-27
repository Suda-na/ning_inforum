package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lnforum.R;
import com.example.lnforum.fragment.profile.ProfileCommentsFragment;
import com.example.lnforum.fragment.profile.ProfileEyeFragment;
import com.example.lnforum.fragment.profile.ProfilePostsFragment;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "extra_user_name";

    private static final String API_BASE = "http://192.168.159.1:8080/api/cuser";
    private static final String API_USER_INFO = API_BASE + "/userInfoByUsername?username=";
    private static final String API_USER_STATS = API_BASE + "/user_stats?userId=";

    private String userName;
    private Integer userId;
    private boolean isFollowed = false;
    private TextView followBtn;
    private TextView chatBtn;
    private TextView followCount;
    private TextView fansCount;
    private TextView likeCount;

    private ViewPager2 viewPager;
    private List<Fragment> fragmentList;
    private View postsIndicator;
    private TextView postsTab;
    private TextView commentsTab;
    private TextView eyeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        View root = findViewById(R.id.user_profile_root);
        applyProfileStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        if (userName == null || userName.isEmpty()) userName = "狐友";

        initViews();
    }

    private void initViews() {
        ImageView back = findViewById(R.id.user_back);
        ImageView more = findViewById(R.id.user_more);
        TextView name = findViewById(R.id.user_name);
        followBtn = findViewById(R.id.user_follow_btn);
        chatBtn = findViewById(R.id.user_chat_btn);
        followCount = findViewById(R.id.user_follow_count);
        fansCount = findViewById(R.id.user_fans_count);
        likeCount = findViewById(R.id.user_like_count);
        postsIndicator = findViewById(R.id.user_posts_indicator);
        postsTab = findViewById(R.id.user_posts_text);
        commentsTab = findViewById(R.id.user_comments_tab);
        eyeTab = findViewById(R.id.user_eye_tab);
        viewPager = findViewById(R.id.user_profile_viewpager);

        name.setText(userName);
        
        // 初始化显示为0
        followCount.setText("0");
        fansCount.setText("0");
        likeCount.setText("0");

        // 数量点击（扩大区域到容器）
        findViewById(R.id.user_follow_container).setOnClickListener(v -> FollowListActivity.open(this, FollowListActivity.TYPE_FOLLOWING, userName));
        findViewById(R.id.user_fans_container).setOnClickListener(v -> FollowListActivity.open(this, FollowListActivity.TYPE_FANS, userName));

        setupViewPager();
        setupTabClicks();

        back.setOnClickListener(v -> finish());
        more.setOnClickListener(v -> showMoreMenu());

        // 关注按钮点击事件
        followBtn.setOnClickListener(v -> {
            isFollowed = !isFollowed;
            updateFollowButton();
            Toast.makeText(this, isFollowed ? "已关注 " + userName : "已取消关注 " + userName, Toast.LENGTH_SHORT).show();
        });

        // 私信按钮点击事件
        chatBtn.setOnClickListener(v -> {
            if (!isFollowed) {
                // 未关注时弹出提示
                Toast.makeText(this, "请先关注", Toast.LENGTH_SHORT).show();
            } else {
                // 已关注时跳转到私信页面
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_TITLE, userName);
                intent.putExtra(ChatActivity.EXTRA_TYPE, "user");
                intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "user_" + userName.hashCode());
                startActivity(intent);
            }
        });

        // 初始化关注按钮状态
        updateFollowButton();
        
        // 加载用户信息和统计数据
        loadUserInfo();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (userId != null) {
            loadUserStats();
        }
    }
    
    private void loadUserInfo() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_USER_INFO + userName)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "加载用户信息失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CUser>>(){}.getType();
                        CResult<CUser> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            CUser user = result.getData();
                            userId = user.getUserId();
                            // 更新Fragment的userId
                            updateFragmentUserIds();
                            // 加载统计数据
                            loadUserStats();
                        } else {
                            Toast.makeText(UserProfileActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "解析用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    private void loadUserStats() {
        if (userId == null) return;
        
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_USER_STATS + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 失败时不显示错误，保持默认值0
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<Map<String, Integer>>>(){}.getType();
                        CResult<Map<String, Integer>> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            Map<String, Integer> stats = result.getData();
                            Object following = stats.get("followingCount");
                            Object fans = stats.get("fansCount");
                            Object likes = stats.get("likeCount");

                            int followingCountValue = following instanceof Number ? ((Number) following).intValue() : 0;
                            int fansCountValue = fans instanceof Number ? ((Number) fans).intValue() : 0;
                            int likeCountValue = likes instanceof Number ? ((Number) likes).intValue() : 0;

                            followCount.setText(String.valueOf(followingCountValue));
                            fansCount.setText(String.valueOf(fansCountValue));
                            likeCount.setText(String.valueOf(likeCountValue));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    
    private void updateFragmentUserIds() {
        if (userId == null || fragmentList == null) return;
        // 更新Fragment的参数，让它们使用新的userId
        for (Fragment fragment : fragmentList) {
            Bundle args = fragment.getArguments();
            if (args == null) {
                args = new Bundle();
                fragment.setArguments(args);
            }
            args.putInt("arg_user_id", userId);
        }
        // 通知ViewPager适配器数据已更改，触发Fragment重新创建
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    private void setupViewPager() {
        fragmentList = new ArrayList<>();
        // 创建Fragment（userId会在获取到后通过setArguments更新）
        fragmentList.add(ProfilePostsFragment.newInstance(null));
        fragmentList.add(ProfileCommentsFragment.newInstance(null));
        fragmentList.add(ProfileEyeFragment.newInstance(null));

        viewPager.setAdapter(new UserProfilePagerAdapter(this));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }

    private void setupTabClicks() {
        findViewById(R.id.user_posts_tab).setOnClickListener(v -> viewPager.setCurrentItem(0));
        commentsTab.setOnClickListener(v -> viewPager.setCurrentItem(1));
        eyeTab.setOnClickListener(v -> viewPager.setCurrentItem(2));
        updateTabStatus(0);
    }

    private void updateTabStatus(int position) {
        // reset
        postsIndicator.setVisibility(View.GONE);
        postsTab.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));
        commentsTab.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));
        eyeTab.setTextColor(ContextCompat.getColor(this, R.color.nav_unselected));

        int primary = ContextCompat.getColor(this, R.color.primary_blue);
        switch (position) {
            case 0:
                postsIndicator.setVisibility(View.VISIBLE);
                postsTab.setTextColor(primary);
                break;
            case 1:
                commentsTab.setTextColor(primary);
                break;
            case 2:
                eyeTab.setTextColor(primary);
                break;
        }
    }

    private void updateFollowButton() {
        if (isFollowed) {
            followBtn.setText("已关注");
            followBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            followBtn.setText("关注");
            followBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.round_button_bg));
        }
    }

    private void showMoreMenu() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_menu_user_report, null);
        view.findViewById(R.id.menu_report_user).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(ReportActivity.EXTRA_TYPE, "user");
            intent.putExtra(ReportActivity.EXTRA_TARGET_ID, userName);
            intent.putExtra(ReportActivity.EXTRA_TARGET_NAME, userName);
            startActivity(intent);
        });
        view.findViewById(R.id.menu_black_user).setOnClickListener(v -> {
            Toast.makeText(this, "已拉黑，后续可在黑名单管理", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        view.findViewById(R.id.menu_cancel_user).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }

    private class UserProfilePagerAdapter extends FragmentStateAdapter {
        public UserProfilePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    private void applyProfileStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }
}


