package com.example.seniorcareplusproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("UPDATE users SET name = :name, contactNo = :contactNo, primarySOS = :primarySOS, age = :age WHERE id = :userId")
    void updateUser(int userId, String name, String contactNo, String primarySOS, int age);

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    void resetPassword(int userId, String newPassword);
}

