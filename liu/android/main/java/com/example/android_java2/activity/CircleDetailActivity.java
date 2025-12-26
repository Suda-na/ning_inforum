package com.example.android_java2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.adapter.CircleCommentAdapter;
import com.example.android_java2.R;
import com.example.android_java2.model.CircleComment;
import com.example.android_java2.model.CirclePost;
import com.example.android_java2.repository.CircleRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CircleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    private CirclePost post;
    private TextView title, content, author, time, tag, views, comments, likes, followBtn;
    private ImageView likeIcon, watchBtn;
    private RecyclerView commentsList;
    private CircleCommentAdapter commentAdapter;
    private final List<CircleComment> commentData = new ArrayList<>();
    private android.widget.LinearLayout imagesContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_detail);

        View root = findViewById(R.id.circle_detail_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        post = CircleRepository.getPost(postId);
        if (post == null) {
            Toast.makeText(this, "帖子不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        post.addView();

        initViews();
        bindData();
        initComments();
        initActions();
    }

    private void initViews() {
        title = findViewById(R.id.detail_title);
        content = findViewById(R.id.detail_content);
        author = findViewById(R.id.detail_author);
        time = findViewById(R.id.detail_time);
        tag = findViewById(R.id.detail_tag);
        views = findViewById(R.id.detail_views);
        comments = findViewById(R.id.detail_comments);
        likes = findViewById(R.id.detail_likes);
        followBtn = findViewById(R.id.detail_follow);
        likeIcon = findViewById(R.id.detail_like_icon);
        watchBtn = findViewById(R.id.detail_watch);
        commentsList = findViewById(R.id.detail_comments_list);
        imagesContainer = findViewById(R.id.detail_images_container);
    }

    private void bindData() {
        title.setText(post.getTitle());
        content.setText(post.getContent());
        author.setText(post.getAuthor());
        time.setText(post.getTime());
        tag.setText(post.getTag());
        views.setText(String.valueOf(post.getViews()));
        comments.setText(String.valueOf(post.getComments()));
        likes.setText(String.valueOf(post.getLikes()));
        refreshLike();
        refreshWatch();
        refreshFollow();
        
        // 加载图片
        loadImages(post.getImages());
    }

    private void initComments() {
        commentData.clear();
        commentData.addAll(CircleRepository.getComments(post.getId()));
        commentsList.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CircleCommentAdapter(commentData, this::showCommentActions);
        commentsList.setAdapter(commentAdapter);
    }

    private void initActions() {
        findViewById(R.id.detail_back).setOnClickListener(v -> finish());
        findViewById(R.id.detail_more).setOnClickListener(v -> showMoreActions());
        findViewById(R.id.detail_user_area).setOnClickListener(v -> openUserProfile());
        likeIcon.setOnClickListener(v -> {
            post.toggleLike();
            likes.setText(String.valueOf(post.getLikes()));
            refreshLike();
        });
        watchBtn.setOnClickListener(v -> {
            post.toggleWatch();
            refreshWatch();
        });
        followBtn.setOnClickListener(v -> {
            post.toggleFollow();
            refreshFollow();
            Toast.makeText(this, post.isFollowed() ? "已关注" : "已取消关注", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.detail_send_comment).setOnClickListener(v -> sendComment());
    }

    private void refreshLike() {
        int color = post.isLiked() ? ContextCompat.getColor(this, R.color.primary_blue)
                : ContextCompat.getColor(this, R.color.text_secondary);
        likeIcon.setColorFilter(color);
        likes.setTextColor(color);
    }

    private void refreshWatch() {
        int color = post.isWatched() ? ContextCompat.getColor(this, R.color.primary_blue)
                : ContextCompat.getColor(this, R.color.text_secondary);
        watchBtn.setColorFilter(color);
    }

    private void refreshFollow() {
        if (post.isFollowed()) {
            followBtn.setText("已关注");
            followBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            followBtn.setText("关注");
            followBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.round_button_bg));
        }
    }

    private void loadImages(List<String> imageUrls) {
        imagesContainer.removeAllViews();
        if (imageUrls == null || imageUrls.isEmpty()) {
            imagesContainer.setVisibility(View.GONE);
            return;
        }
        imagesContainer.setVisibility(View.VISIBLE);

        for (String url : imageUrls) {
            ImageView imageView = new ImageView(this);
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(200)
            );
            params.setMargins(0, dpToPx(8), 0, 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
            // 这里后续可以接入图片加载库如Glide
            // Glide.with(this).load(url).into(imageView);
            imagesContainer.addView(imageView);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void sendComment() {
        EditText input = findViewById(R.id.detail_comment_input);
        String text = input.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        CircleRepository.addComment(post.getId(), "我", text);
        commentData.clear();
        commentData.addAll(CircleRepository.getComments(post.getId()));
        comments.setText(String.valueOf(commentData.size()));
        commentAdapter.notifyDataSetChanged();
        input.setText("");
        Toast.makeText(this, "已发布", Toast.LENGTH_SHORT).show();
    }

    private void showMoreActions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_menu_more, null);
        TextView report = view.findViewById(R.id.menu_report);
        TextView cancel = view.findViewById(R.id.menu_cancel);
        report.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(ReportActivity.EXTRA_TYPE, "post");
            intent.putExtra(ReportActivity.EXTRA_TARGET_ID, post.getId());
            intent.putExtra(ReportActivity.EXTRA_TARGET_NAME, post.getAuthor());
            startActivity(intent);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }

    private void openUserProfile() {
        android.content.Intent intent = new android.content.Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, post.getAuthor());
        startActivity(intent);
    }

    private void showCommentActions(CircleComment comment, View anchor) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_menu_comment, null);
        view.findViewById(R.id.action_copy).setOnClickListener(v -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("comment", comment.getContent()));
            Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        view.findViewById(R.id.action_reply).setOnClickListener(v -> {
            EditText input = findViewById(R.id.detail_comment_input);
            input.setText("回复 " + comment.getAuthor() + "：");
            input.setSelection(input.getText().length());
            dialog.dismiss();
        });
        view.findViewById(R.id.action_report).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(ReportActivity.EXTRA_TYPE, "comment");
            intent.putExtra(ReportActivity.EXTRA_TARGET_ID, comment.getId());
            intent.putExtra(ReportActivity.EXTRA_TARGET_NAME, comment.getAuthor());
            startActivity(intent);
        });
        view.findViewById(R.id.comment_menu_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}

