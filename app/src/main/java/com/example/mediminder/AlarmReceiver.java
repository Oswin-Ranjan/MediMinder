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

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "meds";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.mediminder, null);
        Bitmap largeIcon = null;
        if (drawable instanceof BitmapDrawable) {
            largeIcon = ((BitmapDrawable) drawable).getBitmap();
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (only once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MediMinder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders to take your medicine");
            manager.createNotificationChannel(channel);
        }

        // Intent to open MainActivity on tap
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.mediminder)
                .setContentTitle("MediMinder")
                .setContentText("Time to take: " + medicineName)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
}
}
