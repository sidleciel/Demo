package com.rhodes.demo.joystick;

import com.rhodes.demo.Util.ExecTerminal;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.Util.StringUtils;

import java.util.*;

/**
 * Created by xiet on 2015/10/21.
 */
public class DumpInputInfo {
    static final String FILTER_EVENT_HUB_STATE                                 = "Event Hub State:";
    static final String FILTER_EVENT_HUB_STATE_BUILTINKEYBOARDID               = "  BuiltInKeyboardId:";
    static final String FILTER_EVENT_HUB_STATE_DEVICES                         = "  Devices:";
    static final String FILTER_INPUT_READER_STATE                              = "Input Reader State:";
    static final String FILTER_INPUT_READER_STATE_DEVICE                       = "  Device ";
    static final String FILTER_INPUT_READER_STATE_DEVICE_MOTION_RANGES         = "    Motion Ranges:";
    static final String FILTER_INPUT_READER_STATE_DEVICE_KEYBOARD_INPUT_MAPPER = "    Keyboard Input Mapper:";
    static final String FILTER_INPUT_READER_STATE_DEVICE_JOYSTICK_INPUT_MAPPER = "    Joystick Input Mapper:";
    static final String FILTER_INPUT_READER_STATE_CONFIGURATION                = "  Configuration:";

    static final String KEY_EVENT_HUB_STATE_DEVICES_IDENTIFIER         = "Identifier";
    static final String KEY_EVENT_HUB_STATE_DEVICES_IDENTIFIER_BUS     = "bus";
    static final String KEY_EVENT_HUB_STATE_DEVICES_IDENTIFIER_VENDOR  = "vendor";
    static final String KEY_EVENT_HUB_STATE_DEVICES_IDENTIFIER_PRODUCT = "product";
    static final String KEY_EVENT_HUB_STATE_DEVICES_IDENTIFIER_VERSION = "version";
    static final String KEY_INPUT_READER_STATE_DEVICE_GENERATION       = "Generation";
    static final String KEY_INPUT_READER_STATE_DEVICE_ISEXTERNAL       = "IsExternal";
    static final String KEY_INPUT_READER_STATE_DEVICE_SOURCES          = "Sources";
    static final String KEY_INPUT_READER_STATE_DEVICE_KEYBOARDTYPE     = "KeyboardType";


    public static String[] dumpsysInput() {
        Logger.log("call method dumpsysInput-------------------------------------------------------------------------");

        final String spliter = " ";
//        String CMD_DUMPSYS_INPUT = "adb shell dumpsys input";
        String CMD_DUMPSYS_INPUT = "dumpsys input";
        String[] ret = null;
        ret = ExecTerminal.execSu(CMD_DUMPSYS_INPUT).split("\n");
        Logger.log(ret);
        return ret;
    }

