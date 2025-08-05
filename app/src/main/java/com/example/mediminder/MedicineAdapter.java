package com.example.mediminder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;

    public MedicineAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.nameText.setText(medicine.getName());

        // Format time to HH:MM
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", medicine.getHour(), medicine.getMinute());
        holder.timeText.setText("Time: " + time);
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView timeText;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.medicineName);
            timeText = itemView.findViewById(R.id.medicineTime);
        }
    }
}
