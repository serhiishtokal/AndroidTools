package com.shtokal.tools.metalDetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.shtokal.tools.MyApplication;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

public class Common {
    boolean vibrate;
    Context context = MyApplication.getContext();


    public void vibrate(){
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }


    public boolean isVibrate() {
        vibrate = PreferenceManager.getDefaultSharedPreferences(
                context).getBoolean("vibrate", true);
        return vibrate;
    }

    public Boolean readSharedPreferencesByBool(String key){
        SharedPreferences sp = context.getSharedPreferences("com.shtokal.tools.metalDetector",MODE_PRIVATE);
        Boolean res = sp.getBoolean(key,true);
        return res;
    }

    public String readSharedPreferencesByString(String key){
        SharedPreferences sp = context.getSharedPreferences("com.shtokal.tools.metalDetector",MODE_PRIVATE);
        String res = sp.getString(key,"80");
        return res;
    }

    public Boolean isMagneticSensorAvailable() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        return magneticSensor != null;
    }



    public void showDialog(Context context){
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("OOOOOOOps!")
                .setMessage("Your device does not have a Hall sensor！")
                .setNegativeButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    }
