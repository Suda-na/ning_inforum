package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.adapter.BlacklistAdapter;
import com.example.lnforum.R;
import com.example.lnforum.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的黑名单页面，展示被屏蔽的用户列表。
 */
public class BlacklistActivity extends AppCompatActivity {

    private final List<String> blacklist = new ArrayList<>();
    private BlacklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        View root = findViewById(R.id.blacklist_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        ImageView backButton = findViewById(R.id.back_button);
        RecyclerView recyclerView = findViewById(R.id.blacklist_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blacklist.addAll(MessageRepository.getBlacklist(this));
        adapter = new BlacklistAdapter(blacklist, this::removeUser);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }

    private void removeUser(String username) {
        MessageRepository.removeFromBlacklist(this, username);
        refresh();
    }

    private void refresh() {
        blacklist.clear();
        blacklist.addAll(MessageRepository.getBlacklist(this));
        adapter.notifyDataSetChanged();
    }
}

