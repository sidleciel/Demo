package com.rhodes.demo.Util;

import android.util.Log;

import java.io.*;

/**
 * Created by xiet on 2015/9/16.
 */
public class ExecTerminal {
    public static final String TAG = "ExecTerminal";

    public static String exec(String cmd) {
        Log.d(TAG, "^ Executing '" + cmd + "'");
        try {
            Process process = Runtime.getRuntime().exec("sh");
            DataInputStream is = new DataInputStream(process.getInputStream());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                String fullOutput = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    fullOutput += line + "\n";
                }
                return fullOutput;
            } catch (IOException e) {
                Log.e(TAG, "^ exec, IOException 1");
                e.printStackTrace();
            }

            process.waitFor();

        } catch (IOException e) {
            Log.e(TAG, "^ exec, IOException 2");
            e.printStackTrace();

        } catch (InterruptedException e) {
            Log.e(TAG, "^ exec, InterruptedException");
            e.printStackTrace();
        }
        return "";
    }

    public static String execSu(String cmd) {
        Log.d(TAG, "^ Executing as SU '" + cmd + "'");
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataInputStream is = new DataInputStream(process.getInputStream());
            DataOutputStream os = new DataOutputStream(process
                    .getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            try {
                String fullOutput = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    fullOutput = fullOutput + line + "\n";
                }
                return fullOutput;
            } catch (IOException e) {// It seems IOException is thrown when it reaches EOF.
                e.printStackTrace();
                Log.e(TAG, "^ execSU, IOException 1");
            }
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "^ execSU, IOException 2");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "^ execSU, InterruptedException");
        }
        return "";
    }

}
