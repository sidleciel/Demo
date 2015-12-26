package com.rhodes.demo.joystick.map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import com.rhodes.demo.Util.ConfigFile;
import com.rhodes.demo.Util.SafeMethods;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiet on 2015/6/11.
 */
public class KeyMapManager extends Object {
    private static final String mapFileDir = Environment.getExternalStorageDirectory().getPath() + "/MG/config";
    private static final String TAG = KeyMapManager.class.getSimpleName();

    public static KeyMap.EmuMap emuMap;
    static ConfigFile[] mapIniFile = new ConfigFile[KeyMap.MAX_FILE_NUM];
    private static Context context;
    int ids[] = {0, 0, 0, 0};

    /**
     * @param gamer def is 0(1P).
     * @return
     */
    private static File getMapFile(int gamer) {
        if (KeyMap.inifiles[gamer] == null || KeyMap.inifiles[gamer].equals(""))
            return null;
        return new File(mapFileDir + "/" + KeyMap.inifiles[gamer]);
    }

    /**
     * write KeyMap config to file
     *
     * @param context_
     * @param map
     * @param data
     * @hide
     */
    public static void writeToConfig(Context context_, KeyMap.EmuMap map, Map<String, String> data) {
        context = context_;
        emuMap = map;
        if (map != null) {
            writeToConfig(map.section, data);
        }
    }

    /**
     * @param section
     * @return
     * @hide
     */
    public static Map getMap(String section) {

        Map<String, String> data = new HashMap<String, String>();
        load(null);
        ConfigFile.ConfigSection c = mapIniFile[0].get(section);

        Set set = c.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            data.put(key, c.get(key));

        }

