package com.zcyk.service;

import com.zcyk.dto.Download;
import com.zcyk.pojo.*;
import com.zcyk.dto.ResultData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service

public interface ProjectfolderService {
    /*添加项目父文件夹*/
    void addProjectParentFolder(Project project, String user_id)throws Exception;
    /*判断用户权限*/
    boolean jugle(User user,String id,int type);
    /*根据父ID查询父文件夹*/
    List<ProjectFolder> getProjectParentFolder(String project_id);
    /*获取文件夹*/
    ProjectFolder getProjectFolder(String folder_id);
    /*在当前目录下新建文件夹*/
    ResultData addProjectFolder(ProjectFolder projectFolder,User user)throws Exception;

    /*根据id文件夹及文件*/
    Map<String, Object> getProjectFolderAndFileById(String projectfolder_id)throws Exception;

    /*获取用户所在项目文件夹*/
    List<ProjectFolder> getProjectFolderByUser(String user_account);

    /*删除项目文件夹*/
    ResultData deleteProjectFolder(List<String> ids, String folder_id,User user)throws Exception;

    boolean deleteFolder(String id)throws Exception;

    /*修改项目文件夹*/
    ResultData updateProjectFolder(ProjectFolder projectFolder)throws Exception;

    /*搜索项目文件夹及文件*/
    Map<String, Object> searchProjectFolder(String index, String folder_id, User user)throws Exception;


    void downloadFolderAndFileZip(List<Download> list) throws IOException;
}
