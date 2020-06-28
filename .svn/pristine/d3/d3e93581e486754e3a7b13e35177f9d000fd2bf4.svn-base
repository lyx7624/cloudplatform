package com.zcyk.service;


import com.zcyk.pojo.File;
import com.zcyk.dto.ResultData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
* 功能描述: w文件服务接口
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/10 9:10
*/
public interface FileService {


    /*查找文件*/
    File findFile(File file);

    /*上传文件到文件夹*/
    ResultData upFile(MultipartFile file, String folder_id,Integer cover) throws Exception;

    /*修改文件名称*/
    void updateFileName(File file)throws Exception;

    /*删除文件*/
    ResultData deletedFile(String file_id, String folder_id)throws Exception;

    /*统计当前文件夹的大小*/
    Map<String,Object> folderSize(String user_id)throws Exception;

    /*个人云盘大小*/
    Map<String,Object> userfolder_size(String user_id)throws Exception;

    /*企业使用容量*/
    Map<String,Object> companySize()throws Exception;

    /*根据文件URL查询文件*/
    File findFileByUrl(String Url)throws Exception;

    /*上传文件到服务器*/
    String upFileToServer(MultipartFile file,String contextPath,String fileName) throws Exception;


    /*获取文件流*/
    void getImage(String path, HttpServletResponse response) throws IOException;






}