        return data;
    }

    /**
     * write [section] KeyMap to config file
     *
     * @param sectionTitle
     * @param data
     * @hide
     */
    public static void writeToConfig(String sectionTitle, Map<String, String> data) {
        if (emuMap != null)
            load(emuMap);
        else load(null);

        Set set = data.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) i.next();
            String key = entry.getKey();
            String name = data.get(key);
            Log.i(TAG, "writeToConfig-name          -------------" + name);
            Log.i(TAG, "writeToConfig-key           -------------" + key);
            Log.i(TAG, "writeToConfig-mapIniFile[0] -------------" + mapIniFile[0]);
            if (mapIniFile[0] != null)
                mapIniFile[0].put(sectionTitle, name, key);
        }

        save();
    }

    /**
     * visibility of keyboard set and get.
     *
     * @param gamer user index
     * @param hide  is hide virtual keyboard or not. true is hide, otherwise.
     * @return
     */
    public static boolean virtualKeyboard(int gamer, Boolean... hide) {
        load(emuMap);

        boolean flag = SafeMethods.toBoolean(mapIniFile[gamer].get(KeyMap.emuKeyMapSection, KeyMap.emuKeysKey[0]), true);
        if (hide != null && hide.length != 0) {
            flag = hide[0];
            modifyKeyMap(gamer, KeyMap.emuKeyMapSection, KeyMap.emuKeysKey[0], String.valueOf(flag));
        }

        return flag;
    }

    public synchronized static void load(KeyMap.EmuMap map) {
        emuMap = map;
        for (int i = 0; i < mapIniFile.length; i++) {
            File file = getMapFile(i);
            if (file == null) continue;
            String path = file.getAbsolutePath();
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists() && file.isFile()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mapIniFile[i] = new ConfigFile(path);
            prepareData(i);
        }
    }

    public synchronized static void save() {
        for (int i = 0; i < mapIniFile.length; i++) {
            if (mapIniFile[i] != null) {
                mapIniFile[i].save();

                try {
                    saveStatAction(context, emuMap, getMapFile(i).getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void saveStatAction(Context context, KeyMap.EmuMap emuMap, String absolutePath) throws Exception {
        Class<?> keyMapActions = null;
        try {
            keyMapActions = Class.forName("com.join.mgps.joystick.KeyMapActions");
            if (keyMapActions != null) {
                Method method = keyMapActions.getMethod("saveStat", Context.class, KeyMap.EmuMap.class, String.class);
                method.invoke(keyMapActions.newInstance(), context, emuMap, absolutePath);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void modifyKeyMap(int gamer, String sectionTitle, String parameter, String value) {
        ConfigFile config = mapIniFile[gamer];

        config.put(sectionTitle, parameter, value);
        config.save();
    }

    public static KeyMapEvent getKeyMapEvent(int gamer, KeyEvent keyEvent) {
        if (emuMap == null) return null;
        KeyMapEvent ret = new KeyMapEvent();

        ConfigFile config = mapIniFile[gamer];
        if (config == null) return ret;

        ret.setKeycode(keyEvent.getKeyCode());
        ret.setEvent(keyEvent);

        String keyName = "null";
        ConfigFile.ConfigSection keySection = mapIniFile[gamer].get(KeyMap.keySection);
        Iterator<String> keyIterator = keySection.keySet().iterator();
        while (keyIterator.hasNext()) {
            String s = keyIterator.next();
            if (Integer.parseInt(keySection.get(s)) == (keyEvent.getKeyCode())) {
                keyName = s;
            }
        }
        ret.setKeyName(keyName);

        String emuKeyName = "null";
        int emuKeyIndex = -1;
        ConfigFile.ConfigSection emuKeySection = mapIniFile[gamer].get(emuMap.section);
        for (int i = 0; i < emuMap.keysKey.length; i++) {
            if (emuKeySection.get(emuMap.keysKey[i]).equals(ret.getKeyName())) {
                emuKeyName = emuMap.keysKey[i];
                emuKeyIndex = i;
            }
        }
        ret.setEmuKeyName(emuKeyName);
        ret.setEmuKeyIndex(emuKeyIndex);

        return ret;
    }

    private static void prepareData(int gamer) {
        ConfigFile configFile = mapIniFile[gamer];

        for (int j = 0; j < KeyMap.sections.length; j++) {
            String sectionTitle = KeyMap.sections[j];
            ConfigFile.ConfigSection section = configFile.get(sectionTitle);
            if (section == null) {
                section = new ConfigFile.ConfigSection(sectionTitle);
                configFile.put(section);
                putDefSectionKeys(section);
            }

        }
    }

    private static void putDefSectionKeys(ConfigFile.ConfigSection section) {
        String sectionTitle = section.name;

        if (sectionTitle.equals(KeyMap.keySection)) {
            for (int i = 0; i < KeyMap.keysKey.length; i++) {
                section.put(KeyMap.keysKey[i], String.valueOf(KeyMap.defJoystickKeys[i]));
            }
        } else if (sectionTitle.equals(KeyMap.emuKeyMapSection)) {
            for (int i = 0; i < KeyMap.emuKeysKey.length; i++) {
                section.put(KeyMap.emuKeysKey[i], String.valueOf(KeyMap.emuDefKeysValue[i]));
            }
        } else {
            KeyMap.EmuMap emuMap = null;
            if (sectionTitle.equals(KeyMap.EmuMap.FBA.section)) {
                emuMap = KeyMap.EmuMap.FBA;
            } else if (sectionTitle.equals(KeyMap.EmuMap.FC.section)) {
                emuMap = KeyMap.EmuMap.FC;
            } else if (sectionTitle.equals(KeyMap.EmuMap.SFC.section)) {
                emuMap = KeyMap.EmuMap.SFC;
            } else if (sectionTitle.equals(KeyMap.EmuMap.PSP.section)) {
                emuMap = KeyMap.EmuMap.PSP;
            } else if (sectionTitle.equals(KeyMap.EmuMap.GBA.section)) {
                emuMap = KeyMap.EmuMap.GBA;
            }
            if (emuMap == null) return;
            Iterator<Map.Entry<String, String>> iterator = emuMap.map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                section.put(entry.getKey(), entry.getValue());
            }

        }
    }

}
