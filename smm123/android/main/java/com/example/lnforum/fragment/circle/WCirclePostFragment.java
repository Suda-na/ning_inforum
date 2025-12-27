package com.example.lnforum.fragment.circle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import com.example.lnforum.R;
import com.example.lnforum.activity.WCircleDetailActivity;
import com.example.lnforum.activity.WLoginActivity;
import com.example.lnforum.adapter.WCirclePostAdapter;
import com.example.lnforum.model.WCirclePost;
import com.example.lnforum.repository.WApiClient;
import com.example.lnforum.repository.WCircleRepository;
import com.example.lnforum.repository.WSessionManager;
import com.example.lnforum.repository.CSessionManager;
import com.example.lnforum.model.CUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WCirclePostFragment extends Fragment {

    private static final String TAG = "WCirclePostFragment";
    private RecyclerView postsRecyclerView;
    private FloatingActionButton fabPost;
    private TextView filterText;
    private ImageView filterIcon;
    private PopupWindow filterPopup;
    private LinearLayout subTabs;
    private TextView selectedTab;
    private WCirclePostAdapter adapter;
    private List<WCirclePost> data;
    private WSessionManager sessionManager;
    private CSessionManager cSessionManager;
    
    // 当前筛选状态
    private Integer currentTagId = null;
    private String currentSortType = "newest"; // newest, hottest, mostComments
    private Map<String, Integer> tagNameToIdMap = new HashMap<>(); // 标签名到ID的映射

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_circle_post, container, false);

        sessionManager = WSessionManager.getInstance(requireContext());
        cSessionManager = CSessionManager.getInstance(requireContext());
        
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
        data = new ArrayList<>();
        adapter = new WCirclePostAdapter(requireContext(), data, new WCirclePostAdapter.OnPostActionListener() {
            @Override
            public void onOpen(WCirclePost post) {
                if (!cSessionManager.isLoggedIn()) {
                    startActivity(new android.content.Intent(getContext(), WLoginActivity.class));
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(), WCircleDetailActivity.class);
                intent.putExtra(WCircleDetailActivity.EXTRA_POST_ID, post.getId());
                startActivity(intent);
            }

            @Override
            public void onLike(WCirclePost post) {
                if (!cSessionManager.isLoggedIn()) {
                    startActivity(new android.content.Intent(getContext(), WLoginActivity.class));
                    return;
                }
                
                // 调用后端API进行点赞/取消点赞
                CUser currentUser = cSessionManager.getCurrentCUser();
                if (currentUser == null) {
                    Log.e(TAG, "用户未登录，无法点赞");
                    return;
                }
                Integer userId = currentUser.getUserId();
                
                new Thread(() -> {
                    boolean success = WCircleRepository.toggleLike(post.getId(), userId);
                    if (success) {
                        // 重新获取帖子详情以同步点赞状态和数量
                        try {
                            WCirclePost updatedPost = WCircleRepository.getPost(post.getId(), userId);
                            if (updatedPost != null) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    // 更新帖子数据
                                    int position = data.indexOf(post);
                                    if (position >= 0) {
                                        data.set(position, updatedPost);
                                        adapter.notifyItemChanged(position);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "重新获取帖子详情失败", e);
                            // 如果获取失败，至少更新本地状态
                            new Handler(Looper.getMainLooper()).post(() -> {
                                post.toggleLike();
                                int position = data.indexOf(post);
                                if (position >= 0) {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "点赞操作失败");
                    }
                }).start();
            }
        });
        postsRecyclerView.setAdapter(adapter);
        
        // 加载数据
        loadPosts();
        loadTags();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从详情页返回时刷新数据，同步浏览量等变化
        loadPosts();
    }
    
    /**
     * 刷新数据（供下拉刷新调用）
     */
    public void refresh() {
        loadPosts();
    }
    
    /**
     * 从后端加载帖子列表
     */
    private void loadPosts() {
        new Thread(() -> {
            try {
                // 获取当前登录用户ID（如果已登录）
                Integer userId = null;
                if (cSessionManager.isLoggedIn()) {
                    CUser currentUser = cSessionManager.getCurrentCUser();
                    if (currentUser != null) {
                        userId = currentUser.getUserId();
                    }
                }
                
                List<WCirclePost> posts = WCircleRepository.getPosts(1, 1, 20, currentTagId, currentSortType, userId);
                new Handler(Looper.getMainLooper()).post(() -> {
                    data.clear();
                    data.addAll(posts);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "加载帖子列表失败", e);
            }
        }).start();
    }
    
    /**
     * 从后端加载标签列表
     */
    private void loadTags() {
        new Thread(() -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("categoryId", "1"); // 动态分类
                
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
                Log.e(TAG, "加载标签列表失败", e);
            }
        }).start();
    }
    
    /**
     * 更新标签标签页
     */
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
        
        // 默认选中"全部"
        if (subTabs.getChildCount() > 0) {
            selectTag(null, (TextView) subTabs.getChildAt(0));
        }
    }
    
    /**
     * 创建标签标签页
     */
    private TextView createTagTab(String text) {
        TextView tab = new TextView(getActivity());
        tab.setText(text);
        int grayColor = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
        tab.setTextColor(grayColor);
        tab.setTextSize(12);
        
        // 设置LayoutParams，确保垂直居中
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        tab.setLayoutParams(params);
        tab.setGravity(android.view.Gravity.CENTER);
        tab.setPadding(16, 0, 16, 0);
        
        return tab;
    }
    
    /**
     * 选择标签
     */
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
        loadPosts();
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
        // 标签会在loadTags()中动态加载
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
        final String[] filterOptions = {"新发", "热门", "最多评论"};
        final String[] sortTypes = {"newest", "hottest", "mostComments"};
        for (int i = 0; i < filterOptions.length; i++) {
            final String option = filterOptions[i];
            final String sortType = sortTypes[i];
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
                    
                    // 更新排序类型并重新加载数据
                    currentSortType = sortType;
                    loadPosts();
                    
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

