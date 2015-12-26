package com.rhodes.demo.usb;

import android.hardware.usb.UsbDevice;

import java.util.List;

/**
 * Created by xiet on 2015/10/21.
 */

public abstract class MyUsbOberver {
    public abstract void onConnectionChanged(int size, List<UsbDevice> devices);
}
