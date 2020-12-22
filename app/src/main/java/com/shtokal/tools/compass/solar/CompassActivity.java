package com.shtokal.tools.compass.solar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shtokal.tools.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;

public class CompassActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CHECK_GOOGLE_SETTINGS = 0x99;
    public static final String BACKGROUND = "background";
    public static final String GREETING_MESSAGE = "greeting_message";
    public static final String LOCATION = "location";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private View mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        initializeView();
    }

    private void initializeView() {
        mMainView = findViewById(R.id.compass_layout);
        mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

        findViewById(R.id.iv_location).setOnClickListener(this);

          Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(BACKGROUND) && bundle.containsKey(GREETING_MESSAGE) && bundle.containsKey(LOCATION)) {
            int background = bundle.getInt(BACKGROUND);
            int greetingMessage = bundle.getInt(GREETING_MESSAGE);

            if (background != -1 && greetingMessage != -1) {
                mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), background));


                Location location = bundle.getParcelable(LOCATION);
                if (location != null) {

                } else {
                }
            } else {
                mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

            }
        } else {
            mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

        }
    }

    @SuppressLint("MissingPermission")
    private void initialize() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

       FusedLocationProviderClient fusedLocationProviderClient =
                new FusedLocationProviderClient(getApplicationContext());

        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateDayState(location);

                        }
                    }
                });}
            else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private void updateDayState(Location location) {

        com.shtokal.tools.compass.solar.Location location1 =
                new com.shtokal.tools.compass.solar.Location(location.getLatitude(), location.getLongitude());
        SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(location1, Calendar.getInstance().getTimeZone());

        Date sunrise = sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(Calendar.getInstance()).getTime();
        Date sunset = sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(Calendar.getInstance()).getTime();
        Date current = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        Date noon = calendar.getTime();


        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);

        Date midNight = calendar1.getTime();


            mMainView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_location:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForLocationDialog();
                } else {
                    locationProvidedDialog();
                }
                break;

            default:
                break;
        }
    }

    private void askForLocationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage("allow location permission");
        builder.setPositiveButton(
                R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CompassActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CHECK_GOOGLE_SETTINGS);
                    }
                }
        );
        builder.setNegativeButton(
                R.string.dont_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    /**
     *
     */
    private void locationProvidedDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location");
        builder.setMessage("location permission allowed");
        builder.setPositiveButton(
                R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHECK_GOOGLE_SETTINGS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                }
                break;
        }
    }
}
