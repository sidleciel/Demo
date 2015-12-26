package com.rhodes.demo.Util;


import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by pengsk on 2015/5/12.
 */
public class SignUtil {
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    /**
     * TODO  生成参数签名,1.参数中除resource_id和sign外的参数的值按字符串格式顺序排序
     *
     * @param
     * @return String 加密前的支付参数
     * @throw
     */

    private static String getSafeSign(Map<String, String> map) {
        String appkey="Se+mLqD-mV@y6^Z+";
        List<String> query_string = new ArrayList<String>();
        try {
            for (String key : map.keySet()) {
                if (key.equals("sig") || key.equals("sign")) {
                    continue;
                }
                query_string.add(key + "=" + map.get(key));
            }
            Collections.sort(query_string);
            String str = appkey + "" + query_string.toString().replace("[", "").replace("]", "").replace(", ", "&");
            // 创建具有指定算法名称的信息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            byte[] results = md.digest(str.getBytes());
            // 将得到的字节数组变成字符串返回\
            return byteArrayToHexString(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 轮换字节数组为十六进制字符串
     *
     * @param b 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


    private static Map<String, String> getValueMap(Object obj) {

        Map<String, String> map = new HashMap<String, String>();
        // System.out.println(obj.getClass());
        // 获取f对象对应类中的所有属性域
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            if (Modifier.isStatic(fields[i].getModifiers())){
                continue;
            }
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(obj);
                if (o != null){

                    map.put(varName, o.toString());
                }
                // System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;

    }




    public static String getSign(Object o){
     String s= getSafeSign(getValueMap(o));
        Log.v("info", s);
     return getSafeSign(getValueMap(o));
//        return md5(genSignature(getValue(0)));
    }
}
