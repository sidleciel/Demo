package com.rhodes.demo.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by xiet on 2015/6/22.
 */
public abstract class BtObserver {
    public abstract void onConnectionChanged(int size, List<BluetoothDevice> devices);
}
