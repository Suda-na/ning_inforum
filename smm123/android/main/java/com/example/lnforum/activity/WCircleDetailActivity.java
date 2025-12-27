package com.example.lnforum.activity;

import android.content.Intent;
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
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.model.CUser;

import java.util.ArrayList;
import java.util.List;

public class WCircleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    private WCirclePost post;
    private TextView title, content, author, time, tag, views, comments, likes, followBtn;
    private ImageView backBtn, likeIcon, favoriteIcon, moreBtn;
    private RecyclerView commentsList;
    private WCircleCommentAdapter commentAdapter;
    private final List<WCircleComment> commentData = new ArrayList<>();
    private EditText commentInput;
    private CSessionManager sessionManager;
    private Integer authorId; // 帖子作者ID
    private WCircleComment replyToComment; // 当前要回复的评论，null表示普通评论
    private TextView replyHint; // 回复提示文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_detail);

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        android.util.Log.d("WCircleDetailActivity", "========== WCircleDetailActivity onCreate ==========");
        android.util.Log.d("WCircleDetailActivity", "postId from intent: " + postId);
        
        sessionManager = CSessionManager.getInstance(this);

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
        followBtn = findViewById(R.id.detail_follow);
        moreBtn = findViewById(R.id.detail_more);
        replyHint = findViewById(R.id.reply_hint);
        
        // 初始化回复提示容器
        View replyHintContainer = findViewById(R.id.reply_hint_container);
        if (replyHintContainer != null) {
            findViewById(R.id.btn_cancel_reply).setOnClickListener(v -> {
                replyToComment = null;
                updateReplyHint();
            });
        }
        
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
            CUser currentUser = sessionManager.getCurrentCUser();
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
                authorId = post.getAuthorId();
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
        
        // 设置关注按钮状态
        updateFollowButton();
        
        // 加载头像
        loadAvatar();
        
        // 加载图片
        loadImages();
        android.util.Log.d("WCircleDetailActivity", "========== 帖子数据绑定完成 ==========");
    }
    
    private void updateFollowButton() {
        if (followBtn == null || post == null) return;
        
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null || !sessionManager.isLoggedIn() || authorId == null || authorId.equals(currentUser.getUserId())) {
            // 未登录或是自己的帖子，隐藏关注按钮
            followBtn.setVisibility(View.GONE);
            return;
        }
        
        followBtn.setVisibility(View.VISIBLE);
        if (post.isFollowed()) {
            followBtn.setText("已关注");
            followBtn.setBackgroundResource(R.drawable.edit_text_bg);
            followBtn.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            followBtn.setText("关注");
            followBtn.setBackgroundResource(R.drawable.round_button_bg);
            followBtn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
    }
    
    private void loadAvatar() {
        ImageView avatarView = findViewById(R.id.detail_avatar);
        if (avatarView == null || post == null) return;
        
        // 使用ImageLoader工具类加载头像
        com.example.lnforum.utils.ImageLoader.loadAvatar(post.getAvatar(), avatarView);
        
        // 头像点击事件：跳转到用户主页
        avatarView.setOnClickListener(v -> {
            if (post != null && post.getAuthor() != null && !post.getAuthor().isEmpty()) {
                Intent intent = new Intent(this, com.example.lnforum.activity.UserProfileActivity.class);
                intent.putExtra(com.example.lnforum.activity.UserProfileActivity.EXTRA_USER_NAME, post.getAuthor());
                startActivity(intent);
            }
        });
    }

    private void loadImages() {
        LinearLayout imagesContainer = findViewById(R.id.detail_images_container);
        if (imagesContainer == null || post == null) return;
        
        imagesContainer.removeAllViews();
        List<String> images = post.getImages();
        if (images == null || images.isEmpty()) return;
        
        int imageCount = Math.min(images.size(), 3); // 最多3张
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (12 * getResources().getDisplayMetrics().density); // 12dp转px
        int availableWidth = screenWidth - padding * 2;
        
        if (imageCount == 1) {
            // 单张图片：全宽显示
            String imageUrl = images.get(0);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 12, 0, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(800);
                com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl, imageView, 800);
                imagesContainer.addView(imageView);
            }
        } else if (imageCount == 2) {
            // 两张图片：并排显示，各占一半
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 12, 0, 0);
            rowLayout.setLayoutParams(rowParams);
            
            int imageWidth = (availableWidth - 8) / 2; // 减去间距
            for (int i = 0; i < 2; i++) {
                String imageUrl = images.get(i);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imageWidth,
                        imageWidth);
                    if (i == 0) {
                        params.setMargins(0, 0, 8, 0);
                    }
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    com.example.lnforum.utils.ImageLoader.loadImage(imageUrl, imageView);
                    rowLayout.addView(imageView);
                }
            }
            imagesContainer.addView(rowLayout);
        } else {
            // 三张图片：第一张全宽，下面两张并排
            // 第一张
            String imageUrl1 = images.get(0);
            if (imageUrl1 != null && !imageUrl1.isEmpty()) {
                ImageView imageView1 = new ImageView(this);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 12, 0, 8);
                imageView1.setLayoutParams(params1);
                imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView1.setAdjustViewBounds(true);
                imageView1.setMaxHeight(500);
                com.example.lnforum.utils.ImageLoader.loadImageWithMaxHeight(imageUrl1, imageView1, 500);
                imagesContainer.addView(imageView1);
            }
            
            // 下面两张并排
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLayout.setLayoutParams(rowParams);
            
            int imageWidth = (availableWidth - 8) / 2;
            for (int i = 1; i < 3; i++) {
                String imageUrl = images.get(i);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imageWidth,
                        imageWidth);
                    if (i == 1) {
                        params.setMargins(0, 0, 8, 0);
                    }
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    com.example.lnforum.utils.ImageLoader.loadImage(imageUrl, imageView);
                    rowLayout.addView(imageView);
                }
            }
            imagesContainer.addView(rowLayout);
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
                    
                    // 强制 RecyclerView 重新测量，确保在 ScrollView 中正确显示
                    if (commentsList != null) {
                        commentsList.post(() -> {
                            commentsList.requestLayout();
                            commentsList.invalidate();
                        });
                    }
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
            CUser currentUser = sessionManager.getCurrentCUser();
            if (currentUser == null || !sessionManager.isLoggedIn()) {
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
            CUser currentUser = sessionManager.getCurrentCUser();
            if (currentUser == null || !sessionManager.isLoggedIn()) {
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
        
        // 关注按钮点击事件
        if (followBtn != null) {
            followBtn.setOnClickListener(v -> {
                CUser currentUser = sessionManager.getCurrentCUser();
                if (currentUser == null || !sessionManager.isLoggedIn()) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (authorId == null) {
                    Toast.makeText(this, "无法获取作者信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Integer userId = currentUser.getUserId();
                int actionType = post.isFollowed() ? 1 : 0; // 0=关注，1=取消关注
                
                new Thread(() -> {
                    boolean success = WCircleRepository.followAction(userId, authorId, actionType);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (success) {
                            post.toggleFollow();
                            updateFollowButton();
                            Toast.makeText(this, post.isFollowed() ? "关注成功" : "取消关注成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            });
        }
        
        // 三个点按钮点击事件
        if (moreBtn != null) {
            moreBtn.setOnClickListener(v -> showMoreMenu());
        }
    }
    
    private void showMoreMenu() {
        CUser currentUser = sessionManager.getCurrentCUser();
        boolean isOwner = currentUser != null && sessionManager.isLoggedIn() && authorId != null && authorId.equals(currentUser.getUserId());
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("更多操作");
        
        String[] items;
        if (isOwner) {
            items = new String[]{"删除帖子", "举报"};
        } else {
            items = new String[]{"举报"};
        }
        
        builder.setItems(items, (dialog, which) -> {
            if (isOwner && which == 0) {
                // 删除帖子
                showDeletePostDialog();
            } else {
                // 举报帖子
                reportPost();
            }
        });
        builder.show();
    }
    
    /**
     * 举报帖子
     */
    private void reportPost() {
        if (post == null) return;
        Intent intent = new Intent(this, com.example.lnforum.activity.ReportActivity.class);
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TYPE, "post");
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TARGET_ID, post.getId());
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TARGET_NAME, post.getAuthor());
        startActivity(intent);
    }
    
    private void showDeletePostDialog() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("删除帖子")
            .setMessage("确定要删除这条帖子吗？删除后无法恢复。")
            .setPositiveButton("删除", (dialog, which) -> {
                Toast.makeText(this, "删除帖子功能开发中", Toast.LENGTH_SHORT).show();
                // TODO: 实现删除帖子功能
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void sendComment() {
        String text = commentInput.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer userId = currentUser.getUserId();
        
        // 判断是回复还是普通评论
        final Integer parentId;
        final String successMessage;
        if (replyToComment != null) {
            parentId = Integer.parseInt(replyToComment.getId());
            successMessage = "回复成功";
        } else {
            parentId = null;
            successMessage = "评论成功";
        }

        new Thread(() -> {
            boolean success = WCircleRepository.addComment(post.getId(), userId, text, parentId);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (success) {
                    commentInput.setText("");
                    // 清除回复状态
                    replyToComment = null;
                    updateReplyHint();
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                    // 延迟一下再加载评论，确保后端已保存
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        loadComments();
                        // 重新加载帖子以更新评论数
                        loadPost(post.getId());
                    }, 500);
                } else {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showCommentActions(WCircleComment comment, View anchor) {
        CUser currentUser = sessionManager.getCurrentCUser();
        if (currentUser == null || !sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查是否是自己的评论
        Integer commentUserId = comment.getUserId();
        Integer currentUserId = currentUser.getUserId();
        android.util.Log.d("WCircleDetailActivity", "showCommentActions - commentUserId: " + commentUserId + ", currentUserId: " + currentUserId);
        boolean isOwner = commentUserId != null && currentUserId != null && commentUserId.equals(currentUserId);
        android.util.Log.d("WCircleDetailActivity", "showCommentActions - isOwner: " + isOwner);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("评论操作");
        
        // 根据是否是自己的评论显示不同的菜单
        if (isOwner) {
            // 自己的评论：可以删除和回复
            builder.setItems(new String[]{"删除评论", "回复"}, (dialog, which) -> {
                if (which == 0) {
                    // 删除评论
                    showDeleteCommentDialog(comment);
                } else {
                    // 回复评论
                    startReply(comment);
                }
            });
        } else {
            // 别人的评论：可以回复和举报
            builder.setItems(new String[]{"回复", "举报"}, (dialog, which) -> {
                if (which == 0) {
                    // 回复评论
                    startReply(comment);
                } else {
                    // 举报评论
                    reportComment(comment);
                }
            });
        }
        builder.show();
    }
    
    /**
     * 开始回复评论
     */
    private void startReply(WCircleComment comment) {
        replyToComment = comment;
        updateReplyHint();
        commentInput.requestFocus();
        // 滚动到底部，确保输入框可见
        View rootView = findViewById(R.id.circle_detail_root);
        if (rootView != null) {
            rootView.post(() -> {
                android.widget.ScrollView scrollView = rootView.findViewById(android.R.id.content);
                if (scrollView != null) {
                    scrollView.fullScroll(android.view.View.FOCUS_DOWN);
                }
            });
        }
    }
    
    /**
     * 更新回复提示
     */
    private void updateReplyHint() {
        View replyHintContainer = findViewById(R.id.reply_hint_container);
        if (replyHintContainer == null) return;
        
        if (replyToComment != null) {
            replyHintContainer.setVisibility(android.view.View.VISIBLE);
            replyHint.setText("回复 @" + replyToComment.getAuthor() + "：");
        } else {
            replyHintContainer.setVisibility(android.view.View.GONE);
        }
    }
    
    /**
     * 举报评论
     */
    private void reportComment(WCircleComment comment) {
        Intent intent = new Intent(this, com.example.lnforum.activity.ReportActivity.class);
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TYPE, "comment");
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TARGET_ID, comment.getId());
        intent.putExtra(com.example.lnforum.activity.ReportActivity.EXTRA_TARGET_NAME, comment.getAuthor());
        startActivity(intent);
    }
    
    private void showDeleteCommentDialog(WCircleComment comment) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("删除评论")
            .setMessage("确定要删除这条评论吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                CUser currentUser = sessionManager.getCurrentCUser();
                if (currentUser == null || !sessionManager.isLoggedIn()) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Integer userId = currentUser.getUserId();
                new Thread(() -> {
                    boolean success = WCircleRepository.deleteComment(comment.getId(), userId);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (success) {
                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                            loadComments();
                            // 更新帖子评论数
                            if (post != null) {
                                post.setComments(post.getComments() - 1);
                                comments.setText(String.valueOf(post.getComments()));
                            }
                        } else {
                            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            })
            .setNegativeButton("取消", null)
            .show();
    }
}

