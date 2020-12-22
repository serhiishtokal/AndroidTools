package com.shtokal.tools.metalDetector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shtokal.tools.R;
import com.white.progressview.CircleProgressView;

import java.math.BigDecimal;

import lecho.lib.hellocharts.view.LineChartView;


public class MetalActivity extends AppCompatActivity implements SensorEventListener{
    private TextView mTextX;
    private TextView mTextY;
    private TextView mTextZ;
    private TextView mTotal;
    private SensorManager sensorManager;
    private CircleProgressView progressView;
    private TextView metalState;
    private LineChartView lineChart;
    private LineCharts lineCharts = new LineCharts();
    private Toolbar toolbar;
    double alarmLim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metaldetector);
        initFunc();//Initialize all controls
        if(new Common().readSharedPreferencesByBool("tip")){

        }

                if(!new Common().isMagneticSensorAvailable()){
                    new Common().showDialog(this);
                }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Double rawTotal;//Unprocessed data
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            //Keep the screen always on
            if(new Common().readSharedPreferencesByBool("keep_wake")){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            float X_lateral = sensorEvent.values[0];
            float Y_lateral = sensorEvent.values[1];
            float Z_lateral = sensorEvent.values[2];

            rawTotal = Math.sqrt(X_lateral * X_lateral + Y_lateral * Y_lateral + Z_lateral * Z_lateral);

            BigDecimal total = new BigDecimal(rawTotal);
            double res = total.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            lineCharts.makeCharts(lineChart,(float) res);//Real-time drawing
            mTextX.setText(X_lateral + " μT");
            mTextY.setText(Y_lateral + " μT");
            mTextZ.setText(Z_lateral + " μT");
            mTotal.setText( res + " μT");
            String alarmLimStr = new Common().readSharedPreferencesByString("alarm_limit");

            if(!alarmLimStr.isEmpty()){
                alarmLim = Double.valueOf(alarmLimStr);
            }else {
                alarmLim = 80;
            }
            if (res < alarmLim){
                metalState.setTextColor(Color.rgb(0,0,0));
                metalState.setText("No metal detected");
                int progress = (int)((res / alarmLim )* 100);//Calculation progress
                progressView.setReachBarColor(Color.rgb(30,144,255));
                progressView.setProgress(progress);//progress bar
            }else{
                metalState.setTextColor(Color.rgb(255,0,0));//红色
                metalState.setText("Metal detected!");
                progressView.setReachBarColor(Color.rgb(255,0,0));
                progressView.setProgress(100);//Progress bar full

                if (new Common().isVibrate()){
                    new Common().vibrate();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(MetalActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    private void initFunc(){

        mTextX = (TextView)findViewById(R.id.x);
        mTextY = (TextView)findViewById(R.id.y);
        mTextZ = (TextView)findViewById(R.id.z);
        mTotal = (TextView)findViewById(R.id.total);
        progressView = findViewById(R.id.totalMetalProgress);
        metalState = (TextView) findViewById(R.id.metalDetect);
        lineChart = (LineChartView) findViewById(R.id.chart);
        lineCharts.initView(lineChart);
    }
}
