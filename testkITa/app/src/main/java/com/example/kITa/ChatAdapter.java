package com.example.kITa;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final Context context;
    private final List<Message> messages;

    public ChatAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // Temporarily disable adding images in the chat
    public void addImage(Uri imageUri) {
        // Do nothing for now since images are not to be displayed
    }

    public void setMessages(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
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
            messageImage = itemView.findViewById(R.id.messageImage);
        }

        void bind(Message message) {
            // Only show text messages, hide image field
            messageText.setVisibility(View.VISIBLE);
            messageImage.setVisibility(View.GONE);
            messageText.setText(message.getText());

            timeStamp.setText(message.getFormattedTime());
            timeStamp.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (timeStamp.getVisibility() == View.VISIBLE) {
                    timeStamp.setVisibility(View.GONE);
                } else {
                    timeStamp.setVisibility(View.VISIBLE);
                }
            });

            if (message.getSenderId() == UserSession.getInstance().getId()) {
                itemView.setBackgroundResource(R.drawable.sender_message_background);
                messageText.setTextColor(Color.WHITE);
            } else {
                itemView.setBackgroundResource(R.drawable.receiver_message_background);
                messageText.setTextColor(context.getResources().getColor(R.color.green));
            }
        }
    }
}
