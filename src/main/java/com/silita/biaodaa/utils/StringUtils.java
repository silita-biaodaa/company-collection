package com.silita.biaodaa.utils;

/**
 * Created by 91567 on 2018/4/9.
 */
public class StringUtils {

    public static boolean isNotNull(String str){
        return str != null && !str.trim().equals("");
    }

    public static boolean isNull(String str){
        return !isNotNull(str);
    }
}
