package com.zcyk.controller;

import com.zcyk.dto.ResultData;
import com.zcyk.pojo.ProjectComponent;
import com.zcyk.pojo.ProjectQualityInspection;
import com.zcyk.pojo.ProjectQualityPlan;
import com.zcyk.service.ProjectQualityService;
import com.zcyk.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2020/5/19 15:56
 */
@RestController
@RequestMapping("/bim")
public class TestController {

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectQualityService projectQualityService;


    /**
     * 功能描述：获取质量和安全问题数量
     * 开发人员： lyx
     * 创建时间： 2020/5/19 9:50
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getDetails")
    public ResultData getDetails(String project_id) throws Exception {
        List<Map<String, Integer>> list = new ArrayList<>();
        Map<String, Integer> qualityMap = new HashMap<>();
        Map<String, Integer> securityMap = new HashMap<>();

        List<ProjectQualityPlan> projectQualityPlans =  projectQualityService.getProjectQualityInspection(project_id);
        //获取所有巡检计划
        qualityMap.put("plan",projectQualityPlans.size());
        securityMap.put("plan",projectQualityPlans.size());

        List<ProjectQualityInspection> projectQualityInspections = projectQualityService.getQualityByProjectId(project_id);

        List<ProjectQualityInspection> quality = projectQualityInspections.stream().filter(inspections -> "质量问题".equals(inspections.getProblem_type())).collect(Collectors.toList());
        List<ProjectQualityInspection> security = projectQualityInspections.stream().filter(inspections -> "安全问题".equals(inspections.getProblem_type())).collect(Collectors.toList());
        //问题总数
        qualityMap.put("problem",quality.size());
        securityMap.put("problem",security.size());



        int qualityNum = quality.stream().filter(inspections -> inspections.getStatus() == 2).collect(Collectors.toList()).size();
        int securityNum = security.stream().filter(inspections -> inspections.getStatus() == 2).collect(Collectors.toList()).size();
        //完成整改
        qualityMap.put("alter",qualityNum);
        securityMap.put("alter",securityNum);

        //合格数 = 完成整改
        qualityMap.put("qualified",qualityNum);
        securityMap.put("qualified",securityNum);

        int unQualityNum = quality.stream().filter(inspections -> inspections.getStatus() == 1).collect(Collectors.toList()).size();
        int unScurityNum = security.stream().filter(inspections ->  inspections.getStatus() == 1).collect(Collectors.toList()).size();

        //未完成整改
        qualityMap.put("unAlter",unQualityNum);
        securityMap.put("unAlter",unScurityNum);


        list.add(securityMap);
        list.add(qualityMap);

        return ResultData.WRITE("200","成功获取",list);
    }

    /**
     * 功能描述：根据id查询项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    @RequestMapping("/getProjectByid")
    public ResultData getProjectByid(String id) throws Exception {
        //没有判断是否是已被删除
        return new ResultData().setMsg("成功").setStatus("200").setData(projectService.getProjectById(id));

    }


    /**
     * 功能描述：根据项目获取所有构件
     * 开发人员： lyx
     * 创建时间： 2020/5/19 9:50
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getProjectComponent")
    public ResultData getProjectComponent(String project_id,@RequestParam(defaultValue = "") String search,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) throws Exception {
        return ResultData.WRITE("200","成功获取",projectService.getProjectComponent(project_id,pageNum,pageSize));
    }

    /*2020-05-19  临时   质量问题  安全问题*/
}