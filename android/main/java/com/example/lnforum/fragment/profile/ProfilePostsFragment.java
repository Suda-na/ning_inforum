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
import com.example.lnforum.activity.CircleDetailActivity;
import com.example.lnforum.adapter.CirclePostAdapter;
import com.example.lnforum.model.CPost;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.CirclePost;
import com.example.lnforum.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfilePostsFragment extends Fragment {

    // ✅ 关键点：这里必须请求 "my_posts"，而且要拼上 userId
    private static final String API_URL = "http://192.168.172.1:8081/api/cuser/my_posts?userId=";

    private RecyclerView recyclerView;
    private CirclePostAdapter adapter;
    private List<CirclePost> postList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CirclePostAdapter(getContext(), postList, new CirclePostAdapter.OnPostActionListener() {
            @Override
            public void onOpen(CirclePost post) {
                Intent intent = new Intent(getContext(), CircleDetailActivity.class);
                intent.putExtra(CircleDetailActivity.EXTRA_POST_ID, post.getId());
                startActivity(intent);
            }

            @Override
            public void onLike(CirclePost post) {
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

    private void loadData() {
        // 1. 获取当前登录的用户
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
        if (user == null) {
            // 如果没登录，清空列表
            postList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        // 2. 拼接 URL：只查这个人的动态
        String finalUrl = API_URL + user.getUserId();

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
                            Type type = new TypeToken<CResult<List<CPost>>>(){}.getType();
                            CResult<List<CPost>> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200) {
                                postList.clear(); // 先清空旧数据！

                                if (result.getData() != null) {
                                    for (CPost cp : result.getData()) {
                                        CirclePost p = new CirclePost();
                                        p.setId(String.valueOf(cp.getPostId()));
                                        p.setTitle(cp.getTitle());
                                        p.setContent(cp.getContent());
                                        p.setTime(cp.getCreateTime());
                                        // 强制设置作者为当前用户
                                        p.setAuthor(user.getUsername());
                                        p.setAvatar(user.getAvatar());

                                        // 统计数据
                                        p.setViews(cp.getViews() != null ? cp.getViews() : 0);
                                        p.setLikes(cp.getLikes() != null ? cp.getLikes() : 0);
                                        p.setComments(cp.getComments() != null ? cp.getComments() : 0);

                                        // 图片
                                        List<String> imgs = new ArrayList<>();
                                        if (cp.getImage1() != null) imgs.add(cp.getImage1());
                                        p.setImages(imgs);

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