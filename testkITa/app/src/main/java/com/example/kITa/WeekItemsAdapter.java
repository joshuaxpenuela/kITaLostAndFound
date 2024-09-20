package com.example.kITa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class WeekItemsAdapter extends RecyclerView.Adapter<WeekItemsAdapter.ViewHolder> {

    private ArrayList<OlderItem> itemsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OlderItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WeekItemsAdapter(ArrayList<OlderItem> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_weekitems, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlderItem item = itemsList.get(position);

        if (holder.itemName != null) {
            holder.itemName.setText(item.getItemName());
        }
        if (holder.itemLocation != null) {
            holder.itemLocation.setText(item.getItemLocation());
        }
        if (holder.itemDate != null) {
            holder.itemDate.setText(item.getDate());
        }
        if (holder.itemTime != null) {
            holder.itemTime.setText(item.getTime());
        }
        if (holder.itemImage != null) {
            holder.itemImage.setImageResource(R.drawable.ic_uploadphoto);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemLocation, itemDate, itemTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImg);
            itemName = itemView.findViewById(R.id.itemName);
            itemLocation = itemView.findViewById(R.id.location);
            itemDate = itemView.findViewById(R.id.date);
            itemTime = itemView.findViewById(R.id.time);
        }
    }
}