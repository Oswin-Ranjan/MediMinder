package com.example.mediminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MedicineDao {

 @Query("SELECT * FROM Medicine ORDER BY hour, minute")
 List<Medicine> getAll();

 @Query("SELECT * FROM Medicine WHERE id = :id LIMIT 1")
 Medicine getById(int id);

 @Insert
 long insert(Medicine medicine);

 @Update
 void update(Medicine medicine);

 @Delete
 void delete(Medicine medicine);
}

