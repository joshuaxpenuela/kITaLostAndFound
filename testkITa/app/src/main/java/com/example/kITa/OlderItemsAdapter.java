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

public class OlderItemsAdapter extends RecyclerView.Adapter<OlderItemsAdapter.ViewHolder> {

    private ArrayList<OlderItem> itemsList;
    private Context context; // Context for Glide
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OlderItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Constructor accepting both item list and context
    public OlderItemsAdapter(ArrayList<OlderItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context; // Initialize context for image loading
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_olderlost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlderItem item = itemsList.get(position);

        // Check for null values before setting them
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
            holder.itemDetails.setText("No Details");
            holder.itemDate.setText("N/A");
            holder.itemTime.setText("N/A");
            holder.itemImage.setImageResource(R.drawable.ic_uploadphoto); // Set placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDetails, itemDate, itemTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImg); // Ensure this matches your layout
            itemName = itemView.findViewById(R.id.itemName); // Ensure this matches your layout
            itemDetails = itemView.findViewById(R.id.itemDetails);
            itemDate = itemView.findViewById(R.id.date); // Ensure this matches your layout
            itemTime = itemView.findViewById(R.id.time); // Ensure this matches your layout
        }
    }
}
