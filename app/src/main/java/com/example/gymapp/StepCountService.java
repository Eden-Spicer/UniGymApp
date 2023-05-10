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

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int currentStepCount = 0;
    private boolean isSensorAvailable;

    public StepCountService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isSensorAvailable = stepSensor != null;
            if (isSensorAvailable) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this, stepSensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            currentStepCount++;
            updateStepCount(currentStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateStepCount(int stepCount) {
        Intent intent = new Intent("STEP_COUNT_UPDATED");
        intent.putExtra("step_count", stepCount);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
