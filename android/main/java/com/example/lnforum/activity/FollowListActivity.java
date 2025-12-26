package com.example.lnforum.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CUser; // ✅ 使用统一的 CUser 模型
import com.example.lnforum.repository.CSessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 关注/粉丝列表页 (网络版)
 */
public class FollowListActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_TITLE_NAME = "extra_title_name"; // 传进来的用户名，用于标题显示
    public static final String TYPE_FOLLOWING = "following";
    public static final String TYPE_FANS = "fans";

    // ✅ 后端接口
    private static final String API_URL = "http://192.168.172.1:8081/api/cuser/relation_list";

    private String type;
    private String titleName;
    private RecyclerView recyclerView;
    private FollowAdapter adapter;
    private List<CUser> dataList = new ArrayList<>(); // 数据源改用 CUser

    public static void open(Context context, String type, String titleName) {
        Intent intent = new Intent(context, FollowListActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_TITLE_NAME, titleName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list); // 确保你有这个布局文件

        type = getIntent().getStringExtra(EXTRA_TYPE);
        titleName = getIntent().getStringExtra(EXTRA_TITLE_NAME);
        if (titleName == null) titleName = "";

        initViews();
        // ✅ 从网络加载数据
        loadData();
    }

    private void initViews() {
        ImageView back = findViewById(R.id.follow_back);
        TextView title = findViewById(R.id.follow_title);
        recyclerView = findViewById(R.id.follow_recycler);

        String pageTitle = TYPE_FOLLOWING.equals(type) ? titleName + "的关注" : titleName + "的粉丝";
        title.setText(pageTitle);

        back.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FollowAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        // 1. 获取当前用户
        CUser me = CSessionManager.getInstance(this).getCurrentCUser();
        if (me == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient();

        // 🔴 关键修复：后端要的是 int (0或1)，不能传 "following" 字符串！
        int typeInt = TYPE_FOLLOWING.equals(type) ? 0 : 1;

        // 2. 拼接 URL (注意这里用了 typeInt)
        String url = API_URL + "?userId=" + me.getUserId() + "&type=" + typeInt;

        // 打印一下看看 (可以在 Logcat 看到)
        System.out.println("正在请求关注列表: " + url);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(FollowListActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    // 🔴 增加错误提示：如果后端报 400/500，这里能弹窗看见
                    if (!response.isSuccessful()) {
                        Toast.makeText(FollowListActivity.this, "服务器错误码: " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<CResult<List<CUser>>>(){}.getType();
                        CResult<List<CUser>> result = gson.fromJson(json, listType);

                        if (result != null && result.getCode() == 200) {
                            dataList.clear();
                            // 判空防止崩溃
                            if (result.getData() != null) {
                                dataList.addAll(result.getData());
                            }
                            adapter.notifyDataSetChanged();

                            if (dataList.isEmpty()) {
                                Toast.makeText(FollowListActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FollowListActivity.this, "获取失败: " + (result!=null?result.getMessage():"未知"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FollowListActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private class FollowAdapter extends RecyclerView.Adapter<FollowViewHolder> {

        @NonNull
        @Override
        public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_follow_user, parent, false);
            return new FollowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
            CUser user = dataList.get(position);
            holder.name.setText(user.getUsername());

            String sign = user.getSignature();
            if (sign == null || sign.isEmpty()) sign = "暂无签名";
            holder.desc.setText(sign);

            // 获取当前登录用户ID
            CUser me = CSessionManager.getInstance(FollowListActivity.this).getCurrentCUser();

            if (TYPE_FOLLOWING.equals(type)) {
                // --- 关注列表逻辑 ---
                holder.action.setText("取消关注");
                holder.action.setBackgroundResource(R.drawable.round_button_bg);
                holder.action.setTextColor(getResources().getColor(android.R.color.white));

                holder.action.setOnClickListener(v -> {
                    // 执行取消关注 (ActionType = 1)
                    performAction(me.getUserId(), user.getUserId(), 1, position);
                });

            } else {
                // --- 粉丝列表逻辑 ---
                holder.action.setText("移除粉丝");
                holder.action.setBackgroundResource(R.drawable.edit_text_bg);
                holder.action.setTextColor(getResources().getColor(R.color.primary_blue));

                holder.action.setOnClickListener(v -> {
                    // 执行移除粉丝 (ActionType = 2)
                    performAction(me.getUserId(), user.getUserId(), 2, position);
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    /**
     * 执行网络请求
     * @param myId 当前用户ID
     * @param targetId 目标用户ID
     * @param actionType 1=取关, 2=移除粉丝
     * @param position 列表中的位置（用于删除视图）
     */
    private void performAction(Integer myId, Integer targetId, int actionType, int position) {
        OkHttpClient client = new OkHttpClient();

        // 构建 POST 表单参数
        FormBody body = new FormBody.Builder()
                .add("myId", String.valueOf(myId))
                .add("targetId", String.valueOf(targetId))
                .add("actionType", String.valueOf(actionType))
                .build();

        // 注意：这里要改成 POST 请求
        String actionUrl = "http://192.168.172.1:8081/api/cuser/follow_action";

        Request request = new Request.Builder()
                .url(actionUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(FollowListActivity.this, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Type resultType = new TypeToken<CResult<Object>>(){}.getType();
                        CResult<Object> result = gson.fromJson(json, resultType);

                        if (result != null && result.getCode() == 200) {
                            Toast.makeText(FollowListActivity.this, "操作成功", Toast.LENGTH_SHORT).show();

                            // ✅ 成功后，从列表中移除这一项，实现“丝滑”消失
                            dataList.remove(position);
                            adapter.notifyItemRemoved(position);
                            // 刷新后续位置，防止错位
                            adapter.notifyItemRangeChanged(position, dataList.size());

                        } else {
                            Toast.makeText(FollowListActivity.this, "失败: " + (result!=null?result.getMessage():"未知错误"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private static class FollowViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;
        TextView action;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            // 确保你的 item_follow_user.xml 里有这些 ID
            name = itemView.findViewById(R.id.follow_item_name);
            desc = itemView.findViewById(R.id.follow_item_desc);
            action = itemView.findViewById(R.id.follow_item_action);
        }
    }
}