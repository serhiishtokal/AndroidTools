package com.shtokal.tools.levell.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shtokal.tools.R;
import com.shtokal.tools.levell.utils.AppConstants;


public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView tv_seekbar_val;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Switch switch_vibrate;
    private Switch switch_tilt_angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_seekbar_val = (TextView) findViewById(R.id.tv_seekbar_val);
        switch_vibrate = (Switch) findViewById(R.id.switch_vibrate);
        seekBar.setMax(AppConstants.MAX_RANGE);

        preferences = this.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        int progress = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERANCE_LEVEL, 5);
        seekBar.setProgress(progress);
        tv_seekbar_val.setText("+/- " + progress);

        boolean isVibration = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, true);
        switch_vibrate.setChecked(isVibration);

        boolean isTiltAngle = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_TILT_ANGLE, true);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_seekbar_val.setText("+/- " + progress);

                editor.putInt(AppConstants.SHARED_PREF_KEY_TOLERANCE_LEVEL, progress);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switch_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, isChecked);
                editor.commit();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}