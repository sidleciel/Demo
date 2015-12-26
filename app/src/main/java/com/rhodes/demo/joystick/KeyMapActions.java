package com.rhodes.demo.joystick;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.rhodes.demo.bluetooth.BtManager;
import com.rhodes.demo.joystick.map.KeyMap;

import java.util.Date;
import java.util.List;

/**
 * Created by xiet on 2015/6/18.
 */
public class KeyMapActions {
/*
    public static void saveStat(Context context, KeyMap.EmuMap emuMap, String path) {
        if (emuMap == null && context == null)
            return;
        int type = -1;
        if (emuMap == KeyMap.EmuMap.FBA) {
            type = JoyStickConfig.TYPE_FBA;
        } else if (emuMap == KeyMap.EmuMap.GBA) {
            type = JoyStickConfig.TYPE_GBA;
        } else if (emuMap == KeyMap.EmuMap.SFC) {
            type = JoyStickConfig.TYPE_SFC;
        } else if (emuMap == KeyMap.EmuMap.PSP) {
            type = JoyStickConfig.TYPE_PSP;
        } else if (emuMap == KeyMap.EmuMap.FC) {
            type = JoyStickConfig.TYPE_FC;
        }

//        StatCore.getInstance().send(Event.joystickConfigChanged, "");
        JoyStickConfig joyStickConfig = new JoyStickConfig();
        AccountBean accountBean = AccountUtil_.getInstance_(context).getAccountData();
        if (accountBean != null)
            joyStickConfig.setUid(accountBean.getUid());
        joyStickConfig.setType(type);
        joyStickConfig.setFile(path);
        fillConnectedGamepadInfo(joyStickConfig);
        joyStickConfig.setUpdate_time(new Date().getTime());


        StatFactory.getInstance(context).sendJoyStickConfigPost(JsonMapper.nonDefaultMapper().toJson(joyStickConfig),AccountUtil_.getInstance_(context).getUid());
    }

    private static void fillConnectedGamepadInfo(JoyStickConfig joyStickConfig) {
        String gamepadName = "";
        String gamepadMac = "";
        String split = ";";
        List<BluetoothDevice> connectedList = BtManager.getInstance().getConnectedDevices();
        if (connectedList != null) {
            for (int i = 0; i < connectedList.size(); i++) {
                gamepadName += connectedList.get(i).getName() + split;
                gamepadMac += connectedList.get(i).getAddress().replace(":", "_") + split;
            }
        }
        if (!gamepadName.equals("")) gamepadName.substring(0, gamepadName.length()-1);
        if (!gamepadMac.equals("")) gamepadMac.substring(0, gamepadName.length()-1);
        joyStickConfig.setGamepad_name(gamepadName);
        joyStickConfig.setGamepad_mac(gamepadMac);
    }*/
}
