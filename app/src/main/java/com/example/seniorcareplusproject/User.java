package com.example.seniorcareplusproject;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String contactNo;
    public String primarySOS;
    public int age;
    public String username;
    public String password;
}
