package com.example.lnforum.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class ProfileEyeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CirclePostAdapter adapter;
    private List<CirclePost> postList = new ArrayList<>();

    // 接口地址
    private static final String API_URL = "http://192.168.172.1:8081/api/cuser/my_watches?userId=";

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
                // 收藏列表一般不做点赞操作
            }
        });
        recyclerView.setAdapter(adapter);

        // 第一次加载
        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到这个页面，重新加载数据
        loadData();
    }

    private void loadData() {
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
        if (user == null) {
            // 🔴 关键点1：如果未登录，必须清空列表！
            postList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL + user.getUserId()).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 网络失败可以选择不清空，或者弹窗
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
                                // 🔴 🔴 🔴 关键点2：在添加新数据前，必须清空旧数据！！！
                                postList.clear();

                                if (result.getData() != null) {
                                    for(CPost cp : result.getData()){
                                        CirclePost p = new CirclePost();
                                        p.setId(String.valueOf(cp.getPostId()));
                                        p.setTitle(cp.getTitle());
                                        p.setContent(cp.getContent());
                                        p.setTime(cp.getCreateTime());

                                        // 处理作者名
                                        String author = cp.getAuthorName();
                                        if (author == null || author.isEmpty()) {
                                            author = "用户" + cp.getUserId();
                                        }
                                        p.setAuthor(author);

                                        // 图片
                                        List<String> imgs = new ArrayList<>();
                                        if(cp.getImage1()!=null && !cp.getImage1().isEmpty()) {
                                            imgs.add(cp.getImage1());
                                        }
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