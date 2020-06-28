package com.zcyk.controller;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.util.WordUtils;
import com.zcyk.util.iMsgServer2015;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/9/16 17:36
 */
@Slf4j
@RestController
public class OfficeController {

    private iMsgServer2015 MsgObj = new iMsgServer2015();

    @Value("${contextPath}")
    String mFilePath;

    @RequestMapping("/OfficeServer")
    protected void service(HttpServletRequest request, HttpServletResponse response)  {
        try {
            String mFileName = request.getParameter("FileName");
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mfile = multipartRequest.getFile("FileData");
            JSONObject formData = JSONObject.parseObject(request.getParameter("FormData"));
            String filename = formData.getString("FILENAME");
            String type = filename.substring(filename.lastIndexOf("."));
            String file = filename.substring(0,filename.lastIndexOf("."));
            String filePath = mFilePath + filename;
            File desFile = new File(filePath);
            if (!desFile.getParentFile().exists()) {
                desFile.mkdirs();
            }
            mfile.transferTo(desFile);
            if(type.equals(".doc")||type.equals(".docx")){
                WordUtils.word2pdf(filePath,mFilePath+file+".pdf");
            }
        } catch (Exception e) {
            log.error("OfficeServer转换文件报错",e);
            e.printStackTrace();
        }
    }

    @RequestMapping("/FileDownload")
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            String mFileName = request.getParameter("FileName");
            if (MsgObj.MsgFileLoad(mFilePath + mFileName))
            {
                System.out.println(mFilePath + mFileName);
            }
            MsgObj.Send(response, 0);
        } catch (IOException e) {
            log.error("OfficeServer下载文件报错",e);
            e.printStackTrace();
        }
    }
}