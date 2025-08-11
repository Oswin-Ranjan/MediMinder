package com.example.mediminder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MissedDoseAdapter extends RecyclerView.Adapter<MissedDoseAdapter.VH> {
    private List<MissedDose> data = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MissedDose> list) {
        data = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_missed_dose, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MissedDose md = data.get(position);
        holder.name.setText(md.medicineName);
        holder.status.setText(md.status);
        holder.time.setText(DateFormat.getDateTimeInstance().format(new Date(md.timestamp)));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, time, status;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.missed_item_name);
            time = itemView.findViewById(R.id.missed_item_time);
            status = itemView.findViewById(R.id.missed_item_status);
        }
    }
}
