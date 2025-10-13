package com.example.seniorcareplusproject;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medications")
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String medicineName;
    public String frequency;
    public String time;
}
