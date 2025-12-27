package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lnforum.fragment.circle.WCircleFragment;
import com.example.lnforum.fragment.message.WMessageFragment;
import com.example.lnforum.fragment.profile.ProfileFragment;
import com.example.lnforum.R;
import com.example.lnforum.fragment.setting.SettingsFragment;
import com.example.lnforum.repository.LMessageRepository;

import java.util.ArrayList;
import java.util.List;

public class WMainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout navCircle;
    private LinearLayout navMessage;
    private LinearLayout navProfile;
    private ImageView navCircleIcon;
    private TextView navCircleText;
    private ImageView navMessageIcon;
    private TextView navMessageText;
    private TextView navMessageBadge;
    private ImageView navProfileIcon;
    private TextView navProfileText;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initViews();
        initFragments();
        initViewPager();
        setNavClickListener();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        navCircle = findViewById(R.id.nav_circle);
        navMessage = findViewById(R.id.nav_message);
        navProfile = findViewById(R.id.nav_profile);
        navCircleIcon = findViewById(R.id.nav_circle_icon);
        navCircleText = findViewById(R.id.nav_circle_text);
        navMessageIcon = findViewById(R.id.nav_message_icon);
        navMessageText = findViewById(R.id.nav_message_text);
        navMessageBadge = findViewById(R.id.nav_message_badge);
        navProfileIcon = findViewById(R.id.nav_profile_icon);
        navProfileText = findViewById(R.id.nav_profile_text);
    }
    
    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new WCircleFragment());
        fragmentList.add(new WMessageFragment());
        fragmentList.add(new ProfileFragment());
        fragmentList.add(new SettingsFragment());
    }
    
    private void initViewPager() {
        FragmentStateAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateNavStatus(position);
            }
        });
        
        viewPager.setCurrentItem(0, false);
    }
    
    private void setNavClickListener() {
        // 点击直接跳转，不使用动画（false表示无动画）
        navCircle.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0, false);
            }
        });
        navMessage.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1, false);
            }
        });
        navProfile.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 2) {
                viewPager.setCurrentItem(2, false);
            }
        });
    }
    
    private void updateNavStatus(int position) {
        resetNavStatus();
        switch (position) {
            case 0:
                setNavItemSelected(navCircle, true);
                break;
            case 1:
                setNavItemSelected(navMessage, true);
                break;
            case 2:
                setNavItemSelected(navProfile, true);
                break;
        }
    }
    
    private void resetNavStatus() {
        setNavItemSelected(navCircle, false);
        setNavItemSelected(navMessage, false);
        setNavItemSelected(navProfile, false);
    }
    
    private void setNavItemSelected(LinearLayout navItem, boolean isSelected) {
        if (navItem == null) return;

        ImageView icon = null;
        TextView text = null;
        if (navItem == navMessage) {
            icon = navMessageIcon;
            text = navMessageText;
        } else if (navItem == navCircle) {
            icon = navCircleIcon;
            text = navCircleText;
        } else if (navItem == navProfile) {
            icon = navProfileIcon;
            text = navProfileText;
        }
        if (icon == null || text == null) return;

        if (isSelected) {
            int primaryColor = ContextCompat.getColor(this, R.color.primary_blue);
            icon.setColorFilter(primaryColor);
            text.setTextColor(primaryColor);
        } else {
            int grayColor = ContextCompat.getColor(this, R.color.nav_unselected);
            icon.setColorFilter(grayColor);
            text.setTextColor(grayColor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTotalUnreadBadge();
        // 检查是否需要显示系统通知弹窗
        checkAndShowNotification();
    }
    
    /**
     * 检查并显示系统通知弹窗（进入应用时）
     */
    private void checkAndShowNotification() {
        // 如果当前在首页（索引0），触发通知弹窗
        if (viewPager.getCurrentItem() == 0) {
            Fragment fragment = fragmentList.get(0);
            if (fragment instanceof WCircleFragment) {
                WCircleFragment circleFragment = (WCircleFragment) fragment;
                // 延迟一点时间，确保Fragment已经完全初始化
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    circleFragment.showNotificationDialogOnStart();
                }, 500);
            }
        }
    }

    /**
     * 拉取总未读数并展示到底部"私信"红点
     */
    private void loadTotalUnreadBadge() {
        if (navMessageBadge == null) return;
        LMessageRepository.getTotalUnreadCount(this, (count, error) -> runOnUiThread(() -> {
            updateMessageBadge(count != null ? count : 0);
        }));
    }
    
    /**
     * 更新底部导航栏的私信红点数（供外部调用）
     */
    public void updateMessageBadge(int count) {
        if (navMessageBadge == null) return;
        if (count > 0) {
            navMessageBadge.setVisibility(View.VISIBLE);
            navMessageBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            navMessageBadge.setVisibility(View.GONE);
        }
    }
    
    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fa) {
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
}

