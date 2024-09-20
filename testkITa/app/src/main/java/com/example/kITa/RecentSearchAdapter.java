package com.example.kITa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
    private List<String> recentSearches;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RecentSearchAdapter(List<String> recentSearches, OnItemClickListener listener) {
        this.recentSearches = recentSearches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(recentSearches.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView searchText;
        ImageButton deleteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            searchText = itemView.findViewById(R.id.recentSearchText);
            deleteBtn = itemView.findViewById(R.id.deleteRecentSearchBtn);
        }

        void bind(final String item, final OnItemClickListener listener) {
            searchText.setText(item);
            deleteBtn.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }
    }
}