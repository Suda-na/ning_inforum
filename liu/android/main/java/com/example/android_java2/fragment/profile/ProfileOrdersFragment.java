package com.example.android_java2.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.activity.ErrandDetailActivity;
import com.example.android_java2.adapter.ProfileOrderAdapter;
import com.example.android_java2.model.ErrandOrder;
import com.example.android_java2.repository.ErrandRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单 Fragment：包含“我的发布”和“我的接单”两个列表。
 * 目前使用静态跑腿数据，后续可接入 SSM + MySQL。
 */
public class ProfileOrdersFragment extends Fragment {

    private TextView tabMyPublish;
    private TextView tabMyAccept;
    private TextView selectedTab;
    private RecyclerView ordersRecycler;
    private ProfileOrderAdapter publishAdapter;
    private ProfileOrderAdapter acceptAdapter;
    private boolean showingPublish = true;
    private List<ErrandOrder> publishList = new ArrayList<>();
    private List<ErrandOrder> acceptList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_orders, container, false);
        
        initTabs(view);
        initList(view);
        setTabClickListeners();
        
        return view;
    }

    private void initTabs(View view) {
        tabMyPublish = view.findViewById(R.id.tab_my_publish);
        tabMyAccept = view.findViewById(R.id.tab_my_accept);
        selectedTab = tabMyPublish;
    }

    private void initList(View view) {
        ordersRecycler = view.findViewById(R.id.orders_recycler);
        if (getContext() == null) return;

        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // 构造静态示例数据，后续可改为从后端获取
        ErrandRepository repository = new ErrandRepository();
        List<ErrandOrder> all = repository.getSampleOrders();
        for (ErrandOrder order : all) {
            if ("completed".equals(order.getStatus())) {
                acceptList.add(order); // 已完成视为“我的接单”
            } else {
                publishList.add(order); // 其他状态视为“我的发布”
            }
        }

        publishAdapter = new ProfileOrderAdapter("publish", this::openDetail);
        acceptAdapter = new ProfileOrderAdapter("accept", this::openDetail);

        publishAdapter.setData(publishList);
        acceptAdapter.setData(acceptList);

        // 默认显示我的发布
        ordersRecycler.setAdapter(publishAdapter);
    }

    private void setTabClickListeners() {
        tabMyPublish.setOnClickListener(v -> {
            if (!showingPublish) {
                switchTab(tabMyPublish);
                showingPublish = true;
                ordersRecycler.setAdapter(publishAdapter);
            }
        });

        tabMyAccept.setOnClickListener(v -> {
            if (showingPublish) {
                switchTab(tabMyAccept);
                showingPublish = false;
                ordersRecycler.setAdapter(acceptAdapter);
            }
        });
    }

    private void switchTab(TextView newTab) {
        if (getContext() == null) return;

        if (selectedTab != null) {
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            selectedTab.setTextColor(grayColor);
        }

        selectedTab = newTab;
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        selectedTab.setTextColor(primaryColor);
    }

    private void openDetail(ErrandOrder order) {
        if (getContext() == null) return;
        Intent intent = new Intent(getContext(), ErrandDetailActivity.class);
        intent.putExtra(ErrandDetailActivity.EXTRA_ID, order.getId());
        intent.putExtra(ErrandDetailActivity.EXTRA_TITLE, order.getTitle());
        intent.putExtra(ErrandDetailActivity.EXTRA_DESC, order.getDesc());
        intent.putExtra(ErrandDetailActivity.EXTRA_FROM, order.getFrom());
        intent.putExtra(ErrandDetailActivity.EXTRA_TO, order.getTo());
        intent.putExtra(ErrandDetailActivity.EXTRA_PRICE, order.getPrice());
        intent.putExtra(ErrandDetailActivity.EXTRA_STATUS, order.getStatus());
        intent.putExtra(ErrandDetailActivity.EXTRA_IS_RUNNER, false);
        intent.putExtra(ErrandDetailActivity.EXTRA_PUBLISHER, "我");
        startActivity(intent);
    }
}