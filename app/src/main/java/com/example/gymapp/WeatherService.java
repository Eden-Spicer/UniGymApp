package com.example.gymapp;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService extends IntentService {

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("WeatherService", "Location fetched: lat=" + latitude + ", lon=" + longitude);
                    fetchWeatherData(latitude, longitude);
                } else {
                    Log.d("WeatherService", "Location is null");
                }
            }
        });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        String apiKey = "4845d99335c14d6d97bf3e89563d9138";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("WeatherService", "Weather API request failed");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("WeatherService", "Weather API response: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                        int weatherCode = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
                        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                        temperature = Math.round(temperature - 273.15); // Convert from Kelvin to Celsius and round off

                        // Send a broadcast to update the CardView in Steps activity
                        Intent updateWeatherIntent = new Intent("WEATHER_UPDATED");
                        updateWeatherIntent.putExtra("weather_description", weatherDescription);
                        updateWeatherIntent.putExtra("weather_code", weatherCode);
                        updateWeatherIntent.putExtra("temperature", temperature);
                        LocalBroadcastManager.getInstance(WeatherService.this).sendBroadcast(updateWeatherIntent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("WeatherService", "JSONException while parsing weather API response");
                    }
                } else {
                    Log.d("WeatherService", "Weather API response is not successful");
                }
            }
        });
    }
}