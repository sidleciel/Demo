package com.rhodes.demo.Util;

/**
 * Created by caowd on 2015/8/19.
 */
public class NetMatchJni {
    static
    {
        System.loadLibrary("EnDeCode");
    }
    public static native int EnCode(byte[] buf, int size, int key);
    public static native int DeCode(byte[] buf, int size, int key);

}
