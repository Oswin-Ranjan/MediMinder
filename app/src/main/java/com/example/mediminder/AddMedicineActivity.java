package com.example.mediminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class AddMedicineActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText dosageInput;
    private TimePicker timePicker;
    private MedicineDatabase db;
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        nameInput = findViewById(R.id.medicineName);
        dosageInput = findViewById(R.id.editTextDosage);
        timePicker = findViewById(R.id.timePicker);
        Spinner spinner = findViewById(R.id.instruction_spinner);
        Button saveButton = findViewById(R.id.saveButton);

        db = MedicineDatabase.getInstance(this);

        saveButton.setOnClickListener(v -> {
            if (!checkAndRequestPermissions()) {
                return;
            }

            String name = nameInput.getText().toString();
            String dosage = dosageInput.getText().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String instruction = spinner.getSelectedItem().toString();

            // Compute reminder time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            long triggerMillis = calendar.getTimeInMillis();
            if (triggerMillis < System.currentTimeMillis()) {
                triggerMillis += AlarmManager.INTERVAL_DAY;
            }

            Medicine medicine = new Medicine(name, dosage, instruction, hour, minute);

            long finalTriggerMillis = triggerMillis;
            Executors.newSingleThreadExecutor().execute(() -> {
                db.medicineDao().insert(medicine);

                runOnUiThread(() -> {
                    scheduleReminder(this, name, dosage, instruction,  finalTriggerMillis);
                    Toast.makeText(this, "Medicine saved and reminder set", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
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
    private void scheduleReminder(Context context, String medicineName, String dosage, String instruction, long triggerTimeMillis) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);
        intent.putExtra("dosage", dosage);
        intent.putExtra("instruction", instruction);

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