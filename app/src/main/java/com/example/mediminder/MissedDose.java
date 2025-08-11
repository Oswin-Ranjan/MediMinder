package com.example.mediminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "missed_doses")
public class MissedDose {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int medicineId;
    public String medicineName;
    public long timestamp;
    public String status;
    public MissedDose(int medicineId, String medicineName, long timestamp, String status) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.timestamp = timestamp;
        this.status = status;
    }
}