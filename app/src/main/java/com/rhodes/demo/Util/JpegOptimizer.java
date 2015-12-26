package com.rhodes.demo.Util;

import android.content.Context;
import android.os.AsyncTask;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by xiet on 2015/8/5.
 */
public class JpegOptimizer {
    public static final String TOOL_NAME = "jpegoptim";

    private String tool;
    private String src;
    private String destDir;
    private int threshold;
    private boolean lossy;
    private int quality;
    private boolean preserve;
    private String[] cmdArray;

    private AsyncTask mOptimTask;

    /**
     * initilize jpegoptim
     *
     * @param tool
     * @param src
     * @param destDir
     * @param threshold
     * @param quality
     * @param preserve  save file property
     */
    public JpegOptimizer(String tool, String src, String destDir, int threshold, int quality, boolean preserve) {
        this.tool = tool;
        this.src = src;
        this.destDir = destDir;
        this.threshold = threshold;
        this.quality = quality;
        this.preserve = preserve;

        initCmd();
    }

    private void initCmd() {
        if (this.quality >= 0) {
            this.lossy = true;
        }
        ArrayList list = new ArrayList();
        list.add(this.tool);
        list.add(src);
        list.add("-b");// print progress info in CSV format
        list.add("-T" + this.threshold);
        if (this.lossy) {
            list.add("-m" + this.quality);
        }
        if (this.preserve) {
            list.add("-p");// preserve file timestamps
        }
        if (this.destDir != null) {
            list.add("-od" + this.destDir);// -o overwrite ,-d dest path
        }
        this.cmdArray = new String[list.size()];
        list.toArray(this.cmdArray);
    }

    void doOptim(final optimResultHandler handler) {
        if (mOptimTask == null || mOptimTask.getStatus() == AsyncTask.Status.FINISHED) {
            mOptimTask = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    String[] cmd = null;
                    if (params != null) cmd = (String[]) params;
                    else if (cmdArray != null) {
                        cmd = cmdArray;
                    }

                    if (cmd == null) return null;

                    String resultMsg = doExec(cmd);
                    boolean resultOptimized;
                    // doExec﹕/sdcard/Download/1.jpg,1164x3176,24bit,N,13503273,164081,98.78,optimized
                    if (!StringUtils.isEmpty(resultMsg)) {
                        String[] resultInfo = resultMsg.split(",");
                        if (resultInfo[resultInfo.length - 1].equals("optimized")) {
                            resultOptimized = true;
                        } else {
                            resultOptimized = false;
                        }
                    } else {
                        resultOptimized = false;
                    }

                    if (handler != null) {
                        handler.onResult(resultOptimized, resultMsg);
                    }

                    return resultMsg;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onCancelled(Object o) {
                    super.onCancelled(o);
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                }
            };
        }

        if (mOptimTask.getStatus() != AsyncTask.Status.RUNNING)
            mOptimTask.execute(cmdArray);
    }

    private static String doExec(String[] cmd) {
        String s = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void extract(Context context, String dest) {
        extract(context, TOOL_NAME, dest);
    }

    private static void extract(Context context, String fileName, String dest) {
        File file = new File(dest);

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            // debug(file.getPath() + " not found - extracting");

            InputStream in = null;
            OutputStream out = null;

            try {
                in = context.getAssets().open(fileName);
                out = new FileOutputStream(file);

                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);

            } catch (Exception e) {
                // log(e, "Failed to extract " + file.getPath());

            } finally {
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();

                    // set permission
                    Runtime runtime = Runtime.getRuntime();
                    String[] command = new String[3];
                    command[0] = "chmod";
                    command[1] = "755";
                    command[2] = file.getAbsolutePath();
                    runtime.exec(command);
                } catch (IOException e) {
                    // log(e, "Failed to close stream after extraction");
                }
            }
        }
        // debug(file.getPath() + " already exist - skipped");
    }

    public static interface optimResultHandler {
        void onResult(boolean optimized, String msg);
    }
}
