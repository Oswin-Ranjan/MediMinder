package com.example.mediminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText nameInput, dosageInput, dateEditText, timeEditText;
    private Spinner instructionSpinner;
    private Spinner repeatSpinner;
    private Button saveButton, selectDaysButton;
    private TextView selectedDaysTextView;

    private String selectedRepeatDays = "";
    private MedicineDatabase db;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        nameInput = findViewById(R.id.medicineName);
        dosageInput = findViewById(R.id.editTextDosage);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        repeatSpinner = findViewById(R.id.repeatSpinner);
        instructionSpinner = findViewById(R.id.instruction_spinner);
        saveButton = findViewById(R.id.saveButton);
        selectDaysButton = findViewById(R.id.selectDaysButton);
        selectedDaysTextView = findViewById(R.id.selectedDaysTextView);

        db = MedicineDatabase.getInstance(this);

        // Request permissions right when activity starts
        PermissionHelper.requestNotificationPermission(this);
        PermissionHelper.checkAndRequestExactAlarmPermission(this);

        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
        selectDaysButton.setOnClickListener(v -> showRepeatDaysDialog());

        saveButton.setOnClickListener(v -> {
            if (!PermissionHelper.hasNotificationPermission(this)) {
                Toast.makeText(this, "Please allow notification permission to save reminder", Toast.LENGTH_LONG).show();
                PermissionHelper.requestNotificationPermission(this);
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Please allow exact alarms in settings", Toast.LENGTH_LONG).show();
                    PermissionHelper.checkAndRequestExactAlarmPermission(this);
                    return;
                }
            }

            saveMedicineAndScheduleReminder();
        });
    }

    private void saveMedicineAndScheduleReminder() {
        String name = nameInput.getText().toString().trim();
        String dosage = dosageInput.getText().toString().trim();
        String instruction = instructionSpinner.getSelectedItem().toString();
        String repeatOption = repeatSpinner.getSelectedItem().toString();

        if (name.isEmpty() || dosage.isEmpty() || dateEditText.getText().toString().isEmpty() || timeEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long intervalMillis;
        switch (repeatOption) {
            case "Every day":
                intervalMillis = AlarmManager.INTERVAL_DAY;
                break;
            case "Every week":
                intervalMillis = AlarmManager.INTERVAL_DAY * 7;
                break;
            case "Every 2 weeks":
                intervalMillis = AlarmManager.INTERVAL_DAY * 14;
                break;
            case "Every 3 weeks":
                intervalMillis = AlarmManager.INTERVAL_DAY * 21;
                break;
            case "Every month":
                intervalMillis = AlarmManager.INTERVAL_DAY * 30;
                break;
            case "Do not repeat":
            default:
                intervalMillis = 0;
                break;
        }

        long triggerMillis = calendar.getTimeInMillis();

        Medicine medicine = new Medicine(name, dosage, instruction,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        medicine.setRepeatDays(selectedRepeatDays);

        long finalIntervalMillis = intervalMillis;
        Executors.newSingleThreadExecutor().execute(() -> {
            db.medicineDao().insert(medicine);
            runOnUiThread(() -> {
                scheduleReminder(this, name, dosage, instruction, triggerMillis, finalIntervalMillis);
                Toast.makeText(this, "Medicine saved and reminder set", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    dateEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    timeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminder(Context context, String medicineName, String dosage, String instruction, long triggerTimeMillis, long intervalMillis) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);
        intent.putExtra("dosage", dosage);
        intent.putExtra("instruction", instruction);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (intervalMillis > 0) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTimeMillis, intervalMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
            }
        }
    }

    private void showRepeatDaysDialog() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        boolean[] selectedDays = new boolean[7];
        ArrayList<String> repeatDayList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Repeat on days");

        builder.setMultiChoiceItems(days, selectedDays, (dialog, which, isChecked) -> {
            if (isChecked) {
                repeatDayList.add(days[which]);
            } else {
                repeatDayList.remove(days[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedRepeatDays = TextUtils.join(",", repeatDayList);
            selectedDaysTextView.setText("Repeats on: " + selectedRepeatDays);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}