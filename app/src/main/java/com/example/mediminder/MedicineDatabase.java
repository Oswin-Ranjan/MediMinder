package com.example.mediminder;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Medicine.class, MissedDose.class}, version = 4, exportSchema = false)
public abstract class MedicineDatabase extends RoomDatabase {
    private static volatile MedicineDatabase instance;
    public abstract MedicineDao medicineDao();
    public abstract MissedDoseDao missedDoseDao();

    public static MedicineDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MedicineDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    MedicineDatabase.class, "medicine_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}