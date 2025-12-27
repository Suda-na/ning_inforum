package com.example.lnforum.fragment.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lnforum.R;
import com.example.lnforum.activity.ChatActivity;
import com.example.lnforum.adapter.ConversationsAdapter;
import com.example.lnforum.model.Conversation;
import com.example.lnforum.repository.LMessageRepository;

import java.util.List;

public class LFanMessageFragment extends Fragment {

    private RecyclerView messageRecyclerView;
    private ConversationsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_message_content, container, false);
        
        // 初始化RecyclerView
        messageRecyclerView = view.findViewById(R.id.message_recycler);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        // 立即设置一个空的adapter，避免"No adapter attached"警告
        adapter = new ConversationsAdapter(new java.util.ArrayList<>(), this::startChat);
        messageRecyclerView.setAdapter(adapter);
        
        // 加载数据
        loadConversations();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新数据
        loadConversations();
        // 刷新红点数
        Fragment parent = getParentFragment();
        if (parent instanceof WMessageFragment) {
            ((WMessageFragment) parent).refreshUnreadCounts();
        }
    }

    private void loadConversations() {
        if (getContext() == null) return;
        
        LMessageRepository.getFanConversations(getContext(), new LMessageRepository.ConversationCallback() {
            @Override
            public void onResult(List<Conversation> conversations, String error) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    if (error != null) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    initAdapter(conversations);
                });
            }
        });
    }

    private void initAdapter(List<Conversation> data) {
        if (messageRecyclerView == null) return;
        // 更新adapter的数据，而不是创建新的adapter
        adapter = new ConversationsAdapter(data, this::startChat);
        messageRecyclerView.setAdapter(adapter);
    }

    private void startChat(Conversation conversation) {
        if (getContext() == null) return;
        android.content.Intent intent = new android.content.Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_TITLE, conversation.getTitle());
        intent.putExtra(ChatActivity.EXTRA_TYPE, conversation.getType());
        intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.getId());
        startActivity(intent);
    }
}

