package com.example.android_java2.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import com.example.android_java2.fragment.circle.CircleFragment;
import com.example.android_java2.fragment.message.MessageFragment;
import com.example.android_java2.fragment.profile.ProfileFragment;
import com.example.android_java2.R;
import com.example.android_java2.fragment.setting.SettingsFragment;
import com.example.android_java2.repository.CSessionManager;
import com.example.android_java2.repository.LMessageRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout navCircle;
    private RelativeLayout navMessage;
    private LinearLayout navProfile;
    private TextView navMessageBadge;
    private ImageView navMessageIcon;
    private TextView navMessageText;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // 初始化UI组件
        initViews();
        
        // 初始化Fragment列表
        initFragments();
        
        // 初始化ViewPager
        initViewPager();
        
        // 设置底部导航栏点击事件
        setNavClickListener();
        
        // 设置WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 进入主页后显示通知弹窗
        showNotificationOnStart();
        
        // 加载未读消息数
        loadUnreadCount();
    }
    
    private void showNotificationOnStart() {
        // 延迟执行，确保Fragment已完全加载
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 通过FragmentManager获取CircleFragment
                    androidx.fragment.app.FragmentManager fragmentManager = 
                        ((androidx.fragment.app.FragmentActivity) MainActivity.this).getSupportFragmentManager();
                    
                    // 查找CircleFragment（ViewPager2中的Fragment会有特定的tag）
                    List<androidx.fragment.app.Fragment> fragments = fragmentManager.getFragments();
                    for (androidx.fragment.app.Fragment fragment : fragments) {
                        if (fragment instanceof com.example.android_java2.fragment.circle.CircleFragment) {
                            ((com.example.android_java2.fragment.circle.CircleFragment) fragment)
                                .showNotificationDialogOnStart();
                            break;
                        }
                    }
                    
                    // 如果上面没找到，尝试从fragmentList获取
                    if (fragmentList != null && !fragmentList.isEmpty()) {
                        Fragment circleFragment = fragmentList.get(0);
                        if (circleFragment instanceof com.example.android_java2.fragment.circle.CircleFragment) {
                            ((com.example.android_java2.fragment.circle.CircleFragment) circleFragment)
                                .showNotificationDialogOnStart();
                        }
                    }
                } catch (Exception e) {
                    // 忽略错误，避免崩溃
                    e.printStackTrace();
                }
            }
        }, 1000);
    }
    
    private void initViews() {
        // 初始化ViewPager
        viewPager = findViewById(R.id.view_pager);
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager not found in layout");
        }
        
        // 初始化底部导航栏
        navCircle = findViewById(R.id.nav_circle);
        navMessage = findViewById(R.id.nav_message);
        navProfile = findViewById(R.id.nav_profile);
        navMessageBadge = findViewById(R.id.nav_message_badge);
        navMessageIcon = findViewById(R.id.nav_message_icon);
        navMessageText = findViewById(R.id.nav_message_text);
        
        if (navCircle == null || navMessage == null || navProfile == null) {
            throw new IllegalStateException("Navigation items not found in layout");
        }
    }
    
    private void initFragments() {
        // 创建Fragment列表
        fragmentList = new ArrayList<>();
        fragmentList.add(new CircleFragment()); // 圈子Fragment
        fragmentList.add(new MessageFragment()); // 私信Fragment
        fragmentList.add(new ProfileFragment()); // 我的Fragment
        fragmentList.add(new SettingsFragment()); // 设置Fragment
    }
    
    private void initViewPager() {
        if (viewPager == null || fragmentList == null || fragmentList.isEmpty()) {
            return;
        }
        
        // 创建并设置Adapter
        FragmentStateAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // 设置ViewPager滑动监听，同步底部导航栏选中状态
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position >= 0 && position < fragmentList.size()) {
                    updateNavStatus(position);
                }
            }
        });
        
        // 禁用ViewPager的滑动（如果需要可以通过手势滑动，可以移除这行）
        // viewPager.setUserInputEnabled(false);
        
        // 设置初始页面
        viewPager.setCurrentItem(0, false);
    }
    
    private void setNavClickListener() {
        // 圈子导航点击事件
        navCircle.setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(0, true); // 添加平滑动画
            }
        });
        
        // 私信导航点击事件
        navMessage.setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(1, true); // 添加平滑动画
            }
        });
        
        // 我的导航点击事件
        navProfile.setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(2, true); // 添加平滑动画
            }
        });
    }
    
    private void updateNavStatus(int position) {
        // 重置所有导航项状态
        resetNavStatus();
        
        // 设置当前选中项状态
        switch (position) {
            case 0: // 圈子
                setNavItemSelected(navCircle, true);
                break;
            case 1: // 私信
                setNavMessageSelected(true);
                break;
            case 2: // 我的
                setNavItemSelected(navProfile, true);
                break;
            case 3: // 设置（隐藏导航项，但需要处理）
                // 设置页面不显示底部导航栏选中状态
                break;
            default:
                break;
        }
    }
    
    private void resetNavStatus() {
        // 重置所有导航项状态
        setNavItemSelected(navCircle, false);
        setNavMessageSelected(false);
        setNavItemSelected(navProfile, false);
    }
    
    private void setNavItemSelected(LinearLayout navItem, boolean isSelected) {
        if (navItem == null || navItem.getChildCount() < 2) {
            return;
        }
        
        // 获取导航项中的ImageView和TextView
        View iconView = navItem.getChildAt(0);
        View textView = navItem.getChildAt(1);
        
        if (!(iconView instanceof ImageView) || !(textView instanceof TextView)) {
            return;
        }
        
        ImageView icon = (ImageView) iconView;
        TextView text = (TextView) textView;
        
        // 添加动画效果
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, 
            isSelected ? android.R.anim.fade_in : android.R.anim.fade_out);
        
        if (isSelected) {
            // 选中状态：使用主题蓝色
            int primaryColor = ContextCompat.getColor(this, R.color.primary_blue);
            icon.setColorFilter(primaryColor);
            text.setTextColor(primaryColor);
            icon.startAnimation(scaleAnimation);
        } else {
            // 未选中状态：灰色
            int grayColor = ContextCompat.getColor(this, R.color.nav_unselected);
            icon.setColorFilter(grayColor);
            text.setTextColor(grayColor);
        }
    }

    private void setNavMessageSelected(boolean isSelected) {
        if (navMessageIcon == null || navMessageText == null) {
            return;
        }
        
        if (isSelected) {
            // 选中状态：使用主题蓝色
            int primaryColor = ContextCompat.getColor(this, R.color.primary_blue);
            navMessageIcon.setColorFilter(primaryColor);
            navMessageText.setTextColor(primaryColor);
        } else {
            // 未选中状态：灰色
            int grayColor = ContextCompat.getColor(this, R.color.nav_unselected);
            navMessageIcon.setColorFilter(grayColor);
            navMessageText.setTextColor(grayColor);
        }
    }

    /**
     * 加载总未读消息数
     */
    private void loadUnreadCount() {
        CSessionManager sessionManager = CSessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        LMessageRepository.getTotalUnreadCount(this, new LMessageRepository.UnreadCountCallback() {
            @Override
            public void onResult(Integer count, String error) {
                // 切换到主线程更新UI
                runOnUiThread(() -> {
                    if (error != null) {
                        android.util.Log.e("MainActivity", "获取总未读数失败: " + error);
                        return;
                    }

                    // 更新未读消息数红点
                    if (navMessageBadge != null) {
                        if (count != null && count > 0) {
                            navMessageBadge.setVisibility(View.VISIBLE);
                            if (count > 99) {
                                navMessageBadge.setText("99+");
                            } else {
                                navMessageBadge.setText(String.valueOf(count));
                            }
                        } else {
                            navMessageBadge.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到MainActivity时刷新未读消息数
        loadUnreadCount();
    }
    
    // ViewPager适配器
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