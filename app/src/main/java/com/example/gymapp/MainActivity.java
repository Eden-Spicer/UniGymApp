package com.example.gymapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

// Gather Weather

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

// Gather Day
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar TopAppBarText;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String OPEN_WEATHER_MAP_API_KEY = "4845d99335c14d6d97bf3e89563d9138";
    private OkHttpClient httpClient;
    private ImageView weatherImageView;

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


        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            fetchWeatherData(latitude, longitude);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void fetchWeatherData(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + OPEN_WEATHER_MAP_API_KEY;
        httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API Response", responseBody); // log the JSON response to the console
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateImageViewWithWeatherData(jsonResponse);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("fetchWeatherData", "Response is not successful. Code: " + response.code());
                }
            }
        });
    }

    private void updateImageViewWithWeatherData(JSONObject jsonResponse) {
        try {
            JSONObject weather = jsonResponse.getJSONArray("weather").getJSONObject(0);
            String iconCode = weather.getString("icon");
            String weatherCode = weather.getString("id");

            // Map weather codes to corresponding image resource IDs
            HashMap<String, Integer> weatherImages = new HashMap<>();
            weatherImages.put("800", R.drawable.monkey);
            // Add more mappings for other weather codes here

            // Update the ImageView with the corresponding image resource ID
            weatherImageView = findViewById(R.id.mondayImage);
            if (weatherImages.containsKey(weatherCode)) {
                int imageResourceId = weatherImages.get(weatherCode);
                weatherImageView.setImageResource(imageResourceId);
            } else {
                // Use a default image if no mapping is found for the weather code
                weatherImageView.setImageResource(R.drawable.fitness);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
