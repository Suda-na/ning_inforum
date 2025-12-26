package com.example.android_java2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注/粉丝列表页
 */
public class FollowListActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_TITLE_NAME = "extra_title_name";
    public static final String TYPE_FOLLOWING = "following";
    public static final String TYPE_FANS = "fans";

    private String type;
    private String titleName;
    private RecyclerView recyclerView;
    private FollowAdapter adapter;
    private List<FollowUser> data = new ArrayList<>();

    public static void open(Context context, String type, String titleName) {
        Intent intent = new Intent(context, FollowListActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_TITLE_NAME, titleName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        type = getIntent().getStringExtra(EXTRA_TYPE);
        titleName = getIntent().getStringExtra(EXTRA_TITLE_NAME);
        if (titleName == null) titleName = "";

        initViews();
        loadSampleData();
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

    private void loadSampleData() {
        data.clear();
        if (TYPE_FOLLOWING.equals(type)) {
            data.add(new FollowUser("张小明", "热爱学习的程序员", true));
            data.add(new FollowUser("跑腿小王", "专业跑腿，安全快捷", false));
            data.add(new FollowUser("music_fan", "吉他社副社长 | 寻找乐队伙伴", true));
        } else {
            data.add(new FollowUser("李小红", "考研进行中", false));
            data.add(new FollowUser("校园记者", "校园新闻第一时间", true));
            data.add(new FollowUser("freshman_li", "大一新生，请多关照", false));
        }
        adapter.notifyDataSetChanged();
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
            FollowUser user = data.get(position);
            holder.name.setText(user.name);
            holder.desc.setText(user.desc);

            if (TYPE_FOLLOWING.equals(type)) {
                holder.action.setText("取消关注");
                holder.action.setBackgroundResource(R.drawable.round_button_bg);
                holder.action.setTextColor(getResources().getColor(R.color.white, null));
                holder.action.setOnClickListener(v -> {
                    data.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(FollowListActivity.this, "已取消关注 " + user.name, Toast.LENGTH_SHORT).show();
                });
            } else {
                holder.action.setText(user.mutual ? "已互关" : "互关");
                holder.action.setBackgroundResource(R.drawable.edit_text_bg);
                holder.action.setTextColor(getResources().getColor(R.color.primary_blue, null));
                holder.action.setOnClickListener(v -> {
                    user.mutual = !user.mutual;
                    notifyItemChanged(position);
                    Toast.makeText(FollowListActivity.this, user.mutual ? "已互关" : "已取消互关", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static class FollowViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;
        TextView action;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.follow_item_name);
            desc = itemView.findViewById(R.id.follow_item_desc);
            action = itemView.findViewById(R.id.follow_item_action);
        }
    }

    private static class FollowUser {
        String name;
        String desc;
        boolean mutual;

        FollowUser(String name, String desc, boolean mutual) {
            this.name = name;
            this.desc = desc;
            this.mutual = mutual;
        }
    }
}

