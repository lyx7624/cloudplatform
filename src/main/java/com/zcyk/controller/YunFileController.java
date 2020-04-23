package com.zcyk.controller;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.YunFileModelTrees;
import com.zcyk.exception.YunFileException;
import com.zcyk.exception.ZZJException;
import com.zcyk.pojo.*;
import com.zcyk.dto.YunFileModelTable;
import com.zcyk.service.CompanyService;
import com.zcyk.service.ProjectService;
import com.zcyk.service.UserService;
import com.zcyk.service.YunFileService;
import com.zcyk.dto.ResultData;
import com.zcyk.dto.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/11/12 15:47
 */
@RestController
@RequestMapping("/yunFile")
@Slf4j
public class YunFileController {
    @Autowired
    YunFileService yunFileService;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    /*
    * 1.点击是否有vip
    *   1.1有
    *       一个 直接进入
    *       多个 选择资料员进入 不同的资料员看到的项目是不是要不一致
    *   1.2没有（暂时有）
    *       购买
    *
    *
    * 应该在弄一个拦截器每次登入都需要判断用户是否具有vip且有效
    *   暂时先设置成最后一次进入
    *
    * 如果项目么有单位工程，同步了也不能在云资料软件上显示
    * */


    /**
     * 功能描述：同步项目
     * 开发人员： lyx
     * 创建时间： 2020/3/13 10:05
     */
    String sycProject(List<Project> allYunProject, String user_id) throws Exception {
        String msg = "";
        Project project = null;
        for (int i = allYunProject.size() - 1; i >= 0; i--) {
            project = allYunProject.get(i);
            String s = yunFileService.projectSaveorUpdate(project);
            if ("201".equals(JSONObject.parseObject(s).getString("code"))) {
                //同时同步单位工程
                List<TUnitproject> tUnitprojects = projectService.getUnitproject(project.getId());
                for (int j = 0; j < tUnitprojects.size(); j++) {
                    //单位工程同步到云资料
                    String s1 = yunFileService.projectSaveorUpdate(project, tUnitprojects.get(j),user_id);
                    if ("201".equals(JSONObject.parseObject(s1).getString("code"))) {

                    } else {
                        log.error("单位更新云资料失败（筑业）：" + JSONObject.parseObject(s1).getString("abnorm"));
//                        throw new YunFileException("单位更新到筑智建失败（筑智建）");
                        msg += "\t" + tUnitprojects.get(i).getName() + "单位更新云资料失败：" + JSONObject.parseObject(s1).getString("abnorm")+"\n";
                    }
                }
            } else {
                log.error("项目工程同步到云资料失败（筑业）：" + JSONObject.parseObject(s).getString("abnorm"));
//                        throw new YunFileException("单位更新到筑智建失败（筑智建）");
                msg += project.getProject_name() + "项目工程同步到云资料失败：" + JSONObject.parseObject(s).getString("abnorm")+"\n";
            }
        }
        return msg;
    }

    /**
     * 功能描述：获取企业有效账号 后面可以吧要过期或者已过期的显示出
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("getVip")
    public ResultData getCompanyVip(HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        User nowUser = userService.getNowUser(request);
        return resultData.setData(yunFileService.getYunFileVip(nowUser.getCompany_id()));
    }

    /**
     * 功能描述：选择资料员
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     */
    @RequestMapping("login")
    public ResultData vipLogin(YunfileVip yunfileVip) throws Exception {
        ResultData resultData = new ResultData();
        YunfileVip thisVip = yunFileService.loginUser(yunfileVip);
        if(thisVip == null){
            return resultData.setMsg("请购买会员服务").setStatus("400");
        }
//        if(!thisVip.getPassword().equals(yunfileVip.getPassword())){
//            //过期让前端显示
//            return resultData.setMsg("密码错误").setStatus("400");
//        }
        return resultData.setData(thisVip);
    }