    /**
     * Event Hub State:
     * BuiltInKeyboardId: -2
     * Devices:
     * 25: HJC Game BETOP BFM GAMEPAD
     * Classes: 0x80000141
     * Path: /dev/input/event7
     * Descriptor: a79407f86033882279c195653dfd017b95573cf1
     * Location: usb-msm_hsusb_host-1/input0
     * UniqueId:
     * Identifier: bus=0x0003, vendor=0x20bc, product=0x5500, version=0x0111
     * KeyLayoutFile: /system/usr/keylayout/Generic.kl
     * KeyCharacterMapFile: /system/usr/keychars/Generic.kcm
     * ConfigurationFile:
     * HaveKeyboardLayoutOverlay: false
     */
    public static void filterEventHubState() {
        Logger.log("call method filterEventHubState-----------------------------------------------------------------");
        String[] inputInfos = dumpsysInput();

        Logger.log("start filter filterEventHubState----------------------------------------------------------------");
        boolean startRec = false;
        final String sub_prefix = "  ";
        final String split_value_prefix = ": ";

        List<Integer> devices = new ArrayList<Integer>();
        HashMap<Integer, HashMap<String, String>> devicesInfos = new HashMap<Integer, HashMap<String, String>>();
        HashMap<String, String> deviceInfos = null;

        for (int i = 0; i < inputInfos.length; i++) {
            String info = inputInfos[i];
            if (StringUtils.isEmpty(info)) continue;

            if (info.equals(FILTER_EVENT_HUB_STATE_DEVICES)) {
                startRec = true;
            } else if (startRec) {
                if (!info.startsWith(sub_prefix)) {
                    startRec = false;
                    if (devices.size() > 0) {
                        Integer deviceId = devices.get(devices.size() - 1);
                        if (devicesInfos.containsKey(deviceId)) {
                        } else if (deviceInfos != null) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.putAll(deviceInfos);

                            devicesInfos.put(devices.get(devices.size() - 1), map);
                            deviceInfos = new HashMap<String, String>();
                        }

                    }
                    break;
                } else if (info.startsWith("    ") && !info.startsWith("      ")) {
                    info = cutPrefix(info, sub_prefix);
                    Integer deviceId = Integer.valueOf(info.split(split_value_prefix)[0]);
                    String deviceName = info.split(split_value_prefix)[1];

                    if (deviceInfos != null && !devicesInfos.containsKey(deviceId)) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.putAll(deviceInfos);

                        if (devices.size() > 0)
                            devicesInfos.put(devices.get(devices.size() - 1), map);

                    }

                    deviceInfos = new HashMap<String, String>();
                    if (!devices.contains(deviceId))
                        devices.add(deviceId);

                } else if (info.startsWith("      ")) {
                    info = cutPrefix(info, sub_prefix);
                    String[] kv = info.split(split_value_prefix);
                    String key = kv[0];
                    String value = "";
                    if (kv.length > 1) value = kv[1];
                    if (deviceInfos == null) deviceInfos = new HashMap<String, String>();
                    deviceInfos.put(key, value);
                }
            }
        }

        Logger.log("filterEventHubState result log-----------------------------------------------------------------");
        for (int i = 0; i < devices.size(); i++) {
            Logger.log("deviceId=" + devices.get(i));
            HashMap<String, String> map = devicesInfos.get(devices.get(i));
            if (map == null) continue;

            Set<Map.Entry<String, String>> set = map.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                Logger.log(key + "=" + value);
            }
        }
    }

    private static String cutPrefix(String str, String prefix) {
        String ret = str;
        if (StringUtils.isEmpty(ret)) return ret;
        while (!StringUtils.isEmpty(ret) && ret.startsWith(prefix)) {
            ret = ret.substring(prefix.length());
        }
        return ret;
    }

    /**
     * Input Reader State:
     * Device 25: HJC Game BETOP BFM GAMEPAD
     * Generation: 75
     * IsExternal: true
     * Sources: 0x01000511
     * KeyboardType: 1
     * Motion Ranges:
     * X: source=0x01000010, min=-1.000, max=1.000, flat=0.118, fuzz=0.000, resolution=0.000
     * Y: source=0x01000010, min=-1.000, max=1.000, flat=0.118, fuzz=0.000, resolution=0.000
     * Z: source=0x01000010, min=-1.000, max=1.000, flat=0.118, fuzz=0.000, resolution=0.000
     * RZ: source=0x01000010, min=-1.000, max=1.000, flat=0.118, fuzz=0.000, resolution=0.000
     * GAS: source=0x01000010, min=0.000, max=1.000, flat=0.059, fuzz=0.000, resolution=0.000
     * BRAKE: source=0x01000010, min=0.000, max=1.000, flat=0.059, fuzz=0.000, resolution=0.000
     * HAT_X: source=0x01000010, min=-1.000, max=1.000, flat=0.000, fuzz=0.000, resolution=0.000
     * HAT_Y: source=0x01000010, min=-1.000, max=1.000, flat=0.000, fuzz=0.000, resolution=0.000
     * Keyboard Input Mapper:
     * Parameters:
     * HasAssociatedDisplay: false
     * OrientationAware: false
     * KeyboardType: 1
     * Orientation: 0
     * KeyDowns: 0 keys currently down
     * MetaState: 0x0
     * DownTime: 0
     * Joystick Input Mapper:
     * Axes:
     * X: min=-1.00000, max=1.00000, flat=0.11765, fuzz=0.00000, resolution=0.00000
     * scale=0.00784, offset=-1.00000, highScale=0.00784, highOffset=-1.00000
     * rawAxis=0, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * Y: min=-1.00000, max=1.00000, flat=0.11765, fuzz=0.00000, resolution=0.00000
     * scale=0.00784, offset=-1.00000, highScale=0.00784, highOffset=-1.00000
     * rawAxis=1, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * Z: min=-1.00000, max=1.00000, flat=0.11765, fuzz=0.00000, resolution=0.00000
     * scale=0.00784, offset=-1.00000, highScale=0.00784, highOffset=-1.00000
     * rawAxis=2, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * RZ: min=-1.00000, max=1.00000, flat=0.11765, fuzz=0.00000, resolution=0.00000
     * scale=0.00784, offset=-1.00000, highScale=0.00784, highOffset=-1.00000
     * rawAxis=5, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * GAS: min=0.00000, max=1.00000, flat=0.05882, fuzz=0.00000, resolution=0.00000
     * scale=0.00392, offset=0.00000, highScale=0.00392, highOffset=0.00000
     * rawAxis=9, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * BRAKE: min=0.00000, max=1.00000, flat=0.05882, fuzz=0.00000, resolution=0.00000
     * scale=0.00392, offset=0.00000, highScale=0.00392, highOffset=0.00000
     * rawAxis=10, rawMin=0, rawMax=255, rawFlat=15, rawFuzz=0, rawResolution=0
     * HAT_X: min=-1.00000, max=1.00000, flat=0.00000, fuzz=0.00000, resolution=0.00000
     * scale=1.00000, offset=0.00000, highScale=1.00000, highOffset=0.00000
     * rawAxis=16, rawMin=-1, rawMax=1, rawFlat=0, rawFuzz=0, rawResolution=0
     * HAT_Y: min=-1.00000, max=1.00000, flat=0.00000, fuzz=0.00000, resolution=0.00000
     * scale=1.00000, offset=0.00000, highScale=1.00000, highOffset=0.00000
     * rawAxis=17, rawMin=-1, rawMax=1, rawFlat=0, rawFuzz=0, rawResolution=0
     * Configuration:
     * ExcludedDeviceNames: []
     * VirtualKeyQuietTime: 0.0ms
     * PointerVelocityControlParameters: scale=1.000, lowThreshold=500.000, highThreshold=3000.000, acceleration=3.000
     * WheelVelocityControlParameters: scale=1.000, lowThreshold=15.000, highThreshold=50.000, acceleration=4.000
     * PointerGesture:
     * Enabled: true
     * QuietInterval: 100.0ms
     * DragMinSwitchSpeed: 50.0px/s
     * TapInterval: 150.0ms
     * TapDragInterval: 300.0ms
     * TapSlop: 20.0px
     * MultitouchSettleInterval: 100.0ms
     * MultitouchMinDistance: 15.0px
     * SwipeTransitionAngleCosine: 0.3
     * SwipeMaxWidthRatio: 0.2
     * MovementSpeedRatio: 0.8
     * ZoomSpeedRatio: 0.3
     */
    public static void filterInputReaderState() {
        Logger.log("call method filterInputReaderState--------------------------------------------------------------");
        String[] inputInfos = dumpsysInput();

        Logger.log("start filter filterInputReaderState-------------------------------------------------------------");
        boolean startRec = false;
        boolean isConfigurationSection = false;
        boolean isMotionRanges = false;
        boolean isJoystickInputMapper = false;
        boolean isKeyboardInputMapper = false;
        final String level_tab = "  ";
        final String level_1 = "";
        final String level_2 = level_tab;
        final String level_3 = level_tab + level_tab;
        final String level_4 = level_tab + level_tab + level_tab;
        final String level_5 = level_tab + level_tab + level_tab + level_tab;
        final String split_value_prefix = ": ";

        List<Integer> devices = new ArrayList<Integer>();
        HashMap<Integer, String> devicesMap = new HashMap<Integer, String>();
        HashMap<Integer, HashMap<String, String>> devicesInfosMap = new HashMap<Integer, HashMap<String, String>>();
        HashMap<String, String> deviceInfos = null;

        for (int i = 0; i < inputInfos.length; i++) {
            String info = inputInfos[i];
            if (StringUtils.isEmpty(info)) continue;

            if (info.equals(FILTER_INPUT_READER_STATE)) {
                startRec = true;
            } else if (startRec) {
                if (!info.startsWith(level_2)) {
                    startRec = false;
                    if (devices.size() > 0) {
                        Integer deviceId = devices.get(devices.size() - 1);
                        if (devicesInfosMap.containsKey(deviceId)) {
                        } else if (deviceInfos != null) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.putAll(deviceInfos);

                            devicesInfosMap.put(devices.get(devices.size() - 1), map);
                            deviceInfos = new HashMap<String, String>();
                            isMotionRanges = false;
                            isJoystickInputMapper = false;
                            isKeyboardInputMapper = false;
                            isConfigurationSection = false;
                        }

                    }
                    break;
                } else if (info.startsWith(FILTER_INPUT_READER_STATE_DEVICE)) {
                    info = info.replace(FILTER_INPUT_READER_STATE_DEVICE, "");
                    String[] kv = info.split(split_value_prefix);
                    Integer deviceId = Integer.valueOf(kv[0]);
                    String deviceName = info.split(split_value_prefix)[1];

                    if (deviceInfos != null && !devicesInfosMap.containsKey(deviceId)) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.putAll(deviceInfos);

                        if (devices.size() > 0)
                            devicesInfosMap.put(devices.get(devices.size() - 1), map);

                    }
                    deviceInfos = new HashMap<String, String>();
                    if (!devices.contains(deviceId)) {
                        devices.add(deviceId);
                        devicesMap.put(deviceId, deviceName);
                        isMotionRanges = false;
                        isJoystickInputMapper = false;
                        isKeyboardInputMapper = false;
                        isConfigurationSection = false;
                    }

                } else if (info.startsWith(FILTER_INPUT_READER_STATE_DEVICE_MOTION_RANGES)) {
                    isMotionRanges = true;
                } else if (info.startsWith(FILTER_INPUT_READER_STATE_DEVICE_JOYSTICK_INPUT_MAPPER)) {
                    isJoystickInputMapper = true;
                } else if (info.startsWith(FILTER_INPUT_READER_STATE_DEVICE_KEYBOARD_INPUT_MAPPER)) {
                    isKeyboardInputMapper = true;
                } else if (info.startsWith(FILTER_INPUT_READER_STATE_CONFIGURATION)) {
                    isMotionRanges = false;
                    isJoystickInputMapper = false;
                    isKeyboardInputMapper = false;
                    isConfigurationSection = true;
                } else if (info.startsWith(level_3) && !info.replace(level_3, "").startsWith(level_2)) {
                    if (isConfigurationSection
                            || isMotionRanges
                            || isJoystickInputMapper
                            || isKeyboardInputMapper) continue;
                    info = cutPrefix(info, level_2);
                    String[] kv = info.split(split_value_prefix);
                    String key = kv[0];
                    String value = "";
                    if (kv.length > 1) value = kv[1];
                    if (deviceInfos == null) deviceInfos = new HashMap<String, String>();
                    deviceInfos.put(key, value);
                }
            }
        }

        Logger.log("filterInputReaderState result log--------------------------------------------------------------");
        for (int i = 0; i < devices.size(); i++) {
            Integer deviceId = devices.get(i);
            Logger.log("deviceId=" + deviceId + " deviceName=" + devicesMap.get(deviceId));
            HashMap<String, String> map = devicesInfosMap.get(devices.get(i));
            if (map == null) continue;

            Set<Map.Entry<String, String>> set = map.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                Logger.log(key + "=" + value);
            }
        }
    }
}
