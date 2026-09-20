package io.github.matosoe.controlehoras.ui.daily;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.R;

public final class DailyTableAdapter extends RecyclerView.Adapter<DailyTableAdapter.Holder> {
    private final List<String> rows = new ArrayList<>();
    public void submit(List<String> values) { rows.clear(); rows.addAll(values); notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_table, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.text.setText(rows.get(position)); }
    @Override public int getItemCount() { return rows.size(); }
    static final class Holder extends RecyclerView.ViewHolder { final TextView text; Holder(TextView item) { super(item); text = item; } }
}
