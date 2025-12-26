package com.example.lnforum.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lnforum.adapter.ChatAdapter;
import com.example.lnforum.R;
import com.example.lnforum.model.ChatMessage;
import com.example.lnforum.model.CUser;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.repository.LMessageRepository;
import com.example.lnforum.utils.UriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 单聊页面，支持互关、粉丝、管理员。
 */
public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_CONVERSATION_ID = "extra_conversation_id";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private String conversationId;
    private String title;
    private String type;
    private Integer otherUserId;
    private CSessionManager sessionManager;
    private ActivityResultLauncher<String> pickImageLauncher;
    private String otherUserAvatar;

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
        
        // 从conversationId提取otherUserId
        otherUserId = LMessageRepository.extractOtherUserId(conversationId);
        
        sessionManager = CSessionManager.getInstance(this);
        
        // 初始化图片选择器
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    sendImage(uri);
                }
            }
        );

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
        EditText input = findViewById(R.id.chat_input);
        ImageButton send = findViewById(R.id.chat_send);
        ImageButton imageBtn = findViewById(R.id.chat_image_btn);
        
        // 发送文本消息
        send.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
            if (otherUserId == null) {
                Toast.makeText(this, "无法获取对方用户信息", Toast.LENGTH_SHORT).show();
                return;
            }
            sendTextMessage(text);
            input.setText("");
        });
        
        // 选择图片
        if (imageBtn != null) {
            imageBtn.setOnClickListener(v -> {
                if (otherUserId == null) {
                    Toast.makeText(this, "无法获取对方用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickImageLauncher.launch("image/*");
            });
        }
    }
    
    private void sendTextMessage(String text) {
        LMessageRepository.sendTextMessage(this, otherUserId, text, new LMessageRepository.SendMessageCallback() {
            @Override
            public void onResult(boolean success, String error) {
                runOnUiThread(() -> {
                    if (success) {
                        // 发送成功，重新加载消息
                        loadMessages();
                    } else {
                        Toast.makeText(ChatActivity.this, "发送失败: " + (error != null ? error : "未知错误"), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    private void sendImage(Uri uri) {
        // 先上传图片
        File file = UriUtils.getFileFromUri(this, uri);
        if (file == null) {
            Toast.makeText(this, "图片读取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "正在上传图片...", Toast.LENGTH_SHORT).show();
        
        LMessageRepository.uploadImage(this, file, new LMessageRepository.UploadImageCallback() {
            @Override
            public void onResult(String imageUrl, String error) {
                runOnUiThread(() -> {
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // 上传成功，发送图片消息
                        LMessageRepository.sendImageMessage(ChatActivity.this, otherUserId, imageUrl, "", 
                            new LMessageRepository.SendMessageCallback() {
                                @Override
                                public void onResult(boolean success, String error) {
                                    runOnUiThread(() -> {
                                        if (success) {
                                            loadMessages();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "发送失败: " + (error != null ? error : "未知错误"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                    } else {
                        Toast.makeText(ChatActivity.this, "上传失败: " + (error != null ? error : "未知错误"), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
            Toast.makeText(this, "无法获取对方用户信息", Toast.LENGTH_SHORT).show();
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
                    if (!messages.isEmpty()) {
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                });
            }
            
            @Override
            public void onOtherUserAvatar(String avatarUrl) {
                runOnUiThread(() -> {
                    otherUserAvatar = avatarUrl;
                    if (adapter != null) {
                        adapter.setOtherUserAvatar(avatarUrl);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}

