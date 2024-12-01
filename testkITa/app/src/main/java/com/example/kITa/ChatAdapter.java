package com.example.kITa;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Message> messages;
    private Context context;
    private int currentUserId;

    public ChatAdapter(Context context, List<Message> messages, int currentUserId) {
        this.context = context;
        this.messages = new ArrayList<>(messages);
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeStamp;
        ConstraintLayout messageContainer;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timeStamp.setText(message.getFormattedTime());

            // Determine message bubble color and alignment based on sender
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageText.getLayoutParams();

            if (message.getSenderId() == currentUserId) {
                // User's messages (right side)
                params.horizontalBias = 1.0f; // Align to right
                messageText.setBackgroundResource(R.drawable.sender_message_background);
                messageText.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                // Admin's messages (left side)
                params.horizontalBias = 0.0f; // Align to left
                messageText.setBackgroundResource(R.drawable.receiver_message_background);
                messageText.setTextColor(context.getResources().getColor(R.color.green));
            }

            messageText.setLayoutParams(params);
        }
    }
}
