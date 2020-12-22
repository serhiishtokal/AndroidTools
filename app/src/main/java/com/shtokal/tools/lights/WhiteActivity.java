package com.shtokal.tools.lights;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.shtokal.tools.R;

public class WhiteActivity extends AppCompatActivity {

    private RelativeLayout btnBack;
    private SeekBar sbBrightness;
    private SharePreManager sharePreManager;
    private float bv = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_white);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharePreManager = new SharePreManager(this);
        bv = sharePreManager.getBrightnessValue();


        setScreenBrightness(bv);

        btnBack = (RelativeLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sbBrightness = (SeekBar) findViewById(R.id.sbBrightness);
        sbBrightness.setProgress((int) (bv * 100));

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bv = progress / 100f;
                setScreenBrightness(bv);
                sharePreManager.saveBrightness(bv);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setScreenBrightness(float value) {
        if (value <= 0.01f)
            value = 0.01f;

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = value;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
        super.onBackPressed();
    }
}