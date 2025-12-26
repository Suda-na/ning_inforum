package com.example.android_java2.fragment.circle;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_java2.fragment.errand.ErrandFragment;
import com.example.android_java2.fragment.lost_found.LostAndFoundFragment;
import com.example.android_java2.R;
import com.example.android_java2.activity.PublishCircleActivity;
import com.example.android_java2.activity.PublishSecondHandActivity;
import com.example.android_java2.activity.PublishLostFoundActivity;
import com.example.android_java2.activity.PublishErrandActivity;
import com.example.android_java2.activity.SearchActivity;
import com.example.android_java2.adapter.NotificationImageAdapter;
import com.example.android_java2.fragment.market.SecondHandMarketFragment;
import com.example.android_java2.model.Notification;
import com.example.android_java2.repository.NotificationRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CircleFragment extends Fragment {

    private ViewPager2 circleViewPager;
    private TextView circlePostTab;
    private TextView errandTab;
    private TextView secondHandMarketTab;
    private TextView lostAndFoundTab;
    private List<Fragment> fragmentList;
    private FloatingActionButton fabPost;
    private Dialog postDialog;
    
    // 通知相关
    private LinearLayout notificationBanner;
    private TextView notificationTitle;
    private TextView notificationContent;
    private NotificationRepository notificationRepository;
    private Notification currentNotification;
    private Handler scrollHandler;
    private Runnable scrollRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        
        // 初始化标签页
        initTabs(view);
        
        // 初始化Fragment列表
        initFragments();
        
        // 初始化ViewPager
        initViewPager(view);
        
        // 设置标签页点击事件
        setTabClickListeners();
        
        // 初始化发布按钮
        initPostButton(view);
        
        // 初始化系统通知
        initNotification(view);
        
        return view;
    }
    
    private void initTabs(View view) {
        // 找到标签页组件
        circlePostTab = view.findViewById(R.id.circle_post_tab);
        errandTab = view.findViewById(R.id.errand_tab);
        secondHandMarketTab = view.findViewById(R.id.second_hand_market_tab);
        lostAndFoundTab = view.findViewById(R.id.lost_and_found_tab);
        
        // 找到搜索栏（整个区域可点）
        View searchBar = view.findViewById(R.id.search_bar_container);
        if (searchBar == null) {
            searchBar = view.findViewById(R.id.top_bar);
        }
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            });
        }
    }
    
    private void initFragments() {
        // 创建Fragment列表
        fragmentList = new ArrayList<>();
        fragmentList.add(new CirclePostFragment()); // 圈子动态Fragment
        fragmentList.add(new ErrandFragment()); // 跑腿Fragment
        fragmentList.add(new SecondHandMarketFragment()); // 二手集市Fragment
        fragmentList.add(new LostAndFoundFragment()); // 失物招领Fragment
    }
    
    private void initViewPager(View view) {
        // 找到ViewPager2组件
        circleViewPager = view.findViewById(R.id.circle_viewpager);
        
        // 创建并设置Adapter
        CircleViewPagerAdapter adapter = new CircleViewPagerAdapter(getActivity());
        circleViewPager.setAdapter(adapter);
        
        // 设置ViewPager滑动监听，同步标签页选中状态
        circleViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }
    
    private void setTabClickListeners() {
        // 圈子动态标签点击事件
        circlePostTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleViewPager.setCurrentItem(0);
            }
        });
        
        // 跑腿标签点击事件
        errandTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleViewPager.setCurrentItem(1);
            }
        });
        
        // 二手集市标签点击事件
        secondHandMarketTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleViewPager.setCurrentItem(2);
            }
        });
        
        // 失物招领标签点击事件
        lostAndFoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleViewPager.setCurrentItem(3);
            }
        });
    }
    
    private void updateTabStatus(int position) {
        // 重置所有标签状态
        resetTabStatus();
        
        // 设置当前选中标签状态
        switch (position) {
            case 0: // 圈子动态
                setCirclePostTabSelected(true);
                break;
            case 1: // 跑腿
                setErrandTabSelected(true);
                break;
            case 2: // 二手集市
                setSecondHandMarketTabSelected(true);
                break;
            case 3: // 失物招领
                setLostAndFoundTabSelected(true);
                break;
        }
    }
    
    private void resetTabStatus() {
        // 重置所有标签状态
        setCirclePostTabSelected(false);
        setErrandTabSelected(false);
        setSecondHandMarketTabSelected(false);
        setLostAndFoundTabSelected(false);
    }
    
    private void setCirclePostTabSelected(boolean isSelected) {
        if (circlePostTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            circlePostTab.setTextColor(primaryColor);
            circlePostTab.setTextSize(16);
            circlePostTab.setTypeface(circlePostTab.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            circlePostTab.setTextColor(grayColor);
            circlePostTab.setTextSize(14);
            circlePostTab.setTypeface(circlePostTab.getTypeface(), android.graphics.Typeface.NORMAL);
        }
    }
    
    private void setErrandTabSelected(boolean isSelected) {
        if (errandTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            errandTab.setTextColor(primaryColor);
            errandTab.setTextSize(16);
            errandTab.setTypeface(errandTab.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            errandTab.setTextColor(grayColor);
            errandTab.setTextSize(14);
            errandTab.setTypeface(errandTab.getTypeface(), android.graphics.Typeface.NORMAL);
        }
    }
    
    private void setSecondHandMarketTabSelected(boolean isSelected) {
        if (secondHandMarketTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            secondHandMarketTab.setTextColor(primaryColor);
            secondHandMarketTab.setTextSize(16);
            secondHandMarketTab.setTypeface(secondHandMarketTab.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            secondHandMarketTab.setTextColor(grayColor);
            secondHandMarketTab.setTextSize(14);
            secondHandMarketTab.setTypeface(secondHandMarketTab.getTypeface(), android.graphics.Typeface.NORMAL);
        }
    }
    
    private void setLostAndFoundTabSelected(boolean isSelected) {
        if (lostAndFoundTab == null) return;
        
        if (isSelected) {
            // 选中状态
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            lostAndFoundTab.setTextColor(primaryColor);
            lostAndFoundTab.setTextSize(16);
            lostAndFoundTab.setTypeface(lostAndFoundTab.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            // 未选中状态
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            lostAndFoundTab.setTextColor(grayColor);
            lostAndFoundTab.setTextSize(14);
            lostAndFoundTab.setTypeface(lostAndFoundTab.getTypeface(), android.graphics.Typeface.NORMAL);
        }
    }
    
    // ViewPager适配器
    private class CircleViewPagerAdapter extends FragmentStateAdapter {
        
        public CircleViewPagerAdapter(FragmentActivity fa) {
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
    
    private void initPostButton(View view) {
        // 找到发布按钮
        fabPost = view.findViewById(R.id.fab_post);
        
        // 设置点击事件
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示发布弹窗
                showPostDialog();
            }
        });
    }
    
    private void showPostDialog() {
        // 创建弹窗
        postDialog = new Dialog(getActivity());
        postDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        postDialog.setContentView(R.layout.dialog_post);
        
        // 设置弹窗宽度为屏幕宽度的90%
        Window window = postDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        // 设置弹窗点击外部可关闭
        postDialog.setCanceledOnTouchOutside(true);
        
        // 找到弹窗中的四个板块
        LinearLayout postCircle = postDialog.findViewById(R.id.post_circle);
        LinearLayout postErrand = postDialog.findViewById(R.id.post_errand);
        LinearLayout postSecondHand = postDialog.findViewById(R.id.post_second_hand);
        LinearLayout postLostFound = postDialog.findViewById(R.id.post_lost_found);
        
        // 设置点击事件
        postCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理圈子动态发布
                postDialog.dismiss();
                Intent intent = new Intent(getActivity(), PublishCircleActivity.class);
                startActivity(intent);
            }
        });
        
        postErrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理跑腿发布
                postDialog.dismiss();
                Intent intent = new Intent(getActivity(), PublishErrandActivity.class);
                startActivity(intent);
            }
        });
        
        postSecondHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理二手集市发布
                postDialog.dismiss();
                Intent intent = new Intent(getActivity(), PublishSecondHandActivity.class);
                startActivity(intent);
            }
        });
        
        postLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理失物招领发布
                postDialog.dismiss();
                Intent intent = new Intent(getActivity(), PublishLostFoundActivity.class);
                startActivity(intent);
            }
        });
        
        // 显示弹窗
        postDialog.show();
    }
    
    private void initNotification(View view) {
        notificationRepository = NotificationRepository.getInstance();
        notificationBanner = view.findViewById(R.id.notification_banner);
        notificationTitle = view.findViewById(R.id.notification_title);
        notificationContent = view.findViewById(R.id.notification_content);
        
        // 获取最新通知
        currentNotification = notificationRepository.getLatestNotification();
        
        if (currentNotification != null) {
            // 设置通知内容
            notificationTitle.setText(currentNotification.getTitle());
            notificationContent.setText(currentNotification.getContent());
            
            // 启动滚动效果
            startScrolling();
            
            // 设置点击事件
            notificationBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNotificationDialog(currentNotification);
                }
            });
        } else {
            // 如果没有通知，隐藏通知栏
            notificationBanner.setVisibility(View.GONE);
        }
    }
    
    private void startScrolling() {
        if (scrollHandler == null) {
            scrollHandler = new Handler(Looper.getMainLooper());
        }
        
        // 启动标题滚动
        notificationTitle.setSelected(true);
        notificationTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationTitle.setSelected(true);
            }
        }, 100);
        
        // 启动内容滚动
        notificationContent.setSelected(true);
        notificationContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationContent.setSelected(true);
            }
        }, 200);
    }
    
    private void showNotificationDialog(Notification notification) {
        if (notification == null) return;
        
        // 创建弹窗
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification_detail);
        
        // 设置弹窗宽度为屏幕宽度的90%
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        // 设置弹窗点击外部可关闭
        dialog.setCanceledOnTouchOutside(true);
        
        // 设置内容
        TextView titleView = dialog.findViewById(R.id.dialog_notification_title);
        TextView timeView = dialog.findViewById(R.id.dialog_notification_time);
        TextView contentView = dialog.findViewById(R.id.dialog_notification_content);
        RecyclerView imagesView = dialog.findViewById(R.id.dialog_notification_images);
        ImageView closeBtn = dialog.findViewById(R.id.btn_close);
        TextView confirmBtn = dialog.findViewById(R.id.btn_confirm);
        
        if (titleView != null) {
            titleView.setText(notification.getTitle());
        }
        if (timeView != null) {
            timeView.setText(notification.getTime());
        }
        if (contentView != null) {
            contentView.setText(notification.getContent());
        }
        
        // 设置图片
        if (imagesView != null && notification.getImages() != null && !notification.getImages().isEmpty()) {
            imagesView.setVisibility(View.VISIBLE);
            imagesView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            NotificationImageAdapter adapter = new NotificationImageAdapter(notification.getImages());
            imagesView.setAdapter(adapter);
        } else if (imagesView != null) {
            imagesView.setVisibility(View.GONE);
        }
        
        // 关闭按钮
        if (closeBtn != null) {
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        
        // 确定按钮
        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        
        // 显示弹窗
        dialog.show();
    }
    
    /**
     * 显示通知弹窗（供外部调用，如MainActivity）
     */
    public void showNotificationDialogOnStart() {
        if (currentNotification != null) {
            // 延迟显示，确保Fragment已完全加载
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNotificationDialog(currentNotification);
                }
            }, 500);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理滚动Handler
        if (scrollHandler != null && scrollRunnable != null) {
            scrollHandler.removeCallbacks(scrollRunnable);
        }
    }
}
