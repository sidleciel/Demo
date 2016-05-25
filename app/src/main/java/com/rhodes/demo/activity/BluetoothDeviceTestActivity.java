package com.rhodes.demo.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.ClassUtil;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.customview.AnalogView;

import java.util.HashMap;

/**
 * Created by xiet on 2015/9/11.
 */
public class BluetoothDeviceTestActivity extends ActionBarActivity {
    private final String TAG = "BluetoothDeviceTestActivity";

    TextView   L1;
    TextView   L2;
    TextView   R1;
    TextView   R2;
    TextView   LEFT;
    TextView   UP;
    TextView   RIGHT;
    TextView   DOWN;
    TextView   X;
    TextView   A;
    TextView   Y;
    TextView   B;
    TextView   SELECT;
    TextView   START;
    AnalogView ANALOG_L;
    AnalogView ANALOG_R;


    View[] views;
    Integer[]              keycodes = new Integer[]{KeyEvent.KEYCODE_BUTTON_L1, KeyEvent.KEYCODE_BUTTON_L2, KeyEvent.KEYCODE_BUTTON_R1, KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_BUTTON_X, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_Y, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_SELECT, KeyEvent.KEYCODE_BUTTON_START};
    HashMap<Integer, View> map      = new HashMap<Integer, View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_test);

        L1 = (TextView) findViewById(R.id.L1);
        L2 = (TextView) findViewById(R.id.L2);
        R1 = (TextView) findViewById(R.id.R1);
        R2 = (TextView) findViewById(R.id.R2);
        LEFT = (TextView) findViewById(R.id.LEFT);
        UP = (TextView) findViewById(R.id.UP);
        RIGHT = (TextView) findViewById(R.id.RIGHT);
        DOWN = (TextView) findViewById(R.id.DOWN);
        X = (TextView) findViewById(R.id.X);
        A = (TextView) findViewById(R.id.A);
        Y = (TextView) findViewById(R.id.Y);
        B = (TextView) findViewById(R.id.B);
        SELECT = (TextView) findViewById(R.id.SELECT);
        START = (TextView) findViewById(R.id.START);
        ANALOG_L = (AnalogView) findViewById(R.id.ANALOG_L);
        ANALOG_R = (AnalogView) findViewById(R.id.ANALOG_R);

        views = new View[]{L1, L2, R1, R2, LEFT, UP, RIGHT, DOWN, X, A, Y, B, SELECT, START};
        for (int i = 0; i < views.length; i++) {
            map.put(keycodes[i], views[i]);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Logger.log(TAG, "dispatchKeyEvent", event);

        return super.dispatchKeyEvent(event) | processJoystickButton(event);
    }


    boolean processJoystickButton(KeyEvent event) {
        Logger.log(event.toString());

        boolean  ret  = false;
        TextView view = (TextView) map.get(event.getKeyCode());

        if (!isDpadButton(event.getKeyCode()))
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                    ANALOG_L.drawThumb(true);
                    ret = true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                    ANALOG_R.drawThumb(true);
                    ret = true;
                } else if (view != null) {
                    view.setTextColor(getResources().getColor(R.color.bluetooth_device_test_key_pressed));
                    ret = true;
                }
            } else {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                    ANALOG_L.drawThumb(false);
                    ret = true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_THUMBR) {
                    ANALOG_R.drawThumb(false);
                    ret = true;
                } else if (view != null) {
                    view.setTextColor(getResources().getColor(R.color.bluetooth_device_test_key_default));
                    ret = true;
                }
            }

        return ret;
    }

    boolean isDpadButton(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Logger.log(TAG, "onGenericMotionEvent", event);
        return super.onGenericMotionEvent(event) | processJoystickAnalog(event);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    boolean processJoystickAnalog(MotionEvent event) {
        Logger.log(event.toString(), "TILT=" + event.getAxisValue(MotionEvent.AXIS_TILT));

        boolean ret = false;
        //X axis's positive direction is right, Y axis's positive direction is down.
        float l_x = event.getAxisValue(MotionEvent.AXIS_X);
        float l_y = event.getAxisValue(MotionEvent.AXIS_Y);
        float r_x = event.getAxisValue(MotionEvent.AXIS_Z);
        float r_y = event.getAxisValue(MotionEvent.AXIS_RZ);
        Logger.log("processJoystickAnalog--> x=" + l_x + " y=" + l_y + " z=" + r_x + " zr=" + r_y);

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            ANALOG_L.draw(l_x, l_y);
            ANALOG_R.draw(r_x, r_y);

            float lTrigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
            float rTrigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
            Logger.log("processJoystickAnalog--> " + " lTrigger=" + lTrigger + " rTrigger=" + rTrigger);

            if (getJoystickDpadCode(event) == 0) {
                ret = true;
            }
        }

        return ret;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    int getJoystickDpadCode(MotionEvent event) {
        //support multiple gamepad controller

        int   ret  = 0;
        float hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        if (hatX == -1) {
            ret |= KeyEvent.KEYCODE_DPAD_LEFT;
        } else if (hatX == 1) {
            ret |= KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        if (hatY == -1) {
            ret |= KeyEvent.KEYCODE_DPAD_UP;
        } else if (hatY == 1) {
            ret |= KeyEvent.KEYCODE_DPAD_DOWN;
        }
        Logger.log("getJoystickDpadCode--DPAD_FLAG==>", "deviceId=" + event.getDeviceId(), "hatX=" + hatX, "hatY=" + hatY, "keyCode=" + ret);
        return ret;
    }

}
