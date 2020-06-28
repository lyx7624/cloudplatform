package com.zcyk.service;

import com.zcyk.pojo.Department;
import com.zcyk.pojo.DepartmentFolder;
import com.zcyk.dto.Download;
import com.zcyk.dto.ResultData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:部门云盘
 * 开发人员: wujiefeng
 * 创建日期: 2019/8/9 10:03
 */
public interface DepartmentFolderService {


    /*删除部门的文件夹及文件*/
    ResultData deleteDepartmentfolder(List<String> ids, String folder_id)throws Exception;

    /*修改部门文件夹*/
    ResultData updateDepartmentfolder(DepartmentFolder departmentFolder)throws Exception;

    /*建初始文件夹*/
    ResultData addDepartmentParentFolder(Department department)throws Exception;

    /*新建非初始文件夹*/
    ResultData addDepartmentFolder(DepartmentFolder departmentFolder)throws Exception;

    /*根据当前的登录人查询所有的部门文件*/
    Map<String, Object> selectAllDepartmentFolder(String departmentFolder_id)throws Exception;

    /*根据当前的登录人查询所有的部门文件夹*/
    ResultData selectAllDepartmentFolderForTree(String departmentFolder_id)throws Exception;

    /*搜索部门文件夹及文件*/
    Map<String, Object> searchDepartmentFolder(String index, String folder_id)throws Exception;

    void downloadFolderAndFileZip(List<Download> list) throws IOException;
}