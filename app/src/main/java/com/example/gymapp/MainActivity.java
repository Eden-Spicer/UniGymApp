package com.example.gymapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar TopAppBarText;
    private AlertDialog nameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TopAppBarText = findViewById(R.id.topAppBar);

        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);

        MaterialCardView mondayCard = findViewById(R.id.mondayCard);
        MaterialCardView tuesdayCard = findViewById(R.id.tuesdayCard);
        MaterialCardView wednesdayCard = findViewById(R.id.wednesdayCard);
        MaterialCardView thursdayCard = findViewById(R.id.thursdayCard);
        MaterialCardView fridayCard = findViewById(R.id.fridayCard);
        MaterialCardView saturdayCard = findViewById(R.id.saturdayCard);
        MaterialCardView sundayCard = findViewById(R.id.sundayCard);


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
            int itemId = item.getItemId();
            if (itemId == R.id.page_1) {
                // Do something
                return true;
            } else if (itemId == R.id.page_2) {
                // handle click on new activity icon
                Intent intent = new Intent(MainActivity.this, Steps.class);
                startActivity(intent);
                return true;
            }
        return false;
        });

        mondayCard.setOnClickListener(v -> openFullScreenFragment("Monday"));

        tuesdayCard.setOnClickListener(v -> openFullScreenFragment("Tuesday"));

        wednesdayCard.setOnClickListener(v -> openFullScreenFragment("Wednesday"));

        thursdayCard.setOnClickListener(v -> openFullScreenFragment("Thursday"));

        fridayCard.setOnClickListener(v -> openFullScreenFragment("Friday"));

        saturdayCard.setOnClickListener(v -> openFullScreenFragment("Saturday"));

        sundayCard.setOnClickListener(v -> openFullScreenFragment("Sunday"));

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
            if (input.getText() != null) {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    TopAppBarText.setSubtitle(String.format("Hello, %s", name + "! Ready for a workout?"));

                    SharedPreferences.Editor editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                    editor.putString("user_name", name);
                    editor.putBoolean("first_time", false);
                    editor.apply();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        nameDialog = builder.create();
        nameDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (nameDialog != null && nameDialog.isShowing()) {
            nameDialog.dismiss();
        }
        super.onDestroy();
    }

    private void openFullScreenFragment(String dayOfWeek) {
        WorkoutBuilder fullScreenFragment = WorkoutBuilder.newInstance(dayOfWeek);

        // Hide the BottomNavigationView, AppBarLayout, and LinearLayout containing the cards
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        AppBarLayout appBarLayout = findViewById(R.id.topAppBarLayout);
        LinearLayout cardContainer = findViewById(R.id.cardContainer);
        bottomNavigationView.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);
        cardContainer.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Set custom animations
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        // Replace the fragment
        transaction.replace(R.id.fragment_container, fullScreenFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Check if the WorkoutBuilder fragment is visible
        WorkoutBuilder fullScreenFragment = (WorkoutBuilder) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fullScreenFragment != null && fullScreenFragment.isVisible()) {
            // Show the BottomNavigationView, AppBarLayout, and LinearLayout containing the cards
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            AppBarLayout appBarLayout = findViewById(R.id.topAppBarLayout);
            LinearLayout cardContainer = findViewById(R.id.cardContainer);
            bottomNavigationView.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }
}
