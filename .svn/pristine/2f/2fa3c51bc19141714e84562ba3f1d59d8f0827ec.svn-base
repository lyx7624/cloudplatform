package com.zcyk.service;


import com.zcyk.pojo.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* 功能描述: 筑智建的账号和机构相关
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/30 13:47
*/
public interface CQBIMService {


    /**
     * 功能描述：用户新增
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String userSave(String user_account, Project project) throws IOException ;


    /**
     * 功能描述：添加机构
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String institueSave(Company company) throws IOException ;

    /**	新增 工程接口
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String projectSaveorUpdate(Project project) throws IOException ;

    /**	新增单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String projectSaveorUpdate(Project project, TUnitproject tUnitproject) throws IOException ;

    /**
     * 功能描述：机构绑定人员
     *
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：他们机构根据用户名登录，我们采用账号--电话号码
     * 返回值：
     * 异常：
     */
     String institute_user(String company_id, String project_id) throws IOException ;


    /**
     * 功能描述：上传BIM模型到建委
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    String UpBimFile(Map<String,String> map) throws Exception;

    /**
     * 功能描述：上传场布模型到建委
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    String UpSiteLayoutFile(Map<String,String> map) throws  Exception;

    /*判断是不是vip*/
    BimVip isVip(String project_id);

    /*vip激活*/
    void activateVip(String account_id);

    /*购买vip*/
    String buyVip(String project_id);

    /*同步项目和单位工程*/
    String synProject(String id) throws Exception;

    List<BimVip> getAllVipBim(String company_id);
}
