package com.rhodes.demo.joystick.map;

import android.view.KeyEvent;

import java.util.HashMap;

/**
 * Created by xiet on 2015/6/10.
 */
public class KeyMap {

    //=====file section=====
    public static final int MAX_FILE_NUM = 4;
    public static final String[] inifiles = {"key_map.ini", "", "", ""};

    //=====keys section=====
    public static final int[] defJoystickKeys = {KeyEvent.KEYCODE_BUTTON_SELECT, KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_X, KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_Y, KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_BUTTON_L1, KeyEvent.KEYCODE_BUTTON_L2, KeyEvent.KEYCODE_BUTTON_R1, KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_BUTTON_THUMBL, KeyEvent.KEYCODE_BUTTON_THUMBR};
    public static final int[] defKeyboardKeys = {KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_0};

    public static final String keySection = "KEY";
    public static final String[] keysKey = {"KEY_SELECT", "KEY_START",
            "KEY_X", "KEY_A", "KEY_Y", "KEY_B",
            "KEY_L1", "KEY_L2", "KEY_R1", "KEY_R2",
            "KEY_DPAD_LEFT", "KEY_DPAD_UP", "KEY_DPAD_RIGHT", "KEY_DPAD_DOWN", "KEY_BUTTON_THUMBL", "KEY_BUTTON_THUMBR"};

    //=====fba key-value section=====
    public static final String fbaKeyMapSection = "FBA_KEY_MAP";
    public static final String[] fbaKeysKey = {"FBA_KEY_SELECT", "FBA_KEY_START",
            "FBA_KEY_A", "FBA_KEY_B", "FBA_KEY_C", "FBA_KEY_D", "FBA_KEY_E", "FBA_KEY_F", "FBA_KEY_AB", "FBA_KEY_CD", "FBA_KEY_ABC"};
    public static final String[] fbaDefKeysValue = {"KEY_SELECT", "KEY_START",
            "KEY_X", "KEY_A", "KEY_Y", "KEY_B", "KEY_L1", "KEY_L2", "KEY_R1", "KEY_R2", "KEY_BUTTON_THUMBR"};

    //=====fc key-value section=====
    public static final String fcKeyMapSection = "FC_KEY_MAP";
    public static final String[] fcKeysKey = {"FC_KEY_SELECT", "FC_KEY_START",
            "FC_KEY_A", "FC_KEY_B", "FC_KEY_TURBO_A", "FC_KEY_TURBO_B", "FC_KEY_AB"};
    public static final String[] fcDefKeysValue = {"KEY_SELECT", "KEY_START",
            "KEY_A", "KEY_B", "KEY_X", "KEY_Y", "KEY_R1"};

    //=====sfc key-value section=====
    public static final String sfcKeyMapSection = "SFC_KEY_MAP";
    public static final String[] sfcKeysKey = {"SFC_KEY_SELECT", "SFC_KEY_START", "SFC_KEY_X", "SFC_KEY_A", "SFC_KEY_Y", "SFC_KEY_B", "SFC_KEY_L", "SFC_KEY_R", "SFC_KEY_FASTFORWARD"};
    public static final String[] sfcDefKeysValue = {"KEY_SELECT", "KEY_START", "KEY_X", "KEY_A", "KEY_Y", "KEY_B", "KEY_L1", "KEY_R1", "KEY_L2"};

    //=====psp key-value section=====
    public static final String pspKeyMapSection = "PSP_KEY_MAP";
    public static final String[] pspKeysKey = {"PSP_KEY_SELECT", "PSP_KEY_START", "PSP_KEY_X", "PSP_KEY_A", "PSP_KEY_Y", "PSP_KEY_B", "PSP_KEY_L", "PSP_KEY_R"};
    public static final String[] pspDefKeysValue = {"KEY_SELECT", "KEY_START", "KEY_X", "KEY_A", "KEY_Y", "KEY_B", "KEY_L1", "KEY_R1"};

    //=====gba key-value section=====
    public static final String gbaKeyMapSection = "GBA_KEY_MAP";
    public static final String[] gbaKeysKey = {"GBA_KEY_SELECT", "GBA_KEY_START", "GBA_KEY_A", "GBA_KEY_B", "GBA_KEY_L", "GBA_KEY_R", "GBA_KEY_TURBO_A", "GBA_KEY_TURBO_B", "GBA_KEY_FASTFORWARD"};
    public static final String[] gbaDefKeysValue = {"KEY_SELECT", "KEY_START", "KEY_B", "KEY_X", "KEY_Y", "KEY_A", "KEY_R1", "KEY_L1", "KEY_L2",};

    //=====emu key-value section=====
    public static final String emuKeyMapSection = "EMU_KEY_MAP";
    public static final String[] emuKeysKey = {"hideVirtual", fbaKeyMapSection, fcKeyMapSection, sfcKeyMapSection, gbaKeyMapSection, pspKeyMapSection};
    public static final String[] emuDefKeysValue = {"true", "1", "1", "1", "1", "1"};
    public static final String[] sections = {keySection, emuKeyMapSection, fbaKeyMapSection, sfcKeyMapSection, fcKeyMapSection, gbaKeyMapSection, pspKeyMapSection};

    public enum EmuMap {
        FBA(fbaKeyMapSection, fbaKeysKey, fbaDefKeysValue),
        FC(fcKeyMapSection, fcKeysKey, fcDefKeysValue),
        SFC(sfcKeyMapSection, sfcKeysKey, sfcDefKeysValue),
        PSP(pspKeyMapSection, pspKeysKey, pspDefKeysValue),
        GBA(gbaKeyMapSection, gbaKeysKey, gbaDefKeysValue);

        public String section;
        public String[] keysKey;
        public HashMap<String, String> map;

        EmuMap(String section, String[] keysKey, String[] defKeysValue) {
            this.section = section;
            this.keysKey = keysKey;

            if (keysKey == null) return;

            boolean defValueNull = false;
            if (defKeysValue == null || defKeysValue.length != keysKey.length) defValueNull = true;

            map = new HashMap();
            for (int i = 0; i < keysKey.length; i++) {
                if (defValueNull)
                    map.put(keysKey[i], "");
                else
                    map.put(keysKey[i], defKeysValue[i]);
            }

        }

        public String defValue(String key) {
            if (map != null) return "";
            return (String) map.get(key);
        }

    }
}
