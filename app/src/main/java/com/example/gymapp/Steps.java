package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Steps extends AppCompatActivity {

    private TextView stepCountText;
    private ProgressBar progressBar;
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private BroadcastReceiver stepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentStepCount = intent.getIntExtra("step_count", 0);
            updateStepCount(currentStepCount);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        stepCountText = findViewById(R.id.step_count_number);
        progressBar = findViewById(R.id.stepBar);

        requestPermissionsIfNeeded();

        BottomNavigationView botNavMenu = findViewById(R.id.bottom_navigation);
        botNavMenu.setSelectedItemId(R.id.page_2);

        botNavMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_2:
                    return true;
                case R.id.page_1:
                    // handle click on new activity icon
                    Intent intent1 = new Intent(Steps.this, MainActivity.class);
                    startActivity(intent1);
                    return true;
            }
            return false;
        });

        Intent weatherServiceIntent = new Intent(this, WeatherService.class);
        startService(weatherServiceIntent);

    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void requestPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_PERMISSIONS);
        } else {
            startStepCountService();
        }
    }

    private void startStepCountService() {
        Intent intent = new Intent(this, StepCountService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            startStepCountService();
        } else {
            // Permission denied
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Permission denied, pedometer feature won't work.", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(R.id.bottom_navigation);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(stepCountReceiver, new IntentFilter("STEP_COUNT_UPDATED"));

        LocalBroadcastManager.getInstance(this).registerReceiver(weatherUpdateReceiver, new IntentFilter("WEATHER_UPDATED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stepCountReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherUpdateReceiver);
    }

    private int getStepCountForToday() {
        SharedPreferences prefs = getSharedPreferences("STEP_COUNT_PREFS", Context.MODE_PRIVATE);
        String todayDate = getCurrentDate();
        return prefs.getInt(todayDate, 0);
    }

    private void updateStepCount(int stepCount) {
        SharedPreferences sharedPref = getSharedPreferences("STEP_COUNT_PREFS", Context.MODE_PRIVATE);
        int currentStepCount = getStepCountForToday();
        currentStepCount += stepCount;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getCurrentDate(), currentStepCount);
        editor.apply();

        TextView stepMotivationText = findViewById(R.id.step_count_text);
        stepCountText.setText(currentStepCount + " / 10000");

        if (currentStepCount >= 10000) {
            stepMotivationText.setText("Congratulations, you've done it!");
        } else if (currentStepCount >= 9000) {
            stepMotivationText.setText("Great job, almost there!");
        } else if (currentStepCount >= 6000) {
            stepMotivationText.setText("Keep going, you're doing great!");
        } else if (currentStepCount >= 5000) {
            stepMotivationText.setText("You're halfway there!");
        } else if (currentStepCount >= 2000) {
            stepMotivationText.setText("Great start, keep it up!");
        }

        progressBar.setProgress(currentStepCount);
    }

    private BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String weatherDescription = intent.getStringExtra("weather_description");
            updateWeatherCard(weatherDescription);
        }
    };

    private void updateWeatherCard(String weatherDescription) {
        TextView weatherText = findViewById(R.id.weatherText);
        weatherText.setText(weatherDescription);
    }
}
