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
    private final List<TodayEntryItem> items = new ArrayList<>();
    void submitList(@NonNull List<TodayEntryItem> newItems) { items.clear(); items.addAll(newItems); notifyDataSetChanged(); }
    @NonNull @Override public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_entry, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        TodayEntryItem item = items.get(position); holder.time.setText(item.timeRange); holder.category.setText(item.categoryName); holder.duration.setText(item.duration);
    }
    @Override public int getItemCount() { return items.size(); }
    static final class EntryViewHolder extends RecyclerView.ViewHolder {
        final TextView time, category, duration;
        EntryViewHolder(@NonNull View view) { super(view); time = view.findViewById(R.id.entry_time); category = view.findViewById(R.id.entry_category); duration = view.findViewById(R.id.entry_duration); }
    }
}
