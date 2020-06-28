package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.Company;
import com.zcyk.pojo.User;
import com.zcyk.dto.ResultData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 企业注册接口
 */
public interface CompanyService {
    /*企业注册*/
    ResultData addCompany(Company company,HttpServletResponse response)throws Exception;

    /*修改企业*/
    ResultData updateCompany(Company company)throws Exception;

    /*注销企业*/
    ResultData deleteCompany(Company company, String code)throws Exception;

    /*查询企业信息*/
    ResultData demandCompany(String company_id)throws Exception;

    /*修改企业联系电话*/
    ResultData updateCompany_phonenum(Company company, String code)throws Exception;

    /*上传企业logo*/
    ResultData uploadCompany_logo(MultipartFile file)throws Exception;

    /*获取上传的图片*/
    void getImage(String path, HttpServletResponse response) throws Exception;

    /*查询公司所有人*/
    List<User> selectAllUser(String company_id)throws Exception;

    /*设置是否为企业管理员*/
    ResultData setCompanyManager(String user_id, String status)throws Exception;

    /*邀请个人进企业*/
    ResultData inviteOneUser(User user)throws Exception;

    /*批量邀请进公司*/
    Map<String, Object> inviteUserToCompany(MultipartFile file) throws Exception;

    /*移除公司人员*/
    ResultData rmCompanyUser(String user_id)throws Exception;

    /*搜索企业人员*/
    PageInfo<User> searchCompanyUser(int pageNum, int pageSize, String index, HttpServletRequest request)throws Exception;


    /*移交管理员*/
    ResultData turnOverManager(String user_id)throws Exception;

    /*获取所有公司名称*/
    List<String> getAllCompany()throws Exception;


    /*根据id查询公司*/
    Company getCompany(String company_id);
}
