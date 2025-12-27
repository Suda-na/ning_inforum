package com.example.lnforum.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.utils.HttpUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布二手Activity（S开头，调用SPostController）
 */
public class SPublishSecondHandActivity extends AppCompatActivity {

    private final String[] tagOptions = new String[]{
            "校园二手", "二手教材", "毕业甩卖", "数码闲置", "宿舍神器",
            "考研资料", "服饰鞋包", "求购", "低价出物"
    };

    private EditText titleInput;
    private EditText descInput;
    private EditText priceInput;
    private ChipGroup tagGroup;
    private TextView selectedTagView;
    private LinearLayout addImageLayout;
    private RecyclerView imageGrid;
    private List<Uri> selectedImages;
    private ImageGridAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_IMAGE = 1002;
    private static final int MAX_IMAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_second_hand);

        View root = findViewById(R.id.publish_second_root);
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
        descInput = findViewById(R.id.input_desc);
        priceInput = findViewById(R.id.input_price);
        tagGroup = findViewById(R.id.tag_group);
        selectedTagView = findViewById(R.id.selected_tag);
        addImageLayout = findViewById(R.id.btn_add_image);
        imageGrid = findViewById(R.id.image_grid);
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        TextView publish = findViewById(R.id.btn_publish);
        TextView publishBottom = findViewById(R.id.btn_publish_bottom);

        View.OnClickListener publishAction = v -> handlePublish();
        publish.setOnClickListener(publishAction);
        publishBottom.setOnClickListener(publishAction);

        back.setOnClickListener(v -> finish());

        // 添加图片按钮点击事件
        if (addImageLayout != null) {
            addImageLayout.setOnClickListener(v -> pickImage());
        }
    }

    private void handlePublish() {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String tag = selectedTagView.getText().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "请先填写描述信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(this, "请选择分类标签", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }

        // 获取当前用户ID
        WSessionManager sessionManager = WSessionManager.getInstance(this);
        com.example.lnforum.model.WUser currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建请求数据
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("userId", currentUser.getUserId());
            jsonData.put("title", title);
            jsonData.put("desc", desc);
            jsonData.put("tagName", tag);
            if (!TextUtils.isEmpty(price)) {
                jsonData.put("price", price);
            }

            // 添加图片信息（目前先传URI，后续可以扩展为上传）
            if (selectedImages != null && !selectedImages.isEmpty()) {
                if (selectedImages.size() > 0) {
                    jsonData.put("image1", selectedImages.get(0).toString());
                }
                if (selectedImages.size() > 1) {
                    jsonData.put("image2", selectedImages.get(1).toString());
                }
                if (selectedImages.size() > 2) {
                    jsonData.put("image3", selectedImages.get(2).toString());
                }
            }

            // 发送网络请求到SPostController
            HttpUtil.post("/api/android/post/publish/secondhand", jsonData, new HttpUtil.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject result = new JSONObject(response);
                            boolean success = result.optBoolean("success", false);
                            String message = result.optString("message", "");

                            if (success) {
                                Toast.makeText(SPublishSecondHandActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(SPublishSecondHandActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SPublishSecondHandActivity.this, "解析响应失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(SPublishSecondHandActivity.this, "发布失败: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "构建请求数据失败", Toast.LENGTH_SHORT).show();
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
            addImageLayout.setVisibility(View.VISIBLE);
        } else {
            imageGrid.setVisibility(View.VISIBLE);
            addImageLayout.setVisibility(selectedImages.size() >= MAX_IMAGES ? View.GONE : View.VISIBLE);

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

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}

