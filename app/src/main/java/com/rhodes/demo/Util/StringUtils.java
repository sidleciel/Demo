package com.rhodes.demo.Util;

import java.util.regex.Pattern;

/**
 * Created by lala on 15/7/21.
 */
public class StringUtils {
    public static boolean isNotEmpty(String str) {
        if (str == null || str.equals("")) return false;
        return true;
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) return true;
        return false;
    }

    public static boolean isNumeric(String code) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(code).matches();
    }

    public static String getSafeString(String str) {
        return str == null ? "" : str;
    }

    public static String getNameNoExt(String path) {
        int start = path.lastIndexOf('/');
        int end = path.lastIndexOf('.');
        if (end == -1)
            end = path.length();
        if (start == -1)
            return path.substring(0, end);
        else
            return path.substring(start + 1, end);
    }

    public static String getNameWithExt(String path) {
        int start = path.lastIndexOf('/');

        return path.substring(start + 1, path.length());
    }

    public static String getNameNoExtWithPath(String path) {
        int end = path.lastIndexOf('.');
        if (end == -1)
            end = path.length();
        return path.substring(0, end);
    }
}
