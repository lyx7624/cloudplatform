package com.zcyk.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.Download;
import com.zcyk.pojo.*;
import com.zcyk.dto.ResultData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述: 项目模块
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/10/8 15:56
 */

public interface ProjectService {

    /*判断用户权限*/
    boolean judeUser(String id)throws Exception;

    /*修改项目*/
    ResultData updateProject(Project project)throws Exception;

    /*添加项目*/
    ResultData addProject(Project project)throws Exception;

    /*查询项目*/
    PageInfo<Project> getAllProject(int pageNum, int pageSize, String search)throws Exception;

    /*根据企业id*/
    List<Project> getAllProject(String company_id)throws Exception;

    /*根据时间范围查询项目*/
    List<Project> getProjectByTime(String startTime, String endTime)throws Exception;

    /*根据id查询项目 所有状态*/
    Project getProjectById(String id)throws Exception;

    /*根据id删除项目*/
    Integer removeProjectById(String id)throws Exception;

//    void removeProjectUser();

    //单位工程########################################################


    /*查询单位工程*/
    PageInfo<TUnitproject> getUnitproject(int pageNum, int pageSize, String search, String project_id)throws Exception;
    List<TUnitproject> getUnitproject(String project_id)throws Exception;
    /*添加单位工程*/
    HashMap<String, List<String>> addUnitproject(List<TUnitproject> projects)throws Exception;

    /*根据删除单位工程*/
    Integer removeUnitprojectById(String id)throws Exception;

    /*根据项目id删除单位工程*/
    Integer deleteUnitprojectByProject(String id)throws Exception;

    /*修改单位工程*/
    Integer updateUnitprojectById(TUnitproject tUnitproject)throws Exception;

    /*根据id获取到单位工程*/
    TUnitproject selectUnitprojectById(String id)throws Exception;

    //单位工程记录########################################################



    //项目成员#####################################################################################
    /*添加项目成员*/
    ResultData addProjectUser(UserProjectRole userProjectRole)throws Exception;
    /*查询各职位人数*/
    Map<String,Object> getUserCount(String projectmodel_id)throws Exception;
    /*查询某职位成员详细信息*/
    Map<String, Object> getProjectUser(String projectmodel_id)throws Exception;
    /*删除项目成员*/
    ResultData removeProjectUser(String id)throws Exception;

    /*根据项目移除所有的人员*/
    void removeUserByProject(String id);
    /*用户修改电话号码，同时修改角色表*/
    void updateRoleAccount(String account);

    //BIM+#####################################################################################


    /*查询Bim项目信息*/
    Map<String,Object> getBimProject(int pageNum, int pageSize, Integer sort, String search) throws UnsupportedEncodingException;

    /*保存模型信息*/
    ResultData updateBimProject(Project project) throws Exception;

    /*更改项目BIM类型状态*/
    ResultData cancelBimProject(Project project)throws Exception;

    /*查询项目对应模型*/
    PageInfo<Model> getAllModel(String project_id,String search,int pageNum,int pageSize)throws Exception;

    /*新增模型*/
    ResultData addModel(Model model)throws Exception;

    /*删除模型*/
    ResultData deleteModel(String id)throws Exception;

    /*编辑模型信息*/
    ResultData updateModel(Model model)throws Exception;


    /*导入平面图*/
    ResultData addImage(MultipartFile file,String project_id) throws Exception;

    /*查看历史版本*/
    ResultData getVersion(String mid)throws Exception;

    /*恢复历史版本*/
    ResultData backVersion(String id)throws Exception;

    //模型中##################################################################


    //设计变更#################################################################
    /*新增设计变更记录*/
    ResultData addModelChange(Model_change model_change)throws Exception;

    /*编辑变更记录*/
    ResultData updateModelChange(Model_change model_change)throws Exception;

    /*删除变更记录*/
    ResultData deleteModelChange(List<String> ids)throws Exception;

    /*查询变更记录*/
    ResultData getModelChange(String mid,String search)throws Exception;

    /*下载变更记录*/
    void modelChangeDownload(List<Download> list) throws IOException;

    //造价管理##############################################################
    /*新增造价管理*/
    ResultData addModelCost(Model_cost model_cost)throws Exception;

    /*编辑造价管理*/
    ResultData updateModelCost(Model_cost model_cost)throws Exception;

    /*删除造价管理*/
    ResultData deleteModelCost(List<String> ids)throws Exception;

    /*查询造价管理*/
    ResultData getModelCost(String mid,String search)throws Exception;

    /*计算项目总价*/
    ResultData getModelPrice(String project_id)throws Exception;

    //质量追踪管理############################################################
    /*新增质量追踪*/
    ResultData addModelQuality(Model_quality model_quality)throws Exception;

    /*编辑质量追踪*/
    ResultData updateModelQuality(Model_quality model_quality)throws Exception;

    /*删除质量追踪*/
    ResultData deleteModelQuality(List<String> ids)throws Exception;

    /*查询质量追踪*/
    ResultData getModelQuality(String mid,String search)throws Exception;

    /*下载质量追踪文件*/
    void downloadModelQuality(List<Download> list) throws IOException;

    /*获取单位工程*/
    ResultData getUnitProject(String project_id)throws Exception;


    //###########工程动态
    /*查询工程动态*/
    PageInfo<ProjectDynamic> getAllProjectDynamic(int pageNum, int pageSize, String search, String company_id)throws Exception;

    /*新增修改工程动态*/
    void updateProjectDynamic(ProjectDynamic projectDynamic) throws Exception;


    /*获取用户所在的项目*/
    List<Project> getUserAllProject(String user_account);

    /*临时          项目构件   2020-05-19新加 */
    void updateComponent(ProjectComponent projectComponent);

    PageInfo<ProjectComponent> getProjectComponent(String project_id, int pageNum, int pageSize);

    void deleteProjectComponent(String id);


    /*临时          项目构件   2020-05-19新加 */
}
