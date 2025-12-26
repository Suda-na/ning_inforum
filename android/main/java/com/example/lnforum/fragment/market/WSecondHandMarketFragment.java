package com.example.lnforum.fragment.market;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.activity.WLoginActivity;
import com.example.lnforum.adapter.WSecondHandAdapter;
import com.example.lnforum.model.WSecondHandItem;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.repository.WSecondHandRepository;
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.activity.WSecondHandDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSecondHandMarketFragment extends Fragment {

    private RecyclerView listView;
    private WSecondHandAdapter adapter;
    private List<WSecondHandItem> data;
    private WSessionManager sessionManager;
    private Integer currentTagId = null;
    private String currentSortType = null; // newest或其他
    private LinearLayout subTabs;
    private TextView selectedTab;
    private Map<String, Integer> tagNameToIdMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_hand_market, container, false);
        initList(view);
        loadTags(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && data != null) {
            loadItems(1, 10);
        }
    }

    private void initList(View view) {
        listView = view.findViewById(R.id.second_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionManager = WSessionManager.getInstance(requireContext());
        data = new ArrayList<>();
        adapter = new WSecondHandAdapter(requireContext(), data, new WSecondHandAdapter.OnItemClickListener() {
            @Override
            public void onOpen(WSecondHandItem item) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new Intent(getContext(), WLoginActivity.class));
                    return;
                }
                Intent intent = new Intent(getContext(), WSecondHandDetailActivity.class);
                intent.putExtra(WSecondHandDetailActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
        loadItems(1, 10);
    }

    public void refresh() {
        loadItems(1, 10);
    }

    private void loadItems(int page, int pageSize) {
        new Thread(() -> {
            List<WSecondHandItem> items = WSecondHandRepository.getItems(page, pageSize, currentTagId, currentSortType);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    data.clear();
                    data.addAll(items);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void loadTags(View view) {
        subTabs = view.findViewById(R.id.sub_tabs);
        if (subTabs == null) return;
        
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("categoryId", "3"); // 二手集市分类
                
                WApiClient.ApiResponse response = WApiClient.get("/app/tag/list", params);
                if (response.success && response.getCode() == 200) {
                    Object dataObj = response.getData();
                    if (dataObj instanceof JSONArray) {
                        JSONArray tagsArray = (JSONArray) dataObj;
                        List<String> tagNames = new ArrayList<>();
                        
                        for (int i = 0; i < tagsArray.length(); i++) {
                            JSONObject tagJson = tagsArray.getJSONObject(i);
                            String tagName = tagJson.getString("name");
                            Integer tagId = tagJson.getInt("tagId");
                            tagNames.add(tagName);
                            tagNameToIdMap.put(tagName, tagId);
                        }
                        
                        new Handler(Looper.getMainLooper()).post(() -> {
                            updateTagTabs(tagNames);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e("WSecondHandMarketFragment", "加载标签列表失败", e);
            }
        }).start();
    }
    
    private void updateTagTabs(List<String> tagNames) {
        if (subTabs == null) return;
        
        subTabs.removeAllViews();
        
        // 添加"全部"标签
        TextView allTab = createTagTab("全部");
        allTab.setOnClickListener(v -> {
            selectTag(null, allTab);
        });
        subTabs.addView(allTab);
        
        // 添加其他标签
        for (String tagName : tagNames) {
            TextView tab = createTagTab(tagName);
            tab.setOnClickListener(v -> {
                Integer tagId = tagNameToIdMap.get(tagName);
                selectTag(tagId, tab);
            });
            subTabs.addView(tab);
        }
        
        // 添加"新发"排序标签
        TextView newestTab = createTagTab("新发");
        newestTab.setOnClickListener(v -> {
            selectSort("newest", newestTab);
        });
        subTabs.addView(newestTab);
        
        // 默认选中"全部"（第一个标签）
        selectTag(null, allTab);
    }
    
    private void selectSort(String sortType, TextView tab) {
        // 取消之前选中的标签样式
        if (selectedTab != null) {
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            selectedTab.setTextColor(grayColor);
            selectedTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        
        // 设置当前标签为选中状态
        selectedTab = tab;
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        selectedTab.setTextColor(primaryColor);
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // 更新排序条件并重新加载数据
        currentSortType = sortType;
        currentTagId = null; // 清除标签筛选
        loadItems(1, 10);
    }
    
    private TextView createTagTab(String text) {
        TextView tab = new TextView(getActivity());
        tab.setText(text);
        int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
        tab.setTextColor(grayColor);
        tab.setTextSize(12);
        tab.setPadding(16, 8, 16, 8);
        return tab;
    }
    
    private void selectTag(Integer tagId, TextView tab) {
        // 取消之前选中的标签样式
        if (selectedTab != null) {
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            selectedTab.setTextColor(grayColor);
            selectedTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        
        // 设置当前标签为选中状态
        selectedTab = tab;
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        selectedTab.setTextColor(primaryColor);
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // 更新筛选条件并重新加载数据
        currentTagId = tagId;
        currentSortType = null; // 清除排序筛选
        loadItems(1, 10);
    }
}

