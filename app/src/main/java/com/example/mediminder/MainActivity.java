package com.example.mediminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private MedicineDatabase db;
    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> allMeds = new ArrayList<>();

    private Button btnAll, btnPending, btnTaken, btnMissed;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionHelper.requestNotificationPermission(this);
        PermissionHelper.checkAndRequestExactAlarmPermission(this);

        db = MedicineDatabase.getInstance(this);

        recyclerView = findViewById(R.id.medicineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MedicineAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnTaken = findViewById(R.id.btnTaken);
        btnMissed = findViewById(R.id.btnMissed);

        FloatingActionButton addButton = findViewById(R.id.add_medicine_btn);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });


        // Set button listeners
        btnAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            filterMedicines();
            Toast.makeText(this, "Showing all medicines", Toast.LENGTH_SHORT).show();
        });

        btnPending.setOnClickListener(v -> {
            currentFilter = "PENDING";
            filterMedicines();
        });

        btnTaken.setOnClickListener(v -> {
            currentFilter = "TAKEN";
            filterMedicines();
        });

        btnMissed.setOnClickListener(v -> {
            currentFilter = "MISSED";
            filterMedicines();
        });

        loadMedicines();
    }

    private void loadMedicines() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Medicine> meds = db.medicineDao().getAll();
            runOnUiThread(() -> {
                allMeds.clear();
                allMeds.addAll(meds);
                filterMedicines();
            });
        });
    }

    private void filterMedicines() {
        List<Medicine> filtered = new ArrayList<>();

        switch (currentFilter) {
            case "PENDING":
                for (Medicine m : allMeds) if (m.isPending()) filtered.add(m);
                break;
            case "TAKEN":
                for (Medicine m : allMeds) if (m.isTaken()) filtered.add(m);
                break;
            case "MISSED":
                for (Medicine m : allMeds) if (m.isMissed()) filtered.add(m);
                break;
            default:
                filtered.addAll(allMeds);
        }

        adapter.updateList(filtered);
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
}