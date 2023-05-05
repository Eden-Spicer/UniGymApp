package com.example.gymapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

// Gather Weather
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// Gather Day
import java.time.LocalDate;
import java.time.DayOfWeek;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar TopAppBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TopAppBarText = findViewById(R.id.topAppBar);

        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);

        CardView mondayCard = findViewById(R.id.mondayCard);
        CardView tuesdayCard = findViewById(R.id.tuesdayCard);
        CardView wednesdayCard = findViewById(R.id.wednesdayCard);
        CardView thursdayCard = findViewById(R.id.thursdayCard);
        CardView fridayCard = findViewById(R.id.fridayCard);
        CardView saturdayCard = findViewById(R.id.saturdayCard);
        CardView sundayCard = findViewById(R.id.sundayCard);

        // Converting dp to px

        float elevationDp = 8;
        float density = getResources().getDisplayMetrics().density;
        float elevationPx = elevationDp * density;

        // Gathering day for card elevation and outline

        LocalDate currentDay = LocalDate.now();
        DayOfWeek day = currentDay.getDayOfWeek();


        switch (day) {
            case MONDAY:
                mondayCard.setCardElevation(elevationPx);
                TextView monText = findViewById(R.id.mondayText);
                monText.setTypeface(null, Typeface.BOLD);
                break;
            case TUESDAY:
                tuesdayCard.setCardElevation(elevationPx);
                TextView tueText = findViewById(R.id.tuesdayText);
                tueText.setTypeface(null, Typeface.BOLD);
                break;
            case WEDNESDAY:
                wednesdayCard.setCardElevation(elevationPx);
                TextView wedText = findViewById(R.id.wednesdayText);
                wedText.setTypeface(null, Typeface.BOLD);
                break;
            case THURSDAY:
                thursdayCard.setCardElevation(elevationPx);
                TextView thuText = findViewById(R.id.thursdayText);
                thuText.setTypeface(null, Typeface.BOLD);
                break;
            case FRIDAY:
                fridayCard.setCardElevation(elevationPx);
                TextView friText = findViewById(R.id.fridayText);
                friText.setTypeface(null, Typeface.BOLD);
                break;
            case SATURDAY:
                saturdayCard.setCardElevation(elevationPx);
                TextView satText = findViewById(R.id.saturdayText);
                satText.setTypeface(null, Typeface.BOLD);
                break;
            case SUNDAY:
                sundayCard.setCardElevation(elevationPx);
                TextView sunText = findViewById(R.id.sundayText);
                sunText.setTypeface(null, Typeface.BOLD);
                break;
            default:
                System.out.println("You have discovered a new day in the week!");
                break;
        }

        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        if (prefs.getBoolean("first_time", true)) {
            showNameDialog();
        } else {
          String storedName = prefs.getString("user_name", "User");
            TopAppBarText.setSubtitle(String.format("Hello, %s", storedName + "! Ready for a workout?"));
        }

        bottomNavMenu.setOnItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case R.id.page_1:

                return true;
            case R.id.page_2:
                // handle click on new activity icon
                Intent intent = new Intent(MainActivity.this, Steps.class);
                startActivity(intent);
                return true;
            }
        return false;
        });

    }

    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to FlexyFrog");

        TextInputLayout inputLayout = new TextInputLayout(this);
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

        TextInputEditText input = new TextInputEditText(inputLayout.getContext());
        input.setHint("Enter your name");
        inputLayout.addView(input);


        builder.setView(inputLayout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                TopAppBarText.setSubtitle(String.format("Hello, %s", name + "! Ready for a workout?"));

                SharedPreferences.Editor editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                editor.putString("user_name", name);
                editor.putBoolean("first_time", false);
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
