package com.example.lnforum.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.adapter.ImageGridAdapter;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.repository.WCircleRepository;
import com.example.lnforum.activity.WLoginActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class PublishCircleActivity extends AppCompatActivity {

    private final String[] tagOptions = new String[]{
            "校园日常", "学习搭子", "期末冲刺", "校园干饭指南",
            "宿舍日常", "校园拍照", "社团招新", "校园求助",
            "找搭子", "图书馆日常", "校园散步", "院系活动"
    };

    private EditText titleInput;
    private EditText contentInput;
    private TextView selectedTagView;
    private ChipGroup tagGroup;
    private LinearLayout addImageLayout;
    private RecyclerView imageGrid;
    private List<Uri> selectedImages;
    private ImageGridAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int MAX_IMAGES = 3;
    private CSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_circle);

        // 检查登录状态
        sessionManager = CSessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        View root = findViewById(R.id.publish_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });

        selectedImages = new ArrayList<>();
        initViews();
        setupChips();
        setupActions();
    }

    private void initViews() {
        titleInput = findViewById(R.id.input_title);
        contentInput = findViewById(R.id.input_content);
        selectedTagView = findViewById(R.id.selected_tag);
        tagGroup = findViewById(R.id.tag_group);
        // 图片选择功能（如果布局中有这些控件）
        try {
            addImageLayout = findViewById(R.id.btn_add_image);
        } catch (Exception e) {
            addImageLayout = null;
        }
        try {
            imageGrid = findViewById(R.id.image_grid);
        } catch (Exception e) {
            imageGrid = null;
        }
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        TextView publish = findViewById(R.id.btn_publish);

        back.setOnClickListener(v -> finish());
        publish.setOnClickListener(v -> handlePublish());
        
        // 添加图片按钮点击事件（如果存在）
        if (addImageLayout != null) {
            addImageLayout.setOnClickListener(v -> pickImage());
        }
    }

    private void handlePublish() {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();
        String tag = selectedTagView.getText().toString();
        
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请先写点内容再发布", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查登录状态
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // 处理图片URL（目前先传空，后续可以扩展为上传图片获取URL）
        String image1 = null, image2 = null, image3 = null;
        if (selectedImages != null && !selectedImages.isEmpty()) {
            // TODO: 这里应该上传图片到服务器，获取URL后再传递
            // 目前先传空，后续可以扩展
        }
        
        // 创建final变量供lambda使用
        final String finalTitle = title;
        final String finalContent = content;
        final String finalTag = tag;
        final String finalImage1 = image1;
        final String finalImage2 = image2;
        final String finalImage3 = image3;
        final Integer finalUserId = currentUser.getUserId();
        
        // 显示加载提示
        Toast.makeText(this, "发布中...", Toast.LENGTH_SHORT).show();
        
        // 异步发布
        new Thread(() -> {
            boolean success = WCircleRepository.publishCirclePost(
                finalUserId, finalTitle, finalContent, finalTag, finalImage1, finalImage2, finalImage3);
            
            new Handler(Looper.getMainLooper()).post(() -> {
                if (success) {
                    Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    
    private void pickImage() {
        if (selectedImages.size() >= MAX_IMAGES) {
            Toast.makeText(this, "最多只能选择" + MAX_IMAGES + "张图片", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // 多选图片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && selectedImages.size() < MAX_IMAGES; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImages.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    // 单选图片
                    if (selectedImages.size() < MAX_IMAGES) {
                        selectedImages.add(data.getData());
                    }
                }

                updateImageGrid();
            }
        }
    }

    private void updateImageGrid() {
        if (addImageLayout == null || imageGrid == null) {
            return;
        }

        if (selectedImages.isEmpty()) {
            imageGrid.setVisibility(View.GONE);
            if (addImageLayout != null) {
                addImageLayout.setVisibility(View.VISIBLE);
            }
        } else {
            imageGrid.setVisibility(View.VISIBLE);
            if (addImageLayout != null) {
                addImageLayout.setVisibility(selectedImages.size() >= MAX_IMAGES ? View.GONE : View.VISIBLE);
            }

            // 设置网格布局
            if (imageGrid.getLayoutManager() == null) {
                GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
                imageGrid.setLayoutManager(layoutManager);
            }

            // 创建或更新Adapter
            if (imageAdapter == null) {
                imageAdapter = new ImageGridAdapter(selectedImages, position -> {
                    selectedImages.remove(position);
                    imageAdapter.notifyItemRemoved(position);
                    imageAdapter.notifyItemRangeChanged(position, selectedImages.size());
                    updateImageGrid();
                });
                imageGrid.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setupChips() {
        if (tagGroup == null) {
            return;
        }
        tagGroup.setSingleSelection(true);
        tagGroup.setSelectionRequired(false);

        int checkedBg = ContextCompat.getColor(this, R.color.primary_blue_light);
        int normalBg = ContextCompat.getColor(this, R.color.background_white);
        int checkedText = ContextCompat.getColor(this, android.R.color.white);
        int normalText = ContextCompat.getColor(this, R.color.text_secondary);
        int strokeColor = ContextCompat.getColor(this, R.color.divider);
        int rippleColor = ContextCompat.getColor(this, R.color.primary_blue);
        int chipPadding = (int) (getResources().getDisplayMetrics().density * 6);

        ColorStateList bgColors = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{checkedBg, normalBg}
        );
        ColorStateList textColors = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{checkedText, normalText}
        );

        for (String tag : tagOptions) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColor(bgColors);
            chip.setTextColor(textColors);
            chip.setChipStrokeColor(ColorStateList.valueOf(strokeColor));
            chip.setChipStrokeWidth(1f);
            chip.setRippleColor(ColorStateList.valueOf(rippleColor));
            chip.setCheckedIconVisible(false);
            chip.setPadding(chipPadding, 0, chipPadding, 0);
            chip.setOnClickListener(v -> selectedTagView.setText(tag));
            tagGroup.addView(chip);
        }

        if (tagGroup.getChildCount() > 0) {
            Chip first = (Chip) tagGroup.getChildAt(0);
            first.setChecked(true);
            selectedTagView.setText(first.getText());
        }
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}


