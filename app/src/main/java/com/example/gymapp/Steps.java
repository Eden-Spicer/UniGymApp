package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            Toast.makeText(this, "Permission denied, pedometer feature won't work.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(stepCountReceiver, new IntentFilter("STEP_COUNT_UPDATED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stepCountReceiver);
    }

    private void updateStepCount(int stepCount) {
        TextView stepMotivationText = findViewById(R.id.step_count_text);
        stepCountText.setText(stepCount + " / 10000");

        if (stepCount >= 10000) {
            stepMotivationText.setText("Congratulations, you've done it!");
        } else if (stepCount >= 9000) {
            stepMotivationText.setText("Great job, almost there!");
        } else if (stepCount >= 6000) {
            stepMotivationText.setText("Keep going, you're doing great!");
        } else if (stepCount >= 5000) {
            stepMotivationText.setText("You're halfway there!");
        } else if (stepCount >= 2000) {
            stepMotivationText.setText("Great start, keep it up!");
        }

            progressBar.setProgress(stepCount);
    }
}
