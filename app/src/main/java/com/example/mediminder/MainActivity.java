package com.example.mediminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public MedicineDatabase db;
    public RecyclerView recyclerView;
    public MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        boolean userInfoSaved = prefs.getBoolean("user_info_saved", false);
        if (!userInfoSaved) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
            return;
        }

        String name = prefs.getString("name", "User");

        PermissionHelper.requestNotificationPermission(this);
        PermissionHelper.checkAndRequestExactAlarmPermission(this);

        db = MedicineDatabase.getInstance(this);
        recyclerView = findViewById(R.id.medicineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicineAdapter(new java.util.ArrayList<>(), medicine -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.medicineDao().delete(medicine);
                cancelAlarm(medicine);
                runOnUiThread(() -> {
                    adapter.remove(medicine);
                    Toast.makeText(MainActivity.this, "Reminder deleted", Toast.LENGTH_SHORT).show();
                });
            });
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.add_medicine_btn);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void loadMedicines() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Medicine> medicines = db.medicineDao().getAll();
            runOnUiThread(() -> adapter.updateData(medicines));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.REQUEST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
                String name = prefs.getString("name", "User");
                Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cancelAlarm(Medicine medicine) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medicine.alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}