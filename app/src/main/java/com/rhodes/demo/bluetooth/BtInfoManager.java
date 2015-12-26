package com.rhodes.demo.bluetooth;

import android.bluetooth.BluetoothDevice;
import com.rhodes.demo.Util.ExecTerminal;
import com.rhodes.demo.Util.Logger;

import java.util.List;

/**
 * Created by xiet on 2015/9/16.
 */
public class BtInfoManager {

    private final String CMD_DUMPSYS_INPUT = "adb shell dumpsys input";
    private final String DUMP_PREFIX_INPUT_READER_STATE = "Input Reader State:";
    private final String DUMP_SUFFIX_INPUT_READER_STATE = "";
    private final String DUMP_PREFIX_DEVICE = "Device";
    private final String DUMP_PREFIX_DEVICE_SOURCES = "Sources";

    List<BtInfo> mBtInfo;

    public BtInfoManager() {
    }

    public BtInfoManager(List<BluetoothDevice> devices) {
        verify(devices);
    }

    public void verify(List<BluetoothDevice> devices) {
        String[] infos = ExecTerminal.exec(CMD_DUMPSYS_INPUT).split("\n");//all sys input info, you can save this to file.

        boolean startInputReaderStateSection = false;
        String inputReaderStateInfo = "";
        for (int i = 0; i < infos.length; i++) {
            String s = infos[i];

            if (s.equals(DUMP_PREFIX_INPUT_READER_STATE)) {
                startInputReaderStateSection = true;
            } else if (startInputReaderStateSection && s.trim().startsWith(DUMP_PREFIX_DEVICE)) {
                inputReaderStateInfo = s;
            } else if (startInputReaderStateSection && s.trim().startsWith(DUMP_PREFIX_DEVICE_SOURCES)) {
            } else if (startInputReaderStateSection && s.trim().startsWith(DUMP_SUFFIX_INPUT_READER_STATE)) {
                startInputReaderStateSection = false;
                Logger.log(inputReaderStateInfo);
            } else if (startInputReaderStateSection) {
                inputReaderStateInfo += s;

            }
        }

    }

    public class BtInfo {

        private BluetoothDevice mBtDevice;
        private Integer mBtDeviceId;
        private String info;//hold too much memory, only use for debug

        public BtInfo() {
        }

        public BluetoothDevice getBtDevice() {
            return mBtDevice;
        }

        public void setBtDevice(BluetoothDevice mBtDevice) {
            this.mBtDevice = mBtDevice;
        }

        public Integer getBtDeviceId() {
            return mBtDeviceId;
        }

        public void setBtDeviceId(Integer mBtDeviceId) {
            this.mBtDeviceId = mBtDeviceId;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }


}
