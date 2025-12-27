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
import com.example.lnforum.activity.PublishCircleActivity;
import com.example.lnforum.activity.PublishSecondHandActivity;
import com.example.lnforum.activity.PublishLostFoundActivity;
import com.example.lnforum.activity.PublishErrandActivity;
import com.example.lnforum.activity.WSearchActivity;
import com.example.lnforum.fragment.market.WSecondHandMarketFragment;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.repository.WCircleRepository;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.model.CUser;
import com.example.lnforum.utils.ImageLoader;
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
        // 检查登录状态
        com.example.lnforum.repository.CSessionManager sessionManager = 
            com.example.lnforum.repository.CSessionManager.getInstance(getContext());
        if (!sessionManager.isLoggedIn()) {
            // 未登录，跳转到登录页
            android.widget.Toast.makeText(getContext(), "请先登录", android.widget.Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(getContext(), com.example.lnforum.activity.WLoginActivity.class);
            startActivity(loginIntent);
            return;
        }
        
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
                Intent intent = new Intent(getContext(), PublishCircleActivity.class);
                startActivity(intent);
            });
        }
        
        if (postErrand != null) {
            postErrand.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), PublishErrandActivity.class);
                startActivity(intent);
            });
        }
        
        if (postSecondHand != null) {
            postSecondHand.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), PublishSecondHandActivity.class);
                startActivity(intent);
            });
        }
        
        if (postLostFound != null) {
            postLostFound.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), PublishLostFoundActivity.class);
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
                // 获取当前登录用户ID（如果已登录）
                Map<String, String> params = new HashMap<>();
                CSessionManager cSessionManager = CSessionManager.getInstance(requireContext());
                if (cSessionManager.isLoggedIn()) {
                    CUser currentUser = cSessionManager.getCurrentCUser();
                    if (currentUser != null && currentUser.getUserId() != null) {
                        params.put("userId", String.valueOf(currentUser.getUserId()));
                    }
                }
                
                WApiClient.ApiResponse response = WApiClient.get("/app/notification/latest", params);
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
            
            // 创建自定义对话框，使用美观的卡片样式
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            
            // 创建主容器（带圆角和阴影效果）
            android.widget.LinearLayout rootLayout = new android.widget.LinearLayout(getContext());
            rootLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            rootLayout.setBackgroundResource(R.drawable.bg_notification_dialog);
            rootLayout.setPadding(0, 0, 0, 0);
            
            // 创建ScrollView包装内容
            android.widget.ScrollView scrollView = new android.widget.ScrollView(getContext());
            scrollView.setPadding(0, 0, 0, 0);
            scrollView.setFillViewport(true);
            
            android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(getContext());
            mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            mainLayout.setPadding(0, 0, 0, 0);
            
            // 标题栏（系统通知 + 关闭按钮）- 使用渐变背景
            android.widget.RelativeLayout headerLayout = new android.widget.RelativeLayout(getContext());
            headerLayout.setPadding(24, 24, 20, 20);
            headerLayout.setBackgroundColor(android.graphics.Color.WHITE);
            
            // 添加通知图标
            android.widget.ImageView notificationIcon = new android.widget.ImageView(getContext());
            notificationIcon.setId(android.view.View.generateViewId()); // 生成唯一ID
            try {
                notificationIcon.setImageResource(R.drawable.ic_notifications);
                notificationIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_blue));
            } catch (Exception e) {
                // 如果图标不存在，使用文字代替
            }
            RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()),
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            iconParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
            notificationIcon.setLayoutParams(iconParams);
            headerLayout.addView(notificationIcon);
            
            TextView headerTitle = new TextView(getContext());
            headerTitle.setText("系统通知");
            headerTitle.setTextSize(20);
            headerTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            headerTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            RelativeLayout.LayoutParams headerTitleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
            headerTitleParams.addRule(RelativeLayout.RIGHT_OF, notificationIcon.getId());
            headerTitleParams.addRule(RelativeLayout.CENTER_VERTICAL);
            headerTitleParams.setMarginStart((int) android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
            headerTitle.setLayoutParams(headerTitleParams);
            headerLayout.addView(headerTitle);
            
            // 关闭按钮（X）- 更美观的样式
            TextView closeBtn = new TextView(getContext());
            closeBtn.setText("✕");
            closeBtn.setTextSize(24);
            closeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            closeBtn.setPadding(8, 8, 8, 8);
            closeBtn.setClickable(true);
            closeBtn.setFocusable(true);
            RelativeLayout.LayoutParams closeBtnParams = new RelativeLayout.LayoutParams(
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
            closeBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            closeBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
            closeBtn.setGravity(android.view.Gravity.CENTER);
            closeBtn.setLayoutParams(closeBtnParams);
            headerLayout.addView(closeBtn);
            
            // 分隔线 - 更细更优雅
            View divider = new View(getContext());
            divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider));
            android.widget.LinearLayout.LayoutParams dividerParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics()));
            divider.setLayoutParams(dividerParams);
            
            // 内容区域 - 更大的内边距
            android.widget.LinearLayout contentLayout = new android.widget.LinearLayout(getContext());
            contentLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            contentLayout.setPadding(24, 24, 24, 24);
            
            // 主标题 - 更大更醒目
            TextView titleView = new TextView(getContext());
            titleView.setText(mainTitle);
            titleView.setTextSize(20);
            titleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setPadding(0, 0, 0, 12);
            android.widget.LinearLayout.LayoutParams titleParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            titleView.setLayoutParams(titleParams);
            contentLayout.addView(titleView);
            
            // 日期时间 - 更小的字体，更优雅
            if (time != null && !time.isEmpty()) {
                TextView timeView = new TextView(getContext());
                timeView.setText(time);
                timeView.setTextSize(13);
                timeView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                timeView.setPadding(0, 0, 0, 20);
                android.widget.LinearLayout.LayoutParams timeParams = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                timeView.setLayoutParams(timeParams);
                contentLayout.addView(timeView);
            }
            
            // 正文内容 - 更好的行间距和字体大小
            TextView contentView = new TextView(getContext());
            contentView.setText(content);
            contentView.setTextSize(16);
            contentView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            contentView.setLineSpacing(android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()), 1.3f);
            contentView.setPadding(0, 0, 0, (imagesArray != null && imagesArray.length() > 0) ? 20 : 0);
            android.widget.LinearLayout.LayoutParams contentParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            contentView.setLayoutParams(contentParams);
            contentLayout.addView(contentView);
            
            // 图片列表 - 添加圆角和更好的间距
            if (imagesArray != null && imagesArray.length() > 0) {
                for (int i = 0; i < imagesArray.length(); i++) {
                    String imageUrl = imagesArray.optString(i, "");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // 图片容器，添加圆角
                        android.widget.FrameLayout imageContainer = new android.widget.FrameLayout(getContext());
                        android.widget.LinearLayout.LayoutParams containerParams = new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        containerParams.setMargins(0, 20, 0, 0);
                        imageContainer.setLayoutParams(containerParams);
                        imageContainer.setBackgroundResource(R.drawable.bg_notification_dialog);
                        
                        android.widget.ImageView imageView = new android.widget.ImageView(getContext());
                        android.widget.FrameLayout.LayoutParams imageParams = new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(imageParams);
                        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                        imageView.setAdjustViewBounds(true);
                        imageView.setMaxHeight((int) android.util.TypedValue.applyDimension(
                            android.util.TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics()));
                        imageView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background_light));
                        
                        // 使用ImageLoader加载图片
                        ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 400);
                        
                        imageContainer.addView(imageView);
                        contentLayout.addView(imageContainer);
                    }
                }
            }
            
            // 组装布局
            mainLayout.addView(headerLayout);
            mainLayout.addView(divider);
            mainLayout.addView(contentLayout);
            scrollView.addView(mainLayout);
            rootLayout.addView(scrollView);
            
            builder.setView(rootLayout);
            
            // 创建自定义按钮布局
            android.widget.LinearLayout buttonLayout = new android.widget.LinearLayout(getContext());
            buttonLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(android.view.Gravity.END);
            buttonLayout.setPadding(24, 16, 24, 24);
            buttonLayout.setBackgroundColor(android.graphics.Color.WHITE);
            
            android.widget.Button positiveButton = new android.widget.Button(getContext());
            positiveButton.setText("我知道了");
            positiveButton.setTextSize(16);
            positiveButton.setTextColor(android.graphics.Color.WHITE);
            positiveButton.setBackgroundResource(R.drawable.bg_notification_button);
            positiveButton.setPadding(
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()),
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()),
                (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
            android.widget.LinearLayout.LayoutParams buttonParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            positiveButton.setLayoutParams(buttonParams);
            buttonLayout.addView(positiveButton);
            
            // 将按钮布局添加到主布局
            rootLayout.addView(buttonLayout);
            
            android.app.AlertDialog dialog = builder.create();
            
            // 设置按钮点击事件
            positiveButton.setOnClickListener(v -> dialog.dismiss());
            
            // 设置关闭按钮点击事件
            closeBtn.setOnClickListener(v -> dialog.dismiss());
            
            dialog.show();
            
            // 设置对话框窗口样式 - 添加阴影和圆角
            android.view.Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                android.view.WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.88);
                params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
                params.gravity = android.view.Gravity.CENTER;
                // 添加阴影效果
                window.setElevation(android.util.TypedValue.applyDimension(
                    android.util.TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
                window.setAttributes(params);
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

