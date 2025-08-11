package com.example.mediminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MarkAsMissedActivity extends AppCompatActivity {

    public static final String EXTRA_MEDICINE_NAME = "medicine_name";
    public static final String EXTRA_MEDICINE_ID = "medicine_id"; // Unique ID for canceling alarm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_as_missed);

        TextView messageTextView = findViewById(R.id.missed_message);

        String medicineName = getIntent().getStringExtra(EXTRA_MEDICINE_NAME);
        int medicineId = getIntent().getIntExtra(EXTRA_MEDICINE_ID, -1);

        String msg;
        if (medicineName != null) {
            msg = "Medicine \"" + medicineName + "\" marked as missed";
        } else {
            msg = "Medicine marked as missed";
        }

        messageTextView.setText(msg);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        if (medicineId != -1) {
            cancelMissedDoseAlarm(medicineId);
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 2000);
    }

    private void cancelMissedDoseAlarm(int medicineId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MissedDoseCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medicineId, // Use medicineId as requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}