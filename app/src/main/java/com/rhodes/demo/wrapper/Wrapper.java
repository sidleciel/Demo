package com.rhodes.demo.wrapper;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.view.MotionEvent;

/**
 * Created by xiet on 2015/7/15.
 */
public class Wrapper {
    public static int SDK = Build.VERSION.SDK_INT;

    public static class MGBtManager {
        public static boolean isSupport() {
            if (SDK >= 11 && isSupportBluetooth())
                return true;
            else
                return false;
        }
    }

    public static boolean isSupportBluetooth() {
        if (BluetoothAdapter.getDefaultAdapter() != null) return true;
        return false;
    }

    public static float getAxisValue(MotionEvent event, int axis) {
        float ret = 0.0f;

        if (SDK >= 12) {
            Wrapper12.getAxisValue(event, axis);
        }
        return ret;
    }

}
