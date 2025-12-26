package com.example.android_java2.fragment.errand;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.activity.ErrandDetailActivity;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.adapter.ErrandAdapter;
import com.example.android_java2.model.ErrandOrder;
import com.example.android_java2.repository.ErrandRepository;
import com.example.android_java2.repository.CSessionManager;

import java.util.ArrayList;
import java.util.List;

public class ErrandFragment extends Fragment {

    private TextView tabWaiting;
    private TextView tabDelivering;
    private TextView tabCompleted;
    private TextView selectedTab;
    private RecyclerView errandRecycler;
    private ErrandAdapter adapter;
    private ErrandRepository repository;
    private CSessionManager sessionManager;
    private String currentStatus = "waiting";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_errand, container, false);
        
        // 初始化标签
        initTabs(view);

        // 初始化列表
        initList(view);
        
        // 设置标签点击事件
        setTabClickListeners();
        
        // 注意：这里只初始化布局，不添加具体内容
        // 内容将在后续对接后端时添加
        
        return view;
    }
    
    private void initTabs(View view) {
        // 找到三个标签
        tabWaiting = view.findViewById(R.id.tab_waiting);
        tabDelivering = view.findViewById(R.id.tab_delivering);
        tabCompleted = view.findViewById(R.id.tab_completed);
        
        // 设置初始选中的标签
        selectedTab = tabWaiting;
    }

    private void initList(View view) {
        errandRecycler = view.findViewById(R.id.errand_recycler);
        if (getContext() == null) return;

        sessionManager = CSessionManager.getInstance(requireContext());

        repository = new ErrandRepository();
        adapter = new ErrandAdapter(order -> {
            if (getContext() == null) return;
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(getContext(), LoginActivity.class));
                return;
            }
            Intent intent = new Intent(getContext(), ErrandDetailActivity.class);
            intent.putExtra(ErrandDetailActivity.EXTRA_ID, order.getId());
            intent.putExtra(ErrandDetailActivity.EXTRA_TAG, order.getTag());
            intent.putExtra(ErrandDetailActivity.EXTRA_TITLE, order.getTitle());
            intent.putExtra(ErrandDetailActivity.EXTRA_DESC, order.getDesc());
            intent.putExtra(ErrandDetailActivity.EXTRA_FROM, order.getFrom());
            intent.putExtra(ErrandDetailActivity.EXTRA_TO, order.getTo());
            intent.putExtra(ErrandDetailActivity.EXTRA_PRICE, order.getPrice());
            intent.putExtra(ErrandDetailActivity.EXTRA_STATUS, order.getStatus());
            // 预留跑腿员标记，后续从后端判断；当前写死为未注册
            intent.putExtra(ErrandDetailActivity.EXTRA_IS_RUNNER, false);
            intent.putExtra(ErrandDetailActivity.EXTRA_PUBLISHER, "发布者");
            startActivity(intent);
        });

        errandRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        errandRecycler.setAdapter(adapter);

        refreshList();
    }
    
    private void setTabClickListeners() {
        // 待接单标签点击事件
        tabWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabWaiting);
                currentStatus = "waiting";
                refreshList();
            }
        });
        
        // 配送中标签点击事件
        tabDelivering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabDelivering);
                currentStatus = "delivering";
                refreshList();
            }
        });
        
        // 已完成标签点击事件
        tabCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabCompleted);
                currentStatus = "completed";
                refreshList();
            }
        });
    }
    
    private void switchTab(TextView newTab) {
        // 取消之前选中的标签样式
        if (selectedTab != null) {
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            selectedTab.setTextColor(grayColor);
            selectedTab.setTypeface(null, Typeface.NORMAL);
        }
        
        // 设置当前标签为选中状态
        selectedTab = newTab;
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        selectedTab.setTextColor(primaryColor);
        selectedTab.setTypeface(null, Typeface.BOLD);
        
        // 这里可以添加标签切换的逻辑，例如刷新跑腿订单列表
    }

    private void refreshList() {
        if (repository == null || adapter == null) return;

        List<ErrandOrder> all = repository.getSampleOrders();
        List<ErrandOrder> filtered = new ArrayList<>();
        for (ErrandOrder order : all) {
            if (currentStatus.equals(order.getStatus())) {
                filtered.add(order);
            }
        }
        adapter.setData(filtered);
    }
}