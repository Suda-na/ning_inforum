package com.example.android_java2.fragment.circle;

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
import com.example.android_java2.activity.CircleDetailActivity;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.adapter.CirclePostAdapter;
import com.example.android_java2.model.CirclePost;
import com.example.android_java2.repository.CircleRepository;
import com.example.android_java2.repository.CSessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CirclePostFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private FloatingActionButton fabPost;
    private TextView filterText;
    private ImageView filterIcon;
    private PopupWindow filterPopup;
    private LinearLayout subTabs;
    private TextView selectedTab;
    private CirclePostAdapter adapter;
    private List<CirclePost> data;
    private CSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_circle_post, container, false);

        sessionManager = CSessionManager.getInstance(requireContext());
        
        // 初始化RecyclerView
        initRecyclerView(view);
        
        // 初始化发布按钮
        initFabPost(view);
        
        // 初始化标签点击事件
        initTabClick(view);
        
        // 初始化过滤下拉菜单
        initFilterPopup(view);
        
        return view;
    }
    
    private void initRecyclerView(View view) {
        // 找到RecyclerView控件
        postsRecyclerView = view.findViewById(R.id.posts_recycler);
        
        // 设置布局管理器
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = CircleRepository.getPosts();
        adapter = new CirclePostAdapter(requireContext(), data, new CirclePostAdapter.OnPostActionListener() {
            @Override
            public void onOpen(CirclePost post) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new android.content.Intent(getContext(), LoginActivity.class));
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(), CircleDetailActivity.class);
                intent.putExtra(CircleDetailActivity.EXTRA_POST_ID, post.getId());
                startActivity(intent);
            }

            @Override
            public void onLike(CirclePost post) {
                post.toggleLike();
            }
        });
        postsRecyclerView.setAdapter(adapter);
    }
    
    private void initFabPost(View view) {
        // 找到发布按钮
        fabPost = view.findViewById(R.id.fab_post);
        
        // 注意：这里只初始化发布按钮，不添加点击事件
        // 点击事件将在后续对接后端时添加
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
                    
                    // 这里可以添加标签切换的逻辑，例如刷新动态列表
                }
            });
            
            // 设置初始选中的标签
            if (tab.getText().equals("校园干饭指南")) {
                selectedTab = tab;
            }
        }
    }
    
    private void initFilterPopup(View view) {
        // 找到过滤文本和图标
        filterText = view.findViewById(R.id.filter_text);
        filterIcon = view.findViewById(R.id.filter_icon);
        
        // 创建弹出菜单布局
        LinearLayout popupLayout = new LinearLayout(getActivity());
        popupLayout.setOrientation(LinearLayout.VERTICAL);
        int whiteColor = ContextCompat.getColor(requireContext(), android.R.color.white);
        popupLayout.setBackgroundColor(whiteColor);
        popupLayout.setPadding(20, 10, 20, 10);
        
        // 添加过滤选项
        final String[] filterOptions = {"新发", "新回", "热门"};
        for (final String option : filterOptions) {
            TextView optionText = new TextView(getActivity());
            optionText.setText(option);
            int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
            optionText.setTextColor(grayColor);
            optionText.setTextSize(12);
            optionText.setPadding(0, 10, 0, 10);
            optionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更新过滤文本
                    filterText.setText(option);
                    
                    // 关闭弹出菜单
                    if (filterPopup != null && filterPopup.isShowing()) {
                        filterPopup.dismiss();
                    }
                }
            });
            popupLayout.addView(optionText);
        }
        
        // 创建弹出菜单
        filterPopup = new PopupWindow(popupLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filterPopup.setFocusable(true);
        
        // 设置点击事件
        filterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示或隐藏弹出菜单
                if (filterPopup != null && filterPopup.isShowing()) {
                    filterPopup.dismiss();
                } else {
                    filterPopup.showAsDropDown(v, 0, 0);
                }
            }
        });
        
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示或隐藏弹出菜单
                if (filterPopup != null && filterPopup.isShowing()) {
                    filterPopup.dismiss();
                } else {
                    filterPopup.showAsDropDown(v, 0, 0);
                }
            }
        });
    }
}