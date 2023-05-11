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
import androidx.appcompat.app.AppCompatDelegate;
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

    // Toolbar at the top of the application
    private MaterialToolbar TopAppBarText;

    // Dialog for first time user name input
    private AlertDialog nameDialog;

    // Method called when the activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Setting the layout of this activity
        setContentView(R.layout.activity_main);

        // Initializing the top app bar text view
        TopAppBarText = findViewById(R.id.topAppBar);

        // Initializing the bottom navigation menu
        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);

        // Initializing the cards for each day of the week
        MaterialCardView mondayCard = findViewById(R.id.mondayCard);
        MaterialCardView tuesdayCard = findViewById(R.id.tuesdayCard);
        MaterialCardView wednesdayCard = findViewById(R.id.wednesdayCard);
        MaterialCardView thursdayCard = findViewById(R.id.thursdayCard);
        MaterialCardView fridayCard = findViewById(R.id.fridayCard);
        MaterialCardView saturdayCard = findViewById(R.id.saturdayCard);
        MaterialCardView sundayCard = findViewById(R.id.sundayCard);


        // Defining card elevation in pixels
        float elevationDp = 10;
        float density = getResources().getDisplayMetrics().density;
        float elevationPx = elevationDp * density;

        // Getting current day of the week
        LocalDate currentDay = LocalDate.now();
        DayOfWeek day = currentDay.getDayOfWeek();

        // Setting the elevation and text typeface for the current day's card
        switch (day) {
            // For each case, increase the card's elevation and set the day's text to bold
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
                // This code should never execute since there are only 7 days in a week
                System.out.println("You have discovered a new day in the week!");
                break;
        }

        // Checking if it's the user's first time using the app
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        if (prefs.getBoolean("first_time", true)) {
            // If it's the first time, display the name dialog
            showNameDialog();
        } else {
            // If not, retrieve the user's name and display a welcome message
            String storedName = prefs.getString("user_name", "User");
            TopAppBarText.setSubtitle(String.format("Hello, %s", storedName + "! Ready for a workout?"));
        }

        // Setting the listener for the bottom navigation menu item selection
        bottomNavMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page_1) {
                // Handle click on the first page
                return true;
            } else if (itemId == R.id.page_2) {
                // Handle click on the second page by starting a new activity
                Intent intent = new Intent(MainActivity.this, Steps.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Setting the listeners for each day's card clicks
        mondayCard.setOnClickListener(v -> openFullScreenFragment("Monday"));

        tuesdayCard.setOnClickListener(v -> openFullScreenFragment("Tuesday"));

        wednesdayCard.setOnClickListener(v -> openFullScreenFragment("Wednesday"));

        thursdayCard.setOnClickListener(v -> openFullScreenFragment("Thursday"));

        fridayCard.setOnClickListener(v -> openFullScreenFragment("Friday"));

        saturdayCard.setOnClickListener(v -> openFullScreenFragment("Saturday"));

        sundayCard.setOnClickListener(v -> openFullScreenFragment("Sunday"));

    }

    // Method to show a dialog for the user to enter their name
    private void showNameDialog() {
        // Creating a new alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to FlexyFrog");

        // Creating a new TextInputLayout
        TextInputLayout inputLayout = new TextInputLayout(this);
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

        // Creating a new TextInputEditText and adding it to the layout
        TextInputEditText input = new TextInputEditText(inputLayout.getContext());
        input.setHint("Enter your name");
        inputLayout.addView(input);

        // Setting the TextInputLayout as the view for the dialog
        builder.setView(inputLayout);

        // Setting the action for the positive button in the dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Checking that the user has entered text
            if (input.getText() != null) {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Updating the toolbar subtitle with the user's name
                    TopAppBarText.setSubtitle(String.format("Hello, %s", name + "! Ready for a workout?"));

                    // Saving the user's name in shared preferences
                    SharedPreferences.Editor editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
                    editor.putString("user_name", name);
                    editor.putBoolean("first_time", false);
                    editor.apply();
                }
            }
        });

        // Setting the action for the negative button in the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Creating and showing the dialog
        nameDialog = builder.create();
        nameDialog.show();
    }

    // Method called when the activity is being destroyed
    @Override
    protected void onDestroy() {
        // Dismissing the name dialog if it's still showing
        if (nameDialog != null && nameDialog.isShowing()) {
            nameDialog.dismiss();
        }
        super.onDestroy();
    }

    // Method to open a full screen fragment for the given day of the week
    private void openFullScreenFragment(String dayOfWeek) {
        // Creating a new instance of the WorkoutBuilder fragment
        WorkoutBuilder fullScreenFragment = WorkoutBuilder.newInstance(dayOfWeek);

        // Hiding the bottom navigation view, app bar layout, and card container
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        AppBarLayout appBarLayout = findViewById(R.id.topAppBarLayout);
        LinearLayout cardContainer = findViewById(R.id.cardContainer);
        bottomNavigationView.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);
        cardContainer.setVisibility(View.GONE);

        // Starting a new fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Setting custom animations for the transaction
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        // Replacing the current fragment with the new one and adding the transaction to the back stack
        transaction.replace(R.id.fragment_container, fullScreenFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Method called when the back button is pressed
    @Override
    public void onBackPressed() {
        // Checking if the WorkoutBuilder fragment is currently visible
        WorkoutBuilder fullScreenFragment = (WorkoutBuilder) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fullScreenFragment != null && fullScreenFragment.isVisible()) {
            // If it is, show the bottom navigation view, app bar layout, and card container
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            AppBarLayout appBarLayout = findViewById(R.id.topAppBarLayout);
            LinearLayout cardContainer = findViewById(R.id.cardContainer);
            bottomNavigationView.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.VISIBLE);
        }

        // Call the super class's onBackPressed method to handle the rest of the back button click event
        super.onBackPressed();
    }
}
