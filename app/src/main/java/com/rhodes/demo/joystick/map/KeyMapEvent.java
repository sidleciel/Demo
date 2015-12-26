package com.rhodes.demo.joystick.map;

import android.view.InputEvent;

/**
 * Created by xiet on 2015/6/11.
 */
public class KeyMapEvent {
    int emuKeyIndex;//emu mapped key index
    String emuKeyName;//emu key's name
    String keyName;//emu key's name
    int keycode;//touched KEY_CODE
    InputEvent event;//keyboard event

    public int getEmuKeyIndex() {
        return emuKeyIndex;
    }

    public void setEmuKeyIndex(int emuKeyIndex) {
        this.emuKeyIndex = emuKeyIndex;
    }

    public String getEmuKeyName() {
        return emuKeyName;
    }

    public void setEmuKeyName(String emuKeyName) {
        this.emuKeyName = emuKeyName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getKeycode() {
        return keycode;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public InputEvent getEvent() {
        return event;
    }

    public void setEvent(InputEvent event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "KeyMapEvent{" +
                "emuKeyIndex=" + emuKeyIndex +
                ", emuKeyName='" + emuKeyName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", keycode=" + keycode +
                ", event=" + event.toString() +
                '}';
    }
}
