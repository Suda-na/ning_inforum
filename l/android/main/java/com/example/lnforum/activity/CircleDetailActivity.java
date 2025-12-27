package com.example.lnforum.activity;

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

import com.example.lnforum.R;
import com.example.lnforum.adapter.CircleCommentAdapter;
import com.example.lnforum.model.CPost; // 后端模型
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.CircleComment; // 前端模型
import com.example.lnforum.model.CirclePost;   // 前端模型
import com.example.lnforum.repository.CSessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CircleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";

    // 后端接口地址
    private static final String API_DETAIL = "http://192.168.159.1:8080/api/cuser/post_detail?postId=";
    private static final String API_COMMENTS = "http://192.168.159.1:8080/api/cuser/post_comments?postId=";
    private static final String API_ADD_COMMENT = "http://192.168.159.1:8080/api/cuser/add_comment";

    private CirclePost currentPost; // 前端显示用的对象
    private String postId;

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

        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (TextUtils.isEmpty(postId)) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initActions();

        // ✅ 从服务器加载数据
        loadPostDetail();
        loadComments();
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

        commentsList.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CircleCommentAdapter(commentData, this::showCommentActions);
        commentsList.setAdapter(commentAdapter);
    }

    // ✅ 1. 加载帖子详情
    private void loadPostDetail() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_DETAIL + postId).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CircleDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<CPost>>(){}.getType();
                        CResult<CPost> result = gson.fromJson(json, type);

                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            CPost serverPost = result.getData();

                            // 转换为前端模型
                            currentPost = new CirclePost();
                            currentPost.setId(String.valueOf(serverPost.getPostId()));
                            currentPost.setTitle(serverPost.getTitle());
                            currentPost.setContent(serverPost.getContent());
                            currentPost.setAuthor("用户" + serverPost.getUserId()); // 暂用ID，后端若传了authorName更好
                            currentPost.setTime(serverPost.getCreateTime());

                            // 图片处理
                            List<String> imgs = new ArrayList<>();
                            if(serverPost.getImage1() != null) imgs.add(serverPost.getImage1());
                            currentPost.setImages(imgs);

                            bindData(); // 刷新UI
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void bindData() {
        if (currentPost == null) return;
        title.setText(currentPost.getTitle());
        content.setText(currentPost.getContent());
        author.setText(currentPost.getAuthor());
        time.setText(currentPost.getTime());
        tag.setText(currentPost.getTag() == null ? "动态" : currentPost.getTag());
        views.setText(String.valueOf(currentPost.getViews()));
        comments.setText(String.valueOf(currentPost.getComments()));
        likes.setText(String.valueOf(currentPost.getLikes()));

        refreshLike();
        refreshWatch();
        refreshFollow();
        loadImages(currentPost.getImages());
    }

    // ✅ 2. 加载评论列表
    // 📂 CircleDetailActivity.java

    private void loadComments() {
        String url = API_COMMENTS + postId;
        android.util.Log.e("调试评论", "1. 准备请求地址: " + url); // 🖨️ 打印地址

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("调试评论", "❌ 网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                // 🖨️ 关键！看看后端到底返回了什么！
                android.util.Log.e("调试评论", "2. 服务器返回的原始JSON: " + json);

                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<CResult<List<Map<String, Object>>>>(){}.getType();
                        CResult<List<Map<String, Object>>> result = gson.fromJson(json, type);

                        if (result == null) {
                            android.util.Log.e("调试评论", "❌ Result 解析为 null");
                            return;
                        }

                        android.util.Log.e("调试评论", "3. 解析出的 code: " + result.getCode());

                        if (result.getCode() == 200 && result.getData() != null) {
                            commentData.clear();
                            android.util.Log.e("调试评论", "4. 后端返回的数据条数: " + result.getData().size());

                            for (Map<String, Object> map : result.getData()) {
                                // 🖨️ 打印每一条数据，看看字段名对不对
                                android.util.Log.e("调试评论", "--- 正在解析一条数据: " + map.toString());

                                String content = (String) map.get("comment_content");
                                String authorName = (String) map.get("author");
                                String createTime = (String) map.get("create_time");

                                // 处理 ID (不管是int还是double都转string)
                                Object idObj = map.get("interaction_id");
                                String idStr = String.valueOf(idObj).replace(".0", "");

                                CircleComment c = new CircleComment();
                                c.setId(idStr);
                                c.setContent(content);
                                c.setAuthor(authorName != null ? authorName : "匿名");
                                c.setTime(createTime != null ? createTime : "");

                                commentData.add(c);
                            }

                            android.util.Log.e("调试评论", "5. 最终加入 Adapter 的条数: " + commentData.size());
                            commentAdapter.notifyDataSetChanged();
                            comments.setText(String.valueOf(commentData.size()));
                        } else {
                            android.util.Log.e("调试评论", "❌ Code不是200 或者 Data是空");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        android.util.Log.e("调试评论", "❌ 解析过程报错: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void initActions() {
        findViewById(R.id.detail_back).setOnClickListener(v -> finish());
        findViewById(R.id.detail_more).setOnClickListener(v -> showMoreActions());

        likeIcon.setOnClickListener(v -> {
            if (currentPost != null) {
                currentPost.toggleLike();
                likes.setText(String.valueOf(currentPost.getLikes()));
                refreshLike();
                // TODO: 调用后端点赞接口
            }
        });

        watchBtn.setOnClickListener(v -> {
            if (currentPost != null) {
                currentPost.toggleWatch();
                refreshWatch();
            }
        });

        followBtn.setOnClickListener(v -> {
            if (currentPost != null) {
                currentPost.toggleFollow();
                refreshFollow();
                Toast.makeText(this, currentPost.isFollowed() ? "已关注" : "已取消关注", Toast.LENGTH_SHORT).show();
            }
        });

        // 发送评
        findViewById(R.id.detail_send_comment).setOnClickListener(v -> sendCommentToServer());
    }

    // ✅ 3. 发送评论到服务器
    private void sendCommentToServer() {
        EditText input = findViewById(R.id.detail_comment_input);
        String text = input.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        CUser user = CSessionManager.getInstance(this).getCurrentCUser();
        if (user == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("postId", postId)
                .add("userId", String.valueOf(user.getUserId()))
                .add("content", text)
                .build();

        Request request = new Request.Builder().url(API_ADD_COMMENT).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CircleDetailActivity.this, "发送失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    input.setText("");
                    Toast.makeText(CircleDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    loadComments(); // 重新加载评论列表
                });
            }
        });
    }

    private void refreshLike() {
        if (currentPost == null) return;
        int color = currentPost.isLiked() ? ContextCompat.getColor(this, R.color.primary_blue)
                : ContextCompat.getColor(this, R.color.text_secondary);
        likeIcon.setColorFilter(color);
        likes.setTextColor(color);
    }

    private void refreshWatch() {
        if (currentPost == null) return;
        int color = currentPost.isWatched() ? ContextCompat.getColor(this, R.color.primary_blue)
                : ContextCompat.getColor(this, R.color.text_secondary);
        watchBtn.setColorFilter(color);
    }

    private void refreshFollow() {
        if (currentPost == null) return;
        if (currentPost.isFollowed()) {
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
            // 这里建议后续加上 Glide 加载网络图片
            // Glide.with(this).load(url).into(imageView);
            imagesContainer.addView(imageView);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showMoreActions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_menu_more, null);
        view.findViewById(R.id.menu_report).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra(ReportActivity.EXTRA_TYPE, "post");
            intent.putExtra(ReportActivity.EXTRA_TARGET_ID, postId);
            startActivity(intent);
        });
        view.findViewById(R.id.menu_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }

    private void showCommentActions(CircleComment comment, View anchor) {
        // ... 保持原有逻辑不变 ...
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}