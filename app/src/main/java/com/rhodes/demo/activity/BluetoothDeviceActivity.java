package com.rhodes.demo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.ClassUtil;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.Util.ToastUtils;
import com.rhodes.demo.constant.Constants;
import com.rhodes.demo.joystick.inputmanagercompat.InputManagerCompat;
import com.rhodes.demo.usb.MyUsbManager;
import com.rhodes.demo.usb.MyUsbOberver;
import com.rhodes.demo.wrapper.Wrapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class BluetoothDeviceActivity extends ActionBarActivity implements InputManagerCompat.InputDeviceListener {

    private static final int REQUEST_ENABLE_BT     = 2;
    private static final int SHOW_DEVICE_ALL       = 1;
    private static final int SHOW_DEVICE_BONDED    = 2;
    private static final int SHOW_DEVICE_CONNECTED = 3;

    private List<HashMap<String, String>> showDeviceStr = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter adapter;
    private MenuItem      enableItem;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> showDevices               = new ArrayList<BluetoothDevice>();
    private int                   currentShowDeviceCategory = -1;

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.log(action);

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                toggleEnableState();
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                currentShowDeviceCategory = SHOW_DEVICE_ALL;
                refreshDevicesInfo();
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                //discovery finished
            } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)
                    || action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                    || action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                refreshDevicesInfo();
            } else if (action.equals(BluetoothDevice.ACTION_NAME_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_CLASS_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!showDevices.contains(device)) {
                    showDevices.add(device);
                    refreshDevicesInfo();
                }

            }
        }
    };
    private BluetoothProfile mBluetoothProfile;
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {//API 11 AND LATER
        @Override
        public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
            mBluetoothProfile = bluetoothProfile;

            Logger.log("profile:%d, BluetoothProfile:%s", profile, bluetoothProfile.toString());
        }

        @Override
        public void onServiceDisconnected(int profile) {
            mBluetoothProfile = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device);
        ListView listV = (ListView) findViewById(R.id.list_v_devices);

        setListVAdapater(listV);

        startBt();

        //usb accessory
        /*MyUsbManager.getInstance().init(this);
        MyUsbManager.getInstance().registerReceiver();
        MyUsbManager.getInstance().addObserver(myUsbOberver);*/


        establishManage(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Logger.log(event.toString());
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String log = event.toString();
        Logger.log(log);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        //for joystick
//        Logger.log(event);
        float x = Wrapper.getAxisValue(event, MotionEvent.AXIS_X);
        float y = Wrapper.getAxisValue(event, MotionEvent.AXIS_Y);
        float z = Wrapper.getAxisValue(event, MotionEvent.AXIS_Z);
        float zr = Wrapper.getAxisValue(event, MotionEvent.AXIS_RZ);

        if ((event.getDevice().getSources() & InputDevice.SOURCE_CLASS_JOYSTICK) != 0) {
            //TODO::
        }
        return super.onGenericMotionEvent(event);
    }

    private boolean isConnected(BluetoothDevice inputDevice) {
        if (mBluetoothProfile == null) return false;

        if (mBluetoothProfile.getConnectionState(inputDevice) == BluetoothProfile.STATE_CONNECTED) {//API 11 AND LATER
            return true;
        }

        return false;
    }

    private void startBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (Wrapper.SDK >= 11)
            establishProxy();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Logger.log("This device does not support bluetooth");
            return;
        }
        //make sure bluetooth is enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Logger.log("There is bluetooth, but turned off");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Logger.log("The bluetooth is ready to use.");
            //bluetooth is on, so list paired devices from here.
            query();
        }
    }

    private void establishProxy() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        mBluetoothAdapter.getProfileProxy(this, mProfileListener, ClassUtil.getInputDeviceHiddenConstant());//API 11 AND LATER

    }

    private void stopBt() {
        if (mBluetoothAdapter == null) return;

        if (mBluetoothAdapter.isEnabled()) {
            Logger.log("There is bluetooth, will turned off");
            mBluetoothAdapter.disable();
        } else
            Logger.log("There is bluetooth, was turned off");
    }

    private void setListVAdapater(ListView listV) {

        adapter = new SimpleAdapter(this, showDeviceStr, R.layout.item_bluetooth_device_info, new String[]{"info"}, new int[]{R.id.tv_device_info}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(BluetoothDeviceActivity.this);
                    View view = inflater.inflate(R.layout.item_bluetooth_device_info, null);
                    holder = new Holder();
                    holder.infoTv = (TextView) view.findViewById(R.id.tv_device_info);

                    convertView = view;
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                if (holder != null && holder.infoTv != null) {
                    holder.infoTv.setText(showDeviceStr.get(position).get("info"));
                    if (position < showDevices.size() && isConnected(showDevices.get(position))) {
                        holder.infoTv.setTextColor(Color.rgb(64, 106, 88));
                    }
                }

                return convertView;
//                return super.getView(position, convertView, parent);
            }

            class Holder {
                public TextView infoTv;
            }
        };

        listV.setAdapter(adapter);
        listV.setOnItemClickListener(new DeviceItemListener());
        listV.setOnItemLongClickListener(new DeviceItemLongListener());
        initDeviceInfo();
    }

    private void initDeviceInfo() {
        refreshDevicesInfo();
    }

    private synchronized void refreshDevicesInfo() {
        if (showDeviceStr.size() > 0) showDeviceStr.clear();

        query();

        notifyDataChanged();

    }

    public void query() {

        //need to extends BluetoothDeviceActivity
        //final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothAdapter == null)
            startBt();

        Logger.log("Devices Info:");

        switch (currentShowDeviceCategory) {
            case SHOW_DEVICE_ALL:
                startScan();
                break;
            case SHOW_DEVICE_CONNECTED:
                showDevices.clear();
                //ClassUtil.printAllInform(BluetoothProfile.class);
                if (mBluetoothProfile != null) {
                    showDevices = mBluetoothProfile.getConnectedDevices();//API 11 AND LATER
                }

                break;
            case SHOW_DEVICE_BONDED:
            default:
                showDevices.clear();
                showDevices.addAll(mBluetoothAdapter.getBondedDevices());
                break;
        }

        if (showDevices.size() > 0) {
            // Loop through paired devices
            BluetoothDevice blueDev[] = new BluetoothDevice[showDevices.size()];
            String item;
            int i = 0;
            for (BluetoothDevice devicel : showDevices) {
                blueDev[i] = devicel;
                item = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                String info = "";
                info += "Device: " + item + "\n";
                info += "Type: " + Constants.getDeviceMajorTypes(devicel.getBluetoothClass().getMajorDeviceClass()) + "\n";
                info += "BondState: " + Constants.getDeviceBondStates(devicel.getBondState()) + "\n";
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("info", info);
                showDeviceStr.add(map);

                info += devicel.toString();

                Logger.log(info + "TypeValue :" + devicel.getBluetoothClass().getDeviceClass());
                i++;
            }

        } else {
            Logger.log("There are no paired devices");
        }
    }

    private void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            //bluetooth result code.
            if (resultCode == Activity.RESULT_OK) {
                Logger.log("Bluetooth is on.");
                refreshDevicesInfo();
            } else {
                Logger.log("Please turn the bluetooth on.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_device, menu);
        enableItem = menu.getItem(0);

        toggleEnableState();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_enable) {
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
                startBt();
            else
                stopBt();
        } else if (id == R.id.action_scan) {
            if (mBluetoothAdapter != null) {
                showDevices.clear();
                startScan();
            }
        } else if (id == R.id.action_settings_bt) {
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        } else if (id == R.id.action_test_input) {
            startActivity(new Intent(this, BluetoothDeviceTestActivity.class));
        } else {
            if (id == R.id.action_show_device_all) {
                currentShowDeviceCategory = SHOW_DEVICE_ALL;
                showDevices.clear();
                showDevices.addAll(mBluetoothAdapter.getBondedDevices());
                startScan();
            } else if (id == R.id.action_show_device_bonded) {
                currentShowDeviceCategory = SHOW_DEVICE_BONDED;
            } else if (id == R.id.action_show_device_connected) {
                currentShowDeviceCategory = SHOW_DEVICE_CONNECTED;
            }
            refreshDevicesInfo();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        currentShowDeviceCategory = SHOW_DEVICE_ALL;
        mBluetoothAdapter.startDiscovery();
    }

    private void toggleEnableState() {
        if (enableItem == null) {
            return;
        }

        if (mBluetoothAdapter == null) {
            enableItem.setTitle(R.string.action_disable);
        } else if (mBluetoothAdapter.isEnabled()) {
            enableItem.setTitle(R.string.action_enable);
        } else {
            enableItem.setTitle(R.string.action_disable);
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(Constants.HID_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                Logger.log("ConnectThread connection failed");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            Logger.log("ConnectThread connection successful");
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class DeviceItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final BluetoothDevice device = showDevices.get(position);
            onClicked(device);
        }
    }

    private void onClicked(BluetoothDevice device) {
        int bondState = device.getBondState();
        if (bondState != BluetoothDevice.BOND_BONDED) {
            bondDevice(device);
        } else if (!isConnected(device)) {
            connectDevice(device);
        } else {
            disconnectDevice(device);
        }

    }

    private void disconnectDevice(BluetoothDevice device) {

    }

    private void connectDevice(BluetoothDevice device) {
        new ConnectThread(device).start();
    }

    private void bondDevice(BluetoothDevice device) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        try {
            String strPin = "0000";
            ClassUtil.autoBond(device.getClass(), device, strPin);//设置pin值
            ClassUtil.createBond(device.getClass(), device);

            Logger.log("create bond success !");
            connectDevice(device);
        } catch (Exception e) {
            // TODO: handle exception
            Logger.log("create bond failed !");
        }
    }

    private class DeviceItemLongListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            boolean ret = false;

            if (showDevices == null) return ret;

            if (position < showDevices.size()) {
                BluetoothDevice device = (BluetoothDevice) showDevices.toArray()[position];

                try {
                    ret = ClassUtil.removeBond(device.getClass(), device);
                    if (ret) {
                        showDevices.remove(position);
                        refreshDevicesInfo();
                    }

                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ret;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBtReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBtReceiver();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.closeProfileProxy(ClassUtil.getInputDeviceHiddenConstant(), mBluetoothProfile);//API 11 AND LATER
        }

        MyUsbManager.getInstance().unregisterReceiver();
    }

    private void unregisterBtReceiver() {
        unregisterReceiver(btReceiver);
    }

    private void registerBtReceiver() {
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        if (Wrapper.SDK >= 19)
            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        if (Wrapper.SDK >= 15)
            intentFilter.addAction(BluetoothDevice.ACTION_UUID);

        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (Wrapper.SDK >= 11)
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(btReceiver, intentFilter);
    }


    //UsbDevice section
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void printAccessory() {
        try {
            HashMap<String, UsbDevice> map = MyUsbManager.getInstance().getDeviceList();
            Set<Map.Entry<String, UsbDevice>> set = map.entrySet();
            Iterator<Map.Entry<String, UsbDevice>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, UsbDevice> entry = iterator.next();
                UsbDevice usbDevice = entry.getValue();
                Logger.log(entry.getKey(), usbDevice.toString());

                printUsbDeviceProductInfo(usbDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void printUsbDeviceProductInfo(UsbDevice usbDevice) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Logger.log(
                        "getManufacturerName=" + usbDevice.getManufacturerName(),
                        "getProductName=" + usbDevice.getProductName(),
                        "getSerialNumber=" + usbDevice.getSerialNumber()
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String translateDeviceClass(int deviceClass) {
        switch (deviceClass) {
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default:
                return "Unknown USB class!";
        }
    }

    MyUsbOberver myUsbOberver = new MyUsbOberver() {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public void onConnectionChanged(int size, List<UsbDevice> devices) {
            Logger.log("myUsbOberver", "size=" + size);
            for (int i = 0; devices != null && i < devices.size(); i++) {
                UsbDevice usbDevice = devices.get(i);
                Logger.log("myUsbOberver", usbDevice.toString());
                Logger.log("myUsbOberver describeContents=", usbDevice.describeContents());
                Logger.log("myUsbOberver deviceClass=", translateDeviceClass(usbDevice.getDeviceClass()));
                printUsbDeviceProductInfo(usbDevice);
            }
        }
    };

    //InputDevice section

    InputManagerCompat mInputManager;
    protected List<Integer> gamepadDeviceIds;

    public void establishManage(Context context) {
        if (context == null) {
            return;
        }
        mInputManager = InputManagerCompat.Factory.getInputManager(context);
        mInputManager.registerInputDeviceListener(this, null);

        refreshGamepadDeviceIds();
    }

    private void refreshGamepadDeviceIds() {
        if (mInputManager == null)
            return;

        gamepadDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = mInputManager.getInputDeviceIds();
        if (deviceIds != null && deviceIds.length != 0)
            for (int deviceId : deviceIds) {
                InputDevice inputDevice = mInputManager.getInputDevice(deviceId);
                int sources = (inputDevice == null) ? 0 : inputDevice.getSources();

//                if (!isExternal(inputDevice)) continue;// API16
                // if the device is a gamepad/joystick, create a ship to represent it
                if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                        || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                    Logger.log(inputDevice.toString());
                    gamepadDeviceIds.add(deviceId);
                }
            }
    }

    boolean isExternal(InputDevice device) {
        boolean ret = false;
        try {
            ret = ClassUtil.getInputDeviceIsExternal(device.getClass(), device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        if (mInputManager == null)
            return;

        InputDevice inputDevice = mInputManager.getInputDevice(deviceId);
        int sources = (inputDevice == null) ? 0 : inputDevice.getSources();
        // if the device is a gamepad/joystick, create a ship to represent it
        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            gamepadDeviceIds.add(deviceId);
            // if the device has a gamepad or joystick
            Logger.log(inputDevice.toString());
            Toast.makeText(this, "手柄已连接", Toast.LENGTH_SHORT).show();
        }

        notifyDeviceCount();
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {

    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        if (gamepadDeviceIds == null)
            gamepadDeviceIds = new ArrayList<Integer>();

        if (gamepadDeviceIds.contains(deviceId)) {
            gamepadDeviceIds.remove((Integer) deviceId);
            Toast.makeText(this, "手柄已断开", Toast.LENGTH_SHORT).show();
        }

        notifyDeviceCount();
    }

    void notifyDeviceCount() {
        int allCount = 0;
        int bluetoothSize = 0;
        int usbSize = 0;

        refreshGamepadDeviceIds();
        if (gamepadDeviceIds != null) allCount = gamepadDeviceIds.size();
        if (mBluetoothProfile.getConnectedDevices() != null)
            bluetoothSize = mBluetoothProfile.getConnectedDevices().size();
        usbSize = allCount - bluetoothSize;

        Logger.log("AllCount=" + allCount + " ,BluetoothDeviceSize=" + bluetoothSize + " ,UsbDeviceSize=" + usbSize);
        ToastUtils.getInstance(this).showToastSystem("AllCount=" + allCount + " ,BluetoothDeviceSize=" + bluetoothSize + " ,UsbDeviceSize=" + usbSize);
    }

}
