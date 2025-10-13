package com.example.seniorcareplusproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameEditText, contactEditText, primarySOSEditText, ageEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button submitSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.nameEditText);
        contactEditText = findViewById(R.id.contactEditText);
        primarySOSEditText = findViewById(R.id.primarySOSEditText);
        ageEditText = findViewById(R.id.ageEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitSignUp = findViewById(R.id.submitSignUp);

        submitSignUp.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String contactNo = contactEditText.getText().toString().trim();
            String primarySOS = primarySOSEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (name.isEmpty() || contactNo.isEmpty() || primarySOS.isEmpty() || ageStr.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(SignUpActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.name = name;
            user.contactNo = contactNo;
            user.primarySOS = primarySOS;
            user.age = age;
            user.username = username;
            user.password = password;

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "seniorcareplus-db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

            db.userDao().insert(user);

            Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
