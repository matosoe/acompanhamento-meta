package io.github.matosoe.controlehoras.ui.goals;
import android.view.*; import android.widget.TextView; import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView; import java.util.*; import io.github.matosoe.controlehoras.R;
final class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.Holder>{
 interface Listener {void edit(@NonNull GoalRow row);} private final Listener listener; private final List<GoalRow> rows=new ArrayList<>(); GoalsAdapter(Listener l){listener=l;}
 void submit(List<GoalRow> value){int previous=rows.size();rows.clear();if(previous>0)notifyItemRangeRemoved(0,previous);rows.addAll(value);if(!value.isEmpty())notifyItemRangeInserted(0,value.size());}
 @NonNull public Holder onCreateViewHolder(@NonNull ViewGroup p,int t){return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_goal,p,false));}
 public void onBindViewHolder(@NonNull Holder h,int p){GoalRow r=rows.get(p);h.name.setText(r.category.name);h.detail.setText(r.detail);h.itemView.setOnClickListener(v->listener.edit(r));} public int getItemCount(){return rows.size();}
 static final class Holder extends RecyclerView.ViewHolder{final TextView name,detail;Holder(View v){super(v);name=v.findViewById(R.id.goal_name);detail=v.findViewById(R.id.goal_detail);}}
}
