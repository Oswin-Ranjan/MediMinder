 package com.example.mediminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao public interface MedicineDao
{
    @Query("SELECT * FROM Medicine")
    List<Medicine> getAll();
    @Insert
    void insert(Medicine medicine);
    @Delete
    void delete(Medicine medicine);
}

