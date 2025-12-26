package com.example.android_java2.activity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.android_java2.R;

public class SearchActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText searchInput;
    private TextView tabDynamic;
    private TextView tabErrand;
    private TextView tabSecondHand;
    private TextView tabLostFound;
    private TextView tabUser;
    private TextView selectedTab;
    private View tabsContainer;
    private View historyContainer;
    private View resultsContainer;
    private TextView resultDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        View root = findViewById(R.id.search_root);
        applyLightStatusBar(root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        // 初始化控件
        initViews();

        // 设置返回按钮点击事件
        setBackButtonClickListener();

        // 设置标签点击事件
        setTabClickListeners();

        // 搜索输入
        setSearchAction();

        // 整个搜索条点击时也聚焦输入
        View searchBar = findViewById(R.id.search_bar_container);
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                searchInput.requestFocus();
                searchInput.performClick();
            });
        }
    }

    private void initViews() {
        // 找到返回按钮
        btnBack = findViewById(R.id.btn_back);
        searchInput = findViewById(R.id.search_input);

        // 找到标签
        tabDynamic = findViewById(R.id.tab_dynamic);
        tabErrand = findViewById(R.id.tab_errand);
        tabSecondHand = findViewById(R.id.tab_second_hand);
        tabLostFound = findViewById(R.id.tab_lost_found);
        tabUser = findViewById(R.id.tab_user);

        // 设置初始选中的标签
        selectedTab = tabDynamic;

        tabsContainer = findViewById(R.id.tabs_container);
        historyContainer = findViewById(R.id.history_container);
        resultsContainer = findViewById(R.id.results_container);
        resultDesc = findViewById(R.id.search_result_desc);

        // 进入页面先显示历史记录
        historyContainer.setVisibility(View.VISIBLE);
        tabsContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.GONE);
    }

    private void setBackButtonClickListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上一页
                finish();
            }
        });
    }

    private void setTabClickListeners() {
        // 动态标签点击事件
        tabDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabDynamic);
            }
        });

        // 跑腿标签点击事件
        tabErrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabErrand);
            }
        });

        // 二手集市标签点击事件
        tabSecondHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabSecondHand);
            }
        });

        // 失物招领标签点击事件
        tabLostFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabLostFound);
            }
        });

        // 用户标签点击事件
        tabUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(tabUser);
            }
        });
    }

    private void switchTab(TextView newTab) {
        // 取消之前选中的标签样式
        if (selectedTab != null) {
            int grayColor = ContextCompat.getColor(this, R.color.nav_unselected);
            selectedTab.setTextColor(grayColor);
            selectedTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        // 设置当前标签为选中状态
        selectedTab = newTab;
        int primaryColor = ContextCompat.getColor(this, R.color.primary_blue);
        selectedTab.setTextColor(primaryColor);
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);

        // 这里可以添加标签切换的逻辑，例如刷新搜索结果列表
    }

    private void setSearchAction() {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchInput.getText().toString().trim();
                performSearch(keyword);
                return true;
            }
            return false;
        });
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            // 清空时恢复历史搜索
            historyContainer.setVisibility(View.VISIBLE);
            tabsContainer.setVisibility(View.GONE);
            resultsContainer.setVisibility(View.GONE);
            return;
        }

        historyContainer.setVisibility(View.GONE);
        tabsContainer.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.VISIBLE);
        resultDesc.setText("搜索 \"" + keyword + "\" 的动态/跑腿/二手集市/失物招领/用户结果");
    }

    private void applyLightStatusBar(View root) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_white));
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(root);
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }
}
