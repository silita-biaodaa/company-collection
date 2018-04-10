package com.silita.biaodaa.service;

/**
 * Created by 91567 on 2018/4/3.
 */
public class StringTest {
    public static void main(String[] args) {
//        String temp = "2013-6-28(有效期：2019-6-28)";
//        System.out.println(temp.substring(0,temp.indexOf("有效期") - 1));
//        System.out.println(temp.substring(temp.indexOf("有效期") + 4, temp.length() - 1));


        String str1 = "20150511,有效期:2015-1-1至";
        String str2 = "安全生产许可证：（晋）JZ安许证字【2013】000324,有效期至:2016-5-22";

//        System.out.println(str1.substring(0, str1.indexOf("有效期")-1));
//        System.out.println(str1.substring(str1.indexOf("有效期") + 4, str1.indexOf("至")));

        System.out.println(str2.substring(str2.indexOf("安全生产许可证") + 8, str2.indexOf("有效期") - 1));
        System.out.println(str2.substring(str2.indexOf("有效期") + 5));
    }
}
