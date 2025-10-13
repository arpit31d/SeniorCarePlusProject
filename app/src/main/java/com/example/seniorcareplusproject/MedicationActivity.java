package com.example.seniorcareplusproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MedicationActivity extends AppCompatActivity {
    private EditText medicineNameEditText, frequencyEditText, timeEditText;
    private Button saveMedicationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        medicineNameEditText = findViewById(R.id.medicineNameEditText);
        frequencyEditText = findViewById(R.id.frequencyEditText);
        timeEditText = findViewById(R.id.timeEditText);
        saveMedicationButton = findViewById(R.id.saveMedicationButton);

        int userId = getIntent().getIntExtra("userId", -1);
        saveMedicationButton.setOnClickListener(v -> {
            String name = medicineNameEditText.getText().toString().trim();
            String frequency = frequencyEditText.getText().toString().trim();
            String time = timeEditText.getText().toString().trim();

            if (name.isEmpty() || frequency.isEmpty() || time.isEmpty() || userId == -1) {
                Toast.makeText(MedicationActivity.this, "All fields required.", Toast.LENGTH_SHORT).show();
                return;
            }

            Medication medication = new Medication();
            medication.userId = userId;
            medication.medicineName = name;
            medication.frequency = frequency;
            medication.time = time;

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "seniorcareplus-db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

            db.medicationDao().insert(medication);

            Toast.makeText(MedicationActivity.this, "Medication saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
