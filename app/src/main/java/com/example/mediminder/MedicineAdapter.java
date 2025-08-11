package com.example.mediminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executors;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.VH> {

    private final Context context;
    private final List<Medicine> list;
    private final MedicineDatabase db;

    public MedicineAdapter(Context context, List<Medicine> list) {
        this.context = context;
        this.list = list;
        this.db = MedicineDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Medicine m = list.get(position);
        holder.tvName.setText(m.getName());
        holder.tvTime.setText(String.format("%02d:%02d", m.getHour(), m.getMinute()));

        holder.btnTaken.setEnabled(m.isPending());
        holder.btnMissed.setEnabled(m.isPending());

        holder.btnTaken.setOnClickListener(v -> {
            MissedDoseHandler.markAsTaken(context, m);
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.btnMissed.setOnClickListener(v -> {
            MissedDoseHandler.markAsMissed(context, m);
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < list.size()) {
                Medicine medicineToDelete = list.get(pos);

                // Run DB delete in background thread
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.medicineDao().delete(medicineToDelete);

                    // Update RecyclerView on UI thread
                    ((MainActivity) context).runOnUiThread(() -> {
                        list.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, list.size());
                    });
                });
            }
        });

        // Status display
        if (m.isTaken()) {
            holder.tvStatus.setText("Taken");
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else if (m.isMissed()) {
            holder.tvStatus.setText("Missed");
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvStatus;
        Button btnTaken, btnMissed, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvTime = itemView.findViewById(R.id.tvMedicineTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnTaken = itemView.findViewById(R.id.btnTaken);
            btnMissed = itemView.findViewById(R.id.btnMissed);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateList(List<Medicine> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }
}