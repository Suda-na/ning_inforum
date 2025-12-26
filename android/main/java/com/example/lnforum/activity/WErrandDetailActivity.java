package com.example.lnforum.activity;

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
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lnforum.R;
import com.example.lnforum.model.WErrandOrder;
import com.example.lnforum.repository.WErrandRepository;

public class WErrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private WErrandOrder order;
    private ImageView backBtn;
    private TextView title, desc, from, to, price, status, statusSub;
    private TextView contactAdminBtn;
    private TextView acceptOrderBtn;
    private LinearLayout layoutAdminAction;
    private LinearLayout layoutRunnerActions;

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
                    bindData();
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
            
            // 根据状态显示/隐藏按钮
            // 待接单时显示"接单"按钮，已完成或配送中时不显示任何按钮
            // 直接根据状态文字判断，更可靠
            boolean shouldShowButtons = "等待接单".equals(statusText);
            
            android.util.Log.d("WErrandDetailActivity", "按钮显示判断: statusText=" + statusText + ", shouldShowButtons=" + shouldShowButtons);
            android.util.Log.d("WErrandDetailActivity", "layoutRunnerActions是否为null: " + (layoutRunnerActions == null));
            
            if (layoutRunnerActions != null) {
                // 跑腿员操作区域：待接单时显示接单按钮
                int visibility = shouldShowButtons ? View.VISIBLE : View.GONE;
                layoutRunnerActions.setVisibility(visibility);
                android.util.Log.d("WErrandDetailActivity", "设置layoutRunnerActions可见性: " + (visibility == View.VISIBLE ? "VISIBLE" : "GONE") + ", 实际可见性: " + layoutRunnerActions.getVisibility());
            } else {
                android.util.Log.e("WErrandDetailActivity", "layoutRunnerActions为null！");
            }
            
            if (layoutAdminAction != null) {
                // 管理员操作区域：隐藏（不使用）
                layoutAdminAction.setVisibility(View.GONE);
            }
            
            // 设置接单按钮
            android.util.Log.d("WErrandDetailActivity", "acceptOrderBtn是否为null: " + (acceptOrderBtn == null));
            if (acceptOrderBtn != null) {
                if (shouldShowButtons) {
                    acceptOrderBtn.setText("接单");
                    acceptOrderBtn.setVisibility(View.VISIBLE);
                    acceptOrderBtn.setOnClickListener(v -> {
                        // TODO: 实现接单功能
                        Toast.makeText(this, "接单功能待实现", Toast.LENGTH_SHORT).show();
                    });
                    android.util.Log.d("WErrandDetailActivity", "接单按钮已设置，文本: " + acceptOrderBtn.getText());
                } else {
                    acceptOrderBtn.setVisibility(View.GONE);
                }
            } else {
                android.util.Log.e("WErrandDetailActivity", "acceptOrderBtn为null！");
            }
            
            // 设置关注发布者按钮（可选）
            TextView followBtn = findViewById(R.id.btn_follow);
            if (followBtn != null) {
                if (shouldShowButtons) {
                    followBtn.setVisibility(View.VISIBLE);
                    followBtn.setOnClickListener(v -> {
                        Toast.makeText(this, "关注功能待实现", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    followBtn.setVisibility(View.GONE);
                }
            }
            
            // 加载图片
            loadImages();
        } catch (Exception e) {
            android.util.Log.e("WErrandDetailActivity", "绑定数据失败", e);
            Toast.makeText(this, "显示数据失败", Toast.LENGTH_SHORT).show();
        }
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

