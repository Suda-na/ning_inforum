package com.example.lnforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lnforum.R;
import com.example.lnforum.model.WSecondHandItem;
import com.example.lnforum.repository.WSecondHandRepository;

import java.util.List;

public class WSecondHandDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private WSecondHandItem item;
    private TextView title, desc, seller, time, tag, price, location, views;
    private ImageView backBtn;
    private LinearLayout imagesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_hand_detail);

        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        
        new Thread(() -> {
            WSecondHandItem loadedItem = WSecondHandRepository.getItem(itemId);
            runOnUiThread(() -> {
                if (loadedItem == null) {
                    Toast.makeText(this, "商品不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                item = loadedItem;
                initViews();
                bindData();
                initActions();
            });
        }).start();
    }

    private void initViews() {
        backBtn = findViewById(R.id.detail_back);
        title = findViewById(R.id.detail_title);
        desc = findViewById(R.id.detail_desc);
        seller = findViewById(R.id.detail_seller);
        time = findViewById(R.id.detail_time);
        tag = findViewById(R.id.detail_tag);
        price = findViewById(R.id.detail_price);
        location = findViewById(R.id.detail_location);
        views = findViewById(R.id.detail_views);
        imagesContainer = findViewById(R.id.detail_images_container);
    }

    private void bindData() {
        title.setText(item.getTitle());
        desc.setText(item.getDesc());
        seller.setText(item.getSeller());
        time.setText(item.getTime());
        tag.setText(item.getTag());
        price.setText(item.getPrice());
        location.setText(item.getLocation());
        views.setText(item.getViews() + "浏览");
    }

    private void initActions() {
        backBtn.setOnClickListener(v -> finish());
    }
}

