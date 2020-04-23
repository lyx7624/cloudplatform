package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.ProjectQualityInspection;
import com.zcyk.pojo.ProjectQualityInspectionPic;
import com.zcyk.pojo.ProjectQualityPlan;
import com.zcyk.pojo.ProjectQualityPlanDetails;

import java.util.List;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2020/1/2 10:43
 */
public interface ProjectQualityService {


    /**
     * 功能描述：新增/修改记录
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:49
     * 参数：
     * 返回值：
     * 异常：
     */
    String updateQuality(ProjectQualityInspection projectQualityInspection) throws Exception;


    /**
     * 功能描述：新增/修改 图片
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    void updateQualityPic(List<ProjectQualityInspectionPic> picPaths, String projectQualityId) throws Exception;

    /**
     * 功能描述：删除质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    void deleteQuality(String id);

    /**
     * 功能描述：根据id获取质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    ProjectQualityInspection selectQuality(String id) throws Exception;

    /**
     * 功能描述：根据i项目获取所有质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    PageInfo<ProjectQualityInspection> selectQualityByProject(int pageNum, int pageSize, String search, String project_id) throws Exception;


    /**
     * 功能描述：获取质量问题的图片
     * 开发人员： lyx
     * 创建时间： 2020/1/2 11:29
     * 参数：
     * 返回值：
     * 异常：
     */
    List<ProjectQualityInspectionPic> getQualityPic(String quality_id) throws Exception;

    /**
     * 功能描述：完成整改
     * 开发人员： lyx
     * 创建时间： 2020/1/2 11:29
     * 参数：
     * 返回值：
     * 异常：
     */
    void finishDesignAlteration(String id)throws Exception;


    //巡检计划

    /**
     * 功能描述：新增或者修改巡检计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    String updateQualityPlan(ProjectQualityPlan qualityPlans)throws Exception;
    /**
     * 功能描述：新增或者修改
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    void updateQualityPlanDetails(List<ProjectQualityPlanDetails> planDetails,String plan_id)throws Exception;

    /**
     * 功能描述：查询所有计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    PageInfo<ProjectQualityPlan> selectQualityPlanByProject(int pageNum, int pageSize, String search, String project_id)throws Exception;
    /**
     * 功能描述：查询计划详情
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    ProjectQualityPlan selectQualityPlanDetails(String plan_id)throws Exception;

    /**
     * 功能描述：删除计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    void removeQualityPlanDetails(String plan_id)throws Exception;
    /**
     * 功能描述：删除计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    void removeQualityPlan(String plan_id)throws Exception;
    /**
     * 功能描述：质量问题查询计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    ProjectQualityInspection selectQualityByDetails(String id);
}