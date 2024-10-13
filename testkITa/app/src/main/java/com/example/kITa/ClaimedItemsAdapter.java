package com.example.kITa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClaimedItemsAdapter extends RecyclerView.Adapter<ClaimedItemsAdapter.ViewHolder> {

    private List<ClaimedItem> itemsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int itemId);
    }

    public ClaimedItemsAdapter(List<ClaimedItem> itemsList, OnItemClickListener listener) {
        this.itemsList = itemsList;
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
        ClaimedItem item = itemsList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemLocation.setText(item.getLocation());

        // Load img1 into ImageView using Glide
        Glide.with(holder.itemImage.getContext())
                .load(item.getImg1())  // Load imageUrl from ClaimedItem
                .placeholder(R.drawable.ic_uploadphoto) // Placeholder image
                .into(holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImg);  // ImageView for img1
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.location);
        }
    }
}
