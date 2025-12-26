package com.example.android_java2.activity;

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

import com.example.android_java2.R;
import com.example.android_java2.activity.FollowListActivity;
import com.example.android_java2.fragment.profile.ProfileCommentsFragment;
import com.example.android_java2.fragment.profile.ProfileEyeFragment;
import com.example.android_java2.fragment.profile.ProfilePostsFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "extra_user_name";

    private String userName;
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
        followCount.setText("69");
        fansCount.setText("449");
        likeCount.setText("128");

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
    }

    private void setupViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new ProfilePostsFragment());
        fragmentList.add(new ProfileCommentsFragment());
        fragmentList.add(new ProfileEyeFragment());

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


