package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.Download;
import com.zcyk.dto.ResultData;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.FileService;
import com.zcyk.service.ProjectService;
import com.zcyk.service.UserService;
import com.zcyk.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 功能描述: 项目模块
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/10/8 15:56
 */

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectModelMapper projectModelMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ProjectFolderMapper projectFolderMapper;



    @Autowired
    UserMapper userMapper;

    @Autowired
    TUnitprojectMapper unitprojectMapper;

    @Autowired
    TSubrecordMapper tSubrecordMapper;

    @Autowired
    TSubtableMapper tSubtableMapper;

    @Autowired
    UserProjectRoleMapper userProjectRoleMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelVersionMapper modelVersionMapper;

    @Autowired
    ModelChangeMapper modelChangeMapper;

    @Autowired
    ModelCostMapper modelCostMapper;

    @Autowired
    ModelQualityMapper modelQualityMapper;

    @Autowired
    ZZJFileResponseMapper zzjFileResponseMapper;


    @Autowired
    ProjectDynamicMapper projectDynamicMapper;

    @Autowired
    FileService fileService;

    @Autowired
    BimVipMapper bimVipMapper;



    @Autowired
    UserService userService;
    /**
     * 功能描述：查询工程动态
     * 开发人员： lyx
     * 创建时间： 2020/1/2 15:55
     * 参数：
     * 返回值：
     * 异常：
     */
    public  PageInfo<ProjectDynamic> getAllProjectDynamic(int pageNum, int pageSize, String search, String company_id) {
        PageHelper.startPage(pageNum,pageSize);
        List<ProjectDynamic> projectDynamics = projectDynamicMapper.selectByCompanyId(company_id,search);
        PageInfo<ProjectDynamic> pageInfo = new PageInfo<>(projectDynamics);
        return pageInfo;
    }


    public void updateProjectDynamic(ProjectDynamic projectDynamic) {
        User user = userService.getNowUser(request);

        //插入工程动态
        ProjectDynamic dynamic = projectDynamicMapper.selectByRecordId(projectDynamic.getRecord_id());
        if (dynamic == null) {//对象带有recordid type title  添加company_id create_date status
            //插入工程动态
            projectDynamicMapper.insert(projectDynamic.setStatus(1).setCreate_date(new Date()).setCompany_id(user.getCompany_id()));
        }else {//对象带有recordid  title  只需要更新时间获取id
            projectDynamicMapper.updateByPrimaryKeySelective(
                    projectDynamic.setCreate_date(new Date()).setId(dynamic.getId())
            );
        }
    }

    /**
     * 功能描述：判断用户能能不能执行删除
     * 开发人员： lyx
     * 创建时间： 2019/10/12 10:33
     * 参数： 单位工程id 项目id
     * 返回值：
     * 异常：
     */
    public boolean judeUser(String id){
        User nowUser =userService.getNowUser(request);
        //1是否是企业管理员
        if (nowUser.getIscompanymanager()==0){
            //是否是项目管理员
            Integer userProject = userProjectRoleMapper.selectRoleByPhoneAndProject(id, nowUser.getUser_account());
            if(userProject==null || userProject>4){//4以上的都不是管理人员
                return false;
            }
        }
        return true;
    }


    /**
     * 功能描述：修改项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:57
     * 参数：
     * 返回值：
     * 异常：
     */
    public ResultData updateProject(Project project){
        ResultData rd = new ResultData();
        User nowUser =userService.getNowUser(request);
        if(!judeUser(project.getId())){
            return rd.setStatus("402").setMsg("没有权限");
        }
        if(project.getProject_name()!=null
                &&  !project.getProject_name().equals(projectModelMapper.selectByPrimaryKey(project.getId()).getProject_name())){//修改
            if(projectModelMapper.selectProjectName(project.setCompany_id(nowUser.getCompany_id()))!=null){
                return new ResultData().setStatus("401").setMsg("项目名称不能重复");
            }

            //修改文件夹名
            projectFolderMapper.updateFolderName(new ProjectFolder()
                    .setFolder_updatetime(new Date()).setFolder_updateuser(nowUser.getUser_name())
                    .setParent_id(project.getId()).setFolder_name(project.getProject_name()));
        }

        if(project.getProject_code()!=null
                && !project.getProject_code().equals(projectModelMapper.selectByPrimaryKey(project.getId()).getProject_code())
                && projectModelMapper.selectProjectCode(project.setCompany_id(nowUser.getCompany_id()))!=null){
            return new ResultData().setStatus("401").setMsg("项目编码不能重复");

        }

        /*预计开工时间和结束时间能够为空*/
        if(project.getPlannedend_date()==null){
            projectModelMapper.updatePlannedend_dateToNull(project.getId());
        }
        if(project.getPlannedstart_date()==null){
            projectModelMapper.updatePlannedstart_dateToNull(project.getId());
        }
        projectModelMapper.updateByPrimaryKeySelective(project);

        return rd;
    }

    /**
     * 功能描述：新增项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:57
     * 参数：
     * 返回值：
     * 异常：
     */
    public ResultData addProject(Project project){
        ResultData rd = new ResultData();
        BigDecimal bg = new BigDecimal(1024*10*10);
        //获取当前登录用户
        User nowUser =userService.getNowUser(request);

        Project one = projectModelMapper.selectProjectName(project.setCompany_id(nowUser.getCompany_id()));
        if(project.getProject_name()!=null
                &&  projectModelMapper.selectProjectName(project.setCompany_id(nowUser.getCompany_id()))!=null){//修改
            return new ResultData().setStatus("401").setMsg("项目名称不能重复");
        }
        if(project.getProject_code()!=null
                && projectModelMapper.selectProjectCode(project.setCompany_id(nowUser.getCompany_id()))!=null){
            return new ResultData().setStatus("401").setMsg("项目编码不能重复");

        }
        if(one!=null)return rd.setMsg("项目已存在").setStatus("401");
        project.setId(UUID.randomUUID().toString())
                .setCompany_id(nowUser.getCompany_id())
                .setProject_createtime(new Date())
                .setProject_status(1)
                .setWorks(0)
                .setProject_capacity(bg)
                .setCreateuser_id(nowUser.getId())
                .setTransformStatus(0);
        //没有设置项目默认显示图片,应该是分开上传返回url
        String developer_unit = project.getDeveloper_unit();
        int i = projectModelMapper.insertSelective(project);
        if(i!=0){
            rd.setMsg("新增成功").setStatus("200").setData(project);

            /*把项目创建人添加到用户项目表并设为项目管理员*/
            UserProjectRole userProjectRole = new UserProjectRole();
            userProjectRole.setId(UUID.randomUUID().toString())
                    .setProject_id(project.getId()).setStatus(1)
                    .setRole(0).setUser_name(nowUser.getUser_name()).setUser_phone(nowUser.getUser_account());
            userProjectRoleMapper.insertSelective(userProjectRole);

            //生成项目激活码 所有项目都有
            bimVipMapper.insertSelective(new BimVip().setAccount_id(project.getId())
                    .setActivation_code(UUID.nameUUIDFromBytes(project.getId().getBytes()).toString())
                    .setStatus(0)
                    .setActivation_date(new Date()));

            //同时应该项目文件

            //给该项目添加文件夹
            ProjectFolder projectFolder = new ProjectFolder();
            projectFolder.setParent_id(project.getId())
                    .setFolder_createtime(new Date())
                    .setFolder_createuser(nowUser.getUser_name())
                    .setId(UUID.randomUUID().toString())
                    .setFolder_statu(1)
                    .setCreateuser_id(nowUser.getId())
                    .setFolder_name(project.getProject_name())
                    .setProject_id(project.getId());
            projectFolderMapper.insertSelective(projectFolder);
        }else{
            rd.setMsg("新增失败");
            rd.setStatus("400");
        }
        return rd;
    }

    /*获取用户所有项目*/
    public List<Project> getUserAllProject(String user_account) {
        return projectModelMapper.getUserAllProject(user_account);
    }

    /**
     * 功能描述：查询所有的工程，包括模糊查询（没有加入工程状态）
     * 开发人员： lyx
     * 创建时间： 2019/9/29 16:23
     * 参数：
     * 返回值：
     * 异常：
     */
    public PageInfo<Project> getAllProject(int pageNum, int pageSize, String search) {
        User nowUser =userService.getNowUser(request);

        PageHelper.startPage(pageNum,pageSize);
        List<Project> projects = projectModelMapper.selectAllProject(search, nowUser);
        PageInfo<Project> projectModelPageInfo = new PageInfo<>(projects);
        return projectModelPageInfo;
    }


    public List<Project> getAllProject(String company_id) {
        return projectModelMapper.getAllProject(company_id);
    }

    /**
     * 功能描述：查询所有的工程，根据时间
     * 开发人员： lyx
     * 创建时间： 2019/9/29 16:23
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<Project> getProjectByTime(String startTime, String endTime) {
        User nowUser =userService.getNowUser(request);
        return projectModelMapper.selectProjectByTime(startTime,endTime,nowUser.getCompany_id());
    }

    /**
     * 功能描述：根据id查询项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 16:01
     */
    public Project getProjectById(String id) {
        return projectModelMapper.selectByPrimaryKey(id);
    }

    /**
     * 功能描述：根据id删除项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 16:01
     */
    public Integer removeProjectById(String id) {
        return projectModelMapper.deleteProject(id);
    }





    //单位工程##########################################################################
    /**
     * 功能描述：查询单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/8 16:01
     */
    public PageInfo<TUnitproject> getUnitproject(int pageNum, int pageSize, String search, String project_id) {
        PageHelper.startPage(pageNum,pageSize);
        List<TUnitproject> projects = unitprojectMapper.selectUnitprojectByProject(search, project_id);
        PageInfo<TUnitproject> projectModelPageInfo = new PageInfo<>(projects);
        return projectModelPageInfo;
    }

    @Override
    public List<TUnitproject> getUnitproject(String id) {
        return unitprojectMapper.selectUnitprojectById(id);
    }

    /**
     * 功能描述：添加单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/8 11:12
     * 参数：
     * 返回值：
     * 异常：
     */
    public  HashMap<String, List<String>> addUnitproject(List<TUnitproject> projects) {
        ResultData rd = new ResultData();
        //获取当前登录用户
        User nowUser =userService.getNowUser(request);

        //重名单位工程列表
        List<String> names = new ArrayList<>();

        Project parentProject = projectModelMapper.selectByPrimaryKey(projects.get(0).getProject_id());//获取父项目
        //返回添加的单位工程id
        List<String> ids = new ArrayList<>();
        projects.forEach(project->{
            TUnitproject tUnitproject = unitprojectMapper.selectByProjectAndName(project.getName(), project.getProject_id());
            if (tUnitproject != null) {
                names.add(tUnitproject.getName());
                return;
            }
            String code = unitprojectMapper.selectMaxCodeByProject(project.getProject_id());
            if (code == null) {//没有子工程，添加第一个子工程编号
                code = parentProject.getProject_code()+"001";
            }else {
                String codePro=code.substring(0,code.length()-3);
                String num=(Long.parseLong(code.substring(code.length()-3))+1+"");
                if(num.length() ==1) {
                    num = "00" + num;
                    code = codePro + num;
                }else if(num.length()==2){
                    num = "0" + num;
                    code = codePro + num;
                }else if(num.length()==3){
                    code = codePro + num;
                }
            }
            //完整单位工程
            project.setCode(code)
                    .setId(UUID.randomUUID().toString())
                    .setStatus(1)
                    .setSupervising_unit(parentProject.getSupervising_unit())
                    .setSupervising_user(parentProject.getSupervising_user())
                    .setCreatetime(new Date());//是否设置开工日期，等UI设计

            unitprojectMapper.insertSelective(project);
            ids.add(project.getId());
        });
        HashMap<String, List<String>> stringListHashMap = new HashMap<>();
        stringListHashMap.put("names",names);
        stringListHashMap.put("ids",ids);
        return stringListHashMap;
    }


    /**
     * 功能描述：根据id删除单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    public Integer removeUnitprojectById(String id) {
        return unitprojectMapper.deleteUnitprojectByid(id);
    }


    /**
     * 功能描述：根据项目删除单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    public Integer deleteUnitprojectByProject(String id) {
        return unitprojectMapper.deleteUnitprojectByProject(id);
    }

    /**
     * 功能描述：根据id修改单位工程项目
     * 开发人员： lyx
     * 创建时间： 2019/10/8 15:59
     */
    public Integer updateUnitprojectById(TUnitproject tUnitproject) {
        TUnitproject tUnitproject1 = unitprojectMapper.selectByPrimaryKey(tUnitproject.getId());
        TUnitproject thistUnitproject = unitprojectMapper.selectByProjectAndName(tUnitproject.getName(),tUnitproject1.getProject_id());

        if (!tUnitproject1.getName().equals(tUnitproject.getName()) && thistUnitproject != null) {
            return -1;
        }
        return unitprojectMapper.updateByPrimaryKeySelective(tUnitproject);
    }


    /**
     * 功能描述：根据id获取到单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/10 13:48
     * 参数：
     * 返回值：
     * 异常：
     */
    public TUnitproject selectUnitprojectById(String id) {
        return unitprojectMapper.selectByPrimaryKey(id);
    }

    //单位工程记录########################################################





    //项目成员#####################################################################################

    /**
     * 功能描述：添加项目成员
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/9 16:52
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addProjectUser(UserProjectRole userProjectRole){
        userProjectRole.setId(UUID.randomUUID().toString()).setStatus(1);
        int i = userProjectRoleMapper.insertSelective(userProjectRole);
        if(i!=0){
            return new ResultData().setMsg("添加成功").setStatus("200");
        }else {
            return new ResultData().setMsg("添加失败").setStatus("400");
        }
    }

    /**
     * 功能描述：查询各职位人数
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/9 17:08
     * 参数：[ * @param null]
     * 返回值：
     */
    @Override
    public Map<String, Object> getUserCount(String projectmodel_id) {
        Map<String,Object> map = new HashMap<>();
        int i1 = userProjectRoleMapper.selectUserCount(projectmodel_id, 1);
        int i2 = userProjectRoleMapper.selectUserCount(projectmodel_id, 2);
        int i3 = userProjectRoleMapper.selectUserCount(projectmodel_id, 3);
        int i4 = userProjectRoleMapper.selectUserCount(projectmodel_id, 4);
        int i5 = userProjectRoleMapper.selectUserCount(projectmodel_id, 5);
        int i6 = userProjectRoleMapper.selectUserCount(projectmodel_id, 6);
        int i7 = userProjectRoleMapper.selectUserCount(projectmodel_id, 7);
        int i8 = userProjectRoleMapper.selectUserCount(projectmodel_id, 8);
        int i9 = userProjectRoleMapper.selectUserCount(projectmodel_id, 9);
        int i10 = userProjectRoleMapper.selectUserCount(projectmodel_id, 10);
        int i11 = userProjectRoleMapper.selectUserCount(projectmodel_id, 11);
        int i12 = userProjectRoleMapper.selectUserCount(projectmodel_id, 12);
        map.put("one",i1);
        map.put("two",i2);
        map.put("three",i3);
        map.put("four",i4);
        map.put("five",i5);
        map.put("six",i6);
        map.put("seven",i7);
        map.put("eight",i8);
        map.put("nine",i9);
        map.put("ten",i10);
        map.put("eleven",i11);
        map.put("twelve",i12);
        return map;
    }

    /**
     * 功能描述：查询某职位人员详细信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/10 9:29
     * 参数：[ * @param null]
     * 返回值：
     */
    @Override
    public Map<String, Object> getProjectUser(String projectmodel_id) {
        List<UserProjectRole> userProjectModels1 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 1);
        List<UserProjectRole> userProjectModels2 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 2);
        List<UserProjectRole> userProjectModels3 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 3);
        List<UserProjectRole> userProjectModels4 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 4);
        List<UserProjectRole> userProjectModels5 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 5);
        List<UserProjectRole> userProjectModels6 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 6);
        List<UserProjectRole> userProjectModels7 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 7);
        List<UserProjectRole> userProjectModels8 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 8);
        List<UserProjectRole> userProjectModels9 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 9);
        List<UserProjectRole> userProjectModels10 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 10);
        List<UserProjectRole> userProjectModels11 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 11);
        List<UserProjectRole> userProjectModels12 = userProjectRoleMapper.selectUserByRole(projectmodel_id, 12);
        Map<String,Object> map = new HashMap<>();
        map.put("one",userProjectModels1);
        map.put("two",userProjectModels2);
        map.put("three",userProjectModels3);
        map.put("four",userProjectModels4);
        map.put("five",userProjectModels5);
        map.put("six",userProjectModels6);
        map.put("seven",userProjectModels7);
        map.put("eight",userProjectModels8);
        map.put("nine",userProjectModels9);
        map.put("ten",userProjectModels10);
        map.put("eleven",userProjectModels11);
        map.put("twelve",userProjectModels12);
        return map;

    }

    /**
     * 功能描述：移除项目成员
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/10 9:46
     * 参数：[ id]
     * 返回值：
     */
    @Override
    public ResultData removeProjectUser(String id) {
        int i = userProjectRoleMapper.deleteUserById(id);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("删除成功");
        }else {
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
    }

    /**
    * 功能描述: 根据项目删除所有的角色
    * 版本信息: Copyright (c)2019
    * 公司信息: 智辰云科
    * 开发人员: lyx
    * 版本日志: 1.0
    * 创建日期: 2020/3/31 11:21
    */
    public void removeUserByProject(String id) {
        userProjectRoleMapper.deleteAllUser(id);
    }

    @Override
    public void updateRoleAccount(String account) {
        userProjectRoleMapper.updateRoleAccountByUser(account);
    }


    //BIM+#####################################################################################


    /**
     * 功能描述：用于防止以前的项目没有生成激活码
     * 开发人员： lyx
     * 创建时间： 2020/3/24 15:45
     * 参数： [company_id 公司id]
     * 返回值： void
     * 异常：
     */
    public void addProjectBimCode(String company_id){
        List<String> projectModels = projectModelMapper.selectNoBimCodeProject(company_id);
        projectModels.forEach(projectModel->{
            bimVipMapper.insertSelective(new BimVip().setAccount_id(projectModel)
                    .setActivation_code(UUID.nameUUIDFromBytes(projectModel.getBytes()).toString())
                    .setStatus(0)
                    .setActivation_date(new Date()));
        });
    }

    /**
     * 功能描述：查询Bim项目模型列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 14:50
     * 参数：[ * @param null]
     * 返回值：
     */
    @Override
    public Map<String,Object> getBimProject(int pageNum, int pageSize, Integer sort, String search) throws UnsupportedEncodingException {
        Map<String,Object> map = new HashMap<>();
//        PageInfo<Project> projectModelPageInfo = new PageInfo<>(projectModels);
        User user =userService.getNowUser(request);
        addProjectBimCode(user.getCompany_id());
        if(sort == 1){//默认时间降序排列
            PageHelper.startPage(pageNum,pageSize);
            List<Project> projects = projectModelMapper.selectBimProjectByTimeD(user.getCompany_id(), search);
            for(Project project : projects){
                if(project.getModel_pic()!=null) {
                    map.put(project.getId(), URLEncoder.encode(project.getModel_pic(), "utf-8"));
                }
            }
            PageHelper.startPage(pageNum,pageSize);
            PageInfo<Project> projectModelPageInfo = new PageInfo<>(projects);
            map.put("projectModelPageInfo",projectModelPageInfo);
            return map;
        }else if(sort == 2){//按时间升序排列
            PageHelper.startPage(pageNum,pageSize);
            List<Project> projects = projectModelMapper.selectBimProjectByTimeA(user.getCompany_id(), search);
            for(Project project : projects){
                if(project.getModel_pic()!=null) {
                    map.put(project.getId(), URLEncoder.encode(project.getModel_pic(), "utf-8"));
                }
            }
            PageInfo<Project> projectModelPageInfo = new PageInfo<>(projects);
            map.put("projectModelPageInfo",projectModelPageInfo);
            return map;
        }else if(sort == 3){//按进度升序排列
            PageHelper.startPage(pageNum,pageSize);
            List<Project> projects = projectModelMapper.selectBimProjectByWorksA(user.getCompany_id(), search);
            for(Project project : projects){
                if(project.getModel_pic()!=null) {
                    map.put(project.getId(), URLEncoder.encode(project.getModel_pic(), "utf-8"));
                }
            }
            PageInfo<Project> projectModelPageInfo = new PageInfo<>(projects);
            map.put("projectModelPageInfo",projectModelPageInfo);
            return map;
        }else if(sort == 4){//按进度降序排列
            PageHelper.startPage(pageNum,pageSize);
            List<Project> projects = projectModelMapper.selectBimProjectByWorksD(user.getCompany_id(), search);
            for(Project project : projects){
                if(project.getModel_pic()!=null) {
                    map.put(project.getId(), URLEncoder.encode(project.getModel_pic(), "utf-8"));
                }
            }
            PageInfo<Project> projectModelPageInfo = new PageInfo<>(projects);
            map.put("projectModelPageInfo",projectModelPageInfo);
            return map;
        }
        return null;
    }



    /**
     * 功能描述：保存Bim项目模型信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 15:49
     * 参数：[ id,模型id，进度，图片]
     * 返回值：
     */
    public ResultData updateBimProject(Project project){
        ResultData rd = new ResultData();
        int i = projectModelMapper.updateByPrimaryKeySelective(project);
        if(i != 0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 功能描述：更改项目BIM类型状态
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 16:07
     * 参数：[ id]
     * 返回值：
     */
    public ResultData cancelBimProject(Project project){
        ResultData rd = new ResultData();
        int i = projectModelMapper.cancelBimProject(project.getId());
        if(i != 0){
            rd.setMsg("成功");
            rd.setStatus("200");
        }else{
            rd.setStatus("400");
            rd.setMsg("失败");
        }
        return rd;
    }
    /**
     * 功能描述：查询项目模型（模糊查询【按模型名称搜索】）
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:23
     * 参数：[ * @param null]
     * 返回值：
     */
    public  PageInfo<Model> getAllModel(String project_id,String search,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Model> models = modelMapper.getModelByProject_id(project_id,search);
        models.forEach(model -> {
            if(model.getUnitproject_id()!=null) {
                String name = unitprojectMapper.selectByPrimaryKey(model.getUnitproject_id()).getName();
                model.setUnitproject_name(name);
            }
        });
        PageInfo<Model> ModelPageInfo = new PageInfo<>(models);
        return ModelPageInfo;
    }
    /**
     * 功能描述：查询单位工程
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/21 14:52
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getUnitProject(String project_id){
        List<TUnitproject> projects = unitprojectMapper.selectUnitprojectByProject("",project_id);
        return new ResultData().setStatus("200").setMsg("成功").setData(projects);
    }

    /**
     * 功能描述：新增项目模型
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:23
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addModel(Model model){
        User user =userService.getNowUser(request);

        model.setId(UUID.randomUUID().toString())
                .setCreate_time(new Date())
                .setCreate_user(user.getUser_name())
                .setStatu(1)
                .setVersion(new Date());
        int i = modelMapper.insertSelective(model);
        if(i!=0){
            Project project = projectModelMapper.selectByPrimaryKey(model.getProject_id());
            projectModelMapper.updateByPrimaryKeySelective(project.setTransformStatus(0).setId(model.getProject_id()));
            return new ResultData().setStatus("200").setMsg("新增成功");
        }
        return new ResultData().setStatus("400").setMsg("新增失败");
    }

    /**
     * 功能描述：删除项目模型
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:24
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteModel(String id){
        int i = modelMapper.deleteModel(id);
        List<Model_version> versionBymid = modelVersionMapper.getVersionBymid(id);
        if(versionBymid.size()!=0) {
            modelVersionMapper.deleteVersionByMid(id);
        }
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("删除成功");
        }
        return new ResultData().setStatus("400").setMsg("删除失败");
    }

    /**
     * 功能描述：编辑模型信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:24
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData updateModel(Model model){
        Model Omodel = modelMapper.selectByPrimaryKey(model.getId());
        if(Omodel.getModel_url()!=null && !Omodel.getModel_url().equals(model.getModel_url())){
            Project project = projectModelMapper.selectByPrimaryKey(Omodel.getProject_id());
            projectModelMapper.updateByPrimaryKeySelective(project.setTransformStatus(0).setId(project.getId()));
            model.setStatu(1);
        }
        int i = modelMapper.updateByPrimaryKeySelective(model);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("修改成功");
        }
        return new ResultData().setStatus("400").setMsg("修改失败");
    }




    @Value("${rvtPath}")
    String rvtPath;

    /**
     * 功能描述：查看历史版本
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/15 9:49
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getVersion(String mid){
        List<Model_version> versionBymid = modelVersionMapper.getVersionBymid(mid);
        return new ResultData().setStatus("200").setMsg("成功").setData(versionBymid);
    }

    /**
     * 功能描述：恢复历史版本
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/15 10:04
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData backVersion(String id){
        Model_version model_version = modelVersionMapper.selectByPrimaryKey(id);
        Model oldModel = modelMapper.selectByPrimaryKey(model_version.getMid());
        /*将当前版本 保存到版本表中*/
        Model_version nowModel_version = new Model_version().setId(UUID.randomUUID().toString())
                .setMid(oldModel.getId())
                .setVersion(oldModel.getVersion())
                .setModel_file_name(oldModel.getModel_file_name())
                .setModel_id(oldModel.getModel_id())
                .setModel_name(oldModel.getModel_name())
                .setAccess_key(oldModel.getAccess_key())
                .setModel_size(oldModel.getModel_size())
                .setModel_type(oldModel.getModel_type())
                .setCreate_time(oldModel.getCreate_time())
                .setModel_url(oldModel.getModel_url())
                .setCreate_user(oldModel.getCreate_user())
                .setProject_id(oldModel.getProject_id())
                .setStatu(oldModel.getStatu());
        modelVersionMapper.insert(nowModel_version);


        /*替换模型表的版本数据*/
        int i = modelMapper.updateByPrimaryKeySelective(new Model().setId(model_version.getMid())
                .setModel_file_name(model_version.getModel_file_name())
                .setModel_url(model_version.getModel_url())
                .setVersion(model_version.getVersion())
                .setCreate_time(model_version.getCreate_time())
                .setCreate_user(model_version.getCreate_user())
                .setModel_size(model_version.getModel_size())
                .setStatu(model_version.getStatu())
                .setAccess_key(model_version.getAccess_key())
                .setModel_id(model_version.getModel_id())
                .setModel_name(model_version.getModel_name())
                .setModel_type(model_version.getModel_type())
                .setProject_id(model_version.getProject_id()));
        Model model = modelMapper.selectByPrimaryKey(model_version.getMid());

        /*版本库中删除 当前版本信息*/
        modelVersionMapper.deleteByPrimaryKey(id);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("成功").setData(model);
        }
        return new ResultData().setStatus("400").setMsg("失败");
    }
    /**
     * 功能描述：删除历史版本
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/15 14:32
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteVersion(String id){
        int i = modelVersionMapper.deleteVersionById(id);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("删除成功");
        }
        return new ResultData().setStatus("400").setMsg("删除失败");
    }

    /**
     * 功能描述：导入平面图
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/21 14:01
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addImage(MultipartFile file,String project_id) throws Exception {
        String fileName = file.getOriginalFilename();
        String type = fileName.substring(fileName.lastIndexOf("."));
        String fileName1 = UUID.randomUUID().toString().replaceAll("\\-", "");
        User user =userService.getNowUser(request);

        if(!fileName.substring(fileName.lastIndexOf(".")+1).equals("pdf")){
            return new ResultData().setStatus("401").setMsg("导入失败，只能导入PDF文件！");
        }
        String url = contextPath+ fileService.upFileToServer(file, contextPath, fileName + type);

        //计算文件大小 单位（MB）
        BigDecimal fileSize = new BigDecimal(file.getSize());
        BigDecimal a = new BigDecimal(1048576);
        BigDecimal Mb = fileSize.divide(a,1,ROUND_HALF_UP);

        Model model = new Model().setId(UUID.randomUUID().toString())
                .setModel_type(6)
                .setCreate_time(new Date())
                .setCreate_user(user.getUser_name())
                .setStatu(1)
                .setModel_url(url)
                .setModel_size(Mb)
                .setModel_file_name(fileName)
                .setModel_name(fileName.substring(0,fileName.lastIndexOf(".")))
                .setProject_id(project_id);
        int i = modelMapper.insert(model);
        if(i!=0) {
            return new ResultData().setStatus("200").setMsg("导入成功").setData(fileName1+type);
        }
        return new ResultData().setStatus("400").setMsg("导入失败");
    }


    /*----------------------模型中-----------------------------------*/




    /**
     * 功能描述：模型中文件下载
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:18
     * 参数：[ * @param null]
     * 返回值：
     */
    @Value("${contextPath}")
    String contextPath;
    public void downloadFile(List<String> urls,int i) throws IOException {
        String realPath = contextPath + "temp" + UUID.randomUUID().toString().replace("-", "") + "/";//创建根目录
        java.io.File pathFile = new java.io.File(realPath);
        try {
            if (!pathFile.exists()) {//如果文件夹不存在
                pathFile.mkdirs();//创建多级文件夹
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String downloadName = "下载.zip";
        String fileName;
        for (String url : urls) {
            if (i == 1) {//变更记录文件
                Model_change model_change = modelChangeMapper.selectModelChangeByUrl(url);
                fileName = model_change.getFile_name();
                downloadName = "设计变更文件.zip";
            } else {
                Model_quality model_quality = modelQualityMapper.selectModelQualityByUrl(url);
                fileName = model_quality.getFile_name();
                downloadName = "质量追踪文件.zip";
            }
            java.io.File f = new java.io.File(url);
            if (f.exists()) {
                /*file.getFile_name改为 file.getFile_url*/
                java.io.File t = new java.io.File(realPath + fileName);//文件名
                DownloadUtils.copyFile(f, t);//复制
            }
        }

        java.io.File targetZipFile = new java.io.File(realPath + downloadName);
        DataInputStream in = null;
        OutputStream out = null;
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            //生成压缩包
            List<String> path = new ArrayList<>();
            DownloadUtils.getPath(new java.io.File(realPath), path);
            if (path.size() > 0) {
                DownloadUtils.zipFile(path, targetZipFile, realPath);

                //下载压缩包
                String zipName = targetZipFile.getName();
                // 以流的形式下载文件
                zipName = URLEncoder.encode(zipName, "UTF-8");
                String userAgent = request.getHeader("User-Agent");


                if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                    zipName = new String(zipName.getBytes("UTF-8"), "ISO8859-1"); // firefox浏览器
                } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                    zipName = URLEncoder.encode(zipName, "UTF-8");// IE浏览器
                }else if (request.getHeader("User-Agent").toUpperCase().indexOf("CHROME") > 0) {
                    zipName = new String(zipName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
                }

                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-disposition", "attachment; filename=" + zipName);// 设定输出文件头
                response.setContentType("application/octet-stream");// 定义输出类型
//            Cookie fileDownload=new Cookie("fileDownload", "true");
//            fileDownload.setPath("/");
//            response.addCookie(fileDownload);
                //输入流：本地文件路径
                in = new DataInputStream(new FileInputStream(new java.io.File(realPath + downloadName)));
                //输出流
                out = response.getOutputStream();
                //输出文件
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                out.flush();
            } else {
                response.setContentType("application/json;charset=utf-8");
                Map<String, Object> map = new HashMap<String, Object>();
                PrintWriter outp = response.getWriter();
                outp.write("文件不存在无法下载");
                outp.flush();
                outp.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//删除临时文件夹
            if (out != null)
                out.close();
            if (out != null)
                out.close();
//            System.out.println("删除文件夹");
            DownloadUtils.delete(new java.io.File(realPath));///删除文件夹
//            fileLogService.inLog("下载",userfolderMapper.selectByPrimaryKey(list.get(0).getId()).getFolder_name(),"文件夹");//下载日志
        }


    }


    /*-------------------变更记录-----------------*/


    /**
     * 功能描述：新增变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 15:14
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addModelChange(Model_change model_change){
        model_change.setId(UUID.randomUUID().toString())
                .setStatu(1)
                .setCreate_time(new Date());
        int i = modelChangeMapper.insert(model_change);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("新增成功");
        }
        return new ResultData().setStatus("400").setMsg("新增失败");
    }

    /**
     * 功能描述：编辑变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 15:49
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData updateModelChange(Model_change model_change){
        int i = modelChangeMapper.updateByPrimaryKeySelective(model_change);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("修改成功");
        }
        return new ResultData().setStatus("400").setMsg("修改失败");
    }
    /**
     * 功能描述：删除变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 15:57
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteModelChange(List<String> ids){
        int i=0;
        for(String id:ids) {
            modelChangeMapper.updateByPrimaryKeySelective(new Model_change().setId(id).setStatu(0));
            i=i+1;
        }
        if(i==ids.size()){
            return new ResultData().setStatus("200").setMsg("全部删除成功");
        }else if(i==0) {
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
        return new ResultData().setStatus("401").setMsg("部分删除失败");
    }
    /**
     * 功能描述：查询变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:09
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getModelChange(String mid,String search){
        List<Model_change> model_changes = modelChangeMapper.selectModelChange(mid, search);
        return new ResultData().setStatus("200").setMsg("成功").setData(model_changes);
    }
    /**
     * 功能描述：下载变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:04
     * 参数：[ * @param null]
     * 返回值：
     */
    public void modelChangeDownload(List<Download> list) throws IOException {
        List<String> ids = new ArrayList<>();
        for (Download d:list){
            ids.add(d.getId());
        }
        List<String> urls = new ArrayList<>();
        for(String id:ids){
            Model_change model_change = modelChangeMapper.selectByPrimaryKey(id);
            urls.add(model_change.getFile_url());
        }
        downloadFile(urls,1);
    }

    /*-------------------造价管理-----------------------*/
    /**
     * 功能描述：新增造价管理列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:26
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addModelCost(Model_cost model_cost){
        BigDecimal price = model_cost.getUint_price();
        BigDecimal amount = model_cost.getAmount();
        BigDecimal total = price.multiply(amount);
        model_cost.setId(UUID.randomUUID().toString())
                .setCreate_time(new Date())
                .setTotal_prices(total)
                .setStatu(1);
        int i = modelCostMapper.insert(model_cost);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("新增成功");
        }
        return new ResultData().setStatus("400").setMsg("新增失败");
    }

    /**
     * 功能描述：编辑造价管理列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:37
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData updateModelCost(Model_cost model_cost){
        int i = modelCostMapper.updateByPrimaryKeySelective(model_cost);
        Model_cost Omodel_cost = modelCostMapper.selectByPrimaryKey(model_cost);
        BigDecimal price = Omodel_cost.getUint_price();
        BigDecimal amount = Omodel_cost.getAmount();
        BigDecimal total = price.multiply(amount);
        int o = modelCostMapper.updateByPrimaryKeySelective(Omodel_cost.setTotal_prices(total));
        if(i!=0&o!=0){
            return new ResultData().setStatus("200").setMsg("修改成功");
        }
        return new ResultData().setStatus("400").setMsg("修改失败");
    }

    /**
     * 功能描述：删除造价管理列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:49
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteModelCost(List<String> ids){
        int i=0;
        for(String id:ids) {
            modelCostMapper.updateByPrimaryKeySelective(new Model_cost().setId(id).setStatu(0));
            i=i+1;
        }
        if(i==ids.size()){
            return new ResultData().setStatus("200").setMsg("全部删除成功");
        }else if(i==0) {
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
        return new ResultData().setStatus("401").setMsg("部分删除失败");
    }
    /**
     * 功能描述：查询造价管理列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 16:55
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getModelCost(String mid,String search){
        List<Model_cost> model_costs = modelCostMapper.selectModelCost(mid, search);
        return new ResultData().setStatus("200").setMsg("成功").setData(model_costs);
    }
    /**
     * 功能描述：计算项目总价
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 17:06
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getModelPrice(String project_id){
        List<Model> models = modelMapper.getModelByProject_id(project_id, "");
        BigDecimal modelPrice = new BigDecimal(0);
        for(Model model:models){
            List<BigDecimal> totalPrices = modelCostMapper.getTotalPrices(model.getId());
            for (BigDecimal totalPrice:totalPrices){
                modelPrice = modelPrice.add(totalPrice);
            }
        }
        return new ResultData().setStatus("200").setMsg("成功").setData(modelPrice);
    }


    /*-------------------质量追踪管理-----------------------*/


    /**
     * 功能描述：新增质量追踪列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 8:57
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData addModelQuality(Model_quality model_quality){
        model_quality.setCreate_time(new Date())
                .setId(UUID.randomUUID().toString())
                .setStatu(1);
        int i = modelQualityMapper.insert(model_quality);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("新增成功");
        }
        return new ResultData().setStatus("400").setMsg("新增失败");
    }
    /**
     * 功能描述：编辑质量追踪列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 9:10
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData updateModelQuality(Model_quality model_quality){
        int i = modelQualityMapper.updateByPrimaryKeySelective(model_quality);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("修改成功");
        }
        return new ResultData().setStatus("400").setMsg("修改失败");
    }
    /**
     * 功能描述：删除质量追踪列表
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 9:12
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData deleteModelQuality(List<String> ids){
        int i=0;
        for(String id:ids) {
            modelQualityMapper.updateByPrimaryKeySelective(new Model_quality().setId(id).setStatu(0));
            i=i+1;
        }
        if(i==ids.size()){
            return new ResultData().setStatus("200").setMsg("全部删除成功");
        }else if(i==0) {
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
        return new ResultData().setStatus("401").setMsg("部分删除失败");
    }

    /**
     * 功能描述：下载质量追踪文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:08
     * 参数：[ * @param null]
     * 返回值：
     */
    public void downloadModelQuality(List<Download> list) throws IOException {
        List<String> ids = new ArrayList<>();
        for (Download d:list){
            ids.add(d.getId());
        }
        List<String> urls = new ArrayList<>();
        for(String id:ids){
            Model_quality model_quality = modelQualityMapper.selectByPrimaryKey(id);
            urls.add(model_quality.getFile_url());
        }
        downloadFile(urls,2);
    }
    /**
     * 功能描述：查询质量追踪文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:18
     * 参数：[ * @param null]
     * 返回值：
     */
    public ResultData getModelQuality(String mid,String search){
        List<Model_quality> model_qualities = modelQualityMapper.selectModelQuality(mid, search);
        return new ResultData().setStatus("200").setMsg("成功").setData(model_qualities);
    }

}
