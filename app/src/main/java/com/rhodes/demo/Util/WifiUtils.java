package com.rhodes.demo.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Wifi 工具类
 * <p/>
 * 封装了Wifi的基础操作方法，方便获取Wifi连接信息以及操作Wifi
 */

public class WifiUtils {
    private static WifiUtils mWifiUtils = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiInfo mWifiInfo;
    private DhcpInfo mDhcpInfo;
    private List<ScanResult> mWifiList;
    private WifiManager.WifiLock mWifiLock;
    public WifiManager mWifiManager;
    private NetworkInfo mNetworkInfo;
    private Context mContext;

    private WifiUtils(Context paramContext) {
        mWifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        mDhcpInfo = mWifiManager.getDhcpInfo();
        mWifiInfo = mWifiManager.getConnectionInfo();
        mNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mContext = paramContext;
    }

    public void setNewWifiManagerInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        mDhcpInfo = mWifiManager.getDhcpInfo();
    }

    public static WifiUtils getInstance(Context paramContext) {
        if (mWifiUtils == null)
            mWifiUtils = new WifiUtils(paramContext);
        return mWifiUtils;
    }

    private WifiConfiguration isExsits(String paramString) {
        if(mWifiManager==null||mWifiManager.getConfiguredNetworks()==null)return null;
        Iterator<WifiConfiguration> localIterator = mWifiManager.getConfiguredNetworks().iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if (!localIterator.hasNext())
                return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        }
        while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
        return localWifiConfiguration;
    }

    /**
     * 获取热点状态
     *
     * @return boolean值，对应热点的开启(true)和关闭(false)
     */
    public boolean getWifiApState() {
        try {
            int i = ((Integer) mWifiManager.getClass().getMethod("getWifiApState", new Class[0])
                    .invoke(mWifiManager, new Object[0])).intValue();
            return (3 == i) || (13 == i);
        } catch (Exception localException) {
        }
        return false;
    }

    /**
     * 判断是否连接上wifi
     *
     * @return boolean值(isConnect), 对应已连接(true)和未连接(false)
     */
    public boolean isWifiConnect() {
        return mNetworkInfo.isConnected();
    }

    public boolean isWifiConnect(Context context) {
        mNetworkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mNetworkInfo.isConnected();
    }

    public void AcquireWifiLock() {
        mWifiLock.acquire();
    }

    public void CreatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    public void OpenWifi() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
    }

    public void openWifi(Context context){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
    }

    public boolean isWifiEnable(){
        boolean rs = mWifiManager.isWifiEnabled();
        return rs;
    }

    public void ReleaseWifiLock() {
        if (mWifiLock.isHeld())
            mWifiLock.release();
    }

    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int i = mWifiManager.addNetwork(paramWifiConfiguration);
        mWifiManager.enableNetwork(i, true);
    }

    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    public void closeWifi(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(false);
    }

    public void connectConfiguration(int paramInt) {
        if (paramInt > mWifiConfiguration.size())
            return;
        mWifiManager.enableNetwork(
                ((WifiConfiguration) mWifiConfiguration.get(paramInt)).networkId, true);
    }

    public void disableNetwork(Context context,int netId){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.disableNetwork(netId);
        mWifiManager.saveConfiguration();
    }

    public void removeNetwork(Context context,int netId) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mWifiManager.removeNetwork(netId);
            mWifiManager.saveConfiguration();

    }

    public void createWiFiAP(WifiConfiguration paramWifiConfiguration, boolean paramBoolean) {
        try {
            Class<? extends WifiManager> localClass = mWifiManager.getClass();
            Method setupMethod = localClass.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiManager localWifiManager = mWifiManager;
            setupMethod.invoke(localWifiManager, paramWifiConfiguration, paramBoolean);
            return;
        } catch (Exception localException) {
        }
    }


    public WifiConfiguration createAPInfo(String ssid, String password) {
        WifiConfiguration netConfig = new WifiConfiguration();
        // 设置wifi热点名称
        netConfig.SSID = ssid;

        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        if (password != null) {
            // 设置wifi热点密码
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.preSharedKey = password;
        }
        return netConfig;
    }

    public WifiConfiguration createWifiInfo(String ssid, String paramString2, int paramInt,
                                            String paramString3) {
        WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
        localWifiConfiguration1.allowedAuthAlgorithms.clear();
        localWifiConfiguration1.allowedGroupCiphers.clear();
        localWifiConfiguration1.allowedKeyManagement.clear();
        localWifiConfiguration1.allowedPairwiseCiphers.clear();
        localWifiConfiguration1.allowedProtocols.clear();
        if ("wt".equals(paramString3)) {
            localWifiConfiguration1.SSID = ("\"" + ssid + "\"");
            WifiConfiguration localWifiConfiguration2 = isExsits(ssid);
            if (localWifiConfiguration2 != null)
                removeNetwork(mContext,localWifiConfiguration2.networkId);
            if (paramInt == 1) {
                localWifiConfiguration1.wepKeys[0] = "";
                localWifiConfiguration1.allowedKeyManagement.set(0);
                localWifiConfiguration1.wepTxKeyIndex = 0;
            } else if (paramInt == 2) {
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.wepKeys[0] = ("\"" + paramString2 + "\"");
            } else {
                localWifiConfiguration1.preSharedKey = ("\"" + paramString2 + "\"");
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.allowedAuthAlgorithms.set(0);
                localWifiConfiguration1.allowedGroupCiphers.set(2);
                localWifiConfiguration1.allowedKeyManagement.set(1);
                localWifiConfiguration1.allowedPairwiseCiphers.set(1);
                localWifiConfiguration1.allowedGroupCiphers.set(3);
                localWifiConfiguration1.allowedPairwiseCiphers.set(2);
            }
        } else {
            localWifiConfiguration1.SSID = ssid;
            localWifiConfiguration1.allowedAuthAlgorithms.set(1);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            localWifiConfiguration1.allowedKeyManagement.set(0);
            localWifiConfiguration1.wepTxKeyIndex = 0;
            if (paramInt == 1) {
                localWifiConfiguration1.wepKeys[0] = "";
                localWifiConfiguration1.allowedKeyManagement.set(0);
                localWifiConfiguration1.wepTxKeyIndex = 0;
            } else if (paramInt == 2) {
                localWifiConfiguration1.hiddenSSID = true;
                localWifiConfiguration1.wepKeys[0] = paramString2;
            } else if (paramInt == 3) {
                localWifiConfiguration1.preSharedKey = paramString2;
                localWifiConfiguration1.allowedAuthAlgorithms.set(0);
                localWifiConfiguration1.allowedProtocols.set(1);
                localWifiConfiguration1.allowedProtocols.set(0);
                localWifiConfiguration1.allowedKeyManagement.set(1);
                localWifiConfiguration1.allowedPairwiseCiphers.set(2);
                localWifiConfiguration1.allowedPairwiseCiphers.set(1);
            }
        }
        return localWifiConfiguration1;
    }

    public void disconnectWifi(int paramInt) {
        mWifiManager.disableNetwork(paramInt);
    }

    public String getApSSID() {
        try {
            Method localMethod = mWifiManager.getClass().getDeclaredMethod(
                    "getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod.invoke(mWifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }


    public WifiInfo getmWifiInfo() {
        return mWifiInfo;
    }

    public String getBSSID() {
        if (mWifiInfo == null)
            return "NULL";
        return mWifiInfo.getBSSID();
    }

    public String getSSID() {

        if (mWifiInfo == null)
            return "NULL";
        String ssid = mWifiInfo.getSSID();
        if(ssid==null||ssid.trim().equals("")){
            return "";
        }
        ssid = ssid.replaceAll("\"","");
        return ssid;
    }

    public String getSSID(Context context){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        String ssid = mWifiInfo.getSSID();
        if (ssid==null)return "";
        ssid = ssid.replaceAll("\"","");
        return ssid;
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    public String getLocalIPAddress() {
        if (mWifiInfo == null)
            return "NULL";
        return intToIp(mWifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        if (mDhcpInfo == null)
            return "NULL";
        return intToIp(mDhcpInfo.serverAddress);
    }

    public String getMacAddress() {
        if (mWifiInfo == null)
            return "NULL";
        return mWifiInfo.getMacAddress();
    }

    public int getNetworkId() {
        if (mWifiInfo == null)
            return 0;
        return mWifiInfo.getNetworkId();
    }

    public int getWifiApStateInt() {
        try {
            int i = ((Integer) mWifiManager.getClass().getMethod("getWifiApState", new Class[0]).invoke(mWifiManager, new Object[0])).intValue();
            return i;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 4;
    }

    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; ; i++) {
            if (i >= 2)
                return localStringBuilder;
            localStringBuilder.append("Index_" + Integer.valueOf(i + 1).toString() + ":");
            localStringBuilder.append(((ScanResult) mWifiList.get(i)).toString());
            localStringBuilder.append("/n");
        }
    }

    public void setWifiList() {
        mWifiList = mWifiManager.getScanResults();
    }

    public void startScan() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
    }

    public String intToIp(int paramIntip) {
        return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
    }


}