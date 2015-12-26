package com.rhodes.demo.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.rhodes.demo.R;

/**
 * Created by xiet on 2015/9/15.
 */
public class AnalogView extends View {

    private boolean drawThumb;

    private float axisX;
    private float axisY;
//        private final float threshold = 0.01f;

    public AnalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw
        Resources res = getResources();
        int backgroundColor = res.getColor(R.color.bluetooth_device_test_key_default);
        int foreground = res.getColor(R.color.bluetooth_device_test_key_pressed);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        int widthHalf = width / 2;
        int heightHalf = height / 2;
        int radius = widthHalf < heightHalf ? widthHalf : heightHalf;

        try {
//            canvas.drawColor(Color.WHITE);
            double direct = Math.sqrt(this.axisX * this.axisX + this.axisY * this.axisY);
            if (direct > 1.0) {
                Paint boderPaint = new Paint();
                boderPaint.setAntiAlias(true);// 去锯齿
                boderPaint.setColor(Color.RED);
                boderPaint.setStyle(Paint.Style.STROKE);
                boderPaint.setStrokeWidth(2);
                canvas.drawRect(0, 0, width, height, boderPaint);
            }

            // draw bg
            Paint paint = new Paint();
            paint.setAntiAlias(true);// 去锯齿
            if (drawThumb) {
                paint.setColor(foreground);
            } else {
                paint.setColor(backgroundColor);
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            canvas.drawCircle(widthHalf, heightHalf, radius, paint);

            //draw annalog point
            Paint analogPaint = new Paint();
            analogPaint.setAntiAlias(true);// 去锯齿
            analogPaint.setColor(foreground);
            analogPaint.setStyle(Paint.Style.FILL);
            analogPaint.setStrokeWidth(3);
            float annalogRadius = 5.0f * radius / 100;
            canvas.drawCircle(widthHalf + this.axisX * radius, heightHalf + this.axisY * radius, annalogRadius, analogPaint);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDraw(canvas);
    }

    public void draw(float axisX, float axisY) {
        this.axisX = axisX;
        this.axisY = axisY;

        postInvalidate();

    }

    public void drawThumb(boolean draw) {
        this.drawThumb = draw;

        postInvalidate();

    }
}