package com.shtokal.tools.lights;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shtokal.tools.R;

import java.text.DecimalFormat;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LightActivity extends AppCompatActivity {
    private TextView tvLightStatus;
    private RelativeLayout rtlTurnOnOffFlashlight;
    private RadioGroup rbgMode;
    private LinearLayout twinkleSetting;
    private TextView tvTwinkleMessage;
    private SeekBar sbTwinkleTime;
    private ImageView imvLightBuld;

    private RadioButton rbFlash;

    private boolean IsSupportFlash = false;
    private boolean IsFlashlightOn = false;
    private Camera camera;
    private SharePreManager sharePreManager;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private static final int SCREEN_LIGHT_REQUEST_CODE = 1;
    private static final int FLASH_MODE = 1;
    private static final int SCREEN_MODE = 2;
    private static final int TWINKLE_MODE = 3;
    private static final int SOS_MODE = 4;
    private int currentRdbID = -1;
    private int delay = 100;
    private BlinkThread blinkThread;
    private Animation fadeIn;
    private Animation fadeOut;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_light);

        initView();


        final Typeface regular = Typeface.createFromAsset(getAssets(), "montserrat.ttf");

        IsSupportFlash = checkFlashSupport();


        setListener();

        sharePreManager = new SharePreManager(this);

        Log.d("S", "onCreateView");
    }


    public void setListener() {
        rtlTurnOnOffFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                rtlTurnOnOffFlashlight.startAnimation(animation);
                if (IsFlashlightOn) {
                    TurnOffFlashlight();
                } else {
                    int MODE = getCurrentMode();
                    TurnOnFlashlight(MODE);
                }
            }
        });

        rbgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int MODE = getCurrentMode();
                ChangeFlashlightMode(MODE);

                try {
                    if (currentRdbID != -1)
                        ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.WHITE);

                    currentRdbID = rbgMode.getCheckedRadioButtonId();
                    ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.parseColor("#212121"));
                } catch (Exception e) {
                }
            }
        });

        sbTwinkleTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delay = progress;
                DecimalFormat format = new DecimalFormat("#.#");
                tvTwinkleMessage.setText("Blinking time is " + format.format(delay / 1000f) + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public int getCurrentMode() {
        int id = rbgMode.getCheckedRadioButtonId();
        int re = 0;

        switch (id) {
            case R.id.rbFlash:
                re = FLASH_MODE;
                break;

            case R.id.rbScreen:
                re = SCREEN_MODE;
                break;

            case R.id.rbTwinkle:
                re = TWINKLE_MODE;
                break;

            case R.id.rbSOS:
                re = SOS_MODE;
                break;
        }

        return re;
    }

    public void ChangeFlashlightMode(int flashMode) {

        // Show / hide twinkle setting
        showHideTwinkleSetting(flashMode);

        // Check flash support && flashlight_icon status
        if (!IsFlashlightOn) {
            showCustomToast("Please turn on flashlight_icon before");
            return;
        }

        if (!IsSupportFlash)
            showCustomToast("Your device does not support flash, SCREEN MODE is on");

        switch (flashMode) {
            case FLASH_MODE:
                Log.i("MODE", "Change to flash");
                ChangeToFlashMode();
                break;

            case SCREEN_MODE:
                Log.i("MODE", "Change to SCREEN");
                ChangeToScreenMode();
                break;

            case TWINKLE_MODE:
                Log.i("MODE", "Change to TWINKLE");
                ChangeToTwinkleMode();
                break;

            case SOS_MODE:
                Log.i("MODE", "Change to SOS");
                ChangeToSOSMode();
                break;
        }
    }

    public void showHideTwinkleSetting(int flashMode) {

        AnimationSet animation = new AnimationSet(false); //change to false

        if (flashMode != TWINKLE_MODE) {


            fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(300);

            animation.addAnimation(fadeOut);
            twinkleSetting.setAnimation(animation);

            twinkleSetting.setVisibility(LinearLayout.GONE);
        } else {


            twinkleSetting.setVisibility(LinearLayout.VISIBLE);

            fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(1000);

            animation.addAnimation(fadeIn);
            twinkleSetting.setAnimation(animation);

            delay = sbTwinkleTime.getProgress();
        }
    }

    private void ChangeToSOSMode() {
        BlinkRunning = false;
        TurnOnSOSMode();
    }

    private void ChangeToTwinkleMode() {
        BlinkRunning = false;
        TurnOnTwinkleMode();
    }

    private void ChangeToScreenMode() {
        BlinkRunning = false;
        TurnOffFlash();
        TurnOnScreenMode();
    }

    private void ChangeToFlashMode() {
        BlinkRunning = false;
        TurnOnFlashMode();
    }

    public void TurnOnFlashlight(int mode) {

        BlinkRunning = false;

        if (!IsSupportFlash)
            showCustomToast("Your device does not support flash, SCREEN MODE is on");

        switch (mode) {
            case FLASH_MODE:
                TurnOnFlashMode();
                break;

            case SCREEN_MODE:
                TurnOnScreenMode();
                break;

            case TWINKLE_MODE:
                TurnOnTwinkleMode();
                break;

            case SOS_MODE:
                TurnOnSOSMode();
                break;
        }

        IsFlashlightOn = true;
        tvLightStatus.setText("Flashlight on");
        imvLightBuld.setImageResource(R.drawable.light_off);
    }

    public boolean BlinkRunning = false;

    public void TurnOnFlashMode() {
        if (IsSupportFlash) {
            TurnOnFlash();
        } else {
            TurnOnScreenMode();
        }
    }

    public void TurnOnScreenMode() {
        // Turn off flash
        TurnOffFlashlight();

        Intent intent = new Intent(LightActivity.this, WhiteActivity.class);
        startActivityForResult(intent, SCREEN_LIGHT_REQUEST_CODE);
    }

    public void TurnOnSOSMode() {

        if (IsSupportFlash) {
            delay = 300;
            TurnOnFlash();
            BlinkRunning = true;

            if (blinkThread != null)
                blinkThread.interrupt();

            blinkThread = new BlinkThread();
            blinkThread.start();
        } else {
            TurnOnScreenMode();
        }
    }

    public void TurnOnTwinkleMode() {

        if (IsSupportFlash) {
            TurnOnFlash();
            BlinkRunning = true;

            if (blinkThread != null)
                blinkThread.interrupt();

            blinkThread = new BlinkThread();
            blinkThread.start();
        } else {
            TurnOnScreenMode();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void TurnOnFlash() {

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
        } catch (CameraAccessException e) {
        }

    }

    public void TurnOffFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
        }

    }

    public void TurnOffFlashlight() {
        // Stop all blink thread if this is exist
        BlinkRunning = false;

        if (IsSupportFlash)
            TurnOffFlash();

        IsFlashlightOn = false;
        tvLightStatus.setText("Flashlight off");
        imvLightBuld.setImageResource(R.drawable.light_on);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkFlashSupport() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void initView() {
        tvLightStatus =  findViewById(R.id.tvLightStatus);
        rtlTurnOnOffFlashlight =  findViewById(R.id.btnTurnOnOffLight);

        rbgMode =  findViewById(R.id.rbgMode);
        rbFlash = findViewById(R.id.rbFlash);
        RadioButton rbScreen =  findViewById(R.id.rbScreen);
        RadioButton rbTwinkle =  findViewById(R.id.rbTwinkle);
        RadioButton rbSOS =  findViewById(R.id.rbSOS);

        sbTwinkleTime = findViewById(R.id.sbTwinkleTime);
        tvTwinkleMessage = findViewById(R.id.tvTwinkleMessage);
        twinkleSetting =  findViewById(R.id.twinkleSettings);

        imvLightBuld =  findViewById(R.id.imvLightBuld);

        rbFlash.setChecked(true);
        currentRdbID = R.id.rbFlash;
        ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.parseColor("#212121"));
    }

    public void showCustomToast(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_vew,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 140);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCREEN_LIGHT_REQUEST_CODE:
                TurnOffFlashlight();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!IsFlashlightOn || sharePreManager.isDontRemind())
            super.onBackPressed();
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Press иет")
                    .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharePreManager.setRemindValue(sharePreManager.getRemindValue() + 1);
                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        BlinkRunning = false;
        if (blinkThread != null)
            blinkThread.interrupt();

        TurnOffFlash();

        super.onDestroy();
    }

    public class BlinkThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {

            try {
                if (camera == null) {
                    TurnOnFlash();
                }
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];

                boolean flashMode=false;

                while (BlinkRunning) {

                    cameraManager.setTorchMode(cameraId, flashMode);
                    flashMode=!flashMode;
                    sleep(delay);
                }
            } catch (Exception e) {
            }
        }
    }
}