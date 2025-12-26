package com.example.android_java2.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_java2.R;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.repository.CSessionManager;
import com.example.android_java2.repository.LMessageRepository;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private ViewPager2 messageViewPager;
    private TextView followTab;
    private TextView fanTab;
    private TextView adminTab;
    private TextView followTabBadge;
    private TextView fanTabBadge;
    private TextView adminTabBadge;
    private List<Fragment> fragmentList;
    private View emptyContainer;
    private TextView loginAction;
    private CSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        
        sessionManager = CSessionManager.getInstance(requireContext());
        
        // 初始化标签页
        initTabs(view);
        
        // 初始化Fragment列表
        initFragments();
        
        // 初始化ViewPager
        initViewPager(view);
        
        // 设置标签页点击事件
        setTabClickListeners();
        
        // 登录状态 UI 控制
        updateLoginUi();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginUi();
        // 延迟加载未读数，确保view已经初始化
        if (getView() != null) {
            getView().post(() -> {
                loadUnreadCounts();
            });
        } else {
            loadUnreadCounts();
        }
    }
    
    private void initTabs(View view) {
        // 找到标签页组件
        followTab = view.findViewById(R.id.follow_tab);
        fanTab = view.findViewById(R.id.fan_tab);
        adminTab = view.findViewById(R.id.admin_tab);
        followTabBadge = view.findViewById(R.id.follow_tab_badge);
        fanTabBadge = view.findViewById(R.id.fan_tab_badge);
        adminTabBadge = view.findViewById(R.id.admin_tab_badge);
        emptyContainer = view.findViewById(R.id.message_empty_container);
        loginAction = view.findViewById(R.id.message_login_action);

        android.util.Log.d("MessageFragment", "initTabs - fanTabBadge是否为null: " + (fanTabBadge == null));
        android.util.Log.d("MessageFragment", "initTabs - followTabBadge是否为null: " + (followTabBadge == null));
        android.util.Log.d("MessageFragment", "initTabs - adminTabBadge是否为null: " + (adminTabBadge == null));

        loginAction.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        emptyContainer.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
    }
    
    private void initFragments() {
        // 创建Fragment列表
        fragmentList = new ArrayList<>();
        fragmentList.add(new LFollowMessageFragment()); // 互关私信Fragment
        fragmentList.add(new LFanMessageFragment()); // 粉丝来信Fragment
        fragmentList.add(new LAdminMessageFragment()); // 联系管理员Fragment
    }
    
    private void initViewPager(View view) {
        // 找到ViewPager2组件
        messageViewPager = view.findViewById(R.id.message_viewpager);
        
        // 创建并设置Adapter
        MessageViewPagerAdapter adapter = new MessageViewPagerAdapter(getActivity());
        messageViewPager.setAdapter(adapter);
        
        // 设置ViewPager滑动监听，同步标签页选中状态
        messageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }
    
    private void setTabClickListeners() {
        // 互关私信标签点击事件
        followTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageViewPager.setCurrentItem(0);
            }
        });
        
        // 粉丝来信标签点击事件
        fanTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageViewPager.setCurrentItem(1);
            }
        });
        
        // 联系管理员标签点击事件
        adminTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageViewPager.setCurrentItem(2);
            }
        });
    }
    
    private void updateTabStatus(int position) {
        // 重置所有标签状态
        resetTabStatus();
        
        // 设置当前选中标签状态
        switch (position) {
            case 0: // 互关私信
                setFollowTabSelected(true);
                break;
            case 1: // 粉丝来信
                setFanTabSelected(true);
                break;
            case 2: // 联系管理员
                setAdminTabSelected(true);
                break;
        }
    }
    
    private void resetTabStatus() {
        // 重置所有标签状态
        setFollowTabSelected(false);
        setFanTabSelected(false);
        setAdminTabSelected(false);
    }
    
    private void setFollowTabSelected(boolean isSelected) {
        if (followTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            followTab.setTextColor(primaryColor);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            followTab.setTextColor(grayColor);
        }
    }
    
    private void setFanTabSelected(boolean isSelected) {
        if (fanTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            fanTab.setTextColor(primaryColor);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            fanTab.setTextColor(grayColor);
        }
    }
    
    private void setAdminTabSelected(boolean isSelected) {
        if (adminTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            adminTab.setTextColor(primaryColor);
            adminTab.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            adminTab.setTextColor(grayColor);
            adminTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void updateLoginUi() {
        boolean loggedIn = sessionManager.isLoggedIn();
        messageViewPager.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        followTab.setEnabled(loggedIn);
        fanTab.setEnabled(loggedIn);
        adminTab.setEnabled(loggedIn);

        if (emptyContainer != null) {
            emptyContainer.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        }
        if (loginAction != null) {
            loginAction.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 加载未读消息数
     */
    private void loadUnreadCounts() {
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        LMessageRepository.getAllUnreadCounts(requireContext(), new LMessageRepository.AllUnreadCountsCallback() {
            @Override
            public void onResult(Integer total, Integer mutualFollow, Integer fan, Integer admin, String error) {
                // 切换到主线程更新UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (error != null) {
                            android.util.Log.e("MessageFragment", "获取未读数失败: " + error);
                            return;
                        }

                        android.util.Log.d("MessageFragment", "未读数 - 总:" + total + ", 互关:" + mutualFollow + ", 粉丝:" + fan + ", 管理员:" + admin);

                        // 更新互关私信未读数
                        if (followTabBadge != null) {
                            if (mutualFollow != null && mutualFollow > 0) {
                                followTabBadge.setVisibility(View.VISIBLE);
                                if (mutualFollow > 99) {
                                    followTabBadge.setText("99+");
                                } else {
                                    followTabBadge.setText(String.valueOf(mutualFollow));
                                }
                            } else {
                                followTabBadge.setVisibility(View.GONE);
                            }
                        }

                        // 更新粉丝来信未读数
                        if (fanTabBadge != null) {
                            if (fan != null && fan > 0) {
                                fanTabBadge.setVisibility(View.VISIBLE);
                                if (fan > 99) {
                                    fanTabBadge.setText("99+");
                                } else {
                                    fanTabBadge.setText(String.valueOf(fan));
                                }
                            } else {
                                fanTabBadge.setVisibility(View.GONE);
                            }
                        }

                        // 更新联系管理员未读数
                        if (adminTabBadge != null) {
                            if (admin != null && admin > 0) {
                                adminTabBadge.setVisibility(View.VISIBLE);
                                if (admin > 99) {
                                    adminTabBadge.setText("99+");
                                } else {
                                    adminTabBadge.setText(String.valueOf(admin));
                                }
                            } else {
                                adminTabBadge.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
    
    // ViewPager适配器
    private class MessageViewPagerAdapter extends FragmentStateAdapter {
        
        public MessageViewPagerAdapter(FragmentActivity fa) {
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
