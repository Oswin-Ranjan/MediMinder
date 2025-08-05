package com.example.mediminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> medicineList;
    private final OnDeleteClickListener deleteClickListener;
    public interface OnDeleteClickListener {
        void onDelete(Medicine medicine);
    }
    public MedicineAdapter(List<Medicine> list, OnDeleteClickListener listener) {
        this.medicineList = list;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.name.setText(medicine.name);
        holder.time.setText(String.format(Locale.getDefault(), "%02d:%02d", medicine.hour, medicine.minute));

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                deleteClickListener.onDelete(medicineList.get(adapterPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    public void remove(Medicine medicine) {
        int position = medicineList.indexOf(medicine);
        if (position >= 0 && position < medicineList.size()) {
            medicineList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateData(List<Medicine> newList) {
        this.medicineList = newList;
        notifyDataSetChanged();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView name, time;
        Button deleteButton;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medicineName);
            time = itemView.findViewById(R.id.medicineTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}