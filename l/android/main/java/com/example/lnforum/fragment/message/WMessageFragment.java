package com.example.lnforum.fragment.message;

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

import com.example.lnforum.R;
import com.example.lnforum.activity.WLoginActivity;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.repository.LMessageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 私信主页：包含互关私信、粉丝来信、联系管理员三个标签页。
 * 使用 CSessionManager + LMessageRepository 连接 PC 后端。
 */
public class WMessageFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        sessionManager = CSessionManager.getInstance(requireContext());

        initTabs(view);
        initFragments();
        initViewPager(view);
        setTabClickListeners();
        updateLoginUi();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginUi();
        if (getView() != null) {
            getView().post(this::loadUnreadCounts);
        } else {
            loadUnreadCounts();
        }
    }

    private void initTabs(View view) {
        followTab = view.findViewById(R.id.follow_tab);
        fanTab = view.findViewById(R.id.fan_tab);
        adminTab = view.findViewById(R.id.admin_tab);
        followTabBadge = view.findViewById(R.id.follow_tab_badge);
        fanTabBadge = view.findViewById(R.id.fan_tab_badge);
        adminTabBadge = view.findViewById(R.id.admin_tab_badge);
        emptyContainer = view.findViewById(R.id.message_empty_container);
        loginAction = view.findViewById(R.id.message_login_action);

        if (loginAction != null) {
            loginAction.setOnClickListener(v -> startActivity(new Intent(getContext(), WLoginActivity.class)));
        }
        if (emptyContainer != null) {
            emptyContainer.setOnClickListener(v -> startActivity(new Intent(getContext(), WLoginActivity.class)));
        }
    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new LFollowMessageFragment());
        fragmentList.add(new LFanMessageFragment());
        fragmentList.add(new LAdminMessageFragment());
    }

    private void initViewPager(View view) {
        messageViewPager = view.findViewById(R.id.message_viewpager);
        MessageViewPagerAdapter adapter = new MessageViewPagerAdapter(requireActivity());
        messageViewPager.setAdapter(adapter);

        messageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }

    private void setTabClickListeners() {
        if (followTab != null) {
            followTab.setOnClickListener(v -> messageViewPager.setCurrentItem(0));
        }
        if (fanTab != null) {
            fanTab.setOnClickListener(v -> messageViewPager.setCurrentItem(1));
        }
        if (adminTab != null) {
            adminTab.setOnClickListener(v -> messageViewPager.setCurrentItem(2));
        }
    }

    private void updateTabStatus(int position) {
        resetTabStatus();
        switch (position) {
            case 0:
                setFollowTabSelected(true);
                break;
            case 1:
                setFanTabSelected(true);
                break;
            case 2:
                setAdminTabSelected(true);
                break;
        }
    }

    private void resetTabStatus() {
        setFollowTabSelected(false);
        setFanTabSelected(false);
        setAdminTabSelected(false);
    }

    private void setFollowTabSelected(boolean isSelected) {
        if (followTab == null) return;
        int color = ContextCompat.getColor(requireContext(),
                isSelected ? R.color.primary_blue : R.color.nav_unselected);
        followTab.setTextColor(color);
    }

    private void setFanTabSelected(boolean isSelected) {
        if (fanTab == null) return;
        int color = ContextCompat.getColor(requireContext(),
                isSelected ? R.color.primary_blue : R.color.nav_unselected);
        fanTab.setTextColor(color);
    }

    private void setAdminTabSelected(boolean isSelected) {
        if (adminTab == null) return;
        int color = ContextCompat.getColor(requireContext(),
                isSelected ? R.color.primary_blue : R.color.nav_unselected);
        adminTab.setTextColor(color);
        adminTab.setTypeface(null,
                isSelected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    private void updateLoginUi() {
        boolean loggedIn = sessionManager.isLoggedIn();
        if (messageViewPager != null) {
            messageViewPager.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        }
        if (followTab != null) followTab.setEnabled(loggedIn);
        if (fanTab != null) fanTab.setEnabled(loggedIn);
        if (adminTab != null) adminTab.setEnabled(loggedIn);

        if (emptyContainer != null) {
            emptyContainer.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        }
        if (loginAction != null) {
            loginAction.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 刷新未读数（供子Fragment调用）
     */
    public void refreshUnreadCounts() {
        loadUnreadCounts();
    }
    
    private void loadUnreadCounts() {
        if (!sessionManager.isLoggedIn()) {
            return;
        }

        LMessageRepository.getAllUnreadCounts(requireContext(), (total, mutualFollow, fan, admin, error) -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                if (error != null) {
                    android.util.Log.e("WMessageFragment", "获取未读数失败: " + error);
                    return;
                }

                // 互关私信红点
                if (followTabBadge != null) {
                    if (mutualFollow != null && mutualFollow > 0) {
                        followTabBadge.setVisibility(View.VISIBLE);
                        followTabBadge.setText(mutualFollow > 99 ? "99+" : String.valueOf(mutualFollow));
                    } else {
                        followTabBadge.setVisibility(View.GONE);
                    }
                }
                // 粉丝来信红点
                if (fanTabBadge != null) {
                    if (fan != null && fan > 0) {
                        fanTabBadge.setVisibility(View.VISIBLE);
                        fanTabBadge.setText(fan > 99 ? "99+" : String.valueOf(fan));
                    } else {
                        fanTabBadge.setVisibility(View.GONE);
                    }
                }
                // 管理员消息红点
                if (adminTabBadge != null) {
                    if (admin != null && admin > 0) {
                        adminTabBadge.setVisibility(View.VISIBLE);
                        adminTabBadge.setText(admin > 99 ? "99+" : String.valueOf(admin));
                    } else {
                        adminTabBadge.setVisibility(View.GONE);
                    }
                }
                
                // 通知MainActivity更新底部导航栏的总红点数
                if (getActivity() instanceof com.example.lnforum.activity.WMainActivity) {
                    ((com.example.lnforum.activity.WMainActivity) getActivity()).updateMessageBadge(total != null ? total : 0);
                }
            });
        });
    }

    private class MessageViewPagerAdapter extends FragmentStateAdapter {
        MessageViewPagerAdapter(FragmentActivity fa) {
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

