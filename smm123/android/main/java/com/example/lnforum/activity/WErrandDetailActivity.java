package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lnforum.repository.WApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lnforum.R;
import com.example.lnforum.model.WErrandOrder;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.WErrandRepository;
import com.example.lnforum.repository.CSessionManager;

public class WErrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private WErrandOrder order;
    private ImageView backBtn;
    private TextView title, desc, from, to, price, status, statusSub;
    private TextView contactAdminBtn;
    private TextView acceptOrderBtn;
    private TextView followBtn;
    private LinearLayout layoutAdminAction;
    private LinearLayout layoutRunnerActions;
    private boolean isRunner = false; // 是否是跑腿员
    private boolean isFollowed = false; // 是否已关注发布者
    private CUser currentUser; // 当前登录用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand_detail);

        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        
        // 检查itemId是否为空
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "订单ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 获取当前登录用户
        CSessionManager sessionManager = CSessionManager.getInstance(this);
        currentUser = sessionManager.getCurrentCUser();
        
        initViews();
        initActions();
        loadOrder(itemId);
    }

    private void initViews() {
        backBtn = findViewById(R.id.btn_back);
        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        from = findViewById(R.id.detail_from);
        to = findViewById(R.id.detail_to);
        price = findViewById(R.id.detail_price);
        status = findViewById(R.id.status_title);
        statusSub = findViewById(R.id.status_sub);
        contactAdminBtn = findViewById(R.id.btn_contact_admin);
        layoutAdminAction = findViewById(R.id.layout_admin_action);
        layoutRunnerActions = findViewById(R.id.layout_runner_actions);
        acceptOrderBtn = findViewById(R.id.btn_contact_publisher);
        followBtn = findViewById(R.id.btn_follow);
        
        android.util.Log.d("WErrandDetailActivity", "视图初始化检查: layoutRunnerActions=" + (layoutRunnerActions != null) + ", acceptOrderBtn=" + (acceptOrderBtn != null));
        
        // 检查关键视图是否为空
        if (backBtn == null || title == null || desc == null || from == null || to == null || price == null) {
            android.util.Log.e("WErrandDetailActivity", "关键视图初始化失败");
            Toast.makeText(this, "页面初始化失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrder(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "订单ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        new Thread(() -> {
            try {
                WErrandOrder loadedOrder = WErrandRepository.getOrder(orderId);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (loadedOrder == null) {
                        Toast.makeText(this, "订单不存在", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    order = loadedOrder;
                    // 检查用户是否是跑腿员
                    checkRunnerStatus();
                });
            } catch (Exception e) {
                android.util.Log.e("WErrandDetailActivity", "加载订单失败", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "加载订单失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    /**
     * 检查用户是否是跑腿员
     */
    private void checkRunnerStatus() {
        if (currentUser == null || currentUser.getUserId() == null) {
            // 未登录，显示联系管理员按钮
            isRunner = false;
            bindData();
            return;
        }
        
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(currentUser.getUserId()));
                WApiClient.ApiResponse response = WApiClient.get("/app/errand/check-runner", params);
                if (response.success && response.getCode() == 200) {
                    Object dataObj = response.getData();
                    if (dataObj instanceof JSONObject) {
                        JSONObject data = (JSONObject) dataObj;
                        isRunner = data.optBoolean("isRunner", false);
                        android.util.Log.d("WErrandDetailActivity", "用户是否是跑腿员: " + isRunner);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("WErrandDetailActivity", "检查跑腿员状态失败", e);
                isRunner = false;
            }
            
            new Handler(Looper.getMainLooper()).post(() -> {
                bindData();
            });
        }).start();
    }

    private void bindData() {
        if (order == null) {
            android.util.Log.e("WErrandDetailActivity", "订单数据为空，无法绑定");
            return;
        }
        
        try {
            if (title != null) title.setText(order.getTitle() != null ? order.getTitle() : "");
            if (desc != null) desc.setText(order.getDesc() != null ? order.getDesc() : "");
            if (from != null) from.setText(order.getFrom() != null ? order.getFrom() : "");
            if (to != null) to.setText(order.getTo() != null ? order.getTo() : "");
            if (price != null) price.setText("¥" + (order.getPrice() != null ? order.getPrice() : "0.00"));
            
            // 设置状态横幅文字和按钮显示
            String orderStatus = order.getStatus();
            android.util.Log.d("WErrandDetailActivity", "订单状态: " + orderStatus);
            
            // 判断是否为待接单状态（兼容多种可能的状态值）
            boolean isWaiting = "waiting".equals(orderStatus) || 
                               "unaccepted".equals(orderStatus) || 
                               "0".equals(orderStatus) || 
                               "1".equals(orderStatus);
            boolean isCompleted = "completed".equals(orderStatus) || "3".equals(orderStatus);
            boolean isDelivering = "delivering".equals(orderStatus) || "2".equals(orderStatus);
            
            android.util.Log.d("WErrandDetailActivity", "isWaiting: " + isWaiting + ", isCompleted: " + isCompleted + ", isDelivering: " + isDelivering);
            
            // 设置状态标题
            String statusText = "等待接单";
            String statusSubText = "快来接单吧";
            if (isCompleted) {
                statusText = "已完成";
                statusSubText = "订单已完成，请看看其他任务吧";
            } else if (isDelivering) {
                statusText = "配送中";
                statusSubText = "订单正在配送中";
            } else {
                // 默认或待接单状态
                statusText = "等待接单";
                statusSubText = "快来接单吧";
                isWaiting = true; // 如果不是已完成或配送中，默认是待接单
            }
            
            if (status != null) {
                status.setText(statusText);
            }
            if (statusSub != null) {
                statusSub.setText(statusSubText);
            }
            
            // 根据用户角色和订单状态显示不同的按钮
            boolean shouldShowButtons = "等待接单".equals(statusText);
            
            if (shouldShowButtons) {
                if (isRunner) {
                    // 是跑腿员：显示关注和接单按钮
                    if (layoutRunnerActions != null) {
                        layoutRunnerActions.setVisibility(View.VISIBLE);
                    }
                    if (layoutAdminAction != null) {
                        layoutAdminAction.setVisibility(View.GONE);
                    }
                    setupRunnerButtons();
                } else {
                    // 不是跑腿员：显示联系管理员按钮
                    if (layoutRunnerActions != null) {
                        layoutRunnerActions.setVisibility(View.GONE);
                    }
                    if (layoutAdminAction != null) {
                        layoutAdminAction.setVisibility(View.VISIBLE);
                    }
                    setupAdminButton();
                }
            } else {
                // 订单已完成或进行中，隐藏所有按钮
                if (layoutRunnerActions != null) {
                    layoutRunnerActions.setVisibility(View.GONE);
                }
                if (layoutAdminAction != null) {
                    layoutAdminAction.setVisibility(View.GONE);
                }
            }
            
            // 加载图片
            loadImages();
        } catch (Exception e) {
            android.util.Log.e("WErrandDetailActivity", "绑定数据失败", e);
            Toast.makeText(this, "显示数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置跑腿员按钮（关注和接单）
     */
    private void setupRunnerButtons() {
        // 设置关注按钮
        if (followBtn != null) {
            followBtn.setVisibility(View.VISIBLE);
            updateFollowButton();
            followBtn.setOnClickListener(v -> {
                if (currentUser == null || currentUser.getUserId() == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (order == null || order.getPublisherId() == null) {
                    Toast.makeText(this, "无法获取发布者信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isFollowed) {
                    // 已关注，跳转到私信页面
                    openChatWithPublisher();
                } else {
                    // 未关注，执行关注操作
                    followPublisher();
                }
            });
        }
        
        // 设置接单按钮
        if (acceptOrderBtn != null) {
            acceptOrderBtn.setVisibility(View.VISIBLE);
            acceptOrderBtn.setText("接单");
            acceptOrderBtn.setOnClickListener(v -> {
                if (currentUser == null || currentUser.getUserId() == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (order == null || order.getPostId() == null) {
                    Toast.makeText(this, "无法获取订单信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                acceptOrder();
            });
        }
    }

    /**
     * 设置联系管理员按钮
     */
    private void setupAdminButton() {
        if (contactAdminBtn != null) {
            contactAdminBtn.setText("联系管理员成为跑腿员");
            contactAdminBtn.setOnClickListener(v -> {
                // 跳转到私信页面，联系管理员
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_TITLE, "管理员");
                intent.putExtra(ChatActivity.EXTRA_TYPE, "admin");
                intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "admin_support");
                startActivity(intent);
            });
        }
    }

    /**
     * 关注发布者
     */
    private void followPublisher() {
        if (currentUser == null || currentUser.getUserId() == null || order == null || order.getPublisherId() == null) {
            Toast.makeText(this, "无法获取用户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(currentUser.getUserId()));
                params.put("targetUserId", String.valueOf(order.getPublisherId()));
                params.put("actionType", "0"); // 0=关注
                
                WApiClient.ApiResponse response = WApiClient.post("/api/client/follow_action", params);
                if (response.success && response.getCode() == 200) {
                    isFollowed = true;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        updateFollowButton();
                        Toast.makeText(this, "关注成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(this, "关注失败: " + response.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("WErrandDetailActivity", "关注发布者失败", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "关注失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * 更新关注按钮状态
     */
    private void updateFollowButton() {
        if (followBtn == null) return;
        
        if (isFollowed) {
            followBtn.setText("联系该发布者");
            followBtn.setBackgroundResource(R.drawable.edit_text_bg);
            followBtn.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            followBtn.setText("关注发布者");
            followBtn.setBackgroundResource(R.drawable.edit_text_bg);
            followBtn.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    /**
     * 打开与发布者的私信页面
     */
    private void openChatWithPublisher() {
        if (order == null || order.getPublisherId() == null) {
            Toast.makeText(this, "无法获取发布者信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取发布者用户名（从order的tag字段获取，或者从后端获取）
        String publisherName = order.getTag() != null ? order.getTag() : "发布者";
        
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_TITLE, publisherName);
        intent.putExtra(ChatActivity.EXTRA_TYPE, "user");
        intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "user_" + order.getPublisherId());
        startActivity(intent);
    }

    /**
     * 接单
     */
    private void acceptOrder() {
        if (currentUser == null || currentUser.getUserId() == null || order == null || order.getPostId() == null) {
            Toast.makeText(this, "无法获取订单信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示确认对话框
        new android.app.AlertDialog.Builder(this)
            .setTitle("确认接单")
            .setMessage("确定要接下这个订单吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                doAcceptOrder();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    /**
     * 执行接单操作
     */
    private void doAcceptOrder() {
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("postId", String.valueOf(order.getPostId()));
                params.put("acceptorId", String.valueOf(currentUser.getUserId()));
                
                WApiClient.ApiResponse response = WApiClient.post("/app/errand/accept", params);
                if (response.success && response.getCode() == 200) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(this, "接单成功", Toast.LENGTH_SHORT).show();
                        // 刷新页面或返回
                        finish();
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(this, "接单失败: " + response.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("WErrandDetailActivity", "接单失败", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "接单失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void loadImages() {
        LinearLayout imagesContainer = findViewById(R.id.detail_images_container);
        if (imagesContainer == null || order == null) return;
        
        imagesContainer.removeAllViews();
        
        // 从后端获取订单详情，包含图片信息
        new Thread(() -> {
            try {
                WApiClient.ApiResponse response = WApiClient.get("/app/errand/order/" + order.getId(), null);
                if (response.success && response.getCode() == 200) {
                    Object dataObj = response.getData();
                    if (dataObj instanceof JSONObject) {
                        JSONObject data = (JSONObject) dataObj;
                        if (data.has("images")) {
                            Object imagesObj = data.get("images");
                            if (imagesObj instanceof JSONArray) {
                                JSONArray imagesArray = (JSONArray) imagesObj;
                                List<String> imageUrls = new ArrayList<>();
                                for (int i = 0; i < imagesArray.length(); i++) {
                                    String imageUrl = imagesArray.optString(i, "");
                                    if (!imageUrl.isEmpty()) {
                                        imageUrls.add(imageUrl);
                                    }
                                }
                                
                                // 在主线程加载图片
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    loadImagesToContainer(imagesContainer, imageUrls);
                                });
                            }
                        }
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("WErrandDetailActivity", "获取订单详情失败", e);
            }
        }).start();
    }
    
    private void loadImagesToContainer(LinearLayout container, List<String> imageUrls) {
        if (container == null || imageUrls == null || imageUrls.isEmpty()) return;
        
        for (String imageUrl : imageUrls) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 0, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(600);
                
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
                        android.util.Log.e("WErrandDetailActivity", "加载图片失败: " + imageUrl, e);
                    }
                }).start();
                
                container.addView(imageView);
            }
        }
    }

    private void initActions() {
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }
}
