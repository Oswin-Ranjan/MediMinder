package com.example.mediminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TakenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int medicineId = intent.getIntExtra("medicineId", -1);
        int alarmId = intent.getIntExtra("alarmId", -1);
        String medicineName = intent.getStringExtra("medicineName");

        if (medicineId != -1) {
            new Thread(() -> {
                MedicineDatabase db = MedicineDatabase.getInstance(context);
                Medicine m = db.medicineDao().getById(medicineId);
                if (m != null) {
                    MissedDoseHandler.markAsTaken(context, m);
                }
            }).start();
        }
    }
}