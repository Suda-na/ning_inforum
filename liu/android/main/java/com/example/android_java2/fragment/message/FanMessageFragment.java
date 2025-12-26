package com.example.android_java2.fragment.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_java2.adapter.ConversationsAdapter;
import com.example.android_java2.R;
import com.example.android_java2.activity.ChatActivity;
import com.example.android_java2.model.Conversation;
import com.example.android_java2.repository.MessageRepository;

import java.util.List;

public class FanMessageFragment extends Fragment {

    private RecyclerView messageRecyclerView;
    private ConversationsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_message_content, container, false);
        
        // 初始化RecyclerView
        messageRecyclerView = view.findViewById(R.id.message_recycler);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        return view;
    }

    private void initAdapter() {
        List<Conversation> data = MessageRepository.getConversations("fan");
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