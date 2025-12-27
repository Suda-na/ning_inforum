package com.example.lnforum.activity;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class PublishErrandActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText amountInput;
    private EditText remarkInput;
    private EditText startPointInput;
    private EditText endPointInput;
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
        setContentView(R.layout.activity_publish_errand);

        // 检查登录状态
        sessionManager = CSessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        View root = findViewById(R.id.publish_errand_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, top, 0, bottom);
            return insets;
        });

        selectedImages = new ArrayList<>();
        initViews();
        setupActions();
    }

    private void initViews() {
        titleInput = findViewById(R.id.input_title);
        descriptionInput = findViewById(R.id.input_description);
        amountInput = findViewById(R.id.input_amount);
        remarkInput = findViewById(R.id.input_remark);
        startPointInput = findViewById(R.id.input_start_point);
        endPointInput = findViewById(R.id.input_end_point);
        addImageLayout = findViewById(R.id.btn_add_image);
        imageGrid = findViewById(R.id.image_grid);
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        TextView publish = findViewById(R.id.btn_publish);
        TextView publishBottom = findViewById(R.id.btn_publish_bottom);

        back.setOnClickListener(v -> finish());

        View.OnClickListener publishAction = v -> handlePublish();
        publish.setOnClickListener(publishAction);
        publishBottom.setOnClickListener(publishAction);

        // 添加图片按钮点击事件
        addImageLayout.setOnClickListener(v -> pickImage());
    }

    private void handlePublish() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String amount = amountInput.getText().toString().trim();
        String remark = remarkInput.getText().toString().trim();
        String startPoint = (startPointInput != null) ? startPointInput.getText().toString().trim() : null;
        String endPoint = (endPointInput != null) ? endPointInput.getText().toString().trim() : null;

        // 验证必填字段
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "请输入订单描述", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证金额（可选，如果填写了需要是有效数字）
        if (!TextUtils.isEmpty(amount)) {
            try {
                double amountValue = Double.parseDouble(amount);
                if (amountValue < 0) {
                    Toast.makeText(this, "金额不能为负数", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的金额", Toast.LENGTH_SHORT).show();
                return;
            }
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
        }
        
        // 创建final变量供lambda使用
        final String finalTitle = title;
        final String finalDescription = description;
        final String finalAmount = amount;
        final String finalRemark = remark;
        final String finalStartPoint = startPoint;
        final String finalEndPoint = endPoint;
        final String finalImage1 = image1;
        final String finalImage2 = image2;
        final String finalImage3 = image3;
        final Integer finalUserId = currentUser.getUserId();
        
        // 显示加载提示
        Toast.makeText(this, "发布中...", Toast.LENGTH_SHORT).show();
        
        // 异步发布
        new Thread(() -> {
            boolean success = WCircleRepository.publishErrand(
                finalUserId, finalTitle, finalDescription, finalAmount, finalRemark, 
                finalStartPoint, finalEndPoint, finalImage1, finalImage2, finalImage3);
            
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

