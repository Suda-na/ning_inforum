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
import com.example.lnforum.model.CUser;
import com.example.lnforum.model.ErrandOrder;
import com.example.lnforum.repository.CSessionManager;

import java.util.Map;
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
    private static final String API_URL = "http://192.168.159.1:8080/api/cuser/my_orders?userId=";
    private static final String ARG_USER_ID = "arg_user_id";
    private Integer targetUserId;

    private TextView tabMyPublish;
    private TextView tabMyAccept;
    private RecyclerView ordersRecycler;
    private ErrandAdapter adapter;

    // 缓存从后端拉取的所有订单
    private List<Map<String, Object>> allOrders = new ArrayList<>();
    // 当前显示的订单列表
    private List<ErrandOrder> displayList = new ArrayList<>();

    private boolean isShowingPublish = true; // true=我的发布, false=我的接单
    
    public static ProfileOrdersFragment newInstance(Integer userId) {
        ProfileOrdersFragment fragment = new ProfileOrdersFragment();
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
        // 确定要查询的userId：优先使用传入的参数，否则使用当前登录用户
        Integer userIdToQuery = targetUserId;
        if (userIdToQuery == null) {
            CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
            if (user == null) {
                allOrders.clear();
                displayList.clear();
                adapter.setData(displayList);
                return;
            }
            userIdToQuery = user.getUserId();
        }

        OkHttpClient client = new OkHttpClient();
        // 拼接用户ID
        Request request = new Request.Builder().url(API_URL + userIdToQuery).get().build();

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
                            Type type = new TypeToken<CResult<List<Map<String, Object>>>>(){}.getType();
                            CResult<List<Map<String, Object>>> result = gson.fromJson(json, type);

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
        // 确定要查询的userId：优先使用传入的参数，否则使用当前登录用户
        Integer userIdToQuery = targetUserId;
        if (userIdToQuery == null) {
            CUser user = CSessionManager.getInstance(getContext()).getCurrentCUser();
            if (user == null) return;
            userIdToQuery = user.getUserId();
        }

        displayList.clear();
        for (Map<String, Object> taskMap : allOrders) {
            // 获取订单信息
            Object taskIdObj = taskMap.get("taskId");
            Object creatorIdObj = taskMap.get("creatorId");
            Object acceptorIdObj = taskMap.get("acceptorId");
            Object taskStatusObj = taskMap.get("taskStatus");
            
            Integer creatorId = null;
            Integer acceptorId = null;
            Integer taskStatus = null;
            
            if (creatorIdObj instanceof Number) {
                creatorId = ((Number) creatorIdObj).intValue();
            }
            if (acceptorIdObj instanceof Number) {
                acceptorId = ((Number) acceptorIdObj).intValue();
            }
            if (taskStatusObj instanceof Number) {
                taskStatus = ((Number) taskStatusObj).intValue();
            }
            
            // 判断身份
            boolean isMyPublish = (creatorId != null && creatorId.equals(userIdToQuery));
            boolean isMyAccept = (acceptorId != null && acceptorId.equals(userIdToQuery));

            // 筛选逻辑：如果是"我的发布"且我是发布者，或者"我的接单"且我是接单者
            if ((isShowingPublish && isMyPublish) || (!isShowingPublish && isMyAccept)) {

                // 转换模型
                ErrandOrder order = new ErrandOrder();
                if (taskIdObj != null) {
                    order.setId(String.valueOf(taskIdObj));
                }
                order.setTitle((String) taskMap.get("title"));
                order.setDesc((String) taskMap.get("description"));
                
                Object amountObj = taskMap.get("amount");
                if (amountObj instanceof Number) {
                    order.setPrice(((Number) amountObj).doubleValue());
                } else {
                    order.setPrice(0.0);
                }

                // 状态映射
                String status = "waiting";
                if (taskStatus != null && taskStatus == 1) {
                    status = "completed";
                } else if (acceptorId != null) {
                    status = "delivering";
                }
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