package com.rhodes.demo.usb;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.view.InputDevice;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.joystick.DumpInputInfo;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by xiet on 2015/9/18.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class MyUsbManager {

    private static MyUsbManager instance;
    Context    mContext;
    UsbManager sysUsbManager;

    private boolean receiverRegisted = false;
    private List<MyUsbOberver> mMyUsbObservers;

    public static MyUsbManager getInstance() {
        if (instance == null) {
            instance = new MyUsbManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        this.sysUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if (!receiverRegisted) registerReceiver();
    }

    public HashMap<String, UsbDevice> getDeviceList() {
        if (this.sysUsbManager != null) {
            return sysUsbManager.getDeviceList();
        }

        return null;
    }

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Logger.log(usbDevice.toString() + " usbDeviceId=" + usbDevice.getDeviceId());

            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                int[] deviceIds = InputDevice.getDeviceIds();
                int inputDeviceId = deviceIds[deviceIds.length - 1];
                InputDevice inputDevice = InputDevice.getDevice(inputDeviceId);
                if (inputDevice != null) Logger.log(inputDevice.toString());


                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE); //拿到连接的设备
                InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
                int[] id_device = inputManager.getInputDeviceIds();//获取所有的设备id
                InputDevice inputDevice1 = inputManager.getInputDevice(id_device[id_device.length - 1]);
                if (inputDevice1 != null) Logger.log(inputDevice1.toString());
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            }

            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                    || action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                notifyUsbStateChanged();
            }

        }
    };

    public void registerReceiver() {
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        mContext.registerReceiver(usbReceiver, intentFilter);

        receiverRegisted = true;
    }

    public void unregisterReceiver() {
        if (receiverRegisted) {
            mContext.unregisterReceiver(usbReceiver);
            receiverRegisted = false;
        }
    }

    public void addObserver(MyUsbOberver oberver) {
        if (mMyUsbObservers == null) {
            mMyUsbObservers = new ArrayList<MyUsbOberver>();
        }
        mMyUsbObservers.add(oberver);
    }

    public void removeObserver(MyUsbOberver oberver) {
        if (oberver == null || oberver == null) return;

        if (mMyUsbObservers.contains(oberver)) {
            mMyUsbObservers.remove(oberver);
        }

        if (mMyUsbObservers.size() < 1) mMyUsbObservers = null;
    }

    public void notifyUsbStateChanged() {

        HashMap<String, UsbDevice> attachedDevices = getDeviceList();
        List<UsbDevice> devices = new ArrayList<UsbDevice>();
        if (attachedDevices != null && attachedDevices.size() != 0) {
            Iterator<UsbDevice> iterator = attachedDevices.values().iterator();
            while (iterator.hasNext()) {
                UsbDevice usbDevice = iterator.next();
                devices.add(usbDevice);
            }
        }

        for (int i = 0; mMyUsbObservers != null && i < mMyUsbObservers.size(); i++) {
            mMyUsbObservers.get(i).onConnectionChanged(attachedDevices.size(), devices);
        }
    }

    public ArrayList getGameControllerIds() {
        ArrayList gameControllerDeviceIds = new ArrayList();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            Logger.log("deviceId=" + deviceId);
            InputDevice dev = InputDevice.getDevice(deviceId);
            if (dev == null) continue;
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                    Logger.log(InputDevice.getDevice(deviceId).toString());
                }
            }
        }
        return gameControllerDeviceIds;
    }

}
