package com.zcyk.service;


import com.zcyk.pojo.CompanyPublicfolder;
import com.zcyk.dto.Download;
import com.zcyk.dto.ResultData;

import java.util.List;
import java.util.Map;

public interface CompanyfolderService {
    /*新建企业文件夹*/
    ResultData addCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception;

    /*新建顶级文件夹*/
    void addTopCompanyfolder(String company_id);

    /*修改企业文件夹*/
    ResultData updateCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception;

    /*删除企业文件夹*/
    ResultData deleteCompanyfolder(List<String> ids, String folder_id)throws Exception;

    /**获取文件夹及文件**/
    Map<String, Object> Companyfolder(String companyfolder_id)throws Exception;

    /**获取文件夹**/
    List<CompanyPublicfolder> CompanyfolderForTree(String companyfolder_id)throws Exception;

    /*搜索企业文件夹及文件*/
    Map<String, Object> searchUserFolder(String index, String folder_id)throws Exception;

    void companyFolderdownload(List<Download> list) throws Exception;
}
