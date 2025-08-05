package com.example.mediminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {
    private EditText nameInput;
    private TimePicker timePicker;
    private MedicineDatabase db;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        nameInput = findViewById(R.id.medicineName);
        timePicker = findViewById(R.id.timePicker);
        Button saveButton = findViewById(R.id.saveButton);

        db = MedicineDatabase.getInstance(this);

        saveButton.setOnClickListener(v -> {
            if (!checkAndRequestPermissions()) {
                return;
            }

            String name = nameInput.getText().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Medicine medicine = new Medicine(name, hour, minute);
            db.medicineDao().insert(medicine);

            // Calculate alarm trigger time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            long triggerMillis = calendar.getTimeInMillis();
            if (triggerMillis < System.currentTimeMillis()) {
                triggerMillis += AlarmManager.INTERVAL_DAY;
            }

            scheduleReminder(this, name, triggerMillis);
            Toast.makeText(this, "Medicine saved and reminder set", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in system settings", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminder(Context context, String medicineName, long triggerTimeMillis) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),  // unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
        }
    }
}
