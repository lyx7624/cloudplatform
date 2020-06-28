package com.zcyk.controller;


import com.zcyk.dto.ResultData;
import com.zcyk.util.WordUtils;

public class T {

    public static void main(String[] args) throws Exception {


        System.out.println(Double.MAX_VALUE+"");

        WordUtils.pdf2word("C:\\Users\\Administrator\\Documents\\WXWork\\1688851136398927\\Cache\\File\\2020-06\\劳务实名制数据库国家标准字段.doc","C:\\Users\\Administrator\\Documents\\WXWork\\1688851136398927\\Cache\\File\\2020-06\\劳务实名制数据库国家标准字段.pdf");


/*
        Runtime run = Runtime.getRuntime();

        long max = run.maxMemory();

        long total = run.totalMemory();

        long free = run.freeMemory();

        long usable = max - total + free;

        System.out.println("最大内存 = " + max/(1024*1024));
        System.out.println("已分配内存 = " + total/(1024*1024));
        System.out.println("已分配内存中的剩余空间 = " + free/(1024*1024));
        System.out.println("最大可用内存 = " + usable/(1024*1024));

        for (int i = 0; i < 1000000; i++) {
            ResultData resultData = new ResultData();
        }

        Runtime run1 = Runtime.getRuntime();

        long max1 = run1.maxMemory();

        long total1 = run1.totalMemory();

        long free1 = run1.freeMemory();

        long usable1 = max1 - total1 + free1;

        System.out.println("1最大内存 = " + max1/(1024*1024));
        System.out.println("1已分配内存 = " + total1/(1024*1024));
        System.out.println("1已分配内存中的剩余空间 = " + free1/(1024*1024));
        System.out.println("1最大可用内存 = " + usable1/(1024*1024));
*/


    }


}
