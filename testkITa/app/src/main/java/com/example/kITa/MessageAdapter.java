package com.example.kITa;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private int currentUserId;

    public MessageAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeStamp;
        ImageView messageImage;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }

        void bind(Message message) {
            if (message.getMediaUrl() != null) {
                messageText.setVisibility(View.GONE);
            } else {
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.getText());
            }

            timeStamp.setText(message.getFormattedTime());
            timeStamp.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (timeStamp.getVisibility() == View.VISIBLE) {
                    timeStamp.setVisibility(View.GONE);
                } else {
                    timeStamp.setVisibility(View.VISIBLE);
                }
            });

            if (message.getSenderId() == currentUserId) {
                itemView.setBackgroundResource(R.drawable.sender_message_background);
                messageText.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.receiver_message_background);
                messageText.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
            }
        }
    }
}
