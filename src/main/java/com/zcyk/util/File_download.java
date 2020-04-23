package com.zcyk.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.net.UnknownHostException;


@Component
public class File_download {



    public static String downloadFile(String fileName,HttpServletResponse response, HttpServletRequest request) throws UnknownHostException, UnsupportedEncodingException, FileNotFoundException {
        File file = new File(fileName);

        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1"); // firefox浏览器
        } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
        }else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
        }

        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
        response.setCharacterEncoding("UTF-8");
        if(!file.exists()){
          boolean b = file.mkdirs();
            System.out.println(b);
        }
        byte[] buffer = new byte[1024 * 10];
        FileInputStream fis = null;

        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            response.setHeader("msg","success");
            System.out.println("success");

        } catch (Exception e) {
            response.setHeader("msg","未知错误"+e.getMessage());
            e.printStackTrace();
        } finally {

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
