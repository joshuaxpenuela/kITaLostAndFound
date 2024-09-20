package com.example.kITa;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class OlderItemsAdapter extends RecyclerView.Adapter<OlderItemsAdapter.ViewHolder> {

    private ArrayList<OlderItem> itemsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OlderItem item);
    }

    public void setOnItemClickListener(OlderItemsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public OlderItemsAdapter(ArrayList<OlderItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public OlderItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_olderlost, parent, false);
        return new OlderItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OlderItemsAdapter.ViewHolder holder, int position) {
        OlderItem item = itemsList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemLocation.setText(item.getItemLocation());
        holder.itemImage.setImageResource(R.drawable.ic_uploadphoto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        Log.d("OlderItemsAdapter", "Binding item: " + item.getItemName() + " - " + item.getDate() + " " + item.getTime() + " - Status: " + item.getStatus());
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
            itemImage = itemView.findViewById(R.id.itemImg);
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.location);
            time = itemView.findViewById(R.id.time);
        }
    }
}
