package com.silita.service;

import com.silita.utils.CNNumberFormat;

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
        String str3 = "合同日期：2014-4-16,合同价格：万元";

//        System.out.println(str1.substring(0, str1.indexOf("有效期")-1));
//        System.out.println(str1.substring(str1.indexOf("有效期") + 4, str1.indexOf("至")));

//        System.out.println(str2.substring(str2.indexOf("安全生产许可证") + 8, str2.indexOf("有效期") - 1));
//        System.out.println(str2.substring(str2.indexOf("有效期") + 5));

//        System.out.println(str3.substring(str3.indexOf("合同日期") + 5, str3.indexOf("合同价格") - 1));
//        System.out.println(str3.substring(str3.indexOf("合同价格") + 5, str3.indexOf("万元")));


        String test = "8100.000万元人民币";
        System.out.println(CNNumberFormat.ChnStringToNumber(test.replace("人民币", "").replace("元", "")));

        String[] qual = "建筑装修装饰工程专业承包一级|古建筑工程专业承包二级".split("\\|");
        for (int i = 0; i < qual.length; i++) {
            System.out.println(qual[i]);
        }

        System.out.println("湘143171771655".replaceAll("[^0-9]","") + "$$$$$$$");
        System.out.println("建筑".replaceAll("[^0-9]","") + "$$$$$$$");

        System.out.println("430421*7777".substring(0,6));
    }
}
