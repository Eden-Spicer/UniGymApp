package com.example.gymapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class StepCountService extends Service implements SensorEventListener {

    private SensorManager sensorManager; // The SensorManager manages all available sensors on the device.
    private Sensor stepSensor; // The stepSensor measures the number of steps taken by the user.
    private int currentStepCount = 0; // The current number of steps taken by the user.
    private boolean isSensorAvailable; // A flag indicating whether or not the stepSensor is available on the device.

    public StepCountService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Get the SensorManager instance.
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR); // Get the stepSensor instance.
            isSensorAvailable = stepSensor != null; // Check if the stepSensor is available on the device.
            if (isSensorAvailable) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI); // Register the listener for stepSensor events.
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this, stepSensor); // Unregister the listener for stepSensor events.
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) { // Check if the event was triggered by the stepSensor.
            currentStepCount++; // Increment the step count.
            updateStepCount(currentStepCount); // Update the step count.
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateStepCount(int stepCount) {
        Intent intent = new Intent("STEP_COUNT_UPDATED"); // Create a new intent with the "STEP_COUNT_UPDATED" action.
        intent.putExtra("step_count", stepCount); // Add the step count as an extra to the intent.
        sendBroadcast(intent); // Broadcast the intent to all interested parties.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
