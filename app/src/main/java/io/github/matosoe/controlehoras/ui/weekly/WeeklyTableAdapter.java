package io.github.matosoe.controlehoras.ui.weekly;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.R;
public final class WeeklyTableAdapter extends RecyclerView.Adapter<WeeklyTableAdapter.Holder> {
    public interface Listener { void onWeekSelected(int index); }
    private final List<String> rows = new ArrayList<>(); private final List<Integer> weekIndexes = new ArrayList<>(); private final Listener listener;
    public WeeklyTableAdapter(Listener listener) { this.listener = listener; }
    public void submit(List<String> values, List<Integer> indexes) { int previous=rows.size(); rows.clear(); weekIndexes.clear(); if(previous>0) notifyItemRangeRemoved(0,previous); rows.addAll(values); weekIndexes.addAll(indexes); if(!values.isEmpty()) notifyItemRangeInserted(0,values.size()); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) { return new Holder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_table, parent, false)); }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.text.setText(rows.get(position)); holder.itemView.setOnClickListener(v -> listener.onWeekSelected(weekIndexes.get(position))); }
    @Override public int getItemCount() { return rows.size(); }
    static final class Holder extends RecyclerView.ViewHolder { final TextView text; Holder(TextView item) { super(item); text = item; } }
}
