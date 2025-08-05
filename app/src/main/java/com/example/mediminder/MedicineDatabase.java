package com.example.mediminder;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Medicine.class}, version = 3)
public abstract class MedicineDatabase extends RoomDatabase {
    private static MedicineDatabase instance;
    public static synchronized MedicineDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MedicineDatabase.class, "medicine_db").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract MedicineDao medicineDao();

}
