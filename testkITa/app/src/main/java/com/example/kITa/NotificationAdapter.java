package com.example.kITa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<NotificationItem> notifications;

    public NotificationAdapter(Context context, List<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notificationlost, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);

        // Set notification title
        holder.notifTitle.setText(notification.getTitle());
        holder.notifTitle.setTextColor(context.getResources().getColor(R.color.green));

        // Set notification message
        holder.notifPreview.setText(notification.getMessage());
        holder.notifPreview.setTextColor(context.getResources().getColor(R.color.green));

        // Set notification icon
        holder.itemImg.setImageResource(R.drawable.ic_notification);
        holder.itemImg.setColorFilter(context.getResources().getColor(R.color.green));

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            // You can handle click events here if needed
            // For example, showing more details or marking as read
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        TextView notifTitle, notifPreview;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            notifTitle = itemView.findViewById(R.id.notifTitle);
            notifPreview = itemView.findViewById(R.id.notifPreview);
        }
    }

    public void updateNotifications(List<NotificationItem> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    public void addNotification(NotificationItem notification) {
        notifications.add(notification);
        notifyItemInserted(notifications.size() - 1);
    }

    public void clearNotifications() {
        int size = notifications.size();
        notifications.clear();
        notifyItemRangeRemoved(0, size);
    }
}