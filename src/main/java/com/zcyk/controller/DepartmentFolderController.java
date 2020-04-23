package com.zcyk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcyk.pojo.DepartmentFolder;
import com.zcyk.dto.Download;
import com.zcyk.pojo.File;
import com.zcyk.service.DepartmentFolderService;
import com.zcyk.service.FileService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:部门文件夹
 * 开发人员: xlyx
 * 创建日期: 2019/8/9 9:11
 */
@RestController
@RequestMapping("departmentFolder")
public class DepartmentFolderController {


    @Autowired
    DepartmentFolderService departmentFolderService;
    @Autowired
    FileService fileService;
    @Autowired
    ObjectMapper mapper;



    /**
     * 功能描述：新建部门文件夹;权限判断
     * 开发人员： lyx
     * 创建时间： 2019/8/9 9:12
     * 参数：department_id 部门id folder_name文件夹名 parent_id父id(当前文件夹id)
     */
    @RequestMapping("/addDepartmentFolder")
    public ResultData addDepartmentFolder(DepartmentFolder departmentFolder) throws Exception {
        return departmentFolderService.addDepartmentFolder(departmentFolder);
    }

    /**
     * 功能描述：文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 18:23
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/AllDepartmentfolder")
    public Map<String, Object> AllDepartmentfolder(String departmentFolder_id) throws Exception {
        return departmentFolderService.selectAllDepartmentFolder(departmentFolder_id);
    }

    /**
     * 功能描述：删除部门文件夹及文件;权限判断
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 18:25
     * 参数：[ id 当前文件夹id]
     * 返回值：
     */
    @RequestMapping("/deleteDepartmentfolder")
    @ResponseBody
    public ResultData deleteDepartmentfolder(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        String folder_id = map.get("folder_id").toString();
        return departmentFolderService.deleteDepartmentfolder(ids, folder_id);
    }

    /**
     * 功能描述：修改部门文件夹名称;权限判断
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 17:51
     * 参数：id 文件夹id,folder_name 新的文件夹名称
     * 返回值：
     */
    @RequestMapping("/updateDepartmentfolder")
    @ResponseBody
    public ResultData updateDepartmentfolder(DepartmentFolder departmentFolder) throws Exception {

        return departmentFolderService.updateDepartmentfolder(departmentFolder);
    }


    /**
     * 功能描述：上传文件
     * 开发人员： lyx
     * 创建时间： 2019/8/9 21:43
     * 参数： [file, departmentFolderId 文件夹id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/addFile")
    @ResponseBody
    public ResultData addFile(MultipartFile file, String folder_id,@RequestParam(defaultValue = "0") int cover) throws Exception {

            return fileService.upFile(file, folder_id,cover);

    }

    /**
     * 功能描述：修改文件名
     * 开发人员： lyx
     * 创建时间： 2019/8/10 9:22
     * 参数： [file_name 新的文件名, id 当前文件id, folder_id 文件id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("updateFileName")
    @ResponseBody
    public ResultData updateFileName(File file) throws Exception {
        ResultData resultData = new ResultData();
        if(null!=fileService.findFile(file)){
            return resultData.setMsg("文件已存在").setStatus("400");
        }
        fileService.updateFileName(file);
        return resultData;
    }


    /**
     * 搜索部门文件夹及文件
     */
    @RequestMapping("/searchUserFolder")
    @ResponseBody
    public Map<String, Object> searchUserFolder(String index, String folder_id) throws Exception {
        return departmentFolderService.searchDepartmentFolder(index, folder_id);
    }

    /**
     * 多文件夹多文件下载
     */
    @RequestMapping(value = "/departmentFolderdownload")
    @ResponseBody
    public void userFolderdownload(String listString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(URLEncoder.encode("[{\"id\":\"eadc26ab-5476-4178-8e0d-9b11b9cb6cd1\",\"type\":1},{\"id\":\"398c5937-61a7-4b5b-8df7-23f140e7ae10\",\"type\":0},{\"id\":\"cc794838-256f-4b43-83b0-46867e00a07a\",\"type\":0}]","UTF-8"));
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });
       departmentFolderService.downloadFolderAndFileZip(list);
    }

}