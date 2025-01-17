package com.example.kITa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class TodayItemsAdapter extends RecyclerView.Adapter<TodayItemsAdapter.ViewHolder> {

    private ArrayList<TodayItem> itemsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TodayItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TodayItemsAdapter(ArrayList<TodayItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_todaylost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayItemsAdapter.ViewHolder holder, int position) {
        TodayItem item = itemsList.get(position);

        // Check if item is not null before setting details
        if (item != null) {
            holder.itemName.setText(item.getItemName());
            holder.itemDetails.setText(item.getItemDetails());

            // Load the image using Glide
            Glide.with(context)
                    .load(item.getImageUrl()) // Get image URL from the item
                    .placeholder(R.drawable.ic_uploadphoto) // Placeholder image
                    .into(holder.itemImage); // Load into ImageView

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        } else {
            // Handle case when item is null
            holder.itemName.setText("Unknown Item");
            holder.itemLocation.setText("Unknown Location");
            holder.itemDate.setText("N/A");
            holder.itemTime.setText("N/A");
            holder.itemImage.setImageResource(R.drawable.ic_uploadphoto); // Set placeholder image
            holder.itemDetails.setText("No Details");
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemLocation, itemDate, itemTime, itemDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImg);  // Bind the correct ImageView
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.location); // Ensure this matches your layout
            itemDate = itemView.findViewById(R.id.date); // Ensure this matches your layout
            itemTime = itemView.findViewById(R.id.time); // Ensure this matches your layout
            itemDetails = itemView.findViewById(R.id.itemDetails);
        }
    }
}
