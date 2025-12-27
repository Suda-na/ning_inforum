package com.example.lnforum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.lnforum.R;

public class ErrandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESC = "extra_desc";
    public static final String EXTRA_FROM = "extra_from";
    public static final String EXTRA_TO = "extra_to";
    public static final String EXTRA_PRICE = "extra_price";
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_IS_RUNNER = "extra_is_runner";
    public static final String EXTRA_PUBLISHER = "extra_publisher";

    private TextView statusTitle;
    private TextView statusSub;
    private TextView fromView;
    private TextView toView;
    private TextView titleView;
    private TextView descView;
    private TextView priceView;
    private TextView followBtn;
    private TextView contactPublisherBtn;
    private TextView contactAdminBtn;
    private View runnerLayout;
    private View adminLayout;

    private boolean isRunner = false;
    private boolean followed = false;
    private String status;
    private String publisher;
    private String orderTitleForChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand_detail);

        View root = findViewById(R.id.errand_detail_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        bindViews();
        loadDataFromIntent();
        setupActions();
        render();
    }

    private void bindViews() {
        statusTitle = findViewById(R.id.status_title);
        statusSub = findViewById(R.id.status_sub);
        fromView = findViewById(R.id.detail_from);
        toView = findViewById(R.id.detail_to);
        titleView = findViewById(R.id.detail_title);
        descView = findViewById(R.id.detail_desc);
        priceView = findViewById(R.id.detail_price);
        followBtn = findViewById(R.id.btn_follow);
        contactPublisherBtn = findViewById(R.id.btn_contact_publisher);
        contactAdminBtn = findViewById(R.id.btn_contact_admin);
        runnerLayout = findViewById(R.id.layout_runner_actions);
        adminLayout = findViewById(R.id.layout_admin_action);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        String tag = intent.getStringExtra(EXTRA_TAG);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String desc = intent.getStringExtra(EXTRA_DESC);
        String from = intent.getStringExtra(EXTRA_FROM);
        String to = intent.getStringExtra(EXTRA_TO);
        String price = intent.getStringExtra(EXTRA_PRICE);
        status = intent.getStringExtra(EXTRA_STATUS);
        publisher = intent.getStringExtra(EXTRA_PUBLISHER);
        isRunner = intent.getBooleanExtra(EXTRA_IS_RUNNER, false);

        orderTitleForChat = TextUtils.isEmpty(title) ? "跑腿任务" : title;
        titleView.setText(orderTitleForChat);
        descView.setText(TextUtils.isEmpty(desc) ? "暂无描述" : desc);
        fromView.setText(TextUtils.isEmpty(from) ? "未填写起点" : from);
        toView.setText(TextUtils.isEmpty(to) ? "未填写终点" : to);
        priceView.setText("￥" + (TextUtils.isEmpty(price) ? "0.00" : price));

        if (TextUtils.isEmpty(publisher)) {
            publisher = "发布者";
        }
    }

    private void setupActions() {
        ImageView back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> finish());

        contactAdminBtn.setOnClickListener(v -> openChatWithAdmin());
        contactPublisherBtn.setOnClickListener(v -> openChatWithPublisher());
        followBtn.setOnClickListener(v -> toggleFollow());
    }

    private void render() {
        // 状态文案
        if ("waiting".equals(status)) {
            statusTitle.setText("等待接单");
            statusSub.setText("及时联系发布者，抢单更快");
        } else if ("delivering".equals(status)) {
            statusTitle.setText("配送中");
            statusSub.setText("请保持沟通，及时送达");
        } else {
            statusTitle.setText("订单完成");
            statusSub.setText("订单已完成，请看看其他任务吧");
        }

        // 底部操作
        if (isRunner) {
            runnerLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.GONE);
            refreshFollowButton();
        } else {
            runnerLayout.setVisibility(View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        }
    }

    private void openChatWithAdmin() {
        Intent chat = new Intent(this, ChatActivity.class);
        chat.putExtra(ChatActivity.EXTRA_TITLE, "管理员");
        chat.putExtra(ChatActivity.EXTRA_TYPE, "admin");
        chat.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "admin_support");
        startActivity(chat);
    }

    private void openChatWithPublisher() {
        Intent chat = new Intent(this, ChatActivity.class);
        chat.putExtra(ChatActivity.EXTRA_TITLE, publisher);
        chat.putExtra(ChatActivity.EXTRA_TYPE, "user");
        chat.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, "errand_" + orderTitleForChat.hashCode());
        startActivity(chat);
    }

    private void toggleFollow() {
        followed = !followed;
        refreshFollowButton();
        Toast.makeText(this, followed ? "已关注发布者" : "已取消关注", Toast.LENGTH_SHORT).show();
    }

    private void refreshFollowButton() {
        followBtn.setText(followed ? "已关注" : "关注发布者");
        int color = ContextCompat.getColor(this, followed ? R.color.primary_blue_light : R.color.text_primary);
        followBtn.setTextColor(color);
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}

