package com.zcyk.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.mapper.BimVipMapper;
import com.zcyk.mapper.ProjectModelMapper;
import com.zcyk.mapper.TUnitprojectMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.CQBIMService;
import com.zcyk.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述:筑业云资料
 * 开发人员: xlyx
 * 创建日期: 2019/10/30 13:48
 */
@Service
@Primary
@Transactional
@Slf4j
public class CQBIMServiceImpl implements CQBIMService {

    @Value("${JWUrl}")
    private String yunFileUrl;

    @Value("${YunFileTocken}")
    private String yunFileTocken;

    @Autowired
    BimVipMapper bimVipMapper;


    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest request;


    @Autowired
    ProjectModelMapper projectModelMapper;

    @Autowired
    TUnitprojectMapper unitprojectMapper;





    /**
     * 功能描述：用户新增
     * 开发人员： lyx
     * 创建时间： 2019/10/30 14:40
     * 参数：
     * 返回值：
     * 异常：
     */
    public String userSave(String user_account , Project project) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("id", project.getId());
        map.put("username", project.getProject_code());
        map.put("password", project.getProject_code().substring(12));//项目编码后6位
        map.put("showname", project.getProject_name());
        map.put("moblephone",user_account);
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

        map.put("id",company.getCompany_id());
        map.put("instituecode","");
        map.put("instituename",company.getCompany_name());
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
        map.put("designunitname", StringUtils.isBlank(project.getDesign_unit())?"无": project.getDesign_unit());
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
     * 参数：他们机构根据用户名登录，我们采用项目编码
     * 返回值：
     * 异常：
     */
    public String institute_user(String company_id, String project_id) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("institueid",company_id);
        map.put("userid",project_id);
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

    /**
     * 功能描述：判断是不是vip
     * 开发人员： lyx
     * 创建时间： 2020/3/12 10:43
     * 参数： [project_id 项目id]
     * 返回值： com.zcyk.pojo.BimVip
     * 异常：
     */
    public BimVip isVip(String project_id) {
        //状态 3未购买 0未激活 1正在使用 2完成
        //做一个没有注册弥补措施：以前的项目没有生成激活码，但是每个都应该有
        //生成项目激活码 所有项目都有
        BimVip bimVip = bimVipMapper.selectOneVip(project_id);
        if(bimVip==null){
            bimVipMapper.insertSelective(new BimVip().setAccount_id(project_id)
                    .setActivation_code(UUID.nameUUIDFromBytes(project_id.getBytes()).toString())
                    .setActivation_date(new Date()));

        }
        return bimVip;
    }

    /**
     * 功能描述：激活vip
     * 开发人员： lyx
     * 创建时间： 2020/3/12 10:44
     * 参数： [account_id 项目id, activation_code 激活码]
     * 返回值： void
     * 异常：
     */
    public void activateVip(String account_id) {
        bimVipMapper.setVip(account_id);//激活
    }

    /**
     * 功能描述：
     * 开发人员： lyx
     * 创建时间： 2020/3/12 10:44
     * 参数： [project_id 项目id]
     * 返回值： java.lang.String
     * 异常：
     */
    public String buyVip(String project_id) {


        String activation_code = UUID.nameUUIDFromBytes(project_id.getBytes()).toString();
//        String activation_code = UUID.randomUUID(project_id).toString().replace("-", "");

        bimVipMapper.insertSelective(new BimVip()
                .setAccount_id(project_id)
                .setStatus(0)
                .setActivation_code(activation_code)
                .setActivation_date(new Date()));

        return activation_code;
    }
    /**
     * 功能描述：同步项目和单位工程
     * 开发人员： lyx
     * 创建时间： 2020/3/17 14:07
     * 参数： [id 项目id]
     * 返回值： void
     * 异常：
     * @return
     */
    public String synProject(String id) throws Exception {
        String msg = "";
        Project project = projectModelMapper.getProject(id);
        String s3 = projectSaveorUpdate(project);
        if(!"201".equals(JSONObject.parseObject(s3).getString("code"))){
            log.error("项目更新到筑智建失败："+JSONObject.parseObject(s3).getString("abnorm"));
            msg+= project.getProject_name()+":项目更新到筑智建失败\n";
//                throw new ZZJException("项目更新失败（筑智建）");
        }else {
            //单位工程注册 由于单位工程可能会有新增，所以在点击进入项目的时候将所有单位工程进行
            List<TUnitproject> tUnitprojects = unitprojectMapper.selectUnitprojectByProject("",id);
            for (int i = 0; i <tUnitprojects.size() ; i++) {
                //单位工程同步到筑智建
                String s = projectSaveorUpdate(project,tUnitprojects.get(i));
                if("201".equals(JSONObject.parseObject(s).getString("code"))){

                }else {
                    log.error("单位更新到筑智建失败（筑智建）："+JSONObject.parseObject(s).getString("abnorm"));
//                        throw new ZZJException("单位更新到筑智建失败（筑智建）");
                    msg+=tUnitprojects.get(i).getName()+":单位工程更新到筑智建失败\n";
                }
            }
        }

        return msg;
    }


    /**
     * 功能描述：获取公司的所有vip项目
     * 开发人员： lyx
     * 创建时间： 2020/3/17 17:25
     * 参数：
     * 返回值：
     * 异常：
     */
    public List<BimVip> getAllVipBim(String company_id) {
        return bimVipMapper.selectCompanyVipBims(company_id);
    }
}