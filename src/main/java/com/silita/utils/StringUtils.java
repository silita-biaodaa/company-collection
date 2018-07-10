package com.silita.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

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

    public static String defaultIfBlank(String str, String defaultStr) {
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(str, defaultStr);
    }

    public static String md5(String content) {
        return Hashing.md5().hashString(content, Charset.forName("UTF-8")).toString();
    }
}
