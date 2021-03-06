package com.shtokal.tools.levell.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shtokal.tools.R;
import com.shtokal.tools.levell.model.SensorData;
import com.shtokal.tools.levell.utils.AppConstants;
import com.shtokal.tools.levell.view.custom.BubbleLevel;
import com.shtokal.tools.levell.view.custom.LevelGraph;

import static com.shtokal.tools.levell.utils.AppConstants.MAX_RANGE;
import static com.shtokal.tools.levell.utils.AppConstants.MIN_RANGE;


public class Levell extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "mnAct";
    private SensorManager sensorManager;
    private Sensor bubbleSensor;
    private TextView rot;

    private BubbleLevel bubbleLevel;
    private LevelGraph levelGraph;
    private OrientationEventListener orientationEventListener;
    private int screenOrientation;
    private static final int SENSOR_DELAY_TIME = 16 * 1000;
    private float[] filteredValues;
    private static final float LOW_PASS_FILTER_AMOUNT = 1f;
    private SharedPreferences preferences;
    private boolean isTiltAngleShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_level2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rot = (TextView) findViewById(R.id.rotation);
        bubbleLevel = (BubbleLevel) findViewById(R.id.custom_view_bubble);

        preferences = getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
        orientationEventListener = new OrientationEventListener(this, SENSOR_DELAY_TIME) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 315 || orientation < 45) {
                    screenOrientation = 0;
                } else if (orientation >= 45 && orientation < 135) {
                    screenOrientation = 90;
                } else if (orientation >= 135 && orientation < 225) {
                    screenOrientation = 180;
                } else if (orientation >= 225 && orientation < 315) {
                    screenOrientation = 270;
                }
            }
        };
        if (orientationEventListener.canDetectOrientation() == true) {
            Log.d(TAG, "orientation available");
            orientationEventListener.enable();
        } else {
            Log.d(TAG, "orientation not available");
            orientationEventListener.disable();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            bubbleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, bubbleSensor, SENSOR_DELAY_TIME);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values != null) {
            filteredValues = getLowPassFilterValues(event.values, filteredValues);
            double xAxis = filteredValues[0];
            double yAxis = filteredValues[1];
            double zAxis = filteredValues[2];
            double pitch = Math.atan(xAxis / Math.sqrt(Math.pow(yAxis, 2) + Math.pow(zAxis, 2)));
            double roll = Math.atan(yAxis / Math.sqrt(Math.pow(xAxis, 2) + Math.pow(zAxis, 2)));

            long roundedPitch = Math.round(Math.toDegrees(pitch));
            long roundedRoll = Math.round(Math.toDegrees(roll));
            SensorData sensorData = new SensorData();
            sensorData.setPitch(roundedPitch);
            sensorData.setRoll(roundedRoll);
            bubbleLevel.drawBubbleView(sensorData, screenOrientation);

            if (roundedPitch < MIN_RANGE) {
                roundedPitch = MIN_RANGE;
            } else if (roundedPitch > MAX_RANGE) {
                roundedPitch = MAX_RANGE;
            }
            if (roundedRoll < MIN_RANGE) {
                roundedRoll = MIN_RANGE;
            } else if (roundedRoll > MAX_RANGE) {
                roundedRoll = MAX_RANGE;
            }
            rot.setText("x: " + roundedPitch + "°\n" + "y: " + roundedRoll + "°\n");
            rot.setVisibility(isTiltAngleShown ? View.VISIBLE : View.GONE);

            Log.d(TAG, "pitch " + roundedPitch + " roll " + roundedRoll + " orientation: " + screenOrientation);
        }
    }

    private float[] getLowPassFilterValues(float[] newValue, float[] oldValue) {
        if (oldValue == null) {
            return newValue;
        }
        for (int i = 0; i < newValue.length; i++) {
            oldValue[i] = oldValue[i] + LOW_PASS_FILTER_AMOUNT * (newValue[i] - oldValue[i]);
        }
        return oldValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_level, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, bubbleSensor, SENSOR_DELAY_TIME);
        if (preferences != null)
            isTiltAngleShown = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_TILT_ANGLE, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationEventListener.disable();
    }
}