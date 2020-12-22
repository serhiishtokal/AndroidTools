package com.shtokal.tools.levell.view.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.shtokal.tools.levell.model.SensorData;
import com.shtokal.tools.levell.utils.AppConstants;
import com.shtokal.tools.levell.utils.AppUtils;

import static com.shtokal.tools.levell.utils.AppConstants.MAX_RANGE;
import static com.shtokal.tools.levell.utils.AppConstants.MIN_RANGE;
import static com.shtokal.tools.levell.utils.AppConstants.TYPE_LANDSCAPE;
import static com.shtokal.tools.levell.utils.AppConstants.TYPE_PORTRAIT;


public class BubbleLevel extends View {

    private static final float BUBBLE_RADIUS = 55f;
    private Paint paint;
    private String TAG = "lvl";
    private SensorData sensorData;
    private int screenOrientation;
    private int centerWidth;
    private int centerHeight;
    private int viewWidth;
    private int viewHeight;
    private SharedPreferences preferences;
    private int outerBorderWidth;
    private float toleranceLevel;
    private Context context;
    private boolean isVibration;

    public BubbleLevel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        preferences = context.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
        outerBorderWidth = (int) (BUBBLE_RADIUS + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth = getWidth();
        viewHeight = getHeight();
        centerWidth = viewWidth / 2;
        centerHeight = viewHeight / 2;

        toleranceLevel = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERANCE_LEVEL, 5);
        isVibration = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, true);

        Log.d(TAG, "onDraw: width: " + viewWidth + " height: " + viewHeight);
        if (sensorData != null) {
            if ((screenOrientation == 0 | screenOrientation == 180) && ((sensorData.getRoll() >= 45 && sensorData.getRoll() < 135) || (sensorData.getRoll() <= -45 && sensorData.getRoll() > -135))) {
                Log.d(TAG, "type: 1D Bubble portrait");
                draw1DBubble(canvas, TYPE_PORTRAIT);

            } else if (screenOrientation == 90 | screenOrientation == 270) {
                Log.d(TAG, "type: 1D Bubble landscape");
                draw1DBubble(canvas, TYPE_LANDSCAPE);

            } else {
                Log.d(TAG, "type: 2D Bubble flat");
                draw2DBubble(canvas);

            }
        }
    }

    private void draw2DBubble(Canvas canvas) {
        float cx = 0;
        float cy = 0;
        float gap = 35;
        int MULTIPLIER = 15;
        paint.setStyle(Paint.Style.FILL);
        Log.d(TAG, "roll " + sensorData.getRoll() + " pitch " + sensorData.getPitch());

        if (sensorData.getPitch() > MAX_RANGE)
            cx = (float) (MAX_RANGE * MULTIPLIER + centerWidth);
        else if (sensorData.getPitch() < MIN_RANGE)
            cx = (float) (MIN_RANGE * MULTIPLIER + centerWidth);
        else
            cx = (float) (sensorData.getPitch() * MULTIPLIER + centerWidth);

        if (sensorData.getRoll() > MAX_RANGE)
            cy = (float) (viewHeight - (MAX_RANGE * MULTIPLIER + centerHeight));
        else if (sensorData.getRoll() < MIN_RANGE)
            cy = (float) (viewHeight - (MIN_RANGE * MULTIPLIER + centerHeight));
        else
            cy = (float) (viewHeight - (sensorData.getRoll() * MULTIPLIER + centerHeight));

        if ((sensorData.getRoll() > (0 + toleranceLevel) || sensorData.getRoll() < (0 - toleranceLevel)) || (sensorData.getPitch() > (0 + toleranceLevel) || sensorData.getPitch() < (0 - toleranceLevel))) {
            paint.setColor(Color.RED);
            if (isVibration)
                AppUtils.shortVibrate(context);
        } else
            paint.setColor(Color.GREEN);

        canvas.drawCircle(cx, cy, BUBBLE_RADIUS, paint);

        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerWidth, centerHeight, 5f, paint);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerWidth, centerHeight, BUBBLE_RADIUS + MULTIPLIER, paint);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerWidth, centerHeight, ((float) 10 + centerHeight - gap), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(centerWidth, centerHeight, (toleranceLevel * MULTIPLIER) + BUBBLE_RADIUS, paint);
    }

    private void draw1DBubble(Canvas canvas, int type) {
        float cx;
        float cy;
        int borderLeft;
        int borderTop;
        int borderRight;
        int borderBottom;
        int MULTIPLIER = 20;
        paint.setStyle(Paint.Style.FILL);

        if (type == TYPE_PORTRAIT) {
              if (sensorData.getPitch() > MAX_RANGE) {
                cx = viewWidth - (MAX_RANGE * MULTIPLIER + centerWidth);
                cy = centerHeight;
            } else if (sensorData.getPitch() < MIN_RANGE) {
                cx = viewWidth - (MIN_RANGE * MULTIPLIER + centerWidth);
                cy = centerHeight;
            } else {
                cx = (float) (viewWidth - (sensorData.getPitch() * MULTIPLIER + centerWidth));
                cy = centerHeight;
            }

            borderLeft = (int) (centerWidth - (MAX_RANGE * MULTIPLIER) - BUBBLE_RADIUS);
            borderTop = (centerHeight) - outerBorderWidth;
            borderRight = (int) (centerWidth + (MAX_RANGE * MULTIPLIER) + BUBBLE_RADIUS);
            borderBottom = (centerHeight) + outerBorderWidth;


            if (sensorData.getPitch() > (0 + toleranceLevel) || sensorData.getPitch() < (0 - toleranceLevel)) {
                paint.setColor(Color.RED);
                if (isVibration)
                    AppUtils.shortVibrate(context);
            } else
                paint.setColor(Color.GREEN);
        } else {
            if (sensorData.getRoll() > MAX_RANGE) {
                cx = viewWidth - (MAX_RANGE * MULTIPLIER + centerWidth);
                cy = centerHeight;
            } else if (sensorData.getRoll() < MIN_RANGE) {
                cx = viewWidth - (MIN_RANGE * MULTIPLIER + centerWidth);
                cy = centerHeight;
            } else {
                cx = (float) (viewWidth - (sensorData.getRoll() * MULTIPLIER + centerWidth));
                cy = centerHeight;
            }

            borderLeft = (int) (centerWidth - (MAX_RANGE * MULTIPLIER) - BUBBLE_RADIUS);
            borderTop = (centerHeight) - outerBorderWidth;
            borderRight = (int) (centerWidth + (MAX_RANGE * MULTIPLIER) + BUBBLE_RADIUS);
            borderBottom = (centerHeight) + outerBorderWidth;

            if (sensorData.getRoll() > (0 + toleranceLevel) || sensorData.getRoll() < (0 - toleranceLevel)) {
                paint.setColor(Color.RED);
                if (isVibration)
                    AppUtils.shortVibrate(context);
            } else
                paint.setColor(Color.GREEN);
        }

        canvas.drawCircle(cx, cy, BUBBLE_RADIUS, paint);

        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerWidth, centerHeight, 5f, paint);


        canvas.drawLine(centerWidth - BUBBLE_RADIUS, borderTop, centerWidth - BUBBLE_RADIUS, borderBottom, paint);
        canvas.drawLine(centerWidth + BUBBLE_RADIUS, borderTop, centerWidth + BUBBLE_RADIUS, borderBottom, paint);


        paint.setColor(Color.LTGRAY);
        canvas.drawLine(centerWidth - BUBBLE_RADIUS - (toleranceLevel * MULTIPLIER), borderTop, centerWidth - BUBBLE_RADIUS - (toleranceLevel * MULTIPLIER), borderBottom, paint);
        canvas.drawLine(centerWidth + BUBBLE_RADIUS + (toleranceLevel * MULTIPLIER), borderTop, centerWidth + BUBBLE_RADIUS + (toleranceLevel * MULTIPLIER), borderBottom, paint);


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, paint);
    }


    public void drawBubbleView(SensorData sensorData, int screenOrientation) {
        this.sensorData = sensorData;
        this.screenOrientation = screenOrientation;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, (heightSize / 2));
    }
}