package com.example.lnforum.fragment.circle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lnforum.fragment.errand.WErrandFragment;
import com.example.lnforum.fragment.lost_found.WLostAndFoundFragment;
import com.example.lnforum.R;
import com.example.lnforum.activity.SPublishCircleActivity;
import com.example.lnforum.activity.SPublishSecondHandActivity;
import com.example.lnforum.activity.SPublishLostFoundActivity;
import com.example.lnforum.activity.SPublishErrandActivity;
import com.example.lnforum.activity.WSearchActivity;
import com.example.lnforum.fragment.market.WSecondHandMarketFragment;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.repository.WCircleRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WCircleFragment extends Fragment {

    private ViewPager2 circleViewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView circlePostTab;
    private TextView errandTab;
    private TextView secondHandMarketTab;
    private TextView lostAndFoundTab;
    private List<Fragment> fragmentList;
    private FloatingActionButton fabPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        
        initTabs(view);
        initFragments();
        initViewPager(view);
        setTabClickListeners();
        initPostButton(view);
        initSwipeRefresh(view);
        initStatistics(view);
        initTodayHot(view);
        initNotification(view);
        initSearch(view);
        
        return view;
    }

    private void initTabs(View view) {
        circlePostTab = view.findViewById(R.id.circle_post_tab);
        errandTab = view.findViewById(R.id.errand_tab);
        secondHandMarketTab = view.findViewById(R.id.second_hand_market_tab);
        lostAndFoundTab = view.findViewById(R.id.lost_and_found_tab);
    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new WCirclePostFragment());
        fragmentList.add(new WErrandFragment());
        fragmentList.add(new WSecondHandMarketFragment());
        fragmentList.add(new WLostAndFoundFragment());
    }

    private void initViewPager(View view) {
        circleViewPager = view.findViewById(R.id.circle_viewpager);
        CircleViewPagerAdapter adapter = new CircleViewPagerAdapter(getActivity());
        circleViewPager.setAdapter(adapter);
        
        circleViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStatus(position);
            }
        });
    }

    private void setTabClickListeners() {
        circlePostTab.setOnClickListener(v -> circleViewPager.setCurrentItem(0));
        errandTab.setOnClickListener(v -> circleViewPager.setCurrentItem(1));
        secondHandMarketTab.setOnClickListener(v -> circleViewPager.setCurrentItem(2));
        lostAndFoundTab.setOnClickListener(v -> circleViewPager.setCurrentItem(3));
    }

    private void initPostButton(View view) {
        fabPost = view.findViewById(R.id.fab_post);
        fabPost.setOnClickListener(v -> showPublishDialog());
    }

    private void showPublishDialog() {
        // 创建自定义对话框
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.setContentView(R.layout.dialog_post);
        
        // 设置窗口属性
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            android.view.WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 宽度为屏幕的90%
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER;
            window.setAttributes(params);
        }
        
        // 设置点击事件
        LinearLayout postCircle = dialog.findViewById(R.id.post_circle);
        LinearLayout postErrand = dialog.findViewById(R.id.post_errand);
        LinearLayout postSecondHand = dialog.findViewById(R.id.post_second_hand);
        LinearLayout postLostFound = dialog.findViewById(R.id.post_lost_found);
        
        if (postCircle != null) {
            postCircle.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), SPublishCircleActivity.class);
                startActivity(intent);
            });
        }
        
        if (postErrand != null) {
            postErrand.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), SPublishErrandActivity.class);
                startActivity(intent);
            });
        }
        
        if (postSecondHand != null) {
            postSecondHand.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), SPublishSecondHandActivity.class);
                startActivity(intent);
            });
        }
        
        if (postLostFound != null) {
            postLostFound.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), SPublishLostFoundActivity.class);
                startActivity(intent);
            });
        }
        
        dialog.show();
    }

    private void initSwipeRefresh(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refresh();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });
    }

    public void refresh() {
        Fragment currentFragment = fragmentList.get(circleViewPager.getCurrentItem());
        if (currentFragment instanceof WCirclePostFragment) {
            ((WCirclePostFragment) currentFragment).refresh();
        } else if (currentFragment instanceof WErrandFragment) {
            ((WErrandFragment) currentFragment).refresh();
        } else if (currentFragment instanceof WSecondHandMarketFragment) {
            ((WSecondHandMarketFragment) currentFragment).refresh();
        } else if (currentFragment instanceof WLostAndFoundFragment) {
            ((WLostAndFoundFragment) currentFragment).refresh();
        }
    }

    private void initStatistics(View view) {
        TextView universityStats = view.findViewById(R.id.university_stats);
        
        new Thread(() -> {
            Map<String, Integer> stats = WCircleRepository.getStatistics();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (universityStats != null) {
                    int postCount = stats.get("postCount");
                    int userCount = stats.get("userCount");
                    universityStats.setText(postCount + "动态 | " + userCount + "用户");
                }
            });
        }).start();
    }

    private void initTodayHot(View view) {
        TextView todayTopContent = view.findViewById(R.id.today_top_content);
        TextView todayTopViews = view.findViewById(R.id.today_top_views);
        LinearLayout todayTop = view.findViewById(R.id.today_top);
        
        loadTodayHot(todayTopContent, todayTopViews, todayTop);
    }
    
    /**
     * 加载今日最热数据（可复用，用于刷新）
     */
    private void loadTodayHot(TextView todayTopContent, TextView todayTopViews, LinearLayout todayTop) {
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("limit", "1");
                WApiClient.ApiResponse response = WApiClient.get("/app/circle/hot", params);
                if (response.success && response.getCode() == 200) {
                    Object dataObj = response.getData();
                    if (dataObj instanceof JSONObject) {
                        JSONObject data = (JSONObject) dataObj;
                        JSONArray postsArray = data.optJSONArray("posts");
                        if (postsArray != null && postsArray.length() > 0) {
                            JSONObject postJson = postsArray.getJSONObject(0);
                            String title = postJson.optString("title", "暂无");
                            int views = postJson.optInt("views", 0);
                            final String postId = postJson.optString("postId");
                            
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (todayTopContent != null) {
                                    todayTopContent.setText(title);
                                }
                                if (todayTopViews != null) {
                                    todayTopViews.setText(views + "浏览");
                                }
                                if (todayTop != null && postId != null) {
                                    todayTop.setOnClickListener(v -> {
                                        android.content.Intent intent = new android.content.Intent(getContext(), com.example.lnforum.activity.WCircleDetailActivity.class);
                                        intent.putExtra(com.example.lnforum.activity.WCircleDetailActivity.EXTRA_POST_ID, postId);
                                        startActivity(intent);
                                    });
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("WCircleFragment", "加载今日最热失败", e);
            }
        }).start();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 从详情页返回时刷新今日最热数据，同步浏览量变化
        TextView todayTopContent = getView() != null ? getView().findViewById(R.id.today_top_content) : null;
        TextView todayTopViews = getView() != null ? getView().findViewById(R.id.today_top_views) : null;
        LinearLayout todayTop = getView() != null ? getView().findViewById(R.id.today_top) : null;
        if (todayTopContent != null && todayTopViews != null && todayTop != null) {
            loadTodayHot(todayTopContent, todayTopViews, todayTop);
        }
    }

    private void initNotification(View view) {
        TextView notificationTitle = view.findViewById(R.id.notification_title);
        TextView notificationContent = view.findViewById(R.id.notification_content);
        LinearLayout notificationBanner = view.findViewById(R.id.notification_banner);
        
        new Thread(() -> {
            try {
                WApiClient.ApiResponse response = WApiClient.get("/app/notification/latest", null);
                if (response.success && response.getCode() == 200) {
                    Object dataObj = response.getData();
                    if (dataObj instanceof JSONObject) {
                        JSONObject data = (JSONObject) dataObj;
                        String title = data.optString("title", "系统通知标题");
                        String content = data.optString("content", "系统通知内容");
                        final String messageId = data.optString("messageId");
                        final JSONObject notificationData = data;
                        
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (notificationTitle != null) {
                                notificationTitle.setText(title);
                            }
                            if (notificationContent != null) {
                                notificationContent.setText(content);
                            }
                            if (notificationBanner != null && messageId != null) {
                                notificationBanner.setOnClickListener(v -> {
                                    showNotificationDialog(notificationData);
                                });
                            }
                            
                            // 进入首页时弹出系统通知弹窗
                            showNotificationDialog(notificationData);
                        });
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("WCircleFragment", "加载通知失败", e);
            }
        }).start();
    }
    
    /**
     * 在启动时显示通知弹窗（供MainActivity调用）
     */
    public void showNotificationDialogOnStart() {
        // 重新加载通知并显示弹窗
        View view = getView();
        if (view != null) {
            initNotification(view);
        }
    }
    
    /**
     * 显示系统通知弹窗（按照第三张图片的样式）
     */
    private void showNotificationDialog(JSONObject notificationData) {
        if (notificationData == null) return;
        
        try {
            String mainTitle = notificationData.optString("title", "系统通知");
            String content = notificationData.optString("content", "");
            String time = notificationData.optString("time", "");
            
            // 获取图片列表（兼容imageUrl和images字段）
            JSONArray imagesArray = null;
            if (notificationData.has("images")) {
                Object imagesObj = notificationData.get("images");
                if (imagesObj instanceof JSONArray) {
                    imagesArray = (JSONArray) imagesObj;
                } else if (imagesObj instanceof List) {
                    // 如果是List，转换为JSONArray
                    @SuppressWarnings("unchecked")
                    List<String> imagesList = (List<String>) imagesObj;
                    imagesArray = new JSONArray();
                    for (String img : imagesList) {
                        imagesArray.put(img);
                    }
                }
            } else if (notificationData.has("imageUrl")) {
                // 兼容旧的imageUrl字段
                String imageUrl = notificationData.optString("imageUrl", "");
                if (!imageUrl.isEmpty()) {
                    imagesArray = new JSONArray();
                    imagesArray.put(imageUrl);
                }
            }
            
            // 创建自定义对话框
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            
            // 创建主布局（ScrollView包装，支持滚动）
            android.widget.ScrollView scrollView = new android.widget.ScrollView(getContext());
            android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(getContext());
            mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            mainLayout.setPadding(0, 0, 0, 0);
            mainLayout.setBackgroundColor(android.graphics.Color.WHITE);
            
            // 标题栏（系统通知 + 关闭按钮）
            android.widget.RelativeLayout headerLayout = new android.widget.RelativeLayout(getContext());
            headerLayout.setPadding(20, 16, 16, 16);
            headerLayout.setBackgroundColor(android.graphics.Color.WHITE);
            
            TextView headerTitle = new TextView(getContext());
            headerTitle.setText("系统通知");
            headerTitle.setTextSize(18);
            headerTitle.setTextColor(android.graphics.Color.parseColor("#212121"));
            headerTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            RelativeLayout.LayoutParams headerTitleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
            headerTitleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            headerTitleParams.addRule(RelativeLayout.CENTER_VERTICAL);
            headerTitle.setLayoutParams(headerTitleParams);
            headerLayout.addView(headerTitle);
            
            // 关闭按钮（X）
            TextView closeBtn = new TextView(getContext());
            closeBtn.setText("✕");
            closeBtn.setTextSize(20);
            closeBtn.setTextColor(android.graphics.Color.parseColor("#757575"));
            closeBtn.setPadding(8, 8, 8, 8);
            RelativeLayout.LayoutParams closeBtnParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
            closeBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            closeBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
            closeBtn.setLayoutParams(closeBtnParams);
            headerLayout.addView(closeBtn);
            
            // 分隔线
            View divider = new View(getContext());
            divider.setBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"));
            android.widget.LinearLayout.LayoutParams dividerParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1);
            divider.setLayoutParams(dividerParams);
            
            // 内容区域
            android.widget.LinearLayout contentLayout = new android.widget.LinearLayout(getContext());
            contentLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            contentLayout.setPadding(20, 20, 20, 20);
            
            // 主标题
            TextView titleView = new TextView(getContext());
            titleView.setText(mainTitle);
            titleView.setTextSize(18);
            titleView.setTextColor(android.graphics.Color.parseColor("#212121"));
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setPadding(0, 0, 0, 8);
            contentLayout.addView(titleView);
            
            // 日期时间
            if (time != null && !time.isEmpty()) {
                TextView timeView = new TextView(getContext());
                timeView.setText(time);
                timeView.setTextSize(12);
                timeView.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                timeView.setPadding(0, 0, 0, 16);
                contentLayout.addView(timeView);
            }
            
            // 正文内容
            TextView contentView = new TextView(getContext());
            contentView.setText(content);
            contentView.setTextSize(15);
            contentView.setTextColor(android.graphics.Color.parseColor("#424242"));
            contentView.setLineSpacing(4, 1.2f);
            contentView.setPadding(0, 0, 0, (imagesArray != null && imagesArray.length() > 0) ? 16 : 0);
            contentLayout.addView(contentView);
            
            // 图片列表
            if (imagesArray != null && imagesArray.length() > 0) {
                for (int i = 0; i < imagesArray.length(); i++) {
                    String imageUrl = imagesArray.optString(i, "");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        android.widget.ImageView imageView = new android.widget.ImageView(getContext());
                        android.widget.LinearLayout.LayoutParams imageParams = new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageParams.setMargins(0, 16, 0, 0);
                        imageView.setLayoutParams(imageParams);
                        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                        imageView.setAdjustViewBounds(true);
                        imageView.setMaxHeight(400);
                        imageView.setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"));
                        
                        // 加载网络图片
                        new Thread(() -> {
                            try {
                                java.net.URL url = new java.net.URL(imageUrl);
                                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.setConnectTimeout(10000);
                                connection.setReadTimeout(10000);
                                connection.connect();
                                java.io.InputStream input = connection.getInputStream();
                                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(input);
                                input.close();
                                
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    imageView.setImageBitmap(bitmap);
                                });
                            } catch (Exception e) {
                                android.util.Log.e("WCircleFragment", "加载图片失败: " + imageUrl, e);
                            }
                        }).start();
                        
                        contentLayout.addView(imageView);
                    }
                }
            }
            
            // 组装布局
            mainLayout.addView(headerLayout);
            mainLayout.addView(divider);
            mainLayout.addView(contentLayout);
            scrollView.addView(mainLayout);
            
            builder.setView(scrollView);
            
            // 底部按钮
            builder.setPositiveButton("我知道了", null);
            
            android.app.AlertDialog dialog = builder.create();
            
            // 设置关闭按钮点击事件
            closeBtn.setOnClickListener(v -> dialog.dismiss());
            
            dialog.show();
            
            // 设置按钮样式
            android.widget.Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(android.graphics.Color.parseColor("#1976D2"));
                positiveButton.setTextSize(16);
                positiveButton.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }
        } catch (Exception e) {
            android.util.Log.e("WCircleFragment", "显示通知弹窗失败", e);
            e.printStackTrace();
        }
    }

    private void initSearch(View view) {
        LinearLayout searchBarContainer = view.findViewById(R.id.search_bar_container);
        if (searchBarContainer != null) {
            searchBarContainer.setOnClickListener(v -> {
                startActivity(new android.content.Intent(getContext(), WSearchActivity.class));
            });
        }
    }

    private void updateTabStatus(int position) {
        resetTabStatus();
        switch (position) {
            case 0:
                setTabSelected(circlePostTab, true);
                break;
            case 1:
                setTabSelected(errandTab, true);
                break;
            case 2:
                setTabSelected(secondHandMarketTab, true);
                break;
            case 3:
                setTabSelected(lostAndFoundTab, true);
                break;
        }
    }

    private void resetTabStatus() {
        setTabSelected(circlePostTab, false);
        setTabSelected(errandTab, false);
        setTabSelected(secondHandMarketTab, false);
        setTabSelected(lostAndFoundTab, false);
    }

    private void setTabSelected(TextView tab, boolean isSelected) {
        if (tab == null) return;
        if (isSelected) {
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
            tab.setTextColor(primaryColor);
            tab.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            tab.setTextColor(grayColor);
            tab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

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
}

