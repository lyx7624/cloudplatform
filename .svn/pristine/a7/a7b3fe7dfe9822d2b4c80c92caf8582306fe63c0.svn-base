package com.zcyk.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2020/3/26 14:36
 */
@RestController
public class FileController {

    @Value("contextPath")
    String contextPath;

    @RequestMapping("getFile")
    void getFile(String filePath,HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        File file = new File(contextPath+"filePath");
        FileInputStream fileInputStream = new FileInputStream(file);
    }


}