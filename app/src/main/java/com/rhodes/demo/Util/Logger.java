package com.rhodes.demo.Util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.rhodes.demo.BuildConfig;
import com.rhodes.demo.wrapper.Wrapper;

public class Logger {
    private static final String TAG = Logger.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void log(String... s) {
        if (!DEBUG) return;//DEBUG

        String log = "";
        for (int i = 0; i < s.length; i++) {
            log += " " + s[i];
        }

        Log.e(TAG, log);
    }

    public static void log(String formate, Object... args) {
        Log.e(TAG, String.format(formate, args));
    }

    @SuppressLint("NewApi")
    public static void log(KeyEvent event) {

        String log = "\n";
        InputDevice device = event.getDevice();
        log += "getName:" + device.getName() + "\n";
        log += "getId:" + device.getId() + "\n";
        if (Wrapper.SDK >= 16) {
            log += "getDescriptor:" + device.getDescriptor() + "\n";//16
            log += "isVirtual:" + device.isVirtual() + "\n";//16
        }
        if (Wrapper.SDK >= 19) {
            log += "getControllerNumber:" + device.getControllerNumber() + "\n";//19
            log += "getProductId:" + device.getProductId() + "\n";//19
            log += "getVendorId:" + device.getVendorId() + "\n";//19
        }
        log += "getKeyboardType:" + device.getKeyboardType() + "\n";
        log += "describeContents:" + device.describeContents() + "\n";
        log += "getSources:" + device.getSources() + "\n";
        log += "toString:" + device.toString() + "\n";
        log += "\n" + event.toString();

        log(log);
    }

    @SuppressLint("NewApi")
    public static void log(BluetoothDevice device) {
        String log = "\n";
        if (device == null) return;
        log += "" + device.getName();
        log += "" + device.getAddress();
        log += "" + device.getBondState();
        log += "" + device.getType();
        log += "" + device.getUuids();

        log(log);
    }

    @SuppressLint("NewApi")
    public static void log(MotionEvent event) {
        String log = "\n";
        if (event == null) return;

        log += "\ntoString:" + event.toString();
        log += "\naction:" + event.getActionMasked();

        if (Wrapper.SDK >= 12) {//API 12 AND LATER
            log += "\nx_axis=" + event.getAxisValue(MotionEvent.AXIS_X);
            log += "\ny_axis=" + event.getAxisValue(MotionEvent.AXIS_Y);
            log += "\nz_axis=" + event.getAxisValue(MotionEvent.AXIS_Z);
            log += "\nz_rotor_axis=" + event.getAxisValue(MotionEvent.AXIS_RZ);
        }
        log(log);
    }
}
