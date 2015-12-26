package com.rhodes.demo.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import com.rhodes.demo.Util.Logger;
import com.rhodes.demo.Util.SafeMethods;

import java.util.ArrayList;
import java.util.List;

public class BtManager {

    private static BtManager instance;
    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter = null;

    private boolean isEstablishProxy = false;
    private int profile;
    private BluetoothProfile mBluetoothProfile = null;

    private OnBtStateChangedListener mBtStateChangedListener;
    private List<BtObserver> mBtObservers;

    Handler mHandler = new Handler();
    private boolean receiverRegisted = false;
    private int connectedDeviceSize = 0;

    public BtManager() {
        super();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BtManager getInstance() {
        if (instance == null) {
            instance = new BtManager();
        }
        return instance;
    }

    public boolean isEstablishProxy() {
        log("isEstablishProxy");
        return isEstablishProxy;
    }

    public boolean isSupported() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter != null) return true;

        return false;
    }

    public void establish(Context context, int profile) {
        log("establish");
        if (!isSupported()) return;

        if (isEstablishProxy())
            closeProfile();
        if (receiverRegisted) {
            unregisterBtReceiver();
        }

        if (context == null) {
            return;
        }

        this.profile = profile;
        this.mContext = context;

        try {
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isEnabled() && !isEstablishProxy()) {
                    mBluetoothAdapter.getProfileProxy(mContext, mProfileListener, profile);
                }

                registerBtReceiver();
            }
        } catch (Exception e) {
        }
    }

    private void closeProfile() {
        log("closeProfile");
        if (mBluetoothAdapter != null && mBluetoothProfile != null) {
            mBluetoothAdapter.closeProfileProxy(profile, mBluetoothProfile);
            mBluetoothAdapter = null;
            mBluetoothProfile = null;
        }
    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
            log("onServiceConnected");
            isEstablishProxy = true;
            mBluetoothProfile = bluetoothProfile;

            notifyBtStateChanged();
        }

        @Override
        public void onServiceDisconnected(int profile) {
            log("onServiceDisconnected");
            isEstablishProxy = false;
            mBluetoothProfile = null;
        }
    };

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log("btReceiver-onReceive");
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                boolean enabled = mBluetoothAdapter.isEnabled();
                if(!isEstablishProxy&&mContext!=null&&profile!=0&&mBluetoothAdapter.isEnabled()){
                    establish(mContext,profile);
                    notifyBtStateChanged();
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                //TODO:
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                //discovery finished
            } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)
                    || action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                    || action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//            	mHandler.postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						notifyBtStateChanged();
//						
//					}
//				}, 500);
                SafeMethods.sleep(500);
                notifyBtStateChanged();
            } else if (action.equals(BluetoothDevice.ACTION_NAME_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_CLASS_CHANGED)
                    || action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //TODO:
            }
        }
    };

    public void unregisterBtReceiver() {
        log("unregisterBtReceiver");
        if (receiverRegisted) {
            mContext.unregisterReceiver(btReceiver);
            receiverRegisted = false;
        }
    }

    public void registerBtReceiver() {
        log("registerBtReceiver");
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
//        if (Wrapper.SDK >= 19)
//            intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
//        if (Wrapper.SDK >= 15)
//            intentFilter.addAction(BluetoothDevice.ACTION_UUID);

        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        if (Wrapper.SDK >= 11)
//            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        mContext.registerReceiver(btReceiver, intentFilter);

        receiverRegisted = true;
    }

    public void close() {
        log("close");
        closeProfile();
        unregisterBtReceiver();
    }

    public void notifyBtStateChanged() {
        log("notifyBtStateChanged");
        if (mBluetoothProfile == null) return;

        List<BluetoothDevice> connectedDevices = mBluetoothProfile.getConnectedDevices();
        int size = connectedDevices.size();

//      if (connectedDeviceSize != size) {
//			if (connectedDeviceSize < size) {
//				Toast.makeText(mContext, R.string.bluetooth_conn, Toast.LENGTH_SHORT).show();
//			}else {
//				Toast.makeText(mContext, R.string.bluetooth_disconn, Toast.LENGTH_SHORT).show();
//			}
//		}
        connectedDeviceSize = size;

        if (mBtStateChangedListener != null)
            mBtStateChangedListener.onStateChanged(connectedDeviceSize, connectedDevices);

        for (int i = 0; mBtObservers != null && i < mBtObservers.size(); i++) {
            mBtObservers.get(i).onConnectionChanged(connectedDeviceSize, connectedDevices);
        }
    }

    public void setOnBtStateChangedListener(OnBtStateChangedListener l) {
        this.mBtStateChangedListener = l;
    }

    public interface OnBtStateChangedListener {
        public void onStateChanged(int size, List<BluetoothDevice> list);
    }

    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices");
        if (mBluetoothProfile != null) {
            return mBluetoothProfile.getConnectedDevices();
        }

        return null;
    }

    public void addBtObserver(BtObserver btObserver) {
        log("addBtObserver");
        if (mBtObservers == null) {
            mBtObservers = new ArrayList<BtObserver>();
        }
        mBtObservers.add(btObserver);
    }

    public void removeBtObserver(BtObserver btObserver) {
        log("removeBtObserver");
        if (mBtObservers == null || btObserver == null) return;

        if (mBtObservers.contains(btObserver)) {
            mBtObservers.remove(btObserver);
        }

        if (mBtObservers.size() < 1) mBtObservers = null;
    }

    private void log(String string) {
        Logger.log(string);
    }

}
