package com.rhodes.demo.Util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by pengsk on 2015/2/25.
 */
public class SDCardPathUtil {
    /**
     * 获取手机自身内存路径
     *
     */
    public static String getPhoneCardPath() {
        return Environment.getDataDirectory().getPath();

    }

    /**
     * 获取sd卡路径 双sd卡时，根据”设置“里面的数据存储位置选择，获得的是内置sd卡或外置sd卡
     *
     * @return.
     */
    public static String getNormalSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取sd卡路径 双sd卡时，获得的是外置sd卡
     *
     * @return
     */
    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                Log.i("CommonUtil:getSDCardPath", lineStr);
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure",
                                "");
                        return result;
                    }
                }
                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                    Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
                }
            }
        } catch (Exception e) {
            Log.e("CommonUtil:getSDCardPath", e.toString());
            // return Environment.getExternalStorageDirectory().getPath();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (inBr != null) {
                    inBr.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    // 查看所有的sd路径
    public String getSDCardPathEx() {
        String mount = new String();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat("*" + columns[1] + "\n");
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1] + "\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mount;
    }

    // 获取当前路径，可用空间
    public static long getAvailableSize(String path) {
        try {
            File base = new File(path);
            StatFs stat = new StatFs(base.getPath());
            long nAvailableCount = stat.getBlockSize()
                    * ((long) stat.getAvailableBlocks());
            return nAvailableCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static long getAllSizeFromPath(String path){
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return  blockSize * totalBlocks;
    }
    public static String choseLargeStorage(Context context){
        long size=0;
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
// 获取sdcard的路径：外置和内置
        String[] paths=null;
        try {
            paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String repath=getNormalSDCardPath();
        for (int i=0;i<paths.length;i++){
            long s=SDCardPathUtil.getAllSizeFromPath(paths[i]);
            if (s>size){
                repath=paths[i];
                size=SDCardPathUtil.getAllSizeFromPath(paths[i]);
            }
        }
        return repath;
    }

}
