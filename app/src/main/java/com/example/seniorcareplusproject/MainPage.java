package com.example.seniorcareplusproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;  // if not already imported for Toast usage

public class MainPage extends AppCompatActivity {
    private static final String TAG = "MainPage";
    private static final int USER_INFO_REQUEST_CODE = 100;
    private String sosNumber1;
    private String sosNumber2;
    private String sosNumber3;
    private LocationManager locationManager; // For GPS location
    private String currentLocationLink;
    private TextView dashboardInfo; // Dashboard Info TextView
    private boolean sosSent = false; // Track SOS state
    private boolean dashboardExpanded = false;
    private String userName = "Not Available";
    private String userRelativesName = "Not Available";
    private String userContact = "Not Available";
    private String userAddress = "Not Available";
    private String userAge = "Not Available";
    private String userHeight = "Not Available";
    private String userWeight = "Not Available";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        dashboardInfo = findViewById(R.id.dashboardInfo);
        updateDashboardInfo(getDashboardSummary());
        ImageButton expandDashboardButton = findViewById(R.id.expandDashboardButton);
        expandDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboardExpanded = !dashboardExpanded;
                updateDashboardInfo(getDashboardInfo());
            }
        });
        MaterialButton reminder = findViewById(R.id.reminder);
        MaterialButton schedule = findViewById(R.id.schedule);
        MaterialButton sosButton = findViewById(R.id.sosButton);
        MaterialButton setNumbersButton = findViewById(R.id.setNumbersButton);
        MaterialButton emergencyCallButton = findViewById(R.id.emergencyCallButton);
        MaterialButton btnUserInfo = findViewById(R.id.btnUserInfo);

        requestPermissions(); // Request necessary permissions
        setNumbersButton.setOnClickListener(v -> showInputDialog());
        sosButton.setOnClickListener(v -> getCurrentLocationAndSendSOS());

        schedule.setOnClickListener(v -> schedule());
        reminder.setOnClickListener(v -> reminder());
        emergencyCallButton.setOnClickListener(v -> makeEmergencyCall());
        btnUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainPage.this, UserInfoActivity.class);
            startActivityForResult(intent, USER_INFO_REQUEST_CODE);
        });

        Button manageMedicationsButton = findViewById(R.id.manageMedicationsButton);
        Button resetPasswordButton = findViewById(R.id.resetPasswordButton);

        String loggedInUsername = getIntent().getStringExtra("username");



        manageMedicationsButton.setOnClickListener(v -> {
            // Pass user ID or username (fetch from DB) to MedicationActivity
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "seniorcareplus-db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();

            User user = db.userDao().getUserByUsername(loggedInUsername);
            if (user != null) {
                Intent intent = new Intent(MainPage.this, MedicationActivity.class);
                intent.putExtra("userId", user.id);
                startActivity(intent);
            } else {
                Toast.makeText(MainPage.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });

        resetPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainPage.this, DashboardActivity.class);
            intent.putExtra("username", loggedInUsername);
            startActivity(intent);
            // Assume reset password handled on DashboardActivity screen
        });

        // Initialize LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void updateDashboardInfo(String info) {
        // Update the dashboard information
        dashboardInfo.setText(info);
    }

    private String getDashboardSummary() {
        // Return a summary of the dashboard info
        return "Tap to " + (dashboardExpanded ? "Collapse" : "Expand") + " Dashboard";
    }

    private String getDashboardInfo() {
        if (!dashboardExpanded) {
            return getDashboardSummary();
        }

        // Build detailed dashboard info
        StringBuilder sb = new StringBuilder();
        sb.append("User Information:\n");
        sb.append("Name: ").append(userName).append("\n");
        sb.append("Relative's Name: ").append(userRelativesName).append("\n");
        sb.append("Contact: ").append(userContact).append("\n");
        sb.append("Address: ").append(userAddress).append("\n");
        sb.append("Age: ").append(userAge).append("\n");
        sb.append("Height: ").append(userHeight).append("\n");
        sb.append("Weight: ").append(userWeight).append("\n");
        return sb.toString();
    }

    private void schedule() {
        Intent screen3 = new Intent(MainPage.this, Schedule.class);
        startActivity(screen3);
    }

    private void reminder() {
        Intent screen4 = new Intent(MainPage.this, Reminder.class);
        startActivity(screen4);
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter SOS Numbers");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText number1Input = new EditText(this);
        number1Input.setHint("SOS Number 1");
        layout.addView(number1Input);
        EditText number2Input = new EditText(this);
        number2Input.setHint("SOS Number 2");
        layout.addView(number2Input);
        EditText number3Input = new EditText(this);
        number3Input.setHint("SOS Number 3");
        layout.addView(number3Input);
        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            sosNumber1 = number1Input.getText().toString();
            sosNumber2 = number2Input.getText().toString();
            sosNumber3 = number3Input.getText().toString();
            dialog.dismiss();
            Toast.makeText(this, "SOS numbers set!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean isValidPhoneNumber(String number) {
        return number != null && !number.trim().isEmpty() && android.util.Patterns.PHONE.matcher(number).matches();
    }


    private void getCurrentLocation() {
        try {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Generate Google Maps link
                        currentLocationLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                        Log.d(TAG, "Current Location: " + currentLocationLink);
                    }
                }, null);
            } else {
                Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting location", e);
            Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void makeEmergencyCall() {
        if (sosNumber1 != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + sosNumber1));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please set SOS number first!", Toast.LENGTH_SHORT).show();
        }
    }
    private void getCurrentLocationAndSendSOS() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentLocationLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                    sendSmsWithLocation(currentLocationLink);
                }
            }, null);
        } else {
            Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSmsWithLocation(String locationLink) {
        String message = "URGENT SOS MESSAGE!! Patient needs immediate assistance. Current Location: " + locationLink;
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(sosNumber1, null, message, null, null);
            smsManager.sendTextMessage(sosNumber2, null, message, null, null);
            smsManager.sendTextMessage(sosNumber3, null, message, null, null);
            Toast.makeText(this, "SOS sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_INFO_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                userName = data.getStringExtra("USER_NAME");
                userRelativesName = data.getStringExtra("USER_RELATIVES_NAME");
                userContact = data.getStringExtra("USER_CONTACT");
                userAddress = data.getStringExtra("USER_ADDRESS");
                userAge = data.getStringExtra("USER_AGE");
                userHeight = data.getStringExtra("USER_HEIGHT");
                userWeight = data.getStringExtra("USER_WEIGHT");
                updateDashboardInfo(getDashboardInfo());
            } else {
                updateDashboardInfo("No user information entered.");
            }
        }
    }
}
