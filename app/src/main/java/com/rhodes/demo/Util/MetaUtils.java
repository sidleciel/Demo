package com.rhodes.demo.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by mawj on 2014/12/1.
 */
public class MetaUtils {

    /**
     * 获取App编号
     * @param context
     * @return
     */
    public static String getAppID(Context context)  {
        String key = "3";
        try {
        ApplicationInfo appInfo = context.getPackageManager()
                .getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);

            key = appInfo.metaData.getInt("MGSIM_APPKEY") + "";
        } catch (Exception e) {
        }
        return key;
    }

    static String countType;
    /**
     * 获取类型：推广、分享
     * @param context
     * @return
     */
    public static String getCountType(Context context)  {
        if(StringUtils.isNotEmpty(countType))return countType;

        try {
        ApplicationInfo appInfo = context.getPackageManager()
                .getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);

            countType = appInfo.metaData.getInt("count_type") + "";
        } catch (Exception e) {
        }
        return countType;
    }



    static String qudao;

    /**
     * 获取渠道号
     *
     * @param context
     * @return
     * @throws android.content.pm.PackageManager.NameNotFoundException
     */
    public static String getAd(Context context)  {
        if(StringUtils.isNotEmpty(qudao))return qudao;

        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            int key = appInfo.metaData.getInt("qd_code");
            qudao = key+"";
            return qudao;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "-1";
    }

}
