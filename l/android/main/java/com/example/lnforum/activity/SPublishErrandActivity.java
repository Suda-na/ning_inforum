package com.example.lnforum.activity;

import android.content.Intent;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布跑腿Activity（S开头，调用SPostController）
 */
public class SPublishErrandActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText amountInput;
    private EditText remarkInput;
    private LinearLayout addImageLayout;
    private RecyclerView imageGrid;
    private List<Uri> selectedImages;
    private ImageGridAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int MAX_IMAGES = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_errand);

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
            jsonData.put("description", description);
            if (!TextUtils.isEmpty(amount)) {
                jsonData.put("amount", amount);
            }
            if (!TextUtils.isEmpty(remark)) {
                jsonData.put("remark", remark);
            }
            
            // 添加图片信息
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
            HttpUtil.post("/api/android/post/publish/errand", jsonData, new HttpUtil.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject result = new JSONObject(response);
                            boolean success = result.optBoolean("success", false);
                            String message = result.optString("message", "");
                            
                            if (success) {
                                Toast.makeText(SPublishErrandActivity.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(SPublishErrandActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SPublishErrandActivity.this, "解析响应失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(SPublishErrandActivity.this, "发布失败: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "构建请求数据失败", Toast.LENGTH_SHORT).show();
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

