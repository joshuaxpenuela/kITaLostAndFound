package com.example.kITa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClaimedItemsAdapter extends RecyclerView.Adapter<ClaimedItemsAdapter.ViewHolder> {
    private List<ClaimedItem> claimedItemsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int itemId);
    }

    public ClaimedItemsAdapter(List<ClaimedItem> claimedItemsList, OnItemClickListener listener) {
        this.claimedItemsList = claimedItemsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_claimeditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClaimedItem item = claimedItemsList.get(position);
        holder.itemNameTextView.setText(item.getItemName());
        holder.locationTextView.setText(item.getLocation());
    }

    @Override
    public int getItemCount() {
        return claimedItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            locationTextView = itemView.findViewById(R.id.location);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(claimedItemsList.get(position).getId());
                }
            });
        }
    }
}