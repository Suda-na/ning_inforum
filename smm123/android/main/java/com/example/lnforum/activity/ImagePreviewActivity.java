package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lnforum.R;

/**
 * 图片预览Activity - 全屏查看图片
 */
public class ImagePreviewActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "extra_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        if (imageUrl == null || imageUrl.isEmpty()) {
            finish();
            return;
        }

        ImageView imageView = findViewById(R.id.preview_image);
        ImageView closeBtn = findViewById(R.id.preview_close);

        // 加载图片
        Glide.with(this)
            .load(imageUrl)
            .into(imageView);

        // 点击关闭
        closeBtn.setOnClickListener(v -> finish());
        
        // 点击图片也可以关闭
        imageView.setOnClickListener(v -> finish());
    }
}

