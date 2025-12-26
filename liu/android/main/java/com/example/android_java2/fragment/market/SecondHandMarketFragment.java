package com.example.android_java2.fragment.market;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.activity.SecondHandDetailActivity;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.adapter.SecondHandAdapter;
import com.example.android_java2.model.SecondHandItem;
import com.example.android_java2.repository.SecondHandRepository;
import com.example.android_java2.repository.CSessionManager;

import java.util.List;

public class SecondHandMarketFragment extends Fragment {

    private LinearLayout subTabs;
    private TextView selectedTab;
    private RecyclerView listView;
    private SecondHandAdapter adapter;
    private TextView filterText;
    private ImageView filterIcon;
    private PopupWindow filterPopup;
    private List<SecondHandItem> data;
    private CSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_second_hand_market, container, false);
        
        // 初始化标签点击事件
        initTabClick(view);

        // 初始化列表
        initList(view);

        // 初始化过滤
        initFilter(view);
        
        return view;
    }
    
    private void initTabClick(View view) {
        // 找到标签栏容器
        subTabs = view.findViewById(R.id.sub_tabs);
        
        // 获取所有标签
        for (int i = 0; i < subTabs.getChildCount(); i++) {
            final TextView tab = (TextView) subTabs.getChildAt(i);
            
            // 设置点击事件
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    
                    // 这里可以添加标签切换的逻辑，例如刷新二手商品列表
                }
            });
            
            // 设置初始选中的标签
            if (tab.getText().equals("校园二手")) {
                selectedTab = tab;
            }
        }
    }

    private void initList(View view) {
        listView = view.findViewById(R.id.second_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = SecondHandRepository.getItems();
        sessionManager = CSessionManager.getInstance(requireContext());
        adapter = new SecondHandAdapter(requireContext(), data, new SecondHandAdapter.OnItemClickListener() {
            @Override
            public void onOpen(SecondHandItem item) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new android.content.Intent(getContext(), LoginActivity.class));
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(), SecondHandDetailActivity.class);
                intent.putExtra(SecondHandDetailActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }

    private void initFilter(View view) {
        filterText = view.findViewById(R.id.filter_text);
        filterIcon = view.findViewById(R.id.filter_icon);

        LinearLayout popupLayout = new LinearLayout(getActivity());
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        int whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white);
        popupLayout.setBackgroundColor(whiteColor);
        popupLayout.setPadding(20, 10, 20, 10);

        final String[] filterOptions = {"新发", "价格低", "浏览高"};
        for (final String option : filterOptions) {
            TextView optionText = new TextView(getActivity());
            optionText.setText(option);
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            optionText.setTextColor(grayColor);
            optionText.setTextSize(12);
            optionText.setPadding(0, 10, 0, 10);
            optionText.setOnClickListener(v -> {
                filterText.setText(option);
                if (filterPopup != null && filterPopup.isShowing()) {
                    filterPopup.dismiss();
                }
            });
            popupLayout.addView(optionText);
        }

        filterPopup = new PopupWindow(popupLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filterPopup.setFocusable(true);

        View.OnClickListener toggle = v -> {
            if (filterPopup != null && filterPopup.isShowing()) {
                filterPopup.dismiss();
            } else {
                filterPopup.showAsDropDown(v, 0, 0);
            }
        };
        filterText.setOnClickListener(toggle);
        filterIcon.setOnClickListener(toggle);
    }
}