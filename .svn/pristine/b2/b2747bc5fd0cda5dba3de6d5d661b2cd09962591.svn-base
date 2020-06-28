package com.zcyk.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.*;
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
import java.util.Map;

/**
 * 功能描述:筑业云资料
 * 开发人员: xlyx
 * 创建日期: 2019/10/30 13:48
 */
@Service
@Transactional
public class CQBIMServiceImplToJW {

    @Value("${JWUrl}")
    private String yunFileUrl;

    @Value("${YunFileTocken}")
    private String yunFileTocken;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest request;





    /**
     * 功能描述：用户新增
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String userSave(User user,String quartersids,String projectid) throws IOException {
        Map<String, String> map = new HashMap<>();
//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        map.put("id",user.getId());
//        map.put("quartersids",quartersids);//用户岗位
        map.put("username",user.getUser_account());
//        map.put("projectid",projectid);
        map.put("password",user.getUser_account());//用户密码设置成账号
        map.put("showname",user.getUser_name());
        map.put("moblephone",user.getUser_account().length()>=17?"123456789":user.getUser_account());
        map.put("tencentqq","123456");
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl + "userSave.do"+"?token="+yunFileTocken, JSONObject.toJSONString(map));

    }



    /**
     * 功能描述：添加机构
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String institueSave(Company company) throws IOException {
        Map<String, String> map = new HashMap<>();

//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        map.put("id",company.getCompany_id());
        map.put("instituecode","");
        map.put("instituename",company.getCompany_name());
//        map.put("parentid","17116.909424");//我们公司平台节点
//        map.put("instituetype","1");
        map.put("note","智辰云科下机构");
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
    public String projectSaveorUpdate(Project project) throws IOException {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap<>();
//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        String countyCode = project.getProject_code().substring(0,6);
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
        map.put("outinstitueid", project.getCompany_id());//机构id
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
    public String projectSaveorUpdate(Project project, TUnitproject tUnitproject) throws IOException {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap<>();
//        map.put("code","201");
//        return JSONObject.toJSONString(map);
        map.put("id",tUnitproject.getId());
        map.put("projectname",tUnitproject.getName());
        map.put("projecttype","2");
        map.put("parentid", project.getId());
        map.put("county", project.getProject_code().substring(0,6));//区域
        map.put("projectaddress", StringUtils.isBlank(project.getProject_address())?"无": project.getProject_address());
        map.put("startdate",dateFormat.format(project.getStartwork_date()));
        map.put("buildingarea",StringUtils.isBlank(project.getBuildingarea())?"无": project.getBuildingarea());//建筑面积
        map.put("buildingunitname", project.getDeveloper_unit());//建设单位
        map.put("buildingunitresp",StringUtils.isBlank(project.getBuildingunitresp())?"无": project.getBuildingunitresp());//建设负责人
        map.put("supervisionunitname", project.getSupervising_unit());
        map.put("supervisionunitresp", project.getSupervising_user());
        map.put("constructionunitname", project.getConstruction_unit());
        map.put("constructionunitresp",StringUtils.isBlank(project.getConstructionunitresp())?"无": project.getConstructionunitresp());//施工单位负责人
        map.put("prospectunitname",StringUtils.isBlank(project.getSurvey_unit())?"无": project.getSurvey_unit());
        map.put("prospectunitresp",StringUtils.isBlank(project.getProspectunitresp())?"无": project.getProspectunitresp());//勘测负责人
        map.put("designunitname",StringUtils.isBlank(project.getDesign_unit())?"无": project.getDesign_unit());
        map.put("designunitresp",StringUtils.isBlank(project.getDesignunitresp())?"无": project.getDesignunitresp());//设计单位负责人
        map.put("projectcode",tUnitproject.getCode());
        map.put("bimprojecttype","1");
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
    public String institute_user(String company_id, String user_id) throws IOException {
        Map<String, Object> map = new HashMap<>();
//        map.put("code",201);
//        return JSONObject.toJSONString(map);
        map.put("institueid",company_id);
        map.put("userid",user_id);
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.doPostJson(yunFileUrl +  "institute_user.do?token="+yunFileTocken, JSONObject.toJSONString(map));

    }


    /**
     * 功能描述：上传BIM模型到建委
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String UpBimFile(Map<String,String> map) throws Exception {
//        Map<String, Object> map1 = new HashMap<>();
//        map1.put("code","201");
//        return JSONObject.toJSONString(map1);
        System.out.println(JSONObject.toJSONString(map));
        return HttpClientUtils.postFile(null,"http://jsgl.zfcxjw.cq.gov.cn:7003/ConstructionAcceptance/bimModelFileInfoUpload.do?token="+yunFileTocken, map,null);
    }

    /**
     * 功能描述：上传场布模型到建委
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String UpSiteLayoutFile(Map<String, String> map) throws Exception {
        return HttpClientUtils.postFile(null,"http://jsgl.zfcxjw.cq.gov.cn:7003/ConstructionAcceptance/bimSiteLayoutUpload.do?token="+yunFileTocken, map,null);
    }
}