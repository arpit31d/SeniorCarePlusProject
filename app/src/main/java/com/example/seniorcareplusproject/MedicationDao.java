package com.example.seniorcareplusproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MedicationDao {
    @Insert
    void insert(Medication medication);

    @Query("SELECT * FROM medications WHERE userId = :userId")
    List<Medication> getMedicationsForUser(int userId);
}
