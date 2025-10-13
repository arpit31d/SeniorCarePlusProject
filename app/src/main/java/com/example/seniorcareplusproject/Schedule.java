package com.example.seniorcareplusproject;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class Schedule extends AppCompatActivity {

    private TextInputEditText etDate, etTime, etDoctorName, etMeetLink;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialize views
        etDoctorName = findViewById(R.id.etDoctorName);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etMeetLink = findViewById(R.id.etMeetLink);
        btnSubmit = findViewById(R.id.Submit);

        // Date Picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Time Picker
        etTime.setOnClickListener(v -> showTimePicker());

        // Submit Button
        btnSubmit.setOnClickListener(v -> scheduleAppointment());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month+1, year);
            etDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            etTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void scheduleAppointment() {
        String doctorName = etDoctorName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String meetLink = etMeetLink.getText().toString().trim();

        if(doctorName.isEmpty() || date.isEmpty() || time.isEmpty() || meetLink.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!meetLink.startsWith("http")) {
            meetLink = "https://" + meetLink;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(meetLink));
            startActivity(intent);

            Toast.makeText(this, "Appointment Scheduled Successfully!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid Google Meet Link!", Toast.LENGTH_SHORT).show();
        }
    }
}
