package com.example.kITa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExistingUserChatAdapter extends RecyclerView.Adapter<ExistingUserChatAdapter.ChatViewHolder> {

    private final Context context;
    private List<UserConversation> conversations;

    public ExistingUserChatAdapter(Context context) {
        this.context = context;
        this.conversations = new ArrayList<>();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_userchatcard, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        UserConversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    // Method to update the adapter with new conversations
    public void setConversations(List<UserConversation> conversations) {
        this.conversations.clear();
        this.conversations.addAll(conversations);
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView senderName;
        private TextView lastMessage;
        private TextView messageTime;

        ChatViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.receiverName);
            lastMessage = itemView.findViewById(R.id.message);
            messageTime = itemView.findViewById(R.id.timeMessage);
        }

        void bind(UserConversation conversation) {
            senderName.setText("User: " + conversation.getSenderId()); // Display sender or receiver name
            lastMessage.setText(conversation.getMessage()); // Display last message
            messageTime.setText(conversation.getFormattedTime()); // Display formatted time
        }
    }
}
