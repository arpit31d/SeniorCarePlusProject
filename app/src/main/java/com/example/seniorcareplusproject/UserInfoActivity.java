package com.example.seniorcareplusproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etName, etRelativesName, etContact, etAddress, etAge, etHeight, etWeight;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etRelativesName = findViewById(R.id.etRelativesName);
        etContact = findViewById(R.id.etContact);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnSubmit = findViewById(R.id.btnsubmit);

        // Set click listener for submit button
        btnSubmit.setOnClickListener(v -> saveUserInfo());
    }

    private void saveUserInfo() {
        // Get user inputs
        String name = etName.getText().toString().trim();
        String relativesName = etRelativesName.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        // Validate inputs
        if (!name.isEmpty() && !relativesName.isEmpty() && !contact.isEmpty() && !address.isEmpty() && !age.isEmpty() && !height.isEmpty() && !weight.isEmpty()) {
            // Create intent to pass data back to MainPage
            Intent resultIntent = new Intent();
            resultIntent.putExtra("USER_NAME", name);
            resultIntent.putExtra("USER_RELATIVES_NAME", relativesName);
            resultIntent.putExtra("USER_CONTACT", contact);
            resultIntent.putExtra("USER_ADDRESS", address);
            resultIntent.putExtra("USER_AGE", age);
            resultIntent.putExtra("USER_HEIGHT", height);
            resultIntent.putExtra("USER_WEIGHT", weight);

            // Set result and finish activity
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            // Display error message if any field is empty
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
        }
    }
}
