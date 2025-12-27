package com.example.lnforum.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.activity.WCircleDetailActivity;
import com.example.lnforum.adapter.WCirclePostAdapter;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.WCirclePost;
import com.example.lnforum.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfilePostsFragment extends Fragment {

    // ✅ 关键点：这里必须请求 "my_posts"，而且要拼上 userId
    private static final String API_URL = "http://192.168.159.1:8080/api/cuser/my_posts?userId=";
    private static final String ARG_USER_ID = "arg_user_id";

    private RecyclerView recyclerView;
    private WCirclePostAdapter adapter;
    private List<WCirclePost> postList = new ArrayList<>();
    private Integer targetUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WCirclePostAdapter(getContext(), postList, new WCirclePostAdapter.OnPostActionListener() {
            @Override
            public void onOpen(WCirclePost post) {
                Intent intent = new Intent(getContext(), WCircleDetailActivity.class);
                intent.putExtra(WCircleDetailActivity.EXTRA_POST_ID, post.getId());
                startActivity(intent);
            }

            @Override
            public void onLike(WCirclePost post) {
                // 个人中心一般不做点赞交互，或者需要刷新列表
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ 每次回到页面都重新加载，确保换号后数据刷新
        loadData();
    }

    public static ProfilePostsFragment newInstance(Integer userId) {
        ProfilePostsFragment fragment = new ProfilePostsFragment();
        Bundle args = new Bundle();
        if (userId != null) {
            args.putInt(ARG_USER_ID, userId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID)) {
            targetUserId = getArguments().getInt(ARG_USER_ID);
        }
    }

    private void loadData() {
        // 1. 确定要查询的userId：优先使用传入的参数，否则使用当前登录用户
        Integer userIdToQuery = targetUserId;
        if (userIdToQuery == null) {
            CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
            if (user == null) {
                // 如果没登录且没有传入userId，清空列表
                postList.clear();
                adapter.notifyDataSetChanged();
                return;
            }
            userIdToQuery = user.getUserId();
        }

        // 2. 拼接 URL：只查这个人的动态
        String finalUrl = API_URL + userIdToQuery;
        
        // 获取用户信息用于显示作者名和头像
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(finalUrl).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 网络错误处理
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<List<Map<String, Object>>>>(){}.getType();
                            CResult<List<Map<String, Object>>> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200) {
                                postList.clear(); // 先清空旧数据！

                                if (result.getData() != null) {
                                    // 获取用户信息用于显示作者名和头像（优先使用目标用户的信息）
                                    String authorName = null;
                                    String authorAvatar = null;
                                    if (targetUserId != null && user != null && user.getUserId().equals(targetUserId)) {
                                        authorName = user.getUsername();
                                        authorAvatar = user.getAvatar();
                                    }
                                    
                                    for (Map<String, Object> postMap : result.getData()) {
                                        // 提取 postId
                                        Object postIdObj = postMap.get("postId");
                                        String postId = null;
                                        if (postIdObj != null) {
                                            // 确保 postId 是整数格式（去除小数部分）
                                            if (postIdObj instanceof Number) {
                                                postId = String.valueOf(((Number) postIdObj).intValue());
                                            } else {
                                                try {
                                                    postId = String.valueOf(Double.valueOf(postIdObj.toString()).intValue());
                                                } catch (Exception e) {
                                                    postId = String.valueOf(postIdObj);
                                                }
                                            }
                                        }
                                        if (postId == null) continue;
                                        
                                        // 提取标题和内容
                                        String title = (String) postMap.get("title");
                                        if (title == null) title = "";
                                        String content = (String) postMap.get("content");
                                        if (content == null) content = "";
                                        
                                        // 提取时间
                                        Object timeObj = postMap.get("createTime");
                                        String time = timeObj != null ? timeObj.toString() : "刚刚";
                                        
                                        // 设置作者信息
                                        String author = (String) postMap.get("authorName");
                                        if (author == null || author.isEmpty()) {
                                            author = authorName != null ? authorName : "用户";
                                        }
                                        
                                        String avatar = (String) postMap.get("authorAvatar");
                                        if (avatar == null || avatar.isEmpty()) {
                                            avatar = authorAvatar != null ? authorAvatar : "";
                                        }
                                        
                                        // 标签（从 tags 数组获取第一个，或使用 tag 字段）
                                        String tag = "";
                                        Object tagsObj = postMap.get("tags");
                                        if (tagsObj instanceof List && ((List<?>) tagsObj).size() > 0) {
                                            tag = String.valueOf(((List<?>) tagsObj).get(0));
                                        } else {
                                            Object tagObj = postMap.get("tag");
                                            if (tagObj != null) {
                                                tag = tagObj.toString();
                                            }
                                        }

                                        // 统计数据
                                        Object viewsObj = postMap.get("views");
                                        int views = viewsObj instanceof Number ? ((Number) viewsObj).intValue() : 0;
                                        Object likesObj = postMap.get("likes");
                                        int likes = likesObj instanceof Number ? ((Number) likesObj).intValue() : 0;
                                        Object commentsObj = postMap.get("comments");
                                        int comments = commentsObj instanceof Number ? ((Number) commentsObj).intValue() : 0;

                                        // 图片列表（支持多张）
                                        List<String> imgs = new ArrayList<>();
                                        // 优先从 images 数组获取
                                        Object imagesObj = postMap.get("images");
                                        if (imagesObj instanceof List) {
                                            for (Object img : (List<?>) imagesObj) {
                                                if (img != null && !img.toString().isEmpty()) {
                                                    String imgUrl = img.toString();
                                                    if (!imgs.contains(imgUrl)) {
                                                        imgs.add(imgUrl);
                                                    }
                                                }
                                            }
                                        }
                                        // 如果没有 images 数组，尝试从 image1 获取
                                        if (imgs.isEmpty()) {
                                            Object image1Obj = postMap.get("image1");
                                            if (image1Obj != null && !image1Obj.toString().isEmpty()) {
                                                imgs.add(image1Obj.toString());
                                            }
                                        }

                                        // 创建 WCirclePost 对象
                                        WCirclePost p = new WCirclePost(
                                            postId,
                                            author,
                                            avatar,
                                            time,
                                            title,
                                            content,
                                            tag,
                                            views,
                                            comments,
                                            likes,
                                            imgs
                                        );
                                        
                                        // 设置点赞状态
                                        Object likedObj = postMap.get("liked");
                                        if (likedObj instanceof Boolean) {
                                            p.setLiked((Boolean) likedObj);
                                        }
                                        
                                        // 设置收藏状态
                                        Object favoritedObj = postMap.get("favorited");
                                        if (favoritedObj instanceof Boolean) {
                                            p.setFavorited((Boolean) favoritedObj);
                                        }
                                        
                                        // 设置作者ID
                                        Object userIdObj = postMap.get("userId");
                                        if (userIdObj instanceof Number) {
                                            p.setAuthorId(((Number) userIdObj).intValue());
                                        }

                                        postList.add(p);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}