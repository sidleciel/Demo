package com.rhodes.demo.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.ClassUtil;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.bluetooth.BtManager;
import com.rhodes.demo.bluetooth.BtObserver;
import com.rhodes.demo.joystick.inputmanagercompat.InputManagerCompat;
import com.rhodes.demo.usb.MyUsbManager;
import com.rhodes.demo.usb.MyUsbOberver;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiet on 2015/10/21.
 */
public class DeviceInfoActivity extends ActionBarActivity {
    ExpandableListView infoListView;
    DeviceInfoAdapter  deviceInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        infoListView = (ExpandableListView) findViewById(R.id.deviceInfoList);
        deviceInfoAdapter = new DeviceInfoAdapter(this);
        infoListView.setAdapter(deviceInfoAdapter);

        init();

        infoListView.expandGroup(0);
        infoListView.setGroupIndicator(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtManager != null) {
            mBtManager.close();
        }
    }

    private void init() {
        fillScreenInfo();

        initInputManager();
        initBtManager();
        initUsbManager();
        fillGameControllerCountInfo();
    }

    //region ScreenInfo
    private void fillScreenInfo() {
        String section = "Screen";
        clearSection(section);

        Resources      res     = getResources();
        DisplayMetrics dm      = res.getDisplayMetrics();
        String         dpiType = "";
        if (dm.densityDpi >= DisplayMetrics.DENSITY_XXXHIGH) {
            dpiType = "xxxhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_XXHIGH) {
            dpiType = "xxhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_XHIGH) {
            dpiType = "xhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            dpiType = "hdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_TV) {
            dpiType = "tvdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM) {
            dpiType = "mdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_LOW) {
            dpiType = "ldpi";
        }
        List<String> infos = new ArrayList<String>();
        infos.add("density: " + dm.density);//The logical density of the display.
        infos.add("densityDpi: " + dm.densityDpi + "(" + dpiType + ")");//The screen density expressed as dots-per-inch.
        infos.add("scaledDensity: " + dm.scaledDensity);//A scaling factor for fonts displayed on the display.
        infos.add("widthPixels: " + dm.widthPixels);//The absolute width of the display in pixels.
        infos.add("heightPixels: " + dm.heightPixels);//The absolute height of the display in pixels.
        infos.add("xdpi: " + dm.xdpi);//The exact physical pixels per inch of the screen in the X dimension.
        infos.add("ydpi: " + dm.ydpi);//The exact physical pixels per inch of the screen in the Y dimension.
        //calc logical
        float x_inches = dm.widthPixels / dm.xdpi;
        infos.add("x-inches: " + x_inches);
        float y_inches = dm.heightPixels / dm.ydpi;
        infos.add("y-inches: " + y_inches);
        double diagonal = Math.sqrt(Math.pow(x_inches, 2) + Math.pow(y_inches, 2));
//        double diagonal = getScreenSizeOfDevice2();
        double diagonal_cm = new BigDecimal(diagonal * 2.54f).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        infos.add("diagonal(in): " + diagonal + "(≈" + diagonal_cm + "cm)");

        addSection(section, infos);
    }
    //endregion

    //region GameControllerCount

    //region init input manager
    private InputManagerCompat mInputManager;

    private void initInputManager() {
        mInputManager = InputManagerCompat.Factory.getInputManager(this);

        InputManagerCompat.InputDeviceListener mInputDeviceListener = new InputManagerCompat.InputDeviceListener() {
            @Override
            public void onInputDeviceAdded(int deviceId) {
                fillGameControllerCountInfo();
            }

            @Override
            public void onInputDeviceChanged(int deviceId) {
                fillGameControllerCountInfo();
            }

            @Override
            public void onInputDeviceRemoved(int deviceId) {
                fillGameControllerCountInfo();
            }
        };
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null);
    }
    //endregion

    //region init bt manager
    private BtManager mBtManager;

    private void initBtManager() {
        mBtManager = new BtManager();
        mBtManager.establish(this, ClassUtil.getInputDeviceHiddenConstant());
        mBtManager.addBtObserver(new BtObserver() {
            @Override
            public void onConnectionChanged(int size, List<BluetoothDevice> devices) {
                fillGameControllerCountInfo();
            }
        });
    }
    //endregion

    //region init usb manager
    private MyUsbManager mUsbManager;

    private void initUsbManager() {
        mUsbManager = new MyUsbManager();
        mUsbManager.init(this);
        mUsbManager.addObserver(new MyUsbOberver() {
            @Override
            public void onConnectionChanged(int size, List<UsbDevice> devices) {
                fillGameControllerCountInfo();
            }
        });
    }
    //endregion

    private void fillGameControllerCountInfo() {
        String section = "GameControllerCount";
        clearSection(section);
        List<String> infos = new ArrayList<String>();

        //inputdevice
        int                          gameControlerCount = 0;
        List<InputDevice>            gameControllers    = new ArrayList<InputDevice>();
        int[]                        deviceIds          = mInputManager.getInputDeviceIds();
        HashMap<String, InputDevice> gameControllersMap = new HashMap<String, InputDevice>();
        for (int deviceId : deviceIds) {
            InputDevice dev     = mInputManager.getInputDevice(deviceId);
            String      name    = dev.getName();
            int         sources = dev.getSources();
            // if the device is a gamepad/joystick, create a ship to represent it
            if (isGameController(sources)) {
                gameControlerCount++;
                gameControllers.add(dev);
                gameControllersMap.put(name, dev);
//                Logger.log(dev.toString());
            }
        }
        infos.add("AllGameControllerCount: " + gameControlerCount + ";  " +
                "\nGameControllers:" + gameControllers.toString());

        //bluetooth devices
        List<BluetoothDevice>            btDevices               = mBtManager.getConnectedDevices();
        List<BluetoothDevice>            btGameControllerDevices = new ArrayList<BluetoothDevice>();
        HashMap<String, BluetoothDevice> btGameControllersMap    = new HashMap<String, BluetoothDevice>();
        if (btDevices != null && btDevices.size() != 0) {
            for (BluetoothDevice dev : btDevices) {
                if (dev.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL) {
                    btGameControllerDevices.add(dev);
                    btGameControllersMap.put(dev.getName(), dev);
//                    Logger.log(dev);
                }
            }
        }
        infos.add("BluetoothGameControllerCount: " + btGameControllerDevices.size() + ";  " +
                "\nGameControllers:" + btGameControllerDevices.toString());


        //otg devices
        boolean isNameRefCorrect = true;
        for (BluetoothDevice dev : btGameControllerDevices) {
            String name = dev.getName();
            if (!gameControllersMap.containsKey(name)) {
                isNameRefCorrect = false;
                break;
            }
        }

        List<InputDevice> otgGameControllerDevices = new ArrayList<InputDevice>();
        if (gameControlerCount > btGameControllerDevices.size()) {

            if (isNameRefCorrect) {
                for (InputDevice dev : gameControllers) {
                    // if the device is a gamepad/joystick, create a ship to represent it
                    String devName = dev.getName();
                    if (btGameControllersMap.containsKey(devName)) {
                        continue;
                    } else {
                        otgGameControllerDevices.add(dev);
                    }
//                Logger.log(dev.toString());
                }
            } else {
                int otgGameControlerCount = gameControlerCount - btGameControllerDevices.size();
                //TODO: set custom name. OTG1、OTG2...

            }

        }
        String otgInfo = "OTGGameControllerCount: " + (gameControlerCount - btGameControllerDevices.size()) + "\n";
        if (isNameRefCorrect) otgInfo += "GameControllers:" + otgGameControllerDevices.toString();
        infos.add(otgInfo);

        //usb devices
//        HashMap<String, UsbDevice> usbDevices               = mUsbManager.getDeviceList();
//        List<InputDevice>          usbGameControllerDevices = new ArrayList<InputDevice>();
//        if (usbDevices != null && usbDevices.size() != 0) {
//            Collection<UsbDevice> collection = usbDevices.values();
//            Iterator<UsbDevice>   iterator   = collection.iterator();
//
//            while (iterator.hasNext()) {
//                UsbDevice   dev  = iterator.next();
//                int deviceId = dev.getDeviceId();//this deviceId is not input device_id.
//                InputDevice idev = mInputManager.getInputDevice(deviceId);
//                if (idev==null)continue;
//                if (isGameController(idev.getSources())) {
//                    usbGameControllerDevices.add(idev);
////                    Logger.log(idev.toString());
//                }
//            }
//        }
//        infos.add("UsbGameControllerCount: " + usbGameControllerDevices.size() + ";  \nGameControllers:" + usbGameControllerDevices.toString());

        addSection(section, infos);
    }

    private boolean isGameController(int sources) {
        boolean ret = false;
        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            // if the device has a gamepad or joystick
            ret = true;
        }

        return ret;
    }
    //endregion

    //region Section Common
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)//4.2.2
    private double getScreenSizeOfDevice2() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm           = getResources().getDisplayMetrics();
        double         x            = Math.pow(point.x / dm.xdpi, 2);
        double         y            = Math.pow(point.y / dm.ydpi, 2);
        double         screenInches = Math.sqrt(x + y);

        return screenInches;
    }

    private void clearSection(String section) {
        if (deviceInfoAdapter.getSections().contains(section)) {
            deviceInfoAdapter.getInfos().remove(section);
            deviceInfoAdapter.getSections().remove(section);
        }
    }

    private void addSection(String section, List<String> infos) {
        clearSection(section);
        deviceInfoAdapter.getSections().add(section);
        deviceInfoAdapter.getInfos().put(section, infos);

        deviceInfoAdapter.notifyDataSetChanged();
    }

    class DeviceInfoAdapter extends BaseExpandableListAdapter {
        private final String TAG = getClass().getSimpleName();
        List<String>                    sections = null;
        Hashtable<String, List<String>> infos    = null;
        Context                         mContext = null;

        public DeviceInfoAdapter(Context mContext) {
            this.mContext = mContext;
            sections = new ArrayList<String>();
            infos = new Hashtable<String, List<String>>();
        }

        public LayoutInflater getLayoutInflater() {
            return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public List<String> getSections() {
            if (sections == null) sections = new ArrayList<String>();
            return sections;
        }

        public Hashtable<String, List<String>> getInfos() {
            if (infos == null) infos = new Hashtable<String, List<String>>();
            return infos;
        }

        @Override
        public int getGroupCount() {
            return sections == null ? 0 : sections.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (getGroup(groupPosition) == null) return -1;
            return ((List) getGroup(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition > (getSections().size() - 1)) return null;
            return getInfos().get(getSections().get(groupPosition));
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (getGroup(groupPosition) == null) return null;
            return ((List<String>) getGroup(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            try {
                if (convertView != null) {
                    holder = (GroupViewHolder) convertView.getTag();
                } else {
                    holder = new GroupViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.item_device_info_group, null);
                    holder.infoTitle = (TextView) convertView.findViewById(R.id.infoTitle);

                    convertView.setTag(holder);
                }

                String infoTitle = (String) sections.get(groupPosition);
                Logger.log(TAG, infoTitle);
                holder.infoTitle.setText("· " + infoTitle);
                holder.infoTitle.setTextColor(Color.parseColor("#4FC17F"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = null;
            try {
                if (convertView != null) {
                    holder = (ChildViewHolder) convertView.getTag();
                } else {
                    holder = new ChildViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.item_device_info, null);
                    holder.info = (TextView) convertView.findViewById(R.id.info);

                    convertView.setTag(holder);
                }
                String info = (String) getChild(groupPosition, childPosition);
                Logger.log(TAG, info);

                holder.info.setText("" + info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class GroupViewHolder {

            public TextView infoTitle;
        }

        class ChildViewHolder {

            public TextView info;
        }
    }
    //endregion
}
