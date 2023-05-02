package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Steps extends AppCompatActivity {

    private TextView stepCountText;
    private ProgressBar progressBar;

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

        Intent intent = new Intent(this, StepCountService.class);
        startService(intent);

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
        stepCountText.setText(stepCount + " / 10000");
        progressBar.setProgress(stepCount);
    }
}
