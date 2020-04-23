package com.zcyk.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.mapper.ProjectModelMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.mapper.YunfileVipMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.YunFileService;
import com.zcyk.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:云资料
 * 开发人员: xlyx
 * 创建日期: 2019/10/30 13:48
 */
@Service
@Transactional
public class YunFileServiceImpl implements YunFileService {

    @Value("${YunFileUrl}")
    private String yunFileUrl;

    @Value("${YunFileTocken}")
    private String yunFileTocken;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ProjectModelMapper projectModelMapper;


    @Autowired
    YunfileVipMapper yunfileVipMapper;


    /**
     * 功能描述：获取岗位信息
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String getQuarters(){
        Map<String, String> map = new HashMap<>();
        return HttpClientUtils.doGet(yunFileUrl + "quarterslist.do"+"?token="+yunFileTocken,map,null,null);
    }


    /**
     * 功能描述：用户新增
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String userSave(User user) {
        Map<String, String> map = new HashMap<>();
//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        map.put("id",user.getId());
//        map.put("quartersids",quartersids);//用户岗位
        map.put("username",user.getUser_account());
//        map.put("projectid",projectid);
        map.put("password",user.getUser_account());//用户密码设置成账号
        map.put("showname",user.getUser_name());
        map.put("moblephone",user.getUser_account());
        map.put("tencentqq","123456");
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "userSave.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));

    }

    /**
     * 功能描述：用户修改
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String userUpdate(User user,String quartersids,String projectid) {
        Map<String, String> map = new HashMap<>();
//        map.put("projectid",projectid);
//        map.put("quartersids",quartersids);//用户岗位
        map.put("code","201");
        return JSONObject.toJSONString(map);
       /* map.put("username",user.getUser_account());
        map.put("id",user.getId());//没有id
        map.put("password",user.getUser_account());//用户密码设置成账号
        map.put("showname",user.getUser_name());
        map.put("moblephone",user.getUser_account());
        map.put("tencentqq","123456");
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "userUpdate.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));*/

    }

    /**
     * 功能描述：添加机构
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String institueSave(Company company)  {
        Map<String, String> map = new HashMap<>();
//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        map.put("id",company.getCompany_id());
        map.put("instituecode","");
        map.put("instituename",company.getCompany_name());
        map.put("parentid","17116.909424");//我们公司平台节点
        map.put("instituetype","1");
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "institueSave.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));
    }

    /**	新增 工程接口
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String projectSaveorUpdate(Project project)  {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String countyCode = project.getProject_code().substring(0,6);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", project.getId());
        map.put("projectname", project.getProject_name());
        map.put("projecttype","1");
        map.put("parentid","0");
        map.put("county", StringUtils.isBlank(countyCode)?"无":countyCode);//区域
        map.put("projectaddress",StringUtils.isBlank(project.getProject_address())?"无": project.getProject_address());
        map.put("startdate",dateFormat.format(project.getStartwork_date()));
        map.put("buildingarea",StringUtils.isBlank(project.getBuildingarea())?"无": project.getBuildingarea());//建筑面积
        map.put("buildingunitname", project.getDeveloper_unit());//建设单位
        map.put("buildingunitresp",StringUtils.isBlank(project.getBuildingunitresp())?"无": project.getBuildingunitresp());//建设负责人
        map.put("supervisionunitname", project.getSupervising_unit());
        map.put("supervisionunitresp", project.getSupervising_user());
        map.put("constructionunitname", project.getConstruction_unit());
        map.put("constructionunitresp",StringUtils.isBlank(project.getConstructionunitresp())?"无": project.getConstructionunitresp());//施工单位负责人
        map.put("prospectunitname", StringUtils.isBlank(project.getSurvey_unit())?"无": project.getSurvey_unit());
        map.put("prospectunitresp",StringUtils.isBlank(project.getProspectunitresp())?"无": project.getProspectunitresp());//勘测负责人
        map.put("designunitname", StringUtils.isBlank(project.getDesign_unit())?"无": project.getSurvey_unit());
        map.put("designunitresp",StringUtils.isBlank(project.getDesignunitresp())?"无": project.getDesignunitresp());//设计单位负责人
        map.put("projectcode", project.getProject_code());
        map.put("institueid", project.getCompany_id());//机构id
        map.put("bimprojecttype","1");//工程bim类型 我们没有暂时设置成1
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "projectSaveorUpdate.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));
    }

    /**	新增单位工程
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String projectSaveorUpdate(Project project, TUnitproject tUnitproject, String user_id) throws IOException {
        String countyCode = project.getProject_code().substring(0,6);
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap<>();
        map.put("id",tUnitproject.getId());
        map.put("projectname",tUnitproject.getName());
        map.put("projecttype","2");
        map.put("parentid", project.getId());
        map.put("county", StringUtils.isBlank(countyCode)?"无":countyCode);//区域
        map.put("projectaddress",StringUtils.isBlank(project.getProject_address())?"无": project.getProject_address());
        map.put("startdate",dateFormat.format(project.getStartwork_date()));
        map.put("buildingarea",StringUtils.isBlank(project.getBuildingarea())?"无": project.getBuildingarea());//建筑面积
        map.put("buildingunitname", project.getDeveloper_unit());//建设单位
        map.put("buildingunitresp",StringUtils.isBlank(project.getBuildingunitresp())?"无": project.getBuildingunitresp());//建设负责人
        map.put("supervisionunitname", project.getSupervising_unit());
        map.put("supervisionunitresp", project.getSupervising_user());
        map.put("constructionunitname", project.getConstruction_unit());
        map.put("constructionunitresp",StringUtils.isBlank(project.getConstructionunitresp())?"无": project.getConstructionunitresp());//施工单位负责人
        map.put("prospectunitname", StringUtils.isBlank(project.getSurvey_unit())?"无": project.getSurvey_unit());
        map.put("prospectunitresp",StringUtils.isBlank(project.getProspectunitresp())?"无": project.getProspectunitresp());//勘测负责人
        map.put("designunitname", StringUtils.isBlank(project.getDesign_unit())?"无": project.getSurvey_unit());
        map.put("designunitresp",StringUtils.isBlank(project.getDesignunitresp())?"无": project.getDesignunitresp());//设计单位负责人
        map.put("projectcode", project.getProject_code());
        map.put("userid", user_id);
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "projectSaveorUpdate.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));

    }


    /**
     * 功能描述：机构绑定人员
     *
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：他们机构根据用户名登录，我们采用账号--电话号码
     * 返回值：
     * 异常：
     */
    public String institute_user(String company_id, String user_id)  {
        Map<String, String> map = new HashMap<>();
        map.put("code","201");
        return JSONObject.toJSONString(map);
        /*map.put("token",yunFileTocken);
        map.put("institueid",company_id);
        map.put("userids",user_id);
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostUrl(yunFileUrl +  "institute_user.do", map,null);*/

    }
    /**
     * 功能描述：单位工程绑定人员
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：他们机构根据用户名登录，我们采用账号--电话号码
     * 返回值：
     * 异常：
     */
    public String unitproject_user(String user_account, String tUnitproject_id)  {
        Map<String, String> map = new HashMap<>();
        map.put("code","201");
        return JSONObject.toJSONString(map);
       /* map.put("id",tUnitproject_id);//单位工程id
        map.put("username",user_account);
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl +  "unitproject_user.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));*/

    }


    /**
     * 功能描述：根据项目获取模板树
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
    public String getTree(String projectModelId)  {
        return HttpClientUtils.doPostUrl(yunFileUrl + "getProjectTree_ZJ.do"+"?token="+yunFileTocken+"&projectid="+projectModelId, null,null);
    }

    /**
     * 功能描述：根据表格
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
    public String getTable(String modulenodeid)  {
        return HttpClientUtils.doPostUrl(yunFileUrl + "getProjectTree.do"+"?token="+yunFileTocken+"&modulenodeid="+modulenodeid, null,null);
    }

    /**
     * 功能描述：根据表格pdf
     * 开发人员： lyx
     * 创建时间： 2019/11/12 15:45
     * 参数：
     * 返回值：
     * 异常：
     */
    public String getPDF(String projectnodeid)  {
        return HttpClientUtils.doPostUrl(yunFileUrl + "getAttchFile.do"+"?token="+yunFileTocken+"&projectnodeid="+projectnodeid, null,null);
    }

    /**
     * 功能描述：查看该企业vip用户
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<YunfileVip> getYunFileVip(String company_id) {
        List<YunfileVip> byCompany = yunfileVipMapper.getByCompany(company_id);
        byCompany.forEach(vip->{
            long validTime =  vip.getDue_date() - (Long)System.currentTimeMillis();
            if(validTime<=0){
                yunfileVipMapper.setStatus(vip.getAccount(),0);
            }
            if(0<validTime && (validTime/86400000)<90){
                vip.setDue_time((int)(validTime/86400000));
            }

        });
        return byCompany;
    }

    /**
     * 功能描述：查找vip
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    public YunfileVip getOneFileVip(String account) {
        return yunfileVipMapper.selectByAccount(account);
    }

    /**
     * 功能描述：添加企业vip用户
     * 开发人员： lyx
     * 创建时间： 2020/3/12 16:41
     * 参数：
     * 返回值：
     * 异常：
     */
    public void addYunFileVip(YunfileVip yunfileVip) {
        YunfileVip yunfileVip1 = yunfileVipMapper.selectByAccount(yunfileVip.getAccount());
        if(yunfileVip1 != null){//续费
            yunfileVipMapper.updateByPrimaryKey(yunfileVip);
        }else {//注册
            yunfileVipMapper.insertSelective(yunfileVip);
        }
    }


    /**
     * 功能描述：用户登录
     * 开发人员： lyx
     * 创建时间： 2020/3/16 14:43
     * 参数：
     * 返回值：
     * 异常：
     */
    public YunfileVip loginUser(YunfileVip yunfileVip) {
        return yunfileVipMapper.selectByAccount(yunfileVip.getAccount());
    }

    @Override
    public void addProjectToYun(String project_id,String yunfile_account) {
        projectModelMapper.addProjectToYun(project_id,yunfile_account);
    }



}