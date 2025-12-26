package com.example.android_java2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.adapter.ChatAdapter;
import com.example.android_java2.R;
import com.example.android_java2.model.ChatMessage;
import com.example.android_java2.model.CUser;
import com.example.android_java2.repository.CSessionManager;
import com.example.android_java2.repository.LMessageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 单聊页面，支持互关、粉丝、管理员。
 */
public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_CONVERSATION_ID = "extra_conversation_id";

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private String conversationId;
    private String title;
    private String type;
    private Integer otherUserId;
    private EditText inputEditText;
    private ImageButton sendButton;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        View root = findViewById(R.id.chat_root);
        applyLightStatusBar(root);
        // 避免遮挡状态栏时间
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        type = getIntent().getStringExtra(EXTRA_TYPE);
        
        // 从conversationId中提取otherUserId
        otherUserId = LMessageRepository.extractOtherUserId(conversationId);

        initHeader();
        initList();
        initInputArea();
        loadMessages();
    }

    private void initHeader() {
        TextView titleView = findViewById(R.id.chat_title);
        ImageView back = findViewById(R.id.chat_back);
        TextView blockBtn = findViewById(R.id.chat_block_btn);

        titleView.setText(TextUtils.isEmpty(title) ? "聊天" : title);
        back.setOnClickListener(v -> finish());

        if ("admin".equals(type)) {
            blockBtn.setVisibility(View.GONE);
        } else {
            blockBtn.setVisibility(View.VISIBLE);
            blockBtn.setOnClickListener(v -> toggleBlock(blockBtn));
            refreshBlockButton(blockBtn);
        }
    }

    private void toggleBlock(TextView blockBtn) {
        if (TextUtils.isEmpty(title)) return;
        if (LMessageRepository.isBlacklisted(this, title)) {
            LMessageRepository.removeFromBlacklist(this, title);
            Toast.makeText(this, "已将对方移出黑名单", Toast.LENGTH_SHORT).show();
        } else {
            LMessageRepository.addToBlacklist(this, title);
            Toast.makeText(this, "已拉黑，对方消息将被屏蔽", Toast.LENGTH_SHORT).show();
        }
        refreshBlockButton(blockBtn);
    }

    private void refreshBlockButton(TextView blockBtn) {
        boolean blocked = LMessageRepository.isBlacklisted(this, title);
        blockBtn.setText(blocked ? "移出黑名单" : "拉黑");
    }

    private void initList() {
        recyclerView = findViewById(R.id.chat_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);
    }

    private void initInputArea() {
        inputEditText = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.chat_send);
        imageButton = findViewById(R.id.chat_image_btn);
        
        sendButton.setOnClickListener(v -> sendTextMessage());
        
        if (imageButton != null) {
            imageButton.setOnClickListener(v -> pickImage());
        }
    }
    
    private void sendTextMessage() {
        String text = inputEditText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
        
        if (otherUserId == null) {
            Toast.makeText(this, "无法获取对方用户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 禁用发送按钮，防止重复发送
        sendButton.setEnabled(false);
        
        LMessageRepository.sendTextMessage(this, otherUserId, text, new LMessageRepository.SendMessageCallback() {
            @Override
            public void onResult(boolean success, String error) {
                runOnUiThread(() -> {
                    sendButton.setEnabled(true);
                    if (success) {
                        inputEditText.setText("");
                        // 重新加载消息列表
                        loadMessages();
                    } else {
                        Toast.makeText(ChatActivity.this, error != null ? error : "发送失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                sendImageMessage(imageUri);
            }
        }
    }
    
    private void sendImageMessage(Uri imageUri) {
        if (otherUserId == null) {
            Toast.makeText(this, "无法获取对方用户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示上传进度
        Toast.makeText(this, "正在上传图片...", Toast.LENGTH_SHORT).show();
        
        // 将Uri转换为File
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "无法读取图片", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 创建临时文件
            File tempFile = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            
            // 上传图片
            LMessageRepository.uploadImage(this, tempFile, new LMessageRepository.UploadImageCallback() {
                @Override
                public void onResult(String imageUrl, String error) {
                    runOnUiThread(() -> {
                        // 删除临时文件
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                        
                        if (imageUrl != null) {
                            // 发送图片消息
                            LMessageRepository.sendImageMessage(ChatActivity.this, otherUserId, imageUrl, "", 
                                    new LMessageRepository.SendMessageCallback() {
                                @Override
                                public void onResult(boolean success, String error) {
                                    runOnUiThread(() -> {
                                        if (success) {
                                            // 重新加载消息列表
                                            loadMessages();
                                        } else {
                                            Toast.makeText(ChatActivity.this, error != null ? error : "发送失败", 
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ChatActivity.this, error != null ? error : "上传失败", 
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "处理图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 统一主页浅色状态栏样式，保证图标/时间可见。
     */
    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }

    private void loadMessages() {
        if (otherUserId == null) {
            // 如果无法获取otherUserId，可能是管理员入口或其他情况
            messages.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        
        LMessageRepository.getChatMessages(this, otherUserId, new LMessageRepository.ChatMessageCallback() {
            @Override
            public void onResult(List<ChatMessage> chatMessages, String error) {
                runOnUiThread(() -> {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
        }
                    
        messages.clear();
                    if (chatMessages != null) {
                        messages.addAll(chatMessages);
                    }
        adapter.notifyDataSetChanged();
                    
                    // 滚动到底部
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
                });
            }
            
            @Override
            public void onOtherUserAvatar(String avatarUrl) {
                runOnUiThread(() -> {
                    adapter.setOtherUserAvatar(avatarUrl);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 每次显示时刷新消息列表
        loadMessages();
    }
}