    /**
     * 功能描述：查看 企业-用户 云资料项目
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("getYunFileProject")
    public ResultData getYunFileProject(HttpServletRequest request,
                                        @NotNull String user_account,
                                        @RequestParam(defaultValue="") String search) throws Exception {
        ResultData resultData = new ResultData();
        User nowUser = userService.getNowUser(request);
        List<Project> allProject = projectService.getAllProject(nowUser.getCompany_id());

        List<Project> yunProject = allProject.stream().filter(projectModel ->
                projectModel.getYunfile_account()!=null &&
                    user_account.equals(projectModel.getYunfile_account()) &&//找出这个资料员的项目
                        projectModel.getProject_name().contains(search)//搜索
        ).collect(Collectors.toList());
        return resultData.setData(yunProject);
    }

    /**
     * 功能描述：查看 企业-用户 未同步云资料项目
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("getUnYunFileProject")
    public ResultData getUnYunFileProject(HttpServletRequest request, String user_account) throws Exception {
        ResultData resultData = new ResultData();
        User nowUser = userService.getUserByAccount(user_account);
        List<Project> allProject = projectService.getAllProject(nowUser.getCompany_id());
        List<Project> unYunProject = allProject.stream().filter(projectModel ->
                        StringUtils.isBlank(projectModel.getYunfile_account())
        ).collect(Collectors.toList());
        return resultData.setData(unYunProject);
    }

    /**
     * 功能描述：添加项目同步云资料
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/addProjectToYun")
    public ResultData addYunFileProject(@RequestBody PageData pageData) throws Exception {
//        User nowUser = userService.getNowUser(request);
        // 全部测试项目放在王家国的账号下面 173账号的公司下面
        //获取该公司下面的账号。或者以选取的形式
        String user_account = (String) pageData.get("user_account");
        List<String> project_ids =(List) pageData.get("project_ids");
        User vipUser = userService.getUserByAccount(user_account);
        ResultData resultData = new ResultData().setData(user_account);
        List<Project> projects = new ArrayList<>();
        String msg = "";
        for (int i = project_ids.size() - 1; i >= 0; i--) {
            Project project = projectService.getProjectById(project_ids.get(i));
            try {
                yunFileService.addProjectToYun(project_ids.get(i),user_account);//我们服务器设置
                projects.add(project);
            }catch (Exception e){
                msg+= project.getProject_name()+"项目添加到云资料失败（请重新添加）\n";
                log.error("添加项目到云资料失败(本地)"+e.getMessage());
            }
        }
        //这里会出现，同步到我们数据库成功，筑业那边没有
        msg += sycProject(projects,vipUser.getId());//云资料设置
        if (StringUtils.isNotBlank(msg)) {//项目同步失败但是不影响运行
            resultData.setStatus("202").setMsg(msg+"\n请稍后手动更新");
        }
        return resultData;
    }



    /**
     * 功能描述：同步项目到云资料
     *
     * 注意：没有单位工程筑业云软件不能选择该工程
     * 开发人员： lyx
     * 创建时间： 2020-4-21 16:46
     * 参数： [user_account, request]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("updateProjects")
    public ResultData synProjectToYun(String user_account,HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        User vipUser = userService.getUserByAccount(user_account);
        //获取项目
        List<Project> allYunProject = projectService.getAllProject(vipUser.getCompany_id())
                .stream().filter(projectModel ->
                        projectModel.getYunfile_account()!=null &&
                            user_account.equals( projectModel.getYunfile_account())).collect(Collectors.toList());
        /*同步项目*/
        try {
            String msg = sycProject(allYunProject,vipUser.getId());
            if (StringUtils.isNotBlank(msg)) {//项目同步失败但是不影响运行
                resultData.setStatus("202").setMsg(msg);
            }
            }catch (Exception e){
                log.error("云资料同步失败：",e);
                resultData.setStatus("400").setMsg("云资料同步错误（筑业）,请稍后重试");
            }
        return resultData;
    }

    /**
     * 功能描述：购买vip
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:48
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("toBeVip")
    public ResultData buyVip(User user,HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        //判断前面输入的账号是不是在系统中  不注入到我们系统防止这个账号在其他平台
        User userByAccount = userService.getUserByAccount(user.getUser_account());
        if(userByAccount==null){
            return resultData.setMsg("非本公司用户").setStatus("400");
        }

        //可以购买多个账号的话要判断还账号是否还在使用
        YunfileVip oneFileVip = yunFileService.getOneFileVip(user.getUser_account());
        if(oneFileVip.getStatus() == 1){
            return resultData.setMsg("该用户已购买").setStatus("400");
        }
        //1.付费

        //2.企业注册到云资料  判断企业是否已经注册过
        User nowUser = userService.getNowUser(request);
        List<YunfileVip> companyVip = yunFileService.getYunFileVip(nowUser.getCompany_id());
        if(companyVip.size() == 0){//说明企业没有注册过了
            //本地注册 应该要筑业授权  先这样坐着
            // 区分续费！！！！！
            yunFileService.addYunFileVip(new YunfileVip()
                    .setAccount(user.getUser_account())
                    .setCompany_id(nowUser.getCompany_id())
                    .setPassword(user.getUser_password())
                    .setStatus(1).setBuy_date(System.currentTimeMillis()).setDue_date(System.currentTimeMillis()+1440000*30));//一个月vip

            Company company = companyService.getCompany(nowUser.getCompany_id());//筑业注册
            String s = yunFileService.institueSave(company);
            if(!"201".equals(JSONObject.parseObject(s).getString("code"))){
                log.error("企业关联云资料失败："+JSONObject.parseObject(s).getString("abnorm"));
                throw new YunFileException("企业关联云资料失败");
            }else {
                //将这个用户注册
                String s1 = yunFileService.userSave(user);
                if(!"201".equals(JSONObject.parseObject(s).getString("code"))){
                    log.error("用户关联云资料失败："+JSONObject.parseObject(s1).getString("abnorm"));
                    throw new YunFileException("用户关联云资料失败");
                }else {
                    //用户机构关联
                    String s2 = yunFileService.institute_user(company.getCompany_id(),user.getUser_account());
                    if(!"201".equals(JSONObject.parseObject(s).getString("code"))){
                        log.error("用户关联公司失败（云资料）："+JSONObject.parseObject(s2).getString("abnorm"));
                        throw new YunFileException("用户关联公司失败（云资料）");
                    }
                }
            }
        }
        return resultData;

    }









    /**
     * 功能描述：获取树结构
     * 开发人员： lyx
     * 创建时间： 2019/11/12 16:45
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getFileTree")
    public ResultData getFileTree(String project_id) throws Exception {
        ResultData rd = new ResultData();
        List<YunFileModelTrees> modelTree = null;
        try {
            modelTree = YunFileModelTrees.getModelTree(yunFileService.getTree(project_id));
        }catch (Exception e){
            log.error("获取云资料树结构",e);
            throw new ZZJException("筑智建服务连接失败，请稍后重试");
        }
        return rd.setMsg("成功获取数据").setStatus("200").setData(modelTree);
    }


    /**
     * 功能描述：获取表格
     * 开发人员： lyx
     * 创建时间： 2019/11/12 16:45
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getFileTable")
    public ResultData getFileTable(String modulenodeid) throws Exception {
        ResultData rd = new ResultData();
        ArrayList<Map<String, Object>> allTable = new ArrayList<>();
        String javaJson = null;
        try {
            javaJson = StringEscapeUtils.unescapeJava(yunFileService.getTable(modulenodeid));//去除转义
        }catch (Exception e){
            log.error("筑智建获取表格",e);
            throw new ZZJException("筑智建服务连接失败，请稍后重试");
        }

        List<YunFileModelTable> yunFileModelTables = JSONObject.parseArray(javaJson, YunFileModelTable.class);
        Map<String, List<YunFileModelTable>> treeMap = yunFileModelTables.stream().collect(Collectors.groupingBy(YunFileModelTable::getF_unitprojectid));
        for (String key : treeMap.keySet()) {
            Map<String, Object> map = new HashMap<>();
            TUnitproject tUnitproject = projectService.selectUnitprojectById(key);
            map.put("unitProjectName", tUnitproject == null ? "未知单位工程" : tUnitproject.getName());
            map.put("tables", treeMap.get(key));
            allTable.add(map);

        }
        return rd.setMsg("成功获取数据").setStatus("200").setData(allTable);
    }


    /**
     * 功能描述：获取pdf
     * 开发人员： lyx
     * 创建时间： 2019/11/12 16:45
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/getFilePDF")
    public ResultData getFilePDF(String projectnodeid) throws Exception {
        ResultData rd = new ResultData();
        String PDFurl = null;
        try {
            PDFurl = yunFileService.getPDF(projectnodeid);
        }catch (Exception e){
            log.error("筑智建获取pdf",e);
            throw new ZZJException("筑智建服务连接失败，请稍后重试");
        }
        return rd.setMsg("成功获取数据").setStatus("200").setData(PDFurl);
    }




}


