package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.ProjectQualityService;
import com.zcyk.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2020/1/2 10:43
 */
@Service
public class ProjectQualityServiceImpl implements ProjectQualityService {

    @Autowired
    ProjectQualityInspectionMapper qualityMapper;

    @Autowired
    ProjectQualityInspectionPicMapper qualityPicMapper;

    @Autowired
    ProjectDynamicMapper projectDynamicMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectQualityPlanDetailsMapper qualityPlanDetailsMapper;

    @Autowired
    ProjectQualityPlanMapper qualityPlanMapper;

    @Autowired
    UserService userService;

    /**
     * 功能描述：新增/修改记录
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:49
     * 参数：
     * 返回值：
     * 异常：
     */
    public String updateQuality(ProjectQualityInspection projectQualityInspection){

        if(StringUtils.isNotBlank(projectQualityInspection.getId())){
            qualityMapper.updateByPrimaryKeySelective(projectQualityInspection);
        }else {


            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
            int count = qualityMapper.selectDateCount(now.format(format),projectQualityInspection.getProject_id());
            long l = Long.parseLong(now.format(format) + "0000")+count+1;
            projectQualityInspection.setId(UUID.randomUUID().toString()).setStatus(1).setInspection_number(String.valueOf(l)).setAppointed_date(new Date());
            qualityMapper.insertSelective(projectQualityInspection);


            //是否关联巡检问题
            if(StringUtils.isNotBlank(projectQualityInspection.getQuality_plan_details_id())){
                qualityPlanDetailsMapper.updateStatus(projectQualityInspection.getQuality_plan_details_id(),2);
            }





        }
        return projectQualityInspection.getId();
    }


    /**
     * 功能描述：新增/修改 图片
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    public void updateQualityPic(List<ProjectQualityInspectionPic> picPaths, String projectQualityId){

        //删除以前的图片
        qualityPicMapper.deleteByprojectQualityId(projectQualityId);
        ProjectQualityInspectionPic projectQualityPic = new ProjectQualityInspectionPic();
        projectQualityPic.setProject_quality_inspection_id(projectQualityId);
        picPaths.forEach(p-> qualityPicMapper.insertSelective(projectQualityPic.setPic_url(p.getPic_url()).setPic_name(p.getPic_name())));
    }

    /**
     * 功能描述：删除质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    public void deleteQuality(String id){
        String plan_details_id = qualityMapper.selectQualityById(id).getQuality_plan_details_id();
        if (StringUtils.isNotBlank(plan_details_id)) {
            qualityPlanDetailsMapper.updateStatus(plan_details_id,1);//将计划设计为没有问题
        }
        qualityMapper.setQualityStatus(id,0);

        //删除这条工程动态
        projectDynamicMapper.updateStatus(id,0);
    }


    /**
     * 功能描述：根据id获取质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    public ProjectQualityInspection selectQuality(String id){
        return qualityMapper.selectQualityById(id);
    }

    /**
     * 功能描述：根据i项目获取所有质量问题
     * 开发人员： lyx
     * 创建时间： 2020/1/2 10:50
     * 参数：
     * 返回值：
     * 异常：
     */
    public PageInfo<ProjectQualityInspection> selectQualityByProject(int pageNum,int pageSize,String search,String project_id){
        PageHelper.startPage(pageNum,pageSize);
        List<ProjectQualityInspection> projectQualityInspections = qualityMapper.selectQualityByProjectId(project_id, search);
        PageInfo<ProjectQualityInspection> pageInfo = new PageInfo<>(projectQualityInspections);
        return pageInfo;
    }
    /**
     * 功能描述：获取质量问题的图片
     * 开发人员： lyx
     * 创建时间： 2020/1/2 11:29
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<ProjectQualityInspectionPic> getQualityPic(String quality_id){
        return qualityPicMapper.selectByQuality(quality_id);
    }
    /**
     * 功能描述：完成整改
     * 开发人员： lyx
     * 创建时间： 2020/1/2 11:29
     * 参数：
     * 返回值：
     * 异常：
     */
    public void finishDesignAlteration(String id){
        qualityMapper.setQualityStatus(id,2);
    }

    // ###############################巡检计划

