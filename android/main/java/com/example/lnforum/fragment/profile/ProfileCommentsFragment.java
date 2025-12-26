package com.example.lnforum.fragment.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser;
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

public class ProfileCommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyCommentAdapter adapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    // 接口地址
    private static final String API_URL = "http://192.168.172.1:8081/api/cuser/my_comments?userId=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyCommentAdapter(dataList);
        recyclerView.setAdapter(adapter);

        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
        if (user == null) {
            dataList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String finalUrl = API_URL + user.getUserId();

        // 🖨️ 打印请求地址
        Log.e("DEBUG_PROFILE", "正在请求评论: " + finalUrl);

        Request request = new Request.Builder().url(finalUrl).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DEBUG_PROFILE", "网络失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                // 🖨️ 关键日志：看看服务器到底返回了什么鬼东西！
                Log.e("DEBUG_PROFILE", "服务器返回内容: " + json);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            // 尝试解析
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<List<Map<String, Object>>>>(){}.getType();
                            CResult<List<Map<String, Object>>> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200 && result.getData() != null) {
                                dataList.clear();
                                dataList.addAll(result.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("DEBUG_PROFILE", "业务失败或数据为空");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("DEBUG_PROFILE", "解析炸了! 原因: " + e.getMessage());
                            Toast.makeText(getContext(), "数据格式错误，请看日志", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // 内部适配器 (强制黑字，防止看不见)
    class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.VH> {
        List<Map<String, Object>> list;
        MyCommentAdapter(List<Map<String, Object>> list){this.list = list;}

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 动态创建一个简单的布局，确保背景白，字黑
            android.widget.LinearLayout layout = new android.widget.LinearLayout(parent.getContext());
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(30, 30, 30, 30);
            layout.setBackgroundColor(android.graphics.Color.WHITE); // 强制白底

            TextView t1 = new TextView(parent.getContext());
            t1.setId(android.R.id.text1);
            t1.setTextSize(16);
            t1.setTextColor(android.graphics.Color.BLACK); // 强制黑字

            TextView t2 = new TextView(parent.getContext());
            t2.setId(android.R.id.text2);
            t2.setTextSize(12);
            t2.setTextColor(android.graphics.Color.GRAY); // 强制灰字

            layout.addView(t1);
            layout.addView(t2);
            layout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            return new VH(layout);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Map<String, Object> item = list.get(position);
            String content = (String) item.get("comment_content");
            String postTitle = (String) item.get("post_title");

            if (content == null) content = "(无内容)";
            if (postTitle == null) postTitle = "未知帖子";

            holder.t1.setText("我评论: " + content);
            holder.t2.setText("在帖子: 《" + postTitle + "》");
        }

        @Override public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View v) { super(v); t1=v.findViewById(android.R.id.text1); t2=v.findViewById(android.R.id.text2); }
        }
    }
}