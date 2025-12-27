package com.example.lnforum.fragment.errand;

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

import com.example.lnforum.R;
import com.example.lnforum.activity.WErrandDetailActivity;
import com.example.lnforum.activity.WLoginActivity;
import com.example.lnforum.adapter.WErrandAdapter;
import com.example.lnforum.model.WErrandOrder;
import com.example.lnforum.repository.WErrandRepository;
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.repository.CSessionManager;

import java.util.ArrayList;
import java.util.List;

public class WErrandFragment extends Fragment {

    private RecyclerView listView;
    private WErrandAdapter adapter;
    private List<WErrandOrder> data;
    private WSessionManager sessionManager;
    private CSessionManager cSessionManager;
    private String currentStatus = "unaccepted"; // unaccepted或completed

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_errand, container, false);
        initList(view);
        initTabs(view);
        return view;
    }

    private void initTabs(View view) {
        TextView waitingTab = view.findViewById(R.id.tab_waiting);
        TextView completedTab = view.findViewById(R.id.tab_completed);
        
        if (waitingTab != null) {
            waitingTab.setOnClickListener(v -> {
                currentStatus = "unaccepted";
                updateTabStatus(waitingTab, completedTab, true);
                loadItems();
            });
        }
        
        if (completedTab != null) {
            completedTab.setOnClickListener(v -> {
                currentStatus = "completed";
                updateTabStatus(waitingTab, completedTab, false);
                loadItems();
            });
        }
        
        // 初始化时设置默认选中状态
        updateTabStatus(waitingTab, completedTab, true);
    }
    
    private void updateTabStatus(TextView waitingTab, TextView completedTab, boolean isWaitingSelected) {
        if (waitingTab == null || completedTab == null) return;
        
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
        
        if (isWaitingSelected) {
            waitingTab.setTextColor(primaryColor);
            waitingTab.setTypeface(null, android.graphics.Typeface.BOLD);
            completedTab.setTextColor(grayColor);
            completedTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            waitingTab.setTextColor(grayColor);
            waitingTab.setTypeface(null, android.graphics.Typeface.NORMAL);
            completedTab.setTextColor(primaryColor);
            completedTab.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void initList(View view) {
        listView = view.findViewById(R.id.errand_recycler);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionManager = WSessionManager.getInstance(requireContext());
        cSessionManager = CSessionManager.getInstance(requireContext());
        data = new ArrayList<>();
        adapter = new WErrandAdapter(requireContext(), data, new WErrandAdapter.OnItemClickListener() {
            @Override
            public void onOpen(WErrandOrder item) {
                if (!cSessionManager.isLoggedIn()) {
                    startActivity(new Intent(getContext(), WLoginActivity.class));
                    return;
                }
                Intent intent = new Intent(getContext(), WErrandDetailActivity.class);
                intent.putExtra(WErrandDetailActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
        loadItems();
    }

    private void loadItems() {
        android.util.Log.d("WErrandFragment", "开始加载跑腿数据: status=" + currentStatus);
        new Thread(() -> {
            List<WErrandOrder> items = WErrandRepository.getOrders(currentStatus, 1, 20);
            android.util.Log.d("WErrandFragment", "获取到 " + items.size() + " 条跑腿订单");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    data.clear();
                    data.addAll(items);
                    android.util.Log.d("WErrandFragment", "UI更新: data.size()=" + data.size());
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    public void refresh() {
        loadItems();
    }
}

