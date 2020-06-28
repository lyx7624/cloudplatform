package com.zcyk.service;


import com.zcyk.pojo.*;

import java.io.IOException;
import java.util.List;

/**
* 功能描述: 云资料
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/30 13:47
*/
public interface YunFileService {

    /**
     * 功能描述：获取岗位信息
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    String getQuarters()throws Exception;


    /**
     * 功能描述：用户新增
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String userSave(User user) throws IOException ;
    /**
     * 功能描述：用户修改
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String userUpdate(User user,String quartersids,String projectid) throws IOException ;

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
     String projectSaveorUpdate(Project project) throws Exception;

    /**	新增单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
     String projectSaveorUpdate(Project project, TUnitproject tUnitproject, String user_id) throws IOException ;

    /**
     * 功能描述：机构绑定人员
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：他们机构根据用户名登录，我们采用账号--电话号码
     * 返回值：
     * 异常：
     */
     String institute_user(String company_id, String user_account) throws IOException ;
    /**
     * 功能描述：单位工程绑定人员
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：他们机构根据用户名登录，我们采用账号--电话号码
     * 返回值：
     * 异常：
     */
     String unitproject_user(String user_account, String tUnitproject_id) throws Exception ;



    /**
     * 功能描述：根据项目获取模板树
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
     String getTree(String projectModelId) throws Exception;


    /**
     * 功能描述：根据项目表格
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
    String getTable(String modulenodeid) throws Exception;

    /**
     * 功能描述：根据表格pdf
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
    String getPDF(String projectnodeid) throws Exception;

    /**
     * 功能描述：查看该企业vip用户
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    List<YunfileVip> getYunFileVip(String company_id);

    YunfileVip getOneFileVip(String account);
    /**
     * 功能描述：添加企业vip用户
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    void addYunFileVip(YunfileVip yunfileVip);

    /**
     * 功能描述：查看企业云资料项目
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    void addProjectToYun(String project_id,String yunfile_account);


    /**
     * 功能描述：vip登录
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    YunfileVip loginUser(YunfileVip yunfileVip);
}



//zzj