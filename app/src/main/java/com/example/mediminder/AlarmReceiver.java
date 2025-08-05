package com.example.mediminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import android.media.AudioAttributes;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "meds";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");
        String dosage = intent.getStringExtra("dosage");
        String instruction = intent.getStringExtra("instruction");

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.mediminder, null);
        Bitmap largeIcon = null;
        if (drawable instanceof BitmapDrawable) {
            largeIcon = ((BitmapDrawable) drawable).getBitmap();
        }

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String message = "Take: " + medicineName;
        if (dosage != null && !dosage.isEmpty()) {
            message += " (" + dosage + ")";
        }
        if (instruction != null && !instruction.isEmpty()) {
            message += " - " + instruction;
        }

        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.mixkit_happy_bells);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.mediminder)
                .setContentTitle("MediMinder")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Medicine Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for medicine reminder alarms");
            channel.setSound(soundUri, audioAttributes);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}


