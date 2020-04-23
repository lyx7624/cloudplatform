package com.zcyk.service;

import com.zcyk.dto.Download;
import com.zcyk.pojo.UserFolder;
import com.zcyk.dto.ResultData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserfolderService {
    /*查询个人文件夹及文件*/
    Map<String, Object> Alluserfolder(String userfolder_id)throws Exception;
    /*查询个人文件夹*/
    List<UserFolder> AlluserfolderForTree(String userfolder_id)throws Exception;
    /*新建个人文件夹*/
    ResultData addUserfolder(UserFolder userFolder)throws Exception;
    /*删除个人文件夹*/
    ResultData deleteUserfolder(List<String> ids, String folder_id)throws Exception;
    /*修改个人文件夹*/
    ResultData updateUserfolder(UserFolder userFolder)throws Exception;
    /*新建顶级个人文件夹*/
    ResultData addUserTopfolder(String user_id)throws Exception;

    /*查询用户文件夹*/
    UserFolder selectUserfolderByParentId(String user_id)throws Exception;

    /**
     * 批量下载文件夹和文件，压缩zip并返回
     */
    void userFolderdownload(List<Download> list) throws IOException;

    /**/
    Map<String,Object> searchUserFolder(String index, String folder_id);
    /*查询个人云盘大小*/


}
