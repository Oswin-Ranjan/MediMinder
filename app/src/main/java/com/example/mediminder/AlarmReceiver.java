package com.example.mediminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import java.util.concurrent.Executors;

public class AlarmReceiver extends BroadcastReceiver {
    static final String CHANNEL_ID = "meds_channel";
    private static final long MISSED_CHECK_DELAY_MS = 15 * 60 * 1000; // 15 minutes grace

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onReceive(Context context, Intent intent) {
        int medicineId = intent.getIntExtra("medicineId", -1);
        String medicineName = intent.getStringExtra("medicineName");
        String dosage = intent.getStringExtra("dosage");
        String instruction = intent.getStringExtra("instruction");
        int alarmId = intent.getIntExtra("alarmId", -1);

        Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.mediminder, null);
        Bitmap largeIcon = (d instanceof BitmapDrawable) ? ((BitmapDrawable) d).getBitmap() : null;

        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent openPending = PendingIntent.getActivity(context, medicineId,
                openApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent takenIntent = new Intent(context, TakenReceiver.class);
        takenIntent.putExtra("medicineId", medicineId);
        takenIntent.putExtra("alarmId", alarmId);
        takenIntent.putExtra("medicineName", medicineName);
        PendingIntent takenPending = PendingIntent.getBroadcast(context, medicineId + 2000,
                takenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String message = "Take: " + (medicineName != null ? medicineName : "medicine");
        if (dosage != null && !dosage.isEmpty()) message += " (" + dosage + ")";
        if (instruction != null && !instruction.isEmpty()) message += " - " + instruction;

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.mixkit_happy_bells);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.mediminder)
                .setLargeIcon(largeIcon)
                .setContentTitle("MediMinder")
                .setContentText(message)
                .setContentIntent(openPending)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_menu_save, "Taken", takenPending)
                .setSound(soundUri);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Medicine reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Reminders for medicines");
            channel.setSound(soundUri, attrs);
            if (nm != null) nm.createNotificationChannel(channel);
        }

        if (nm != null) nm.notify((int) System.currentTimeMillis(), builder.build());

        if (medicineId != -1 && alarmId != -1) {
            Intent missedIntent = new Intent(context, MissedDoseCheckReceiver.class);
            missedIntent.putExtra("medicineId", medicineId);
            missedIntent.putExtra("medicineName", medicineName);

            PendingIntent missedPending = PendingIntent.getBroadcast(context, alarmId + 1000,
                    missedIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                long when = System.currentTimeMillis() + MISSED_CHECK_DELAY_MS;
                am.setExact(AlarmManager.RTC_WAKEUP, when, missedPending);
            }
        }

        if (medicineId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                MedicineDatabase db = MedicineDatabase.getInstance(context);
                Medicine m = db.medicineDao().getById(medicineId);
            });
        }
    }
}
