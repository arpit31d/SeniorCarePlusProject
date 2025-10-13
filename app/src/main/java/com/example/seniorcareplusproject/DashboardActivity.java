package com.example.seniorcareplusproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {
    private TextView userDetailsTextView;
    private User currentUser;  // Declare currentUser here

    private EditText newPasswordEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userDetailsTextView = findViewById(R.id.userDetailsTextView);


        String username = getIntent().getStringExtra("username");
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "seniorcareplus-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        User user = db.userDao().getUserByUsername(username);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);
        EditText newPasswordEditText = findViewById(R.id.newPasswordEditText);
        userDetailsTextView = findViewById(R.id.userDetailsTextView);



        currentUser = db.userDao().getUserByUsername(username);

        if (currentUser != null) {
            String info = "Name: " + currentUser.name + "\nContact No: " + currentUser.contactNo +
                    "\nPrimary SOS: " + currentUser.primarySOS + "\nAge: " + currentUser.age +
                    "\nUsername: " + currentUser.username;
            userDetailsTextView.setText(info);
        }

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
                return;
            }
            db.userDao().resetPassword(currentUser.id, newPassword);
            Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();
        });
    }
}
