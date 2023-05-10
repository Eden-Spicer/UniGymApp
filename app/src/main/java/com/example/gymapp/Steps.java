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
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Class representing the activity for counting steps
public class Steps extends AppCompatActivity {

    // Views to display the step count and a progress bar
    private TextView stepCountText;
    private ProgressBar progressBar;

    // Request code for runtime permissions
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    // BroadcastReceiver to receive updates about the step count from a service
    private final BroadcastReceiver stepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the current step count from the intent and update the UI
            int currentStepCount = intent.getIntExtra("step_count", 0);
            updateStepCount(currentStepCount);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Initializing views
        stepCountText = findViewById(R.id.step_count_number);
        progressBar = findViewById(R.id.stepBar);

        // Requesting necessary permissions
        requestPermissionsIfNeeded();

        // Setting up BottomNavigationView
        BottomNavigationView botNavMenu = findViewById(R.id.bottom_navigation);
        botNavMenu.setSelectedItemId(R.id.page_2);

        // Setting listener for item selection in BottomNavigationView
        botNavMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page_2) {
                // Current page selected, do nothing
                return true;
            } else if (itemId == R.id.page_1) {
                // Navigating to the MainActivity
                Intent intent1 = new Intent(Steps.this, MainActivity.class);
                startActivity(intent1);
                return true;
            }
            return false;
        });

        // Starting the WeatherService in a new thread
        new Thread(() -> {
            Intent weatherServiceIntent = new Intent(this, WeatherService.class);
            startService(weatherServiceIntent);
        }).start();

        // Getting the step count for today and updating the UI
        int stepCount = getStepCountForToday();
        updateStepCount(stepCount);

        // Registering a broadcast receiver to receive updates about the step count
        registerReceiver(stepCountReceiver, new IntentFilter("STEP_COUNT_UPDATED"));
    }

    // Method to get the current date as a string
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Method to request necessary permissions if they haven't been granted already
    private void requestPermissionsIfNeeded() {
        List<String> permissionsToRequest = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // If any permissions need to be requested, request them
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        } else {
            // If all permissions have been granted, start the step count service
            startStepCountService();
        }
    }

    // Method to start the step count service
    private void startStepCountService() {
        Intent intent = new Intent(this, StepCountService.class);
        startService(intent);
    }

    // Handle permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean permissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }
            if (permissionsGranted) {
                // All required permissions granted
                startStepCountService();
            } else {
                // Some permission(s) denied
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Permission denied, some features won't work.", Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(R.id.bottom_navigation);
                snackbar.show();
            }
        }
    }

    // Register broadcast receivers when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(stepCountReceiver, new IntentFilter("STEP_COUNT_UPDATED"));
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherUpdateReceiver, new IntentFilter("WEATHER_UPDATED"));
    }

    // Unregister broadcast receivers when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stepCountReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherUpdateReceiver);
    }

    // Method to get the step count for the current day from SharedPreferences
    private int getStepCountForToday() {
        SharedPreferences prefs = getSharedPreferences("STEP_COUNT_PREFS", Context.MODE_PRIVATE);
        String todayDate = getCurrentDate();
        return prefs.getInt(todayDate, 0);
    }

    private void updateStepCount(int stepCount) {
        // Update the step count for today in the shared preferences
        SharedPreferences sharedPref = getSharedPreferences("STEP_COUNT_PREFS", Context.MODE_PRIVATE);
        int currentStepCount = getStepCountForToday();
        currentStepCount += stepCount;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getCurrentDate(), currentStepCount);
        editor.apply();

        // Update the step count UI elements and set a motivational message based on the current step count
        TextView stepMotivationText = findViewById(R.id.step_count_text);
        TextView stepCountText = findViewById(R.id.step_count_number);
        ProgressBar progressBar = findViewById(R.id.stepBar);
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

    // Broadcast receiver for receiving weather updates
    private final BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the weather data from the broadcast intent and call updateWeather() to update the weather UI elements
            String weatherDescription = intent.getStringExtra("weather_description");
            int weatherCode = intent.getIntExtra("weather_code", 0);
            double temperature = intent.getDoubleExtra("temperature", 0.0);

            Log.d("Steps", "Weather update received: description=" + weatherDescription + ", code=" + weatherCode + ", temp=" + temperature);

            updateWeather(weatherDescription, weatherCode, temperature);
        }
    };

    private void updateWeather(String weatherDescription, int weatherCode, double temperature) {
        // Update the weather description and temperature text, and select an appropriate weather icon based on the weather code
        TextView weatherTextView = findViewById(R.id.weatherText);
        ImageView weatherImageView = findViewById(R.id.weatherImage);

        String capitalizedDescription = weatherDescription.substring(0, 1).toUpperCase() + weatherDescription.substring(1);

        String weatherString = capitalizedDescription + ", " + temperature + "°C";
        weatherTextView.setText(weatherString);

        int imageResource;
        switch (weatherCode / 100) {
            case 2: // Thunderstorm
                imageResource = R.drawable.thunder_weather;
                break;
            case 3: // Drizzle
            case 5: // Rain
                imageResource = R.drawable.rainy_weather;
                break;
            case 6: // Snow
                imageResource = R.drawable.snow_weather;
                break;
            case 7: // Atmosphere
                imageResource = R.drawable.foggy_weather;
                break;
            case 8: // Clear or Clouds
                if (weatherCode == 800) {
                    imageResource = R.drawable.sunny_weather;
                } else {
                    imageResource = R.drawable.cloudy_weather;
                }
                break;
            default: // Unknown weather code
                imageResource = R.drawable.neutral_weather;
                break;
        }
        weatherImageView.setImageResource(imageResource);
    }
}
