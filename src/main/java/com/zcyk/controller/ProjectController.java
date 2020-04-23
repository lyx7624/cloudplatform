package com.zcyk.controller;

import com.alibaba.fastjson.JSON;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.github.pagehelper.PageInfo;
import com.zcyk.dto.PageData;
import com.zcyk.dto.ResultData;
import com.zcyk.mapper.UploadFileMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.*;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述:项目管理模块
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/10/8 15:12
 */
@RestController
@RequestMapping("/projectModel")
@Slf4j
public class ProjectController {
    @Value("${contextPath}")
    String contextPath;

    @Value("${rvtPath}")
    String rvtPath;

    @Autowired
    ProjectService projectService;


    @Autowired
    SubProjectRecord subProjectRecord;

    @Autowired
    FileService fileService;


    @Autowired
    ProjectDesignService projectDesignService;

    @Autowired
    ProjectQualityService projectQualityService;

    @Autowired
    ObjectMapper mapper;


    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UploadFileMapper uploadFileMapper;


    /**
     * lyx 修改
     * 功能描述：修改项目
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 9:30
     * 参数：[ 项目id ]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateProjectName")
    public ResultData upudateProject(Project project) throws Exception {
        ResultData resultData = new ResultData("200", "修改成功", null);
        if (projectService.judeUser(project.getId())) {
            resultData = projectService.updateProject(project);

        } else {
            return resultData.setStatus("402").setMsg("没有权限");
        }
        return resultData;
    }


    /**
     * 功能描述：用于上传图片
     * 开发人员： lyx
     * 创建时间： 2019/10/12 9:37
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/upFileToProject")
    public ResultData upFileToProject(MultipartFile file, Project project) throws Exception {
        String url = fileService.upFileToServer(file, contextPath, null);
        Project projectModel = projectService.getProjectById(project.getId());
        //生成缩略图
        if (projectModel.getModel_pic() == null && projectModel.getIs_bim() == 1) {
            String originalFilename = file.getOriginalFilename();
            originalFilename = originalFilename.substring(originalFilename.lastIndexOf("\\") == -1 ? 0 : originalFilename.lastIndexOf("\\") + 1);
            BufferedImage img = PicZoom.zoom(contextPath + url);
            ImageIO.write(img, originalFilename.substring(originalFilename.lastIndexOf(".") + 1), new java.io.File(contextPath + url));
            ResultData resultData = upudateProject(project.setPic_url(url).setModel_pic(url));
            return resultData.setData(URLEncoder.encode(url, "utf-8"));
        }
        ResultData resultData = upudateProject(project.setPic_url(url));
        return resultData.setData(URLEncoder.encode(url, "utf-8"));

    }


    /**
     * 功能描述：添加项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:39
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/addProject")
    public ResultData addProject(Project project) throws Exception {
        ResultData resultData = new ResultData("200", "添加成功", null);
        resultData = projectService.addProject(project);
        return resultData;
    }


    /**
     * 功能描述：查询当前的登录人的公司的项目（加入的项目）
     * 开发人员： lyx
     * 创建时间： 2019/9/29 15:42
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getAllProject")
    public ResultData getAllProject
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search) throws Exception {
        ResultData resultData = new ResultData();
        resultData.setData(projectService.getAllProject(pageNum, pageSize, search)).setStatus("200").setMsg("成功");

        return resultData;
    }


    /**
     * 功能描述：根据时间阶段查询项目
     * 开发人员： lyx
     * 创建时间： 2019/9/29 16:46
     * 参数：时间范围
     */
    @RequestMapping("/getProjectByTime")
    public ResultData getProjectByTime(@RequestParam(defaultValue = "1000-01-01") String startTime, @RequestParam(defaultValue = "5000-01-01") String endTime) throws Exception {
        ResultData resultData = new ResultData();
        HashMap<String, Integer> map = new HashMap<>();
        List<Project> projects = projectService.getProjectByTime(startTime, endTime);
        //统计总数
        map.put("count", projects.size());
        //统计是bim数量
        List<Project> conutBim = projects.stream().filter(Project -> Project.getIs_bim() == 1).collect(Collectors.toList());
        map.put("isBim", conutBim.size());
        //统计已完工
        List<Project> countStatus = projects.stream().filter(Project -> Project.getProject_status() == 2).collect(Collectors.toList());
        map.put("finish", countStatus.size());
        return resultData.setData(map).setStatus("200").setMsg("成功");
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
     * 功能描述：根据id删除项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    @RequestMapping("/delProjectByid")
    @Transactional(rollbackFor = Exception.class)
    public ResultData delProjectByid(String id) throws Exception {
        ResultData resultData = new ResultData();
        if (!projectService.judeUser(id)) {
            return resultData.setStatus("403").setMsg("没有权限");
        }
        /*删除所有工程记录、资料、单位工程*/
        List<TUnitproject> tUnitprojects = projectService.getUnitproject(id);
        for (int i = 0; i < tUnitprojects.size(); i++) {
            delUnitproject(tUnitprojects.get(i).getId());
        }

        //删除用户角色
        projectService.removeUserByProject(id);

        //删除工程
        projectService.removeProjectById(id);


        return resultData.setMsg("成功").setStatus("200");
    }


    /**
     * 功能描述：查询所有的工程动态
     * 开发人员： lyx
     * 创建时间： 2020/1/2 15:00
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getAllProjectDynamic")
    public ResultData getAllProjectDynamic(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) throws Exception {
        User nowUser = userService.getNowUser(request);
        PageInfo<ProjectDynamic> projectDynamics = projectService.getAllProjectDynamic(pageNum, pageSize, search, nowUser.getCompany_id());
        return new ResultData().setData(projectDynamics).setStatus("200").setMsg("成功");

    }

    /**
     * 功能描述：查询yige 工程动态
     * 开发人员： lyx
     * 创建时间： 2020/1/2 15:00
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getOneProjectDynamic")
    public ResultData getOneProjectDynamic(ProjectDynamic projectDynamic) throws Exception {
        ResultData resultData = new ResultData();
        switch (projectDynamic.getType()) {
            case 1://质量问题
                ProjectQualityInspection projectQualityInspection = projectQualityService.selectQuality(projectDynamic.getRecord_id());
                if (projectQualityInspection == null) {
                    return resultData.setStatus("400").setStatus("该记录已被清除");
                }
                List<ProjectQualityInspectionPic> pics = projectQualityService.getQualityPic(projectQualityInspection.getId());
                projectQualityInspection.setPicPaths(pics);
                resultData.setData(projectQualityInspection).setStatus("200");
                break;
            case 2://验收记录
                break;
            case 3://巡检计划
                ProjectQualityPlan projectQualityPlan = projectQualityService.selectQualityPlanDetails(projectDynamic.getRecord_id());
                if (projectQualityPlan == null) {
                    return resultData.setStatus("400").setStatus("该记录已被清除");
                }
                resultData.setData(projectQualityPlan).setStatus("200");
                break;

            case 4://设计变更
                ProjectDesignAlteration projectDesignAlteration = projectDesignService.selectDesignAlterationById(projectDynamic.getRecord_id());
                if (projectDesignAlteration.getId() == null) {
                    return resultData.setStatus("400").setStatus("该记录已被清除");
                }
                resultData.setData(projectDesignAlteration).setStatus("200");

                break;
            case 5://施工周期

                break;

        }
        return resultData;

    }
    //单位工程#####################################################################################

    /**
     * 功能描述：查询单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/8 16:07
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getUnitproject")
    public ResultData getUnitproject
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String project_id,
     @RequestParam(defaultValue = "") String search) throws Exception {
        ResultData resultData = new ResultData();
        resultData.setData(projectService.getUnitproject(pageNum, pageSize, search, project_id)).setStatus("200").setMsg("成功");
        return resultData;
    }

    /**
     * 功能描述：添加单位项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 10:40
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/addUnitproject")
    public ResultData addUnitproject(@RequestBody PageData pageData) throws Exception {
        ResultData rd = new ResultData();
        //父工程id
        String project_id = (String) pageData.get("project_id");
        //负责人姓名
        String charge_user = (String) pageData.get("charge_user");
        //负责人姓名
        String charge_phone = (String) pageData.get("charge_phone");
        //项目负责人岗位
        String user_post = (String) pageData.get("user_post");
        //获取到添加的项目
        Object unitProjects = pageData.get("unitprojects");//数组需要转换一下
        List<TUnitproject> projects = JSON.parseArray(JSON.toJSONString(unitProjects), TUnitproject.class);
        //将项目负责人循环到每一个项目中去
        projects.forEach(project -> project.setCharge_user(charge_user).setCharge_phone(charge_phone).setUser_post(user_post).setProject_id(project_id));

        HashMap<String, List<String>> resultData = projectService.addUnitproject(projects);
        //实现分布工程记录的添加
//        List<String> ids = resultData.get("ids");
//            ids.forEach(id->{
//                //新增单位工程添加验收记录四条，18个表格
//                subProjectRecord.addUnitprojectRecord(id);
//            });

        if (resultData.get("names").size() != 0) {
//         重名没有进去到添加       TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            rd.setMsg("部分项目重名未添加成功").setStatus("402").setData(resultData.get("names"));
        } else {
            rd.setMsg("添加成功").setStatus("200");
        }

        return rd;
    }


    /**
     * 功能描述：根据id删除单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    @RequestMapping("/delUnitproject")
    @Transactional(rollbackFor = Exception.class)
    public ResultData delUnitproject(String id) throws Exception {
        ResultData resultData = new ResultData();
        if (!projectService.judeUser(id)) {
            return resultData.setStatus("403").setMsg("没有权限");
        }
        //删除表格资料 2删除记录 1删除表格资料
        subProjectRecord.deleteByUnitprojectId(id);
        //删除单位工程
        projectService.removeUnitprojectById(id);
        return resultData.setMsg("成功").setStatus("200");

    }

    /**
     * 功能描述：根据id获取单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    @RequestMapping("/getUnitprojectById")
    public ResultData getUnitproject(String id) throws Exception {
        return new ResultData().setMsg("成功").setStatus("200").setData(projectService.selectUnitprojectById(id));
    }

    /**
     * 功能描述：根据id修改单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    @RequestMapping("/updUnitproject")
    public ResultData updUnitproject(TUnitproject tUnitproject) throws Exception {
        ResultData resultData = new ResultData();
        TUnitproject thistUnitproject = projectService.selectUnitprojectById(tUnitproject.getId());
        if (!projectService.judeUser(tUnitproject.getId())) {
            return resultData.setStatus("403").setMsg("没有权限");
        }

        Integer integer = projectService.updateUnitprojectById(tUnitproject);
        if (integer == -1) {//0失败没有报异常 不管 1.成功直接返回
            return resultData.setMsg("项目已存在").setStatus("402");
        }

        //给该项目添加文件夹
        //projectfolderService.addProjectParentFolder(project);
        return resultData.setMsg("成功").setStatus("200");

    }


    //验收记录######################################################################
    //验收记录显示

    /**
     * 功能描述：根据单位工程id显示验收记录
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:43
     * 参数：单位工程id
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getUnitprojectRecord")
    public ResultData showUnitprojectRecord(String id) throws Exception {
        ResultData resultData = new ResultData();
        try {
            List<TSubrecord> tSubrecords = subProjectRecord.selectRecordByUnitproject(id);
            return resultData.setMsg("成功").setStatus("200").setData(tSubrecords);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return resultData.setMsg("失败").setStatus("400");

        }
    }

    /**
     * 功能描述：上传文件到表格中
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:43
     * 参数：表格id
     * 返回值：
     * 异常：
     */
    @RequestMapping("/upRecordFile")
    public ResultData upRecordFile(MultipartFile file, String id) throws Exception {
        ResultData resultData = new ResultData();
        try {
            fileService.upFile(file, id,null);
            return resultData.setMsg("成功").setStatus("200");
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return resultData.setMsg("失败").setStatus("400");

        }
    }

    /**
     * 功能描述：从云资料上传文件到表格中
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:43
     * 参数：表格id
     * 返回值：
     * 异常：
     */
    @RequestMapping("upRecordFileByYun")
    public ResultData upRecordFileByYun(String id, File file) throws Exception {
        ResultData resultData = new ResultData();

        file.setFolder_id(id).setId(UUID.randomUUID().toString());
        subProjectRecord.insertFileByYun(file);
        return resultData.setMsg("成功").setStatus("200");

    }

    /**
     * 功能描述：根据表格查询文件
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:43
     * 参数：表格id
     * 返回值：
     * 异常：
     */
    @RequestMapping("showRecordTableFile")
    public ResultData showRecordTableFile(String id) throws Exception {
        List<File> files = subProjectRecord.selectTableFile(id);
        return new ResultData().setMsg("成功").setStatus("200").setData(files);

    }

    /**
     * 功能描述：删除表格下的文件
     * 开发人员： lyx
     * 创建时间： 2019/10/10 9:43
     * 参数：表格id 文件id
     * 返回值：
     * 异常：
     */
    @RequestMapping("delRecordTableFile")
    public ResultData delRecordTableFile(String tSubtable_id, String file_id) throws Exception {
        fileService.deletedFile(file_id, tSubtable_id);
        return new ResultData().setMsg("成功").setStatus("200");
    }

    //项目成员#####################################################################################

    /**
     * 功能描述：添加项目成员
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/9 16:37
     * 参数：[ 项目id：project_id，成员姓名：user_name，联系方式：user_phone，职位：status]
     * 返回值：Resultdata
     */
    @RequestMapping("/addUser")
    public ResultData addProjectModelUser(UserProjectRole userProjectRole) throws Exception {
        ResultData resultData = null;
        resultData = projectService.addProjectUser(userProjectRole);
        return resultData;
    }

    /**
     * 功能描述：查询各职位人数
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/9 17:00
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getCount")
    public ResultData getUserCount(String projectmodel_id) throws Exception {
        return new ResultData().setMsg("成功").setStatus("200").setData(projectService.getUserCount(projectmodel_id));
    }

    /**
     * 功能描述：查询某职位人员详细信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/9 17:28
     * 参数：[ project_id,status]
     * 返回值：
     */
    @RequestMapping("/getProjectModelUser")
    public ResultData getProjectModelUser(String projectmodel_id) throws Exception {
        return new ResultData().setMsg("成功").setStatus("200").setData(projectService.getProjectUser(projectmodel_id));
    }

    /**
     * 功能描述：删除项目成员
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/10 9:44
     * 参数：[ id]
     * 返回值：
     */
    @RequestMapping("/deleteProjectModelUser")
    public ResultData deleteProjectModelUser(String id) throws Exception {
        if (!projectService.judeUser(id)) {
            return new ResultData().setMsg("没有权限").setStatus("400");
        }
        return projectService.removeProjectUser(id);
    }


    /**
     * 功能描述：查询单位工程
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/21 14:52
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getUnitProject")
    public ResultData getUnitProject(String project_id) throws Exception {
        return projectService.getUnitProject(project_id);
    }

}

