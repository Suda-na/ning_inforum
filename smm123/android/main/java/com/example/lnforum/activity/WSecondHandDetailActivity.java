package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lnforum.R;
import com.example.lnforum.model.WSecondHandItem;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.WSecondHandRepository;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.utils.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSecondHandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private WSecondHandItem item;
    private TextView title, desc, seller, time, tag, price, views;
    private ImageView backBtn, avatarView;
    private TextView followBtn;
    private TextView contactBtn;
    private LinearLayout imagesContainer;
    private boolean isFollowed = false; // 是否已关注
    private CUser currentUser; // 当前登录用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_hand_detail);

        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        
        // 获取当前登录用户
        CSessionManager sessionManager = CSessionManager.getInstance(this);
        currentUser = sessionManager.getCurrentCUser();
        
        new Thread(() -> {
            WSecondHandItem loadedItem = WSecondHandRepository.getItem(itemId);
            runOnUiThread(() -> {
                if (loadedItem == null) {
                    Toast.makeText(this, "商品不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                item = loadedItem;
                initViews();
                bindData();
                initActions();
                // 检查关注状态
                checkFollowStatus();
            });
        }).start();
    }

    private void initViews() {
        backBtn = findViewById(R.id.detail_back);
        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        seller = findViewById(R.id.detail_seller);
        time = findViewById(R.id.detail_time);
        tag = findViewById(R.id.detail_tag);
        price = findViewById(R.id.detail_price);
        views = findViewById(R.id.detail_views);
        avatarView = findViewById(R.id.detail_avatar);
        followBtn = findViewById(R.id.detail_follow);
        contactBtn = findViewById(R.id.detail_contact_btn);
        imagesContainer = findViewById(R.id.detail_images_container);
    }

    private void bindData() {
        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        seller.setText(item.getSeller());
        time.setText(item.getTime());
        tag.setText(item.getTag());
        price.setText(item.getPrice());
        views.setText(item.getViews() + "浏览");
        
        // 加载头像（使用ImageLoader工具类，参考圈子动态的实现）
        if (avatarView != null) {
            ImageLoader.loadAvatar(item.getSellerAvatar(), avatarView);
        }
        
        // 加载图片
        loadImages();
    }

    private void initActions() {
        backBtn.setOnClickListener(v -> finish());
        
        // 点击头像跳转到用户主页
        if (avatarView != null) {
            avatarView.setOnClickListener(v -> openUserProfile());
        }
        
        // 点击用户名跳转到用户主页
        if (seller != null) {
            seller.setOnClickListener(v -> openUserProfile());
        }
        
        // 关注按钮点击事件
        if (followBtn != null) {
            followBtn.setOnClickListener(v -> {
                if (currentUser == null || currentUser.getUserId() == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item == null || item.getSellerId() == null) {
                    Toast.makeText(this, "无法获取卖家信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                toggleFollow();
            });
        }
        
        // 联系我按钮点击事件
        if (contactBtn != null) {
            contactBtn.setOnClickListener(v -> {
                if (currentUser == null || currentUser.getUserId() == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (item == null || item.getSellerId() == null) {
                    Toast.makeText(this, "无法获取卖家信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                contactSeller();
            });
        }
    }

    /**
     * 打开用户主页
     */
    private void openUserProfile() {
        if (item == null || item.getSeller() == null || item.getSeller().isEmpty()) {
            Toast.makeText(this, "无法获取卖家信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, item.getSeller());
        startActivity(intent);
    }

    /**
     * 检查关注状态
     */
    private void checkFollowStatus() {
        if (currentUser == null || currentUser.getUserId() == null || item == null || item.getSellerId() == null) {
            updateFollowButton();
            return;
        }
        
        // 这里可以调用API检查关注状态，暂时使用默认值
        // 实际项目中应该从后端获取关注状态
        updateFollowButton();
    }

    /**
     * 切换关注状态
     */
    private void toggleFollow() {
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(currentUser.getUserId()));
                params.put("targetUserId", String.valueOf(item.getSellerId()));
                params.put("actionType", isFollowed ? "1" : "0"); // 0=关注, 1=取消关注
                
                android.util.Log.d("WSecondHandDetailActivity", "关注操作: userId=" + currentUser.getUserId() + ", targetUserId=" + item.getSellerId() + ", actionType=" + (isFollowed ? "1" : "0"));
                
                WApiClient.ApiResponse response = WApiClient.post("/api/cuser/follow_action", params);
                android.util.Log.d("WSecondHandDetailActivity", "关注响应: success=" + response.success + ", code=" + response.getCode() + ", message=" + response.getMessage());
                
                if (response.success && response.getCode() == 200) {
                    isFollowed = !isFollowed;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        updateFollowButton();
                        Toast.makeText(this, isFollowed ? "关注成功" : "取消关注成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        String errorMsg = response.getMessage() != null && !response.getMessage().isEmpty() 
                            ? response.getMessage() : "操作失败";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        android.util.Log.e("WSecondHandDetailActivity", "关注失败: " + errorMsg);
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("WSecondHandDetailActivity", "关注操作异常", e);
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * 联系卖家（关注并跳转私信）
     */
    private void contactSeller() {
        if (!isFollowed) {
            // 未关注，先关注
            new Thread(() -> {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("userId", String.valueOf(currentUser.getUserId()));
                    params.put("targetUserId", String.valueOf(item.getSellerId()));
                    params.put("actionType", "0"); // 0=关注
                    
                    android.util.Log.d("WSecondHandDetailActivity", "联系卖家-关注操作: userId=" + currentUser.getUserId() + ", targetUserId=" + item.getSellerId());
                    
                    WApiClient.ApiResponse response = WApiClient.post("/api/cuser/follow_action", params);
                    android.util.Log.d("WSecondHandDetailActivity", "联系卖家-关注响应: success=" + response.success + ", code=" + response.getCode() + ", message=" + response.getMessage());
                    
                    if (response.success && response.getCode() == 200) {
                        isFollowed = true;
                        new Handler(Looper.getMainLooper()).post(() -> {
                            updateFollowButton();
                            // 关注成功后跳转到私信页面
                            openChatWithSeller();
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            String errorMsg = response.getMessage() != null && !response.getMessage().isEmpty() 
                                ? response.getMessage() : "关注失败";
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                            android.util.Log.e("WSecondHandDetailActivity", "联系卖家-关注失败: " + errorMsg);
                        });
                    }
                } catch (Exception e) {
                    android.util.Log.e("WSecondHandDetailActivity", "联系卖家-关注异常", e);
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(this, "关注失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        } else {
            // 已关注，直接跳转到私信页面
            openChatWithSeller();
        }
    }

    /**
     * 打开与卖家的私信页面
     */
    private void openChatWithSeller() {
        if (item == null || item.getSeller() == null) {
            Toast.makeText(this, "无法获取卖家信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_TITLE, item.getSeller());
        intent.putExtra(ChatActivity.EXTRA_TYPE, "user");
        intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "user_" + (item.getSellerId() != null ? item.getSellerId() : item.getSeller().hashCode()));
        startActivity(intent);
    }

    /**
     * 更新关注按钮状态
     */
    private void updateFollowButton() {
        if (followBtn == null) return;
        
        if (isFollowed) {
            followBtn.setText("已关注");
            followBtn.setBackgroundResource(R.drawable.edit_text_bg);
            followBtn.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            followBtn.setText("关注");
            followBtn.setBackgroundResource(R.drawable.round_button_bg);
            followBtn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }

    /**
     * 加载图片
     */
    private void loadImages() {
        if (imagesContainer == null || item == null) return;
        
        imagesContainer.removeAllViews();
        List<String> images = item.getImages();
        if (images == null || images.isEmpty()) return;
        
        for (String imageUrl : images) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        android.util.Log.e("WSecondHandDetailActivity", "加载图片失败: " + imageUrl, e);
                    }
                }).start();
                
                imagesContainer.addView(imageView);
            }
        }
    }
}
