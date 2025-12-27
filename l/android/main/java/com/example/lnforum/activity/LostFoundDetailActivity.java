package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.lnforum.R;
import com.example.lnforum.model.LostFoundItem;
import com.example.lnforum.repository.LostFoundRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class LostFoundDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private LostFoundItem item;
    private TextView title, desc, user, time, tag, location, views, followBtn;
    private ImageView backBtn, moreBtn;
    private LinearLayout imagesContainer;
    private boolean isFollowed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_detail);

        View root = findViewById(R.id.lost_found_detail_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        item = LostFoundRepository.getItem(itemId);
        if (item == null) {
            Toast.makeText(this, "信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        item.addView();

        initViews();
        bindData();
        initActions();
    }

    private void initViews() {
        backBtn = findViewById(R.id.detail_back);
        moreBtn = findViewById(R.id.detail_more);
        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        user = findViewById(R.id.detail_user);
        time = findViewById(R.id.detail_time);
        tag = findViewById(R.id.detail_tag);
        location = findViewById(R.id.detail_location);
        views = findViewById(R.id.detail_views);
        followBtn = findViewById(R.id.detail_follow);
        imagesContainer = findViewById(R.id.detail_images_container);
    }

    private void bindData() {
        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        user.setText(item.getUser());
        time.setText(item.getTime());
        tag.setText(item.getTag());
        location.setText(item.getLocation());
        views.setText(item.getViews() + "浏览");

        // 设置标签颜色
        int tagColor = "招领".equals(item.getTag())
                ? ContextCompat.getColor(this, R.color.primary_blue)
                : ContextCompat.getColor(this, R.color.text_secondary);
        tag.setTextColor(tagColor);

        // 加载图片
        loadImages(item.getImages());
        
        // 初始化关注按钮状态
        updateFollowButton();
    }

    private void loadImages(List<String> imageUrls) {
        imagesContainer.removeAllViews();
        if (imageUrls == null || imageUrls.isEmpty()) {
            imagesContainer.setVisibility(View.GONE);
            return;
        }
        imagesContainer.setVisibility(View.VISIBLE);

        for (String url : imageUrls) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(200)
            );
            params.setMargins(0, dpToPx(8), 0, 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
            // 这里后续可以接入图片加载库如Glide
            // Glide.with(this).load(url).into(imageView);
            imagesContainer.addView(imageView);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void initActions() {
        backBtn.setOnClickListener(v -> finish());
        moreBtn.setOnClickListener(v -> showMoreActions());
        
        followBtn.setOnClickListener(v -> {
            isFollowed = !isFollowed;
            updateFollowButton();
            Toast.makeText(this, isFollowed ? "已关注" : "已取消关注", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.detail_user_area).setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);

            startActivity(intent);
        });
    }
    
    private void updateFollowButton() {
        if (isFollowed) {
            followBtn.setText("已关注");
            followBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            followBtn.setText("关注");
            followBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.round_button_bg));
        }
    }
    
    private void showMoreActions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_menu_more, null);
        TextView report = view.findViewById(R.id.menu_report);
        TextView cancel = view.findViewById(R.id.menu_cancel);
        report.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(ReportActivity.EXTRA_TYPE, "post");
            intent.putExtra(ReportActivity.EXTRA_TARGET_ID, item.getId());
            intent.putExtra(ReportActivity.EXTRA_TARGET_NAME, item.getUser());
            startActivity(intent);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}

