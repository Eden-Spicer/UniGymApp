package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Steps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        int currentStepCount = 6969;

        BottomNavigationView botNavMenu = findViewById(R.id.bottom_navigation);
        botNavMenu.setSelectedItemId(R.id.page_2);

        botNavMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_2:

                    return true;
                case R.id.page_1:
                    // handle click on new activity icon
                    Intent intent = new Intent(Steps.this, MainActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });

        ProgressBar progressBar = findViewById(R.id.stepBar);
        progressBar.setProgress(currentStepCount);
    }
}