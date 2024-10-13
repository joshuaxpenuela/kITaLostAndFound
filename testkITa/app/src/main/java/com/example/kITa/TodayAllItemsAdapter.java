package com.example.kITa;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class TodayAllItemsAdapter extends RecyclerView.Adapter<TodayAllItemsAdapter.ViewHolder> {

    private ArrayList<TodayItem> itemsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TodayItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TodayAllItemsAdapter(ArrayList<TodayItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_olderlost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodayItem item = itemsList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemLocation.setText(item.getItemLocation());

        // Load the image from the provided imageUrl (img1) using Glide
        Glide.with(context)
                .load(item.getImageUrl()) // Load image from the URL
                .placeholder(R.drawable.ic_uploadphoto)  // Placeholder image in case image doesn't load
                .into(holder.itemImage);  // Load into itemImage (ImageView)

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        Log.d("TodayAllItemsAdapter", "Binding item: " + item.getItemName() + " - " + item.getDate() + " " + item.getTime() + " - Status: " + item.getStatus());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemLocation, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImg);  // Correctly bind the ImageView (itemImg)
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.location);
            time = itemView.findViewById(R.id.time);  // Time field if available
        }
    }
}
