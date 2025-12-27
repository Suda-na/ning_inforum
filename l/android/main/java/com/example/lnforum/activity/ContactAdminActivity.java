package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.lnforum.R;

/**
 * 联系管理员页面，提供联系方式和简单说明。
 */
public class ContactAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_admin);

        View root = findViewById(R.id.contact_admin_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        ImageView backButton = findViewById(R.id.back_button);
        Button startChat = findViewById(R.id.start_admin_chat_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startChat.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_TITLE, "管理员");
            intent.putExtra(ChatActivity.EXTRA_TYPE, "admin");
            intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "c_admin");
            startActivity(intent);
        });
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}

