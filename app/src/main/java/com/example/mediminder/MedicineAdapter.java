package com.example.mediminder;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;
    private OnDeleteClickListener deleteClickListener;

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
        holder.time.setText(String.format("%02d:%02d", medicine.hour, medicine.minute));

        holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(holder.itemView.getContext(), medicine));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void updateData(List<Medicine> newList) {
        medicineList.clear();
        medicineList.addAll(newList);
        notifyDataSetChanged();
    }

    public void remove(Medicine medicine) {
        int position = medicineList.indexOf(medicine);
        if (position != -1) {
            medicineList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void showDeleteConfirmationDialog(Context context, Medicine medicine) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton("Yes", (dialog, which) -> deleteClickListener.onDelete(medicine))
                .setNegativeButton("Cancel", null)
                .show();
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