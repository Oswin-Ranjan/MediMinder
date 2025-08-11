package com.example.mediminder;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executors;

public class MissedDosesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MissedDoseAdapter adapter;
    private MedicineDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_doses);

        recyclerView = findViewById(R.id.recyclerViewMissed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MissedDoseAdapter();
        recyclerView.setAdapter(adapter);

        db = MedicineDatabase.getInstance(this);
        loadMissed();
    }

    private void loadMissed() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MissedDose> list = db.missedDoseDao().getAll();
            runOnUiThread(() -> adapter.setData(list));
        });
    }
}