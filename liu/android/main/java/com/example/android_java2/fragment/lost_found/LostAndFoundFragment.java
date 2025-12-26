package com.example.android_java2.fragment.lost_found;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.R;
import com.example.android_java2.activity.LostFoundDetailActivity;
import com.example.android_java2.activity.LoginActivity;
import com.example.android_java2.adapter.LostFoundAdapter;
import com.example.android_java2.model.LostFoundItem;
import com.example.android_java2.repository.LostFoundRepository;
import com.example.android_java2.repository.CSessionManager;

import java.util.ArrayList;
import java.util.List;

public class LostAndFoundFragment extends Fragment {

    private TextView tabLost;
    private TextView tabFound;
    private RecyclerView listView;
    private LostFoundAdapter adapter;
    private List<LostFoundItem> data;
    private boolean isLostTab = true;
    private CSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);
        tabLost = view.findViewById(R.id.tab_lost);
        tabFound = view.findViewById(R.id.tab_found);
        listView = view.findViewById(R.id.lost_list);
        initTabClick();
        initList();
        return view;
    }

    private void initTabClick() {
        tabLost.setOnClickListener(v -> setSelected(true));
        tabFound.setOnClickListener(v -> setSelected(false));
        setSelected(true);
    }

    private void setSelected(boolean isLost) {
        int primary = ContextCompat.getColor(requireContext(), R.color.primary_blue);
        int gray = ContextCompat.getColor(requireContext(), R.color.nav_unselected);
        tabLost.setTextColor(isLost ? primary : gray);
        tabLost.setTypeface(null, isLost ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        tabFound.setTextColor(isLost ? gray : primary);
        tabFound.setTypeface(null, isLost ? android.graphics.Typeface.NORMAL : android.graphics.Typeface.BOLD);
        isLostTab = isLost;
        applyFilter();
    }

    private void initList() {
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = LostFoundRepository.getItems();
        sessionManager = CSessionManager.getInstance(requireContext());
        adapter = new LostFoundAdapter(requireContext(), filteredData(), new LostFoundAdapter.OnItemClickListener() {
            @Override
            public void onOpen(LostFoundItem item) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new android.content.Intent(getContext(), LoginActivity.class));
                    return;
                }
                android.content.Intent intent = new android.content.Intent(getContext(), LostFoundDetailActivity.class);
                intent.putExtra(LostFoundDetailActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }

    private void applyFilter() {
        if (adapter != null) {
            adapter.setData(filteredData());
        }
    }

    private List<LostFoundItem> filteredData() {
        if (data == null) return new ArrayList<>();
        List<LostFoundItem> result = new ArrayList<>();
        String target = isLostTab ? "失物" : "招领";
        for (LostFoundItem item : data) {
            if (target.equals(item.getTag())) {
                result.add(item);
            }
        }
        return result;
    }
}