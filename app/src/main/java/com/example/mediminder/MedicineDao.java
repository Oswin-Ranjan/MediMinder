 package com.example.mediminder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao public interface MedicineDao { @Insert void insert(Medicine medicine);

    @Query("SELECT * FROM Medicine")
    List<Medicine> getAll();

}

