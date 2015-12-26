package com.rhodes.demo.constant;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.util.SparseArray;

import java.util.UUID;

/**
 * Created by xiet on 2015/5/29.
 */
public class Constants {
    public static final long SCAN_PERIOD = 10000;

    public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final UUID HID_UUID = UUID.fromString("00001124-0000-1000-8000-00805f9b34fb");

    public static String getDeviceMajorTypes(int key) {
        SparseArray<String> names = new SparseArray<String>();
        names.append(BluetoothClass.Device.Major.AUDIO_VIDEO, "AUDIO_VIDEO");//
        names.append(BluetoothClass.Device.Major.COMPUTER, "COMPUTER");//电脑
        names.append(BluetoothClass.Device.Major.HEALTH, "HEALTH");//
        names.append(BluetoothClass.Device.Major.IMAGING, "IMAGING");//
        names.append(BluetoothClass.Device.Major.MISC, "MISC");//
        names.append(BluetoothClass.Device.Major.NETWORKING, "NETWORKING");//
        names.append(BluetoothClass.Device.Major.PERIPHERAL, "PERIPHERAL");//外设
        names.append(BluetoothClass.Device.Major.PHONE, "PHONE");//电话
        names.append(BluetoothClass.Device.Major.TOY, "TOY");//
        names.append(BluetoothClass.Device.Major.UNCATEGORIZED, "UNCATEGORIZED");//
        names.append(BluetoothClass.Device.Major.WEARABLE, "WEARABLE");//

        return names.get(key);
    }

    public static String getDeviceBondStates(int key) {
        SparseArray<String> names = new SparseArray<String>();
        names.append(BluetoothDevice.BOND_BONDED, "BOND_BONDED");
        names.append(BluetoothDevice.BOND_BONDING, "BOND_BONDING");
        names.append(BluetoothDevice.BOND_NONE, "BOND_NONE");

        return names.get(key);
    }
}
