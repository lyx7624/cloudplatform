package com.zcyk.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.*;
import com.zcyk.service.FileService;
import com.zcyk.service.ProjectService;
import com.zcyk.service.ProjectQualityService;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
import com.zcyk.dto.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述: 项目质量
 * 开发人员: lyx
 * 创建日期: 2019/12/30 16:55
 */
@RestController
public class ProjectQualityController {

    @Autowired
    UserService userService;

    @Autowired
    ProjectQualityService projectQualityService;

    @Value("${contextPath}")
    String absolutePath;

    @Autowired
    ProjectService projectService;

    @Autowired
    FileService fileService;

    //质量问题
    /**
     * 功能描述：上传巡检图
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/uploadQualityPic")
    public ResultData uploadDesignPic(MultipartFile file) throws Exception {
        ResultData resultData = new ResultData();
        try {
            return resultData.setMsg("上传成功").setData(fileService.upFileToServer(file, absolutePath, null));
        }catch (Exception e){
            return resultData.setMsg("上传失败").setStatus("400");
        }

    }

    /**
     * 根据路径获取图片
     */
    @RequestMapping(value = "/project/quality/pic/{path:.+}")
    public void getImage(@PathVariable String path, HttpServletResponse response) throws Exception {
        fileService.getImage(absolutePath+path, response);
    }

    /**
     * 功能描述：新增或者修改质量问题
     * 创建时间： 2020/1/2 10:42
     * 开发人员： lyx
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/updateQuality")
    @Transactional(rollbackFor = Exception.class)
    public ResultData updateQuality(@RequestBody PageData pageData) throws Exception {
        ResultData resultData = new ResultData();
        ProjectDynamic projectDynamic = new ProjectDynamic();
        ProjectQualityInspection projectQualityInspection = JSON.parseObject(JSON.toJSONString(pageData.get("projectQualityInspection")), ProjectQualityInspection.class);
        if(StringUtils.isBlank(projectQualityInspection.getId())){
            resultData.setMsg("新增问题记录成功");
        }else {
            resultData.setMsg("修改问题记录成功");
        }
        String projectQualityId = projectQualityService.updateQuality(projectQualityInspection);
        projectQualityService.updateQualityPic(projectQualityInspection.getPicPaths(),projectQualityId);

        //插入工程动态 问题
        projectDynamic.setRecord_id(projectQualityInspection.getId()).setType(1).setTitle("质量问题-"+projectQualityInspection.getToponym()+"-"+projectQualityInspection.getProject_name());
        projectService.updateProjectDynamic(projectDynamic);

        return resultData;
    }

    /**
     * 功能描述：巡检记录完成整改
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:42
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/finishQuality")
    public ResultData finishQuality(String id, HttpServletRequest request) throws Exception {
        User user = userService.getNowUser(request);
        String informant = projectQualityService.selectQuality(id).getInformant();
        if(informant.equals(user.getUser_name())){//瞎搞
            projectQualityService.finishDesignAlteration(id);
            return  new ResultData().setMsg("操作成功").setStatus("200");
        }else {
            return  new ResultData().setMsg("非填报人不能完成整改").setStatus("400");
        }
    }

    /**
     * 功能描述：删除巡检记录
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/removeQuality")
    public ResultData removeQuality(String id){
        projectQualityService.deleteQuality(id);
        return new ResultData().setMsg("删除问题记录成功").setStatus("200");
    }

    /**
     * 功能描述：根据项目查询所有的巡检记录
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/getProjectQuality")
    public ResultData getProjectQuality(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String search,
            @RequestParam String project_id) throws Exception {

        PageInfo<ProjectQualityInspection> projectQualityInspections = projectQualityService.selectQualityByProject(pageNum,pageSize,search,project_id);
        return new ResultData().setMsg("操作成功").setStatus("200").setData(projectQualityInspections);
    }

    /**
     * 功能描述：查询单个问题
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/getQuality")
    public ResultData getQuality(String id,String quality_plan_details_id) throws Exception {
        ProjectQualityInspection projectQualityInspection = null;
        if(StringUtils.isNotBlank(quality_plan_details_id)){
            projectQualityInspection = projectQualityService.selectQualityByDetails(quality_plan_details_id);//质量问题跳转
        }else {
            projectQualityInspection = projectQualityService.selectQuality(id);
        }
        List<ProjectQualityInspectionPic> pics = projectQualityService.getQualityPic(projectQualityInspection.getId());
        projectQualityInspection.setPicPaths(pics);
        return new ResultData().setMsg("操作成功").setStatus("200").setData(projectQualityInspection);
    }


    //#################################巡检计划

    /**
     * 功能描述：新增巡检计划
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/updateQualityPlan")
    public ResultData updateQualityPlan(@RequestBody PageData pageData) throws Exception {
        ResultData resultData = new ResultData();
        ProjectDynamic projectDynamic = new ProjectDynamic();
        ProjectQualityPlan qualityPlans = JSON.parseObject(JSON.toJSONString(pageData.get("qualityPlan")), ProjectQualityPlan.class);
        if(StringUtils.isBlank(qualityPlans.getId())){
            resultData.setMsg("新增巡检计划成功");
        }else {
            resultData.setMsg("修改巡检计划成功");
        }
        //添加，修改巡检计划
        String plan_id = projectQualityService.updateQualityPlan(qualityPlans);
        //添加或修改巡检计划详情
        projectQualityService.updateQualityPlanDetails(qualityPlans.getPlanDetails(),plan_id);

        //插入工程动态
        projectDynamic.setType(3).setRecord_id(qualityPlans.getId()).setTitle("巡检计划-"+qualityPlans.getPlan_name()+"-"+qualityPlans.getProject_name());
        projectService.updateProjectDynamic(projectDynamic);
        return resultData;
    }

    /**
     * 功能描述：获取项目巡检记录
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/getProjectQualityPlan")
    public ResultData getProjectQualityPlan(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String search,
            @RequestParam String project_id) throws Exception {

        PageInfo<ProjectQualityPlan> pageInfo = projectQualityService.selectQualityPlanByProject(pageNum,pageSize,search,project_id);
        return new ResultData().setData(pageInfo).setStatus("200").setMsg("成功获取");
    }

    /**
     * 功能描述：获取项目单个巡检记录
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/getQualityPlanDetails")
    public ResultData getQualityPlanDetails(String plan_id) throws Exception {
        ProjectQualityPlan projectQualityPlans = projectQualityService.selectQualityPlanDetails(plan_id);
        return new ResultData().setMsg("成功获取").setData(projectQualityPlans).setStatus("200");
    }

    /**
     * 功能描述：删除项目单个巡检记录
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/removeQualityPlanDetails")
    public ResultData removeQualityPlanDetails(String plan_details_id) throws Exception {
        projectQualityService.removeQualityPlanDetails(plan_details_id);
        return new ResultData().setMsg("删除记录成功").setStatus("200");
    }
    /**
     * 功能描述：删除项目巡检计划
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectquality/removeQualityPlan")
    public ResultData removeQualityPlan(String plan_id) throws Exception {
        projectQualityService.removeQualityPlan(plan_id);

        return new ResultData().setMsg("删除计划成功").setStatus("200");

    }

}