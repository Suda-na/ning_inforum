package com.example.lnforum.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.lnforum.R;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.repository.WCircleRepository;
import com.example.lnforum.activity.WLoginActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class PublishLostFoundActivity extends AppCompatActivity {

    private final String[] tagOptions = new String[]{"失物", "招领"};

    private EditText titleInput;
    private EditText descInput;
    private EditText contactInput;
    private EditText locationInput;
    private ChipGroup tagGroup;
    private TextView selectedTagView;
    private CSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_lost_found);

        // 检查登录状态
        sessionManager = CSessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        View root = findViewById(R.id.publish_lost_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });

        initViews();
        setupChips();
        setupActions();
    }

    private void initViews() {
        titleInput = findViewById(R.id.input_title);
        descInput = findViewById(R.id.input_desc);
        contactInput = findViewById(R.id.input_contact);
        locationInput = findViewById(R.id.input_location);
        tagGroup = findViewById(R.id.tag_group);
        selectedTagView = findViewById(R.id.selected_tag);
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        TextView publish = findViewById(R.id.btn_publish);
        TextView publishBottom = findViewById(R.id.btn_publish_bottom);

        View.OnClickListener publishAction = v -> handlePublish();
        publish.setOnClickListener(publishAction);
        publishBottom.setOnClickListener(publishAction);

        back.setOnClickListener(v -> finish());
    }

    private void handlePublish() {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String tag = selectedTagView.getText().toString();
        String contactInfo = contactInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

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
        
        // 检查登录状态
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // 创建final变量供lambda使用
        final String finalTitle = title;
        final String finalDesc = desc;
        final String finalTag = tag;
        final String finalContactInfo = contactInfo;
        final String finalLocation = location;
        final Integer finalUserId = currentUser.getUserId();
        
        // 显示加载提示
        Toast.makeText(this, "发布中...", Toast.LENGTH_SHORT).show();
        
        // 异步发布（图片暂时传null，后续可以扩展）
        new Thread(() -> {
            boolean success = WCircleRepository.publishLostFound(
                finalUserId, finalTitle, finalDesc, finalTag, finalContactInfo, finalLocation, null, null, null);
            
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

