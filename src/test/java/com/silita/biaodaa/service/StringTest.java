package com.silita.biaodaa.service;

/**
 * Created by 91567 on 2018/4/3.
 */
public class StringTest {
    public static void main(String[] args) {
        String temp = "2013-6-28(有效期：2019-6-28)";
        System.out.println(temp.substring(0,temp.indexOf("有效期") - 1));
        System.out.println(temp.substring(temp.indexOf("有效期") + 4, temp.length() - 1));
    }
}
