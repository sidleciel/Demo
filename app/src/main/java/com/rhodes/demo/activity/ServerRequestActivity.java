package com.rhodes.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * Created by xiet on 2015/8/24.
 */
public class ServerRequestActivity extends Activity {
    private static final String TAG = ServerRequestActivity.class.getSimpleName();

    //    String URL_OFFLINE = "http://192.168.78.5:8000/api/android/index.php";
//    String URL_ONLINE = "http://ctapi.mg3721.com/stdserver/api";
//    String Json = "{\"deviceid\":\"865852029694252\",\"messages\":{\"args\":{\"pn\":1},\"modeltype\":\"getCommunityBanner\"},\"requesttype\":\"load\",\"sign\":\"c21431c683e41e67267bd80d4223da27\",\"version\":\"16_1.5.0\"}";
    String URL_OFFLINE = "http://192.168.78.5:10001/forum/forum/follow";
    String URL_ONLINE  = "http://192.168.78.5:10001/forum/forum/follow";
    String Json        = "{\"fid\":1,\"uid\":30061,\"token\":\"QQLT87ByB4VVZvyb\"}";
    String postResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_request);
        new Thread() {
            public void run() {
                boolean online = false;
                postResult = doHttpPost(Json, online ? URL_ONLINE : URL_OFFLINE);
            }
        }.start();
    }

    public static String doHttpPost(String xmlInfo, String URL) {
        Logger.log(TAG + "-->" + "send info:" + xmlInfo);
        byte[] xmlData = xmlInfo.getBytes();
        InputStream instr = null;
        java.io.ByteArrayOutputStream out = null;
        try {
            java.net.URL url = new java.net.URL(URL);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setRequestProperty("Content-Type", "text/xml");
            urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
            Logger.log(TAG + "-->" + String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            instr = urlCon.getInputStream();
            byte[] bis = input2byte(instr);
            String ResponseString = new String(bis, "UTF-8");
            if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                Logger.log(TAG + "-->" + "receive null");
            }
            Logger.log(TAG + "-->" + "receive:" + ResponseString);
            return ResponseString;

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            try {
                out.close();
                instr.close();

            } catch (Exception ex) {
                return "0";
            }
        }
    }

    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }
}
