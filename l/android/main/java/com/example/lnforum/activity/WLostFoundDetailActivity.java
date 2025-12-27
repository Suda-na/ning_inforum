package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lnforum.R;
import com.example.lnforum.model.WLostFoundItem;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.repository.WLostFoundRepository;

import java.util.List;

public class WLostFoundDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private WLostFoundItem item;
    private ImageView backBtn;
    private TextView title, desc, tag, user, time, location, views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_detail);

        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "信息ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        new Thread(() -> {
            item = WLostFoundRepository.getItem(itemId);
            runOnUiThread(() -> {
                if (item == null) {
                    Toast.makeText(this, "信息不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                initViews();
                bindData();
                initActions();
                // 增加浏览量
                incrementViewCount(itemId);
            });
        }).start();
    }

    private void initViews() {
        backBtn = findViewById(R.id.detail_back);
        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        tag = findViewById(R.id.detail_tag);
        user = findViewById(R.id.detail_user);
        time = findViewById(R.id.detail_time);
        location = findViewById(R.id.detail_location);
        views = findViewById(R.id.detail_views);
    }

    private void bindData() {
        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        tag.setText(item.getTag());
        user.setText(item.getUser());
        time.setText(item.getTime());
        location.setText(item.getLocation());
        if (views != null) {
            views.setText(item.getViews() + "浏览");
        }
        
        // 加载图片
        loadImages();
    }
    
    /**
     * 增加浏览量
     */
    private void incrementViewCount(String itemId) {
        new Thread(() -> {
            try {
                WApiClient.ApiResponse response = WApiClient.post("/app/lostfound/item/" + itemId + "/view", null);
                if (response.success && response.getCode() == 200) {
                    // 浏览量增加成功，更新本地显示
                    runOnUiThread(() -> {
                        item.addView();
                        if (views != null) {
                            views.setText(item.getViews() + "浏览");
                        }
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("WLostFoundDetailActivity", "增加浏览量失败", e);
            }
        }).start();
    }
    
    private void loadImages() {
        LinearLayout imagesContainer = findViewById(R.id.detail_images_container);
        if (imagesContainer == null || item == null) return;
        
        imagesContainer.removeAllViews();
        List<String> images = item.getImages();
        if (images == null || images.isEmpty()) return;
        
        for (String imageUrl : images) {
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
                        
                        runOnUiThread(() -> {
                            imageView.setImageBitmap(bitmap);
                        });
                    } catch (Exception e) {
                        android.util.Log.e("WLostFoundDetailActivity", "加载图片失败: " + imageUrl, e);
                    }
                }).start();
                
                imagesContainer.addView(imageView);
            }
        }
    }

    private void initActions() {
        backBtn.setOnClickListener(v -> finish());
    }
}

