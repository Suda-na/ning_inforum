package com.example.lnforum.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.adapter.ErrandAdapter;
import com.example.lnforum.model.CResult;
import com.example.lnforum.model.CTradeTask;
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.ErrandOrder;
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

public class ProfileOrdersFragment extends Fragment {

    // ✅ 关键点：请求 "my_orders"
    private static final String API_URL = "http://192.168.172.1:8081/api/cuser/my_orders?userId=";

    private TextView tabMyPublish;
    private TextView tabMyAccept;
    private RecyclerView ordersRecycler;
    private ErrandAdapter adapter;

    // 缓存从后端拉取的所有订单
    private List<CTradeTask> allOrders = new ArrayList<>();
    // 当前显示的订单列表
    private List<ErrandOrder> displayList = new ArrayList<>();

    private boolean isShowingPublish = true; // true=我的发布, false=我的接单

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_orders, container, false);

        initTabs(view);
        ordersRecycler = view.findViewById(R.id.orders_recycler);
        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ErrandAdapter(order -> {
            // 点击事件
        });
        ordersRecycler.setAdapter(adapter);

        setTabClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ 每次回到页面重新加载
        loadData();
    }

    private void loadData() {
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
        if (user == null) {
            allOrders.clear();
            displayList.clear();
            adapter.setData(displayList);
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // 拼接用户ID
        Request request = new Request.Builder().url(API_URL + user.getUserId()).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CResult<List<CTradeTask>>>(){}.getType();
                            CResult<List<CTradeTask>> result = gson.fromJson(json, type);

                            if (result != null && result.getCode() == 200 && result.getData() != null) {
                                allOrders.clear(); // 清空旧数据
                                allOrders.addAll(result.getData());
                                filterList(); // 重新筛选并显示
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    // 根据 Tab 和 用户ID 筛选订单
    private void filterList() {
        CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
        if (user == null) return;
        int myUserId = user.getUserId();

        displayList.clear();
        for (CTradeTask task : allOrders) {
            // 判断身份
            boolean isMyPublish = (task.getCreatorId() != null && task.getCreatorId() == myUserId);
            boolean isMyAccept = (task.getAcceptorId() != null && task.getAcceptorId() == myUserId);

            // 筛选逻辑：如果是“我的发布”且我是发布者，或者“我的接单”且我是接单者
            if ((isShowingPublish && isMyPublish) || (!isShowingPublish && isMyAccept)) {

                // 转换模型
                ErrandOrder order = new ErrandOrder();
                order.setId(String.valueOf(task.getTaskId()));
                order.setTitle(task.getTitle());
                order.setDesc(task.getDescription());
                order.setPrice(task.getAmount() != null ? task.getAmount().doubleValue() : 0.0);

                // 状态映射
                String status = "waiting";
                if (task.getTaskStatus() == 1) status = "completed";
                else if (task.getAcceptorId() != null) status = "delivering";
                order.setStatus(status);

                order.setFrom("暂无地点");
                order.setTo("暂无地点");

                displayList.add(order);
            }
        }
        adapter.setData(displayList);
    }

    private void initTabs(View view) {
        tabMyPublish = view.findViewById(R.id.tab_my_publish);
        tabMyAccept = view.findViewById(R.id.tab_my_accept);
        updateTabStyles();
    }

    private void setTabClickListeners() {
        tabMyPublish.setOnClickListener(v -> {
            if (!isShowingPublish) {
                isShowingPublish = true;
                updateTabStyles();
                filterList();
            }
        });

        tabMyAccept.setOnClickListener(v -> {
            if (isShowingPublish) {
                isShowingPublish = false;
                updateTabStyles();
                filterList();
            }
        });
    }

    private void updateTabStyles() {
        if (getContext() == null) return;
        int selectedColor = ContextCompat.getColor(getContext(), R.color.primary_blue);
        int unselectedColor = ContextCompat.getColor(getContext(), R.color.nav_unselected);

        tabMyPublish.setTextColor(isShowingPublish ? selectedColor : unselectedColor);
        tabMyAccept.setTextColor(!isShowingPublish ? selectedColor : unselectedColor);
    }
}