package io.github.matosoe.controlehoras.ui.export;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;

final class ExportEntryAdapter extends RecyclerView.Adapter<ExportEntryAdapter.Holder> {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final List<ExportEntryItem> items = new ArrayList<>();
    void submit(List<ExportEntryItem> values) { int previous=items.size(); items.clear(); if(previous>0) notifyItemRangeRemoved(0,previous); items.addAll(values); if(!values.isEmpty()) notifyItemRangeInserted(0,values.size()); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) { return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_export_entry, parent, false)); }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        ExportEntryItem item = items.get(position); ZoneId zone = ZoneId.systemDefault();
        holder.category.setText(item.categoryName);
        holder.time.setText(holder.itemView.getContext().getString(R.string.export_entry_time,
                Instant.ofEpochMilli(item.entry.startEpochMillis).atZone(zone).format(DATE_TIME),
                Instant.ofEpochMilli(item.entry.endEpochMillis).atZone(zone).format(DATE_TIME)));
        holder.duration.setText(new DurationFormatter().csv(item.entry.durationSeconds));
    }
    @Override public int getItemCount() { return items.size(); }
    static final class Holder extends RecyclerView.ViewHolder { final TextView category, time, duration; Holder(View view) { super(view); category=view.findViewById(R.id.export_entry_category); time=view.findViewById(R.id.export_entry_time); duration=view.findViewById(R.id.export_entry_duration); } }
}
