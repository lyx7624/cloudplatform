package com.zcyk.controller;

import com.alibaba.fastjson.JSON;
import com.zcyk.dto.PageData;
import com.zcyk.dto.ResultData;
import com.zcyk.pojo.*;
import com.zcyk.service.ProjectQualityService;
import com.zcyk.service.ProjectService;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 功能描述: 项目设计
 * 开发人员: lyx
 * 创建日期: 2019/12/30 16:55
 */
@Slf4j
@RestController
public class ProjectDesignController {

    @Autowired
    com.zcyk.service.FileService fileService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectQualityService projectQualityService;

    @Autowired
    com.zcyk.service.UserService userService;

    @Autowired
    com.zcyk.service.ProjectDesignService projectDesignService;



    @Value("${contextPath}")
    String absolutePath;
    /*临时文件路径*/
    String tempFilePath = "/temp";
    /*存放系统模板路径*/
    String templateFilePath = "/template";


    /**
     * 功能描述：上传设计图
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/uploadDesignPic")
    public ResultData uploadDesignPic(MultipartFile file) throws Exception {
        ResultData resultData = new ResultData();
        try {
            resultData.setData(fileService.upFileToServer(file,absolutePath,null));
        } catch (Exception e) {
            resultData.setMsg("上传失败").setStatus("400");
            log.error("上传设计图失败",e);
        }
        return resultData;
    }

    @RequestMapping(value = "/project/design/pic/{path:.+}")
    public void getImage(@PathVariable String path, HttpServletResponse response) throws IOException {
        fileService.getImage(absolutePath+path, response);
    }

    /**
     * 功能描述：新增、修改设计变更
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/addDesignAlteration")
    @Transactional(rollbackFor = Exception.class)
    public ResultData addDesignAlteration(@RequestBody PageData pageData) throws Exception{
        ResultData resultData = new ResultData();
        ProjectDynamic projectDynamic = new ProjectDynamic();
        ProjectDesignAlteration projectDesignAlteration = JSON.parseObject(JSON.toJSONString(pageData.get("projectDesignAlteration")), com.zcyk.pojo.ProjectDesignAlteration.class);
        if(StringUtils.isBlank(projectDesignAlteration.getId())){
            resultData.setMsg("新增设计变更成功");
        }else {
            resultData.setMsg("修改设计变更成功");
        }
       List<ProjectDesignAlterationPic> pics = JSON.parseArray(JSON.toJSONString(pageData.get("picPaths")), com.zcyk.pojo.ProjectDesignAlterationPic.class);
        List<ProjectDesignAlterationOpinion> opinions = JSON.parseArray(JSON.toJSONString(pageData.get("opinions")), com.zcyk.pojo.ProjectDesignAlterationOpinion.class);
        String designAlterationId = projectDesignService.updateDesignAlteration(projectDesignAlteration);
        //没有做验证projectDesignService
        ProjectDesignAlteration pda = projectDesignService.selectDesignAlterationById(designAlterationId);
        if(pda.getStatus() != 2){//变更记录没有已解决才能修改图片和意见
            projectDesignService.updateDesignAlterationPic(pics,designAlterationId);
            projectDesignService.updateDesignAlterationOpinions(opinions,designAlterationId);
        }
        //插入动态
        projectDynamic.setRecord_id(projectDesignAlteration.getId()).setType(4).setTitle("设计变更-" + projectDesignAlteration.getTags() + "-" + projectDesignAlteration.getProject_name()  );
        projectService.updateProjectDynamic(projectDynamic);
        return resultData;
    }

    /**
     * 功能描述：提交回复
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/addDesignReply")
    @Transactional
    public ResultData addDesignReply(com.zcyk.pojo.ProjectDesignAlterationOpinion opinion) throws Exception {
        ResultData resultData = new ResultData();
        ProjectDynamic projectDynamic = new ProjectDynamic();
        //判断这个记录是不是已经不用处理
       ProjectDesignAlteration projectDesignAlteration = projectDesignService.selectDesignAlterationById(opinion.getProject_design_alteration_id());
        if(projectDesignAlteration.getStatus()==2){
            return resultData.setStatus("400").setMsg("变更问题已解决，无法添加新的意见");
        }
        projectDesignService.addDesignReply(opinion);

        //动态 修改
        projectService.updateProjectDynamic(projectDynamic.setRecord_id(opinion.getProject_design_alteration_id())
        .setTitle("设计变更-"+projectDesignAlteration.getTags()+"-"+projectDesignAlteration.getProject_name()));


        resultData.setMsg("回复成功").setStatus("200");

        return resultData;
    }


    /**
     * 功能描述：删除设计变更
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/removeDesignAlteration")
    public ResultData removeDesignAlteration(String id) throws Exception {
        projectDesignService.deleteDesignAlteration(id);
        return new ResultData().setMsg("删除变更记录成功").setStatus("200");
    }

    /**
     * 功能描述：查看
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/getDesignAlteration")
    public ResultData getDesignAlteration(String id) throws Exception {
        ProjectDesignAlteration map = projectDesignService.selectDesignAlterationById(id);
        return new ResultData().setMsg("操作成功").setStatus("200").setData(map);
    }

    /**
     * 功能描述：查询设计变更
     * 开发人员： lyx
     * 创建时间： 2019/12/30 17:09
     * 参数：projecDesignAlteration 项目反馈记录
     * 返回值：search 搜索框，project_id项目id， significance重要程度 ，special_name专项名称
     * 异常：
     */
    @RequestMapping("/projectdesign/queryDesignAlteration")
    public com.zcyk.dto.ResultData queryDesignAlteration
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search,
     @RequestParam(defaultValue = "") String special_name,
     @RequestParam String project_id,
     @RequestParam(defaultValue = "") String significance) throws Exception {
        ResultData resultData = new ResultData();
        if(StringUtils.isNotBlank(special_name)){//进入专项名称中
            resultData.setData(projectDesignService.selectDesignAlteration(pageNum,pageSize,search,special_name,project_id,significance));
        }else {
            resultData.setData(projectDesignService.selectDesignAlteration(pageNum,pageSize,search,project_id));
        }
        return resultData.setMsg("查询记录成功").setStatus("200");
    }


    /**
     * 功能描述：d导出一个项目的变更记录表
     * 开发人员： lyx
     * 创建时间： 2019/12/31 16:39
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/exportDesignAlterationWord")
    public ResultData exportDesignAlterationWord(HttpServletResponse response, HttpServletRequest request, String project_id) throws Exception {
        ResultData resultData = new ResultData();
        List<Map<String, Object>> table1 = new ArrayList<>();
        Map<String, Object> wordDataMap = new HashMap<>();

        String project_name = projectService.getProjectById(project_id).getProject_name();
        //基本数据
        List<com.zcyk.pojo.ProjectDesignAlteration> projectDesignAlterations = projectDesignService.selectDesignAlterationByProjectId(project_id);
        for (int i = 0;i<projectDesignAlterations.size();i++) {
            com.zcyk.pojo.ProjectDesignAlteration pda = projectDesignAlterations.get(i);
            pda.setProject_name(project_name);
            Map<String, Object> map = pda.toMap();
            //图片集合
            List<ProjectDesignAlterationPic> picPaths = projectDesignService.selectDesignAlterationPic(pda.getId());
            //意见集合
            List<ProjectDesignAlterationOpinion> opinions = projectDesignService.selectDesignAlterationOpinions(pda.getId());
            map.put("picPaths",picPaths);
            map.put("opinions",opinions);
            table1.add(map);

        }

        wordDataMap.put("table1",table1);
        //生成文件
        String name = System.currentTimeMillis()+project_name+".docx";
        String filePath = absolutePath+tempFilePath+name;
        File outputFile = new File(filePath);

        // 读取word模板                              ProjectDesignAlterationWordTemplate.docx
        File file = new File(absolutePath+templateFilePath+"/ProjectDesignAlterationWordTemplate.docx");
        FileInputStream fileInputStream = new FileInputStream(file);
        WordTemplate template = new WordTemplate(fileInputStream,absolutePath);
        template.replaceDocument(wordDataMap);
        FileOutputStream fos = new FileOutputStream(outputFile);
        template.getDocument().write(fos);
        //生成目录
        template.generateTOC(filePath);

        //下载
        File_download.downloadFile(filePath,response,request);
        fos.close();
        fileInputStream.close();

        boolean delete = outputFile.delete();
        if (!delete) {
            log.info("{}，文件未删除",name);
        }
        return resultData;

    }


    /**
     * 功能描述：导出意见整合
     * 开发人员： lyx
     * 创建时间： 2020/1/2 9:33
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/projectdesign/exportDesignAlterationOpinions")
    public ResultData exportDesignAlterationOpinions(HttpServletResponse response, HttpServletRequest request, String project_id) throws Exception {
        List<ProjectDesignAlteration> projectDesignAlterations = projectDesignService.selectDesignAlterationByProjectId(project_id);
        ResultData resultData = new com.zcyk.dto.ResultData();
        Project project = projectService.getProjectById(project_id);
        User user = userService.getNowUser(request);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> cls = new HashMap<>();
        Map<String, Object> ops = new HashMap<>();
        data.put("cls", cls);
        data.put("ops", ops);
        cls.put("project_name", project.getProject_name());
        cls.put("recorder", user.getUser_name());
        cls.put("date", now.format(format));

        for (int i = 0; i < projectDesignAlterations.size(); i++) {
            com.zcyk.pojo.ProjectDesignAlteration projectDesignAlteration = projectDesignAlterations.get(i);
            //意见集合
            List<com.zcyk.pojo.ProjectDesignAlterationOpinion> opinions =
                    projectDesignService.selectDesignAlterationOpinions(projectDesignAlteration.getId());
            projectDesignAlteration.setNumOrder(i+1).setOpinions(opinions);
        }

        cls.put("projectFeedbacks", projectDesignAlterations);
        String name = System.currentTimeMillis() + project.getProject_name() + "意见整理.xlsx";
        String filePath = absolutePath+tempFilePath+name;
        String templatePath = absolutePath+templateFilePath+"/ProjectDesignAlterationOpinionTemplate.xlsx";
        File file = new File(filePath);
        FileOutputStream  fos = new FileOutputStream(filePath);
        //根据模板 templatePath 和数据 data 生成 excel 文件，写入 fos 流
        ExcelTemplate.process(data, templatePath, fos);
        //下载
        File_download.downloadFile(filePath,response,request);
        fos.close();

        //删除文件
        boolean delete = file.delete();
        if (!delete) {
            log.info("{}，文件未删除",name);
        }
        return resultData;
    }

}