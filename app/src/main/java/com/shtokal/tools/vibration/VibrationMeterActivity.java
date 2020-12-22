package com.shtokal.tools.vibration;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shtokal.tools.R;
import com.shtokal.tools.soundmeter.World;

import java.util.ArrayList;
import java.util.Date;

public class VibrationMeterActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor vibrationSensor;
    TextView vibration;
    TextView vibration2;
    LineChart mChart;
    public static Typeface tf;
    ArrayList<Entry> yVals;
    Handler handler;
    long currentTime=0;
    long savedTime=0;
    boolean isChart=false;
    int interval= 5000;
    boolean flag = false;
    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            flag = true;
            handler.postDelayed(this, interval);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vibration_meter_main);
        vibration = (TextView) findViewById(R.id.vibration);
        vibration2 = (TextView) findViewById(R.id.vibration2);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        vibrationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, vibrationSensor, 3);

        handler = new Handler();
        tf= Typeface.createFromAsset(this.getAssets(), "fonts/ALKATIP.TTF");
        initChart();
    }

    @Override
    protected void onResume() {
        handler.post(processSensors);
        sensorManager.registerListener(this, vibrationSensor, 3);
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(processSensors);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float vibrationValuef = (float) Math.sqrt((x * x) + (y * y)+ (z * z));
            float vibrationValuef2 = (float) ((float) Math.sqrt((x * x) + (y * y)+ (z * z))-9.80665);
            if(vibrationValuef2<0)  vibrationValuef2=0;

            String vibrationValue = String.format("%.2f", vibrationValuef);
            String vibrationValue2 = String.format("%.2f", vibrationValuef2);

            vibration.setText(String.valueOf(vibrationValue));
            vibration2.setText(String.valueOf(vibrationValue2));
            updateData(vibrationValuef2,0);

        }
    }
    private void initChart() {
        if(mChart!=null){
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                savedTime++;
                isChart=true;
            }
        }else{
            currentTime=new Date().getTime();
            mChart = (LineChart) findViewById(R.id.chart1);
            mChart.setViewPortOffsets(50, 20, 5, 60);
            mChart.setDescription("");
            mChart.setTouchEnabled(true);
            mChart.setDragEnabled(false);
            mChart.setScaleEnabled(true);
            mChart.setPinchZoom(false);
            mChart.setDrawGridBackground(false);
            XAxis x = mChart.getXAxis();
            x.setLabelCount(8, false);
            x.setEnabled(true);
            x.setTypeface(tf);
            x.setTextColor(Color.BLACK);
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setDrawGridLines(true);
            x.setAxisLineColor(Color.BLACK);
            YAxis y = mChart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(Color.BLACK);
            y.setTypeface(tf);
            y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.BLACK);
            y.setAxisMinValue(0);
            y.setAxisMaxValue(30);
            mChart.getAxisRight().setEnabled(true);
            yVals = new ArrayList<Entry>();
            yVals.add(new Entry(0,0));
            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
            set1.setValueTypeface(tf);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.1f);
            set1.setDrawFilled(false);
            set1.setDrawCircles(false);
            set1.setCircleColor(Color.GREEN);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.RED);
            set1.setDrawHorizontalHighlightIndicator(false);

            LineData data;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                data =  mChart.getLineData();
                data.clearValues();
                data.removeDataSet(0);
                data.addDataSet(set1);
            }else {
                data = new LineData(set1);
            }

            data.setValueTextSize(9f);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.getLegend().setEnabled(false);
            mChart.animateXY(2000, 2000);
            mChart.invalidate();
            isChart=true;
        }

    }

    private void updateData(float val, long time) {
        if(mChart==null){
            return;
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            Entry entry=new Entry(savedTime,val);
            set1.addEntry(entry);
            if(set1.getEntryCount()>200){
                set1.removeFirst();
                set1.setDrawFilled(false);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            savedTime++;
        }
    }

}
