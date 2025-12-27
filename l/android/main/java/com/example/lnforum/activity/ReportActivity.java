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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type"; // post, comment, user
    public static final String EXTRA_TARGET_ID = "extra_target_id"; // 被举报对象ID
    public static final String EXTRA_TARGET_NAME = "extra_target_name"; // 被举报对象名称（用户名称等）

    private final String[] reportReasons = new String[]{
            "垃圾信息", "色情低俗", "违法违规", "欺诈", "侵权", "其他"
    };

    private ChipGroup reportTagGroup;
    private EditText contentInput;
    private LinearLayout addImageLayout;
    private RecyclerView imageGrid;
    private List<Uri> selectedImages;
    private ImageGridAdapter imageAdapter;
    private String selectedReason;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int MAX_IMAGES = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        View root = findViewById(R.id.report_root);
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
        reportTagGroup = findViewById(R.id.report_tag_group);
        contentInput = findViewById(R.id.input_content);
        addImageLayout = findViewById(R.id.btn_add_image);
        imageGrid = findViewById(R.id.image_grid);
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        TextView submit = findViewById(R.id.btn_submit);
        TextView submitBottom = findViewById(R.id.btn_submit_bottom);

        back.setOnClickListener(v -> finish());

        View.OnClickListener submitAction = v -> handleSubmit();
        submit.setOnClickListener(submitAction);
        submitBottom.setOnClickListener(submitAction);

        // 添加图片按钮点击事件
        addImageLayout.setOnClickListener(v -> pickImage());
    }

    private void setupChips() {
        if (reportTagGroup == null) {
            return;
        }
        reportTagGroup.setSingleSelection(true);
        reportTagGroup.setSelectionRequired(false);

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

        for (String reason : reportReasons) {
            Chip chip = new Chip(this);
            chip.setText(reason);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColor(bgColors);
            chip.setTextColor(textColors);
            chip.setChipStrokeColor(ColorStateList.valueOf(strokeColor));
            chip.setChipStrokeWidth(1f);
            chip.setRippleColor(ColorStateList.valueOf(rippleColor));
            chip.setCheckedIconVisible(false);
            chip.setPadding(chipPadding, 0, chipPadding, 0);
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    selectedReason = chip.getText().toString();
                }
            });
            reportTagGroup.addView(chip);
        }

        // 监听选择变化
        reportTagGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.size() > 0) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                if (selectedChip != null) {
                    selectedReason = selectedChip.getText().toString();
                }
            } else {
                selectedReason = null;
            }
        });
    }

    private void handleSubmit() {
        // 验证必填字段
        if (TextUtils.isEmpty(selectedReason)) {
            Toast.makeText(this, "请选择举报原因", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = contentInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请填写详细说明", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = getIntent().getStringExtra(EXTRA_TYPE);
        String targetId = getIntent().getStringExtra(EXTRA_TARGET_ID);
        String targetName = getIntent().getStringExtra(EXTRA_TARGET_NAME);

        // 这里后续可以接入SSM和数据库
        // 示例：提交举报数据到后端
        // submitReport(type, targetId, targetName, selectedReason, content, selectedImages);

        StringBuilder message = new StringBuilder("举报已提交：\n");
        message.append("类型：").append(getTypeText(type)).append("\n");
        if (!TextUtils.isEmpty(targetName)) {
            message.append("对象：").append(targetName).append("\n");
        }
        message.append("原因：").append(selectedReason).append("\n");
        message.append("说明：").append(content).append("\n");
        message.append("图片数量：").append(selectedImages.size()).append("\n");
        message.append("后续将接入后端/数据库处理");

        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
        finish();
    }

    private String getTypeText(String type) {
        if ("post".equals(type)) {
            return "动态";
        } else if ("comment".equals(type)) {
            return "评论";
        } else if ("user".equals(type)) {
            return "用户";
        }
        return "未知";
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

