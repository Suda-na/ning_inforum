package com.example.lnforum.fragment.lost_found;

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
import com.example.lnforum.activity.WLoginActivity;
import com.example.lnforum.adapter.WLostFoundAdapter;
import com.example.lnforum.model.WLostFoundItem;
import com.example.lnforum.repository.WLostFoundRepository;
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.activity.WLostFoundDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class WLostAndFoundFragment extends Fragment {

    private RecyclerView listView;
    private WLostFoundAdapter adapter;
    private List<WLostFoundItem> data;
    private WSessionManager sessionManager;
    private String currentType = "lost";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);
        initList(view);
        initTabs(view);
        return view;
    }

    private void initTabs(View view) {
        TextView lostTab = view.findViewById(R.id.tab_lost);
        TextView foundTab = view.findViewById(R.id.tab_found);
        
        if (lostTab != null) {
            lostTab.setOnClickListener(v -> {
                currentType = "lost";
                updateTabStatus(lostTab, foundTab, true);
                loadItems(1, 10);
            });
        }
        
        if (foundTab != null) {
            foundTab.setOnClickListener(v -> {
                currentType = "found";
                updateTabStatus(lostTab, foundTab, false);
                loadItems(1, 10);
            });
        }
        
        // 初始化时设置默认选中状态
        if (lostTab != null && foundTab != null) {
            updateTabStatus(lostTab, foundTab, true);
        }
    }
    
    private void updateTabStatus(TextView lostTab, TextView foundTab, boolean isLostSelected) {
        if (lostTab == null || foundTab == null) return;
        
        int primaryColor = android.graphics.Color.parseColor("#1976D2");
        int grayColor = android.graphics.Color.parseColor("#757575");
        
        if (isLostSelected) {
            lostTab.setTextColor(primaryColor);
            lostTab.setTypeface(null, android.graphics.Typeface.BOLD);
            foundTab.setTextColor(grayColor);
            foundTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            lostTab.setTextColor(grayColor);
            lostTab.setTypeface(null, android.graphics.Typeface.NORMAL);
            foundTab.setTextColor(primaryColor);
            foundTab.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void initList(View view) {
        listView = view.findViewById(R.id.lost_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionManager = WSessionManager.getInstance(requireContext());
        data = new ArrayList<>();
        adapter = new WLostFoundAdapter(requireContext(), data, new WLostFoundAdapter.OnItemClickListener() {
            @Override
            public void onOpen(WLostFoundItem item) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new Intent(getContext(), WLoginActivity.class));
                    return;
                }
                Intent intent = new Intent(getContext(), WLostFoundDetailActivity.class);
                intent.putExtra(WLostFoundDetailActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
        loadItems(1, 10);
    }

    public void refresh() {
        loadItems(1, 10);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 从详情页返回时刷新数据，同步浏览量等变化
        loadItems(1, 10);
    }

    private void loadItems(int page, int pageSize) {
        android.util.Log.d("WLostAndFoundFragment", "开始加载失物招领数据: type=" + currentType + ", page=" + page + ", pageSize=" + pageSize);
        new Thread(() -> {
            List<WLostFoundItem> items = WLostFoundRepository.getItems(currentType, page, pageSize);
            android.util.Log.d("WLostAndFoundFragment", "获取到 " + items.size() + " 条失物招领数据");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    data.clear();
                    data.addAll(items);
                    android.util.Log.d("WLostAndFoundFragment", "UI更新: data.size()=" + data.size());
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }
}

