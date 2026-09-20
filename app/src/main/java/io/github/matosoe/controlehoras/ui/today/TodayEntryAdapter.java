package io.github.matosoe.controlehoras.ui.today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.R;

final class TodayEntryAdapter extends RecyclerView.Adapter<TodayEntryAdapter.EntryViewHolder> {
    interface OnEntryClickListener { void onEntryClick(@NonNull TodayEntryItem item); }
    private final List<TodayEntryItem> items = new ArrayList<>();
    private final OnEntryClickListener listener;
    TodayEntryAdapter(@NonNull OnEntryClickListener listener) { this.listener = listener; }
    void submitList(@NonNull List<TodayEntryItem> newItems) { int previous=items.size(); items.clear(); if(previous>0) notifyItemRangeRemoved(0,previous); items.addAll(newItems); if(!newItems.isEmpty()) notifyItemRangeInserted(0,newItems.size()); }
    @NonNull @Override public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_entry, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        TodayEntryItem item = items.get(position); holder.time.setText(item.timeRange); holder.category.setText(item.categoryName); holder.duration.setText(item.duration); holder.itemView.setOnClickListener(v -> listener.onEntryClick(item));
    }
    @Override public int getItemCount() { return items.size(); }
    static final class EntryViewHolder extends RecyclerView.ViewHolder {
        final TextView time, category, duration;
        EntryViewHolder(@NonNull View view) { super(view); time = view.findViewById(R.id.entry_time); category = view.findViewById(R.id.entry_category); duration = view.findViewById(R.id.entry_duration); }
    }
}
