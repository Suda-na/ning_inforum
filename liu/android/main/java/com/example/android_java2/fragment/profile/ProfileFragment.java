package com.example.android_java2.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_java2.R;
import com.example.android_java2.activity.FollowListActivity;
import com.example.android_java2.activity.MainActivity;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.model.CUser;
import com.example.android_java2.repository.CSessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ViewPager2 profileViewPager;
    private LinearLayout postsTab;
    private TextView commentsTab;
    private TextView eyeTab;
    private TextView ordersTab;
    private List<Fragment> fragmentList;
    private LinearLayout settingsButton;
    private LinearLayout loginButton;
    private TextView profileName;
    private TextView profileIp;
    private TextView profileDays;
    private TextView profileFollowCount;
    private TextView profileFansCount;
    private TextView profileLikeCount;
    private CSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        sessionManager = CSessionManager.getInstance(requireContext());

        // 找到设置/登录按钮
        settingsButton = view.findViewById(R.id.settings_button);
        loginButton = view.findViewById(R.id.login_button);
        profileName = view.findViewById(R.id.profile_name);
        profileIp = view.findViewById(R.id.profile_ip);
        profileDays = view.findViewById(R.id.profile_days);
        profileFollowCount = view.findViewById(R.id.profile_follow_count);
        profileFansCount = view.findViewById(R.id.profile_fans_count);
        profileLikeCount = view.findViewById(R.id.profile_like_count);

        // 数量点击进入列表（扩大点击区域到容器）
        view.findViewById(R.id.profile_follow_container).setOnClickListener(v -> openFollowList(FollowListActivity.TYPE_FOLLOWING));
        view.findViewById(R.id.profile_fans_container).setOnClickListener(v -> openFollowList(FollowListActivity.TYPE_FANS));
        
        // 添加点击事件
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    return;
                }
                // 跳转到设置页面
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    ViewPager2 viewPager = activity.findViewById(R.id.view_pager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(3); // 索引3对应设置Fragment
                    }
                }
            }
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));

        // 初始化标签页
        initTabs(view);
        
        // 初始化Fragment列表
        initFragments();
        
        // 初始化ViewPager
        initViewPager(view);
        
        // 设置标签页点击事件
        setTabClickListeners();
        
        // 刷新登录态相关 UI
        updateLoginUi();
        
        return view;
    }

    private void openFollowList(String type) {
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }

        // ✅ 修复报错点：通过 getCurrentCUser() 获取用户名
        CUser user = sessionManager.getCurrentCUser();
        String username = (user != null) ? user.getUsername() : "";

        FollowListActivity.open(requireContext(), type, username);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginUi();
    }

    private void initTabs(View view) {
        // 找到标签页组件
        postsTab = view.findViewById(R.id.posts_tab);
        commentsTab = view.findViewById(R.id.comments_tab);
        eyeTab = view.findViewById(R.id.eye_tab);
        ordersTab = view.findViewById(R.id.orders_tab);
    }

    private void initFragments() {
        // 创建Fragment列表
        fragmentList = new ArrayList<>();
        fragmentList.add(new ProfilePostsFragment()); // 动态Fragment
        fragmentList.add(new ProfileCommentsFragment()); // 评论Fragment
        fragmentList.add(new ProfileEyeFragment()); // 插眼Fragment
        fragmentList.add(new ProfileOrdersFragment()); // 订单Fragment
    }

    private void initViewPager(View view) {
        // 找到ViewPager2组件
        profileViewPager = view.findViewById(R.id.profile_viewpager);
        
        // 创建并设置Adapter
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getActivity());
        profileViewPager.setAdapter(adapter);
        
        // 设置ViewPager滑动监听，同步标签页选中状态
        profileViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }

    private void setTabClickListeners() {
        // 动态标签点击事件
        postsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewPager.setCurrentItem(0);
            }
        });
        
        // 评论标签点击事件
        commentsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewPager.setCurrentItem(1);
            }
        });
        
        // 插眼标签点击事件
        eyeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewPager.setCurrentItem(2);
            }
        });
        
        // 订单标签点击事件
        ordersTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewPager.setCurrentItem(3);
            }
        });
    }

    private void updateTabStatus(int position) {
        // 重置所有标签状态
        resetTabStatus();
        
        // 设置当前选中标签状态
        switch (position) {
            case 0: // 动态
                setPostsTabSelected(true);
                break;
            case 1: // 评论
                setCommentsTabSelected(true);
                break;
            case 2: // 插眼
                setEyeTabSelected(true);
                break;
            case 3: // 订单
                setOrdersTabSelected(true);
                break;
        }
    }

    private void resetTabStatus() {
        // 重置所有标签状态
        setPostsTabSelected(false);
        setCommentsTabSelected(false);
        setEyeTabSelected(false);
        setOrdersTabSelected(false);
    }

    private void setPostsTabSelected(boolean isSelected) {
        if (postsTab == null) return;
        
        TextView postsText = postsTab.findViewById(R.id.posts_text);
        View postsIndicator = postsTab.findViewById(R.id.posts_indicator);
        
        if (postsText == null || postsIndicator == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            postsText.setTextColor(primaryColor);
            postsIndicator.setVisibility(View.VISIBLE);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            postsText.setTextColor(grayColor);
            postsIndicator.setVisibility(View.GONE);
        }
    }

    private void setCommentsTabSelected(boolean isSelected) {
        if (commentsTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            commentsTab.setTextColor(primaryColor);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            commentsTab.setTextColor(grayColor);
        }
    }

    private void setEyeTabSelected(boolean isSelected) {
        if (eyeTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            eyeTab.setTextColor(primaryColor);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            eyeTab.setTextColor(grayColor);
        }
    }
    
    private void setOrdersTabSelected(boolean isSelected) {
        if (ordersTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            ordersTab.setTextColor(primaryColor);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            ordersTab.setTextColor(grayColor);
        }
    }

    // ViewPager适配器
    private class ProfileViewPagerAdapter extends FragmentStateAdapter {
        
        public ProfileViewPagerAdapter(FragmentActivity fa) {
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

    private void updateLoginUi() {
        boolean loggedIn = sessionManager.isLoggedIn();
        settingsButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);

        if (loggedIn) {
            // ✅ 修复报错点：通过 getCurrentCUser() 获取用户信息
            CUser user = sessionManager.getCurrentCUser();
            String username = (user != null) ? user.getUsername() : "用户";

            profileName.setText(username);
            profileIp.setText("IP属地：未知"); // 这里可以根据 user.getAddress() 优化
            profileDays.setText("欢迎回来");
            profileFollowCount.setText("0"); // 暂无数据
            profileFansCount.setText("0");
            profileLikeCount.setText("0");
        } else {
            profileName.setText("未登录");
            profileIp.setText("登录后展示IP属地");
            profileDays.setText("未登录，欢迎加入校园论坛");
            profileFollowCount.setText("0");
            profileFansCount.setText("0");
            profileLikeCount.setText("0");
        }
    }
}
