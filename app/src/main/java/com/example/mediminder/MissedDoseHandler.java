package com.example.mediminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.concurrent.Executors;

public class MissedDoseHandler {

    private static void cancelMissedCheck(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent missedIntent = new Intent(context, MissedDoseCheckReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                context,
                alarmId + 1000,
                missedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) alarmManager.cancel(pending);
    }

    public static void markAsTaken(Context context, Medicine medicine) {
        medicine.setStatus(Medicine.STATUS_TAKEN);
        medicine.setTakenTimeMillis(System.currentTimeMillis());

        Executors.newSingleThreadExecutor().execute(() -> {
            MedicineDatabase db = MedicineDatabase.getInstance(context);
            db.medicineDao().update(medicine);

            db.missedDoseDao().insert(new MissedDose(medicine.getId(), medicine.getName(), System.currentTimeMillis(), "taken"));
        });

        cancelMissedCheck(context, medicine.getAlarmId());
        Toast.makeText(context, "Medicine marked as taken", Toast.LENGTH_SHORT).show();
    }

    public static void markAsMissed(Context context, Medicine medicine) {
        medicine.setStatus(Medicine.STATUS_MISSED);
        medicine.setTakenTimeMillis(0);

        Executors.newSingleThreadExecutor().execute(() -> {
            MedicineDatabase db = MedicineDatabase.getInstance(context);
            db.medicineDao().update(medicine);
            db.missedDoseDao().insert(new MissedDose(medicine.getId(), medicine.getName(), System.currentTimeMillis(), "missed"));
        });

        cancelMissedCheck(context, medicine.getAlarmId());
        Toast.makeText(context, "Medicine marked as missed", Toast.LENGTH_SHORT).show();
    }
}