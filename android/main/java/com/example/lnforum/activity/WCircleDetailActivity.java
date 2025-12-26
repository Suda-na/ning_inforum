package com.example.lnforum.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.adapter.WCircleCommentAdapter;
import com.example.lnforum.R;
import com.example.lnforum.model.WCircleComment;
import com.example.lnforum.model.WCirclePost;
import com.example.lnforum.repository.WCircleRepository;
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.model.WUser;

import java.util.ArrayList;
import java.util.List;

public class WCircleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    private WCirclePost post;
    private TextView title, content, author, time, tag, views, comments, likes;
    private ImageView backBtn, likeIcon, favoriteIcon;
    private RecyclerView commentsList;
    private WCircleCommentAdapter commentAdapter;
    private final List<WCircleComment> commentData = new ArrayList<>();
    private EditText commentInput;
    private WSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_detail);

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        android.util.Log.d("WCircleDetailActivity", "========== WCircleDetailActivity onCreate ==========");
        android.util.Log.d("WCircleDetailActivity", "postId from intent: " + postId);
        
        sessionManager = WSessionManager.getInstance(this);

        initViews();
        initActions();
        loadPost(postId);
    }

    private void initViews() {
        android.util.Log.d("WCircleDetailActivity", "========== 开始初始化视图 ==========");
        
        backBtn = findViewById(R.id.detail_back);
        title = findViewById(R.id.detail_title);
        content = findViewById(R.id.detail_content);
        author = findViewById(R.id.detail_author);
        time = findViewById(R.id.detail_time);
        tag = findViewById(R.id.detail_tag);
        views = findViewById(R.id.detail_views);
        comments = findViewById(R.id.detail_comments);
        likes = findViewById(R.id.detail_likes);
        likeIcon = findViewById(R.id.detail_like_icon);
        favoriteIcon = findViewById(R.id.detail_watch);
        commentsList = findViewById(R.id.detail_comments_list);
        commentInput = findViewById(R.id.detail_comment_input);
        
        android.util.Log.d("WCircleDetailActivity", "视图初始化结果:");
        android.util.Log.d("WCircleDetailActivity", "  commentsList: " + (commentsList != null ? "成功" : "失败(null)"));
        android.util.Log.d("WCircleDetailActivity", "  commentInput: " + (commentInput != null ? "成功" : "失败(null)"));
        android.util.Log.d("WCircleDetailActivity", "  title: " + (title != null ? "成功" : "失败(null)"));
        android.util.Log.d("WCircleDetailActivity", "  content: " + (content != null ? "成功" : "失败(null)"));
        
        if (commentsList != null) {
            android.util.Log.d("WCircleDetailActivity", "  commentsList可见性: " + (commentsList.getVisibility() == android.view.View.VISIBLE ? "VISIBLE" : "GONE/INVISIBLE"));
        }
        
        android.util.Log.d("WCircleDetailActivity", "========== 视图初始化完成 ==========");
    }

    private void loadPost(String postId) {
        android.util.Log.d("WCircleDetailActivity", "========== 开始加载帖子 ==========");
        android.util.Log.d("WCircleDetailActivity", "postId: " + postId);
        
        new Thread(() -> {
            WUser currentUser = sessionManager.getCurrentUser();
            Integer userId = currentUser != null ? currentUser.getUserId() : null;
            android.util.Log.d("WCircleDetailActivity", "当前用户ID: " + userId);
            
            WCirclePost loadedPost = WCircleRepository.getPost(postId, userId);
            android.util.Log.d("WCircleDetailActivity", "获取到帖子: " + (loadedPost != null ? "成功" : "失败"));
            
            new Handler(Looper.getMainLooper()).post(() -> {
                if (loadedPost == null) {
                    android.util.Log.e("WCircleDetailActivity", "帖子加载失败");
                    Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                post = loadedPost;
                android.util.Log.d("WCircleDetailActivity", "帖子加载成功: id=" + post.getId() + ", title=" + post.getTitle() + ", comments=" + post.getComments());
                bindData();
                // 帖子加载完成后再初始化评论
                initComments();
            });
        }).start();
    }

    private void bindData() {
        android.util.Log.d("WCircleDetailActivity", "========== 开始绑定帖子数据 ==========");
        android.util.Log.d("WCircleDetailActivity", "post信息 - id: " + post.getId() + ", title: " + post.getTitle() + ", comments: " + post.getComments());
        
        title.setText(post.getTitle());
        content.setText(post.getContent());
        author.setText(post.getAuthor());
        time.setText(post.getTime());
        tag.setText(post.getTag());
        views.setText(String.valueOf(post.getViews()));
        comments.setText(String.valueOf(post.getComments()));
        likes.setText(String.valueOf(post.getLikes()));
        
        android.util.Log.d("WCircleDetailActivity", "帖子数据已绑定到UI - comments数量显示: " + post.getComments());

        if (post.isLiked()) {
            likeIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            likeIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        }
        
        // 设置收藏图标颜色
        if (post.isFavorited()) {
            favoriteIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            favoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        }
        
        // 加载头像
        loadAvatar();
        
        // 加载图片
        loadImages();
        android.util.Log.d("WCircleDetailActivity", "========== 帖子数据绑定完成 ==========");
    }
    
    private void loadAvatar() {
        ImageView avatarView = findViewById(R.id.detail_avatar);
        if (avatarView == null || post == null) return;
        
        // 使用ImageLoader工具类加载头像
        com.example.lnforum.utils.ImageLoader.loadAvatar(post.getAvatar(), avatarView);
    }

    private void loadImages() {
        LinearLayout imagesContainer = findViewById(R.id.detail_images_container);
        if (imagesContainer == null || post == null) return;
        
        imagesContainer.removeAllViews();
        List<String> images = post.getImages();
        if (images == null || images.isEmpty()) return;
        
        for (String imageUrl : images) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 0, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(600);
                
                // 使用ImageLoader工具类加载图片
                com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 600);
                
                imagesContainer.addView(imageView);
            }
        }
    }

    private void initComments() {
        android.util.Log.d("WCircleDetailActivity", "========== 开始初始化评论 ==========");
        android.util.Log.d("WCircleDetailActivity", "commentsList是否为null: " + (commentsList == null));
        android.util.Log.d("WCircleDetailActivity", "post是否为null: " + (post == null));
        if (post != null) {
            android.util.Log.d("WCircleDetailActivity", "postId: " + post.getId());
        }
        
        if (commentsList == null) {
            android.util.Log.e("WCircleDetailActivity", "commentsList为null，无法初始化评论列表");
            return;
        }
        
        commentsList.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new WCircleCommentAdapter(commentData, this::showCommentActions);
        commentsList.setAdapter(commentAdapter);
        
        android.util.Log.d("WCircleDetailActivity", "评论适配器已设置，当前commentData大小: " + commentData.size());
        
        if (post != null) {
            loadComments();
        } else {
            android.util.Log.e("WCircleDetailActivity", "post为null，无法加载评论");
        }
    }

    private void loadComments() {
        android.util.Log.d("WCircleDetailActivity", "========== 开始加载评论 ==========");
        android.util.Log.d("WCircleDetailActivity", "postId: " + (post != null ? post.getId() : "null"));
        android.util.Log.d("WCircleDetailActivity", "commentAdapter是否为null: " + (commentAdapter == null));
        android.util.Log.d("WCircleDetailActivity", "commentsList是否为null: " + (commentsList == null));
        
        if (post == null) {
            android.util.Log.e("WCircleDetailActivity", "post为null，无法加载评论");
            return;
        }
        
        new Thread(() -> {
            android.util.Log.d("WCircleDetailActivity", "在线程中调用WCircleRepository.getComments，postId: " + post.getId());
            List<WCircleComment> commentList = WCircleRepository.getComments(post.getId());
            android.util.Log.d("WCircleDetailActivity", "获取到评论列表，大小: " + (commentList != null ? commentList.size() : "null"));
            
            if (commentList != null && !commentList.isEmpty()) {
                for (int i = 0; i < commentList.size(); i++) {
                    WCircleComment comment = commentList.get(i);
                    android.util.Log.d("WCircleDetailActivity", "评论[" + i + "]: id=" + comment.getId() + ", author=" + comment.getAuthor() + ", content=" + comment.getContent());
                }
            }
            
            new Handler(Looper.getMainLooper()).post(() -> {
                android.util.Log.d("WCircleDetailActivity", "在主线程更新UI，更新前commentData大小: " + commentData.size());
                commentData.clear();
                commentData.addAll(commentList);
                android.util.Log.d("WCircleDetailActivity", "更新后commentData大小: " + commentData.size());
                android.util.Log.d("WCircleDetailActivity", "commentAdapter是否为null: " + (commentAdapter == null));
                
                if (commentAdapter != null) {
                    commentAdapter.notifyDataSetChanged();
                    android.util.Log.d("WCircleDetailActivity", "已调用notifyDataSetChanged()");
                    android.util.Log.d("WCircleDetailActivity", "commentsList.getAdapter()是否为null: " + (commentsList.getAdapter() == null));
                    android.util.Log.d("WCircleDetailActivity", "commentsList可见性: " + (commentsList.getVisibility() == android.view.View.VISIBLE ? "VISIBLE" : "GONE/INVISIBLE"));
                } else {
                    android.util.Log.e("WCircleDetailActivity", "commentAdapter为null，无法通知数据更新");
                }
                android.util.Log.d("WCircleDetailActivity", "========== 评论加载完成 ==========");
            });
        }).start();
    }

    private void initActions() {
        backBtn.setOnClickListener(v -> finish());

        likeIcon.setOnClickListener(v -> {
            WUser currentUser = sessionManager.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer userId = currentUser.getUserId();
            
            new Thread(() -> {
                boolean success = WCircleRepository.toggleLike(post.getId(), userId);
                if (success) {
                    WCirclePost updatedPost = WCircleRepository.getPost(post.getId(), userId);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (updatedPost != null) {
                            post = updatedPost;
                            bindData();
                        }
                    });
                }
            }).start();
        });

        favoriteIcon.setOnClickListener(v -> {
            WUser currentUser = sessionManager.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer userId = currentUser.getUserId();
            
            new Thread(() -> {
                boolean success = WCircleRepository.toggleFavorite(post.getId(), userId);
                if (success) {
                    WCirclePost updatedPost = WCircleRepository.getPost(post.getId(), userId);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (updatedPost != null) {
                            post = updatedPost;
                            bindData();
                        }
                    });
                }
            }).start();
        });

        findViewById(R.id.detail_send_comment).setOnClickListener(v -> sendComment());
    }

    private void sendComment() {
        String text = commentInput.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        WUser currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer userId = currentUser.getUserId();

        new Thread(() -> {
            boolean success = WCircleRepository.addComment(post.getId(), userId, text);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (success) {
                    commentInput.setText("");
                    Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
                    loadComments();
                } else {
                    Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showCommentActions(WCircleComment comment, View anchor) {
        // 显示评论操作菜单
    }
}

