package com.example.seniorcareplusproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Reminder extends AppCompatActivity {
    private EditText etMedicineName, etDate, etTime, etFrequency;
    private TextView tvCountdown;
    private Button btnSetReminder;
    private CheckBox checkboxTaken;

    private CountDownTimer countDownTimer;
    private static final int LIVE_NOTIFICATION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize views
        etMedicineName = findViewById(R.id.etMedicineName);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etFrequency = findViewById(R.id.etFrequency);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        checkboxTaken = findViewById(R.id.checkboxTaken);

        // Set Date Picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Set Time Picker
        etTime.setOnClickListener(v -> showTimePicker());

        // Set Reminder Button
        btnSetReminder.setOnClickListener(v -> setMedicineReminder());

        // Listener for Medicine Taken checkbox. If checked, cancel timer and update notification.
        checkboxTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                tvCountdown.setText("Medicine Taken!");
                updateLiveNotification("Medicine Taken!");
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            etTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void setMedicineReminder() {
        String medicineName = etMedicineName.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String frequencyStr = etFrequency.getText().toString();

        if (medicineName.isEmpty() || date.isEmpty() || time.isEmpty() || frequencyStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(frequencyStr);
            if (frequency <= 0) {
                Toast.makeText(this, "Frequency must be greater than zero!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid frequency format!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long startTime = validateAndParseDate(date, time, sdf);
            if (startTime < 0) {
                Toast.makeText(this, "Invalid date/time format!", Toast.LENGTH_SHORT).show();
                return;
            }

            long currentTime = System.currentTimeMillis();
            if (startTime < currentTime) {
                Toast.makeText(this, "Time must be in the future!", Toast.LENGTH_SHORT).show();
                return;
            }

            scheduleAlarm(startTime, frequency, medicineName);
            startCountdown(startTime);

            Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting reminder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private long validateAndParseDate(String date, String time, SimpleDateFormat sdf) {
        try {
            return sdf.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    private void scheduleAlarm(long startTime, int frequency, String medicineName) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicineName", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    startTime,
                    frequency * AlarmManager.INTERVAL_HOUR,
                    pendingIntent);
        }
    }

    private void startCountdown(long startTime) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(startTime - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 3600);
                long minutes = (millisUntilFinished % (1000 * 3600)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
                String countdownText = String.format(Locale.getDefault(), "Countdown: %02d:%02d:%02d", hours, minutes, seconds);
                tvCountdown.setText(countdownText);

                // Update live notification with remaining time if medicine is not yet marked as taken.
                if (!checkboxTaken.isChecked()) {
                    updateLiveNotification(countdownText);
                }
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Time to take your medicine!");
                updateLiveNotification("Time to take your medicine!");
                showNotification("Medicine Reminder", "It's time to take your medicine!");
            }
        }.start();
    }

    // Update live notification with the provided text
    private void updateLiveNotification(String contentText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "medicine_reminder",
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Live countdown for medicine reminder");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, Reminder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "medicine_reminder")
                .setSmallIcon(R.drawable.ic_pill_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Medicine Reminder")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)    // Do not make sound on every update
                .setAutoCancel(false);

        // Use a fixed notification ID to update the same notification
        notificationManager.notify(LIVE_NOTIFICATION_ID, builder.build());
    }

    // Show a one-time notification (for when the countdown finishes)
    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "medicine_reminder",
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for taking medicine");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, Reminder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "medicine_reminder")
                .setSmallIcon(R.drawable.ic_pill_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