    /**
     * 功能描述：新增或者修改巡检计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public String updateQualityPlan(ProjectQualityPlan qualityPlans){
        User user =userService.getNowUser(request);


        if(StringUtils.isNotBlank(qualityPlans.getId())){
            qualityPlanMapper.updateByPrimaryKeySelective(qualityPlans);
        }else {
            qualityPlans.setId(UUID.randomUUID().toString()).setStatus(1).setCreate_date(new Date()).setCreate_user(user.getUser_name());
            qualityPlanMapper.insertSelective(qualityPlans);
        }


        return qualityPlans.getId();
    }
    /**
     * 功能描述：新增/修改/删除计划详情
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public void updateQualityPlanDetails(List<ProjectQualityPlanDetails> planDetails, String plan_id){

//        qualityPlanDetailsMapper.deletePlanDetails

        StringBuffer ids = new StringBuffer();
        planDetails.forEach(detail->{
            if (StringUtils.isNotBlank(detail.getId())){
                qualityPlanDetailsMapper.updateByPrimaryKeySelective(detail);
            }else {
                detail.setId(UUID.randomUUID().toString()).setStatus(1).setCreate_date(new Date());
                qualityPlanDetailsMapper.insertSelective(detail.setQuality_plan_id(plan_id));
            }
            ids.append("'").append(detail.getId()).append("',");
        });

        //删除没有在这个数组类的详情 这样其实很影响效率
        List<String> list = qualityPlanDetailsMapper.selectArrStatus(ids.substring(0, ids.lastIndexOf(",")), plan_id);
        list.forEach(id-> removeQualityPlanDetails(id));



    }
    /**
     * 功能描述：查询所有计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public PageInfo<ProjectQualityPlan> selectQualityPlanByProject(int pageNum, int pageSize, String search, String project_id){
        PageHelper.startPage(pageNum, pageSize);
        List<ProjectQualityPlan> plans = qualityPlanMapper.selectByProject(search,project_id);
        PageInfo<ProjectQualityPlan> pageInfo = new PageInfo<>(plans);
        return pageInfo;
    }
    /**
     * 功能描述：查询计划详情
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public ProjectQualityPlan selectQualityPlanDetails(String plan_id){
        ProjectQualityPlan projectQualityPlan = qualityPlanMapper.selectById(plan_id);
        if(projectQualityPlan!=null){
            List<ProjectQualityPlanDetails> planDetails = qualityPlanDetailsMapper.selectByPlan(plan_id);
            projectQualityPlan.setPlanDetails(planDetails);
        }
        return projectQualityPlan;
    }

    /**
     * 功能描述：删除计划详情
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public void removeQualityPlanDetails(String plan_id){
        //删除质量问题详情
        qualityPlanDetailsMapper.updateStatus(plan_id,0);
        ProjectQualityInspection projectQualityInspection = qualityMapper.selectByDetailsId(plan_id);
        //删除质量问题动态详情
        if(projectQualityInspection!=null&&projectQualityInspection.getId()!=null)projectDynamicMapper.updateStatus(projectQualityInspection.getId(),0);
        //根据计划 删除质量问题
        qualityMapper.updateStatus(plan_id,0);

    }

    /**
     * 功能描述：删除计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public void removeQualityPlan(String plan_id) throws Exception{
        //获取计划详情
        List<ProjectQualityPlanDetails> planDetails = qualityPlanDetailsMapper.selectByPlan(plan_id);
        //删除详情 同时删除详情对应的问题
        for (int i = 0; i < planDetails.size(); i++) {
            if(planDetails.get(i).getId()!=null){
                removeQualityPlanDetails(planDetails.get(i).getId());
            }
        }
        //删除计划
        qualityPlanMapper.updateStatus(plan_id,0);
        //删除工程动态
        projectDynamicMapper.updateStatus(plan_id,0);



    }

    /**
     * 功能描述：质量问题查询计划
     * 开发人员： lyx
     * 创建时间： 2020/1/13 9:22
     * 参数：
     * 返回值：
     * 异常：
     */
    public ProjectQualityInspection selectQualityByDetails(String id) {
        return qualityMapper.selectByDetailsId(id);
    }

    /*临时*/

    @Override
    public List<ProjectQualityPlan> getProjectQualityInspection(String project_id) {
        return qualityPlanMapper.selectByProject("",project_id);
    }

    @Override
    public List<ProjectQualityInspection> getQualityByProjectId(String project_id) {
        return qualityMapper.selectQualityByProjectId(project_id, "");
    }

    /*临时*/

}