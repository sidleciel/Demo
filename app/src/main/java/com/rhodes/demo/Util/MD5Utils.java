package com.rhodes.demo.Util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuw on 2014/11/19.
 */
public class MD5Utils {
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String getSafeSign(Map<String, String> map, String appkey) {
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

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 生成账号系统相关的签名
     *
    public static String getAccountSign(Map<String, String> args) {
        if (args == null || args.size() == 0) return "";

        Set<Map.Entry<String, String>> entries       = null;
        Iterator<Map.Entry<String, String>> iterator      = null;
        List<String> list          = null;
        String appPrivateKey = PPayCenter.getAppPrivateKey();
        StringBuffer sb = new StringBuffer(appPrivateKey);

        entries = args.entrySet();
        iterator = entries.iterator();
        Map.Entry<String, String> entry = null;
        list = new ArrayList<String>();
        while (iterator.hasNext()) {
            entry = iterator.next();
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return MD5Utils.stringToMD5(sb.toString());
    }
    **/
}
