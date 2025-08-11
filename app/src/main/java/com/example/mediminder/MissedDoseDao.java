package com.example.mediminder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MissedDoseDao {
    @Insert
    void insert(MissedDose missedDose);

    @Query("SELECT * FROM missed_doses ORDER BY timestamp DESC")
    List<MissedDose> getAll();
}