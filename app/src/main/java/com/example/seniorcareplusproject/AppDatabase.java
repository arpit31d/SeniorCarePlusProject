package com.example.seniorcareplusproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Medication.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract MedicationDao medicationDao();
}
