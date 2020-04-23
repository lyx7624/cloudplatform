package com.zcyk.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.Download;
import com.zcyk.dto.FileForm;
import com.zcyk.dto.ResultData;
import com.zcyk.exception.ZZJException;
import com.zcyk.mapper.UploadFileMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.*;
import com.zcyk.util.File_upload;
import com.zcyk.util.iMsgServer2015;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 功能描述: 项目质量
 * 开发人员: lyx
 * 创建日期: 2019/12/30 16:55
 */
@RestController
@Slf4j
@RequestMapping("/projectBim")
public class ProjectBimController {

    @Value("${rvtPath}")
    String rvtPath;

    @Autowired
    ProjectService projectService;

    @Autowired
    SubProjectRecord subProjectRecord;

    @Autowired
    FileService fileService;

    @Autowired
    File_upload file_upload;

    @Autowired
    CQBIMService cqbimService;

    @Autowired
    UploadFileMapper uploadFileMapper;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CompanyService companyService;


    @Value("${contextPath}")
    String contextPath;


    /*
    * 1.注册公司
    * 2.注册账号 项目编码
    * 3.将公司和账号绑定
    * 4.注册项目
    *
        1.更新项目
        2.更新单位工程
        不更新就会不能同步到建委
        *
        *
        * 注意：1.当下是一个项目只能一个人管理，所以项目直接关联云资料账号，这样不好!
    * */




    /*
    功能描述：激活项目 注意：如果要一个项目可以重复使用一个激活码，激活码就和项目编码绑定
     */
    @RequestMapping("/activateVip")
    @Transactional(rollbackFor = Exception.class)
    public ResultData activateVip(String project_id, String activation_code,HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData().setMsg("激活成功");
        User user = userService.getNowUser(request);
        //激活企业
        if(cqbimService.getAllVipBim(user.getCompany_id()).size()==0){//企业没有注册过
            Company nowCompany =  companyService.getCompany(user.getCompany_id());
            //机构注册 只能注册一次 如果失败呢？
            String s = cqbimService.institueSave(nowCompany);
            if(!"201".equals(JSONObject.parseObject(s).getString("code"))) {
                log.error("筑智建机构同步失败："+JSONObject.parseObject(s).getString("abnorm"));
                throw new ZZJException("机构同步失败");
            }
        }


        BimVip vip = cqbimService.isVip(project_id);
        if(vip!=null && vip.getActivation_code()!=null){//如果失败呢
            if(vip.getActivation_code().equals(activation_code) || "zcyk888".equals(activation_code)){
                //激活成功  人员 项目 单位工程
                Project project = projectService.getProjectById(project_id);
                //1 用户注册--就是项目注册 机构必须先注册
                String s1 = cqbimService.userSave(user.getUser_account(), project);
                if("201".equals(JSONObject.parseObject(s1).getString("code"))
                        ||"用户名已存在".equals(JSONObject.parseObject(s1).getString("desc"))
                        ||"422".equals(JSONObject.parseObject(s1).getString("code"))){ //创建用户已经被创建的时候使用 注意！！！！！
                    //2.机构绑定人员
                    String s2 =cqbimService.institute_user(user.getCompany_id(), project.getId());
                    if(!"201".equals(JSONObject.parseObject(s2).getString("code"))){
                        log.error("筑智建用户机构关联失败："+JSONObject.parseObject(s2).getString("abnorm"));
                        throw new ZZJException("筑智建用户机构关联失败");
                    }else {
                        //本地修改
                        cqbimService.activateVip(project_id);
                    }
                    //工程注册放到同步到建委最终接口那里同步去了
                }else {
                    log.error("用户同步筑智建失败："+JSONObject.parseObject(s1).getString("abnorm"));
                    throw new ZZJException("用户同步筑智建失败");
                }
            }else{
                resultData.setMsg("激活码错误").setStatus("400");
            }
        }else {
            resultData.setMsg("未购买").setStatus("400");
        }
        return resultData;
    }


    /**
    * 功能描述：购买项目
    * */
    @RequestMapping("/buyVip")
    @Transactional(rollbackFor = Exception.class)
    public ResultData buyVip(String project_id,HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        User user = userService.getNowUser(request);

        Company nowCompany =  companyService.getCompany(user.getCompany_id());
        //1.支付 直接跳过

        //2.数据库
        String activation_code = cqbimService.buyVip(project_id);
        //3.发送激活码  短信，现在先这样
        //机构注册 只能注册一次 如果失败呢？
        String s = cqbimService.institueSave(nowCompany);
        if(!"201".equals(JSONObject.parseObject(s).getString("code"))) {
            log.error("筑智建机构同步失败："+JSONObject.parseObject(s).getString("abnorm"));
            throw new ZZJException("机构同步失败");
        }


        return resultData;//暂时放在里面后期发短信
    }

    /**
     * 功能描述：BIM模型列表查询
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 14:19
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/bimProjectModel")
    public ResultData bimProjectModel (@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(defaultValue = "") String search,
                                       @RequestParam(defaultValue = "1") Integer sort,
                                       HttpServletRequest request) throws UnsupportedEncodingException {
        User nowUser = userService.getNowUser(request);
        return new ResultData().setMsg("成功").setStatus("200").setData(projectService.getBimProject(pageNum,pageSize,sort,search));
    }

   //拦截
    /**
     * 功能描述：查询项目模型（模糊查询【按模型名称搜索】）
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:23
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getModel")
    public ResultData getModel(String project_id,
                               @RequestParam(defaultValue = "") String search,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) throws Exception {


        ResultData resultData = new ResultData();
        //直接在这里判断企业是否具有使用这个项目的资格
        BimVip bimVip = cqbimService.isVip(project_id);
        if(bimVip==null || bimVip.getStatus()==0){//验证码正确
            return resultData.setMsg("请先购买并激活").setStatus("400");
        }
        PageInfo<Model> allModel = projectService.getAllModel(project_id, search, pageNum, pageSize);
        return resultData.setData(allModel);
    }

    /**
     * 功能描述：保存Bim项目模型信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 15:49
     * 参数：[ id,模型id，进度，图片,url]
     * 返回值：ResultData resultData = upudateProject(project.setModel_pic(url));
     */
    @RequestMapping("/updateBimProject")
    public ResultData updateBimProject(Project project) throws Exception {
        return projectService.updateBimProject(project);
    }

    /**
     * 功能描述：更改项目BIM类型状态
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/21 16:07
     * 参数：[ id]
     * 返回值：
     */
    @RequestMapping("/cancelBimProject")
    public ResultData cancelBimProject(Project project) throws Exception {
        return projectService.cancelBimProject(project);
    }



    /**
     * 功能描述：用于BIM+上传图片
     * 开发人员： lyx
     * 创建时间： 2019/10/12 9:37
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/upFileToBimProject")
    public ResultData upFileToBimProject(MultipartFile file, Project project) throws Exception {
        String url = fileService.upFileToServer(file, contextPath, null);
        return new ResultData().setStatus("200").setMsg("成功").setData(URLEncoder.encode(url, "utf-8"));
    }



    /**
     * 功能描述：新增项目模型
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:23
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/addModel")
    public ResultData addModel(Model model) throws Exception {
        return projectService.addModel(model);
    }

    /**
     * 功能描述：删除项目模型
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:24
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/deleteModel")
    public ResultData deleteModel(String id) throws Exception {
        return projectService.deleteModel(id);
    }
    /**
     * 功能描述：编辑模型信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/31 17:24
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/updateModel")
    public ResultData updateModel(Model model) throws Exception {
        return projectService.updateModel(model);
    }




    @PostMapping("/isUpload")
    public ResultData isUpload(@Valid FileForm form) {
        try {
            UploadFile uploadFile = uploadFileMapper.findFileByMd5(form.getMd5());
            Map<String, Object> map = file_upload.findByFileMd5(uploadFile);
            return new ResultData().setStatus("200").setData(map).setMsg("成功");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultData().setStatus("400").setMsg("上传失败");
        }
    }

    @PostMapping("/upload")
    public ResultData upload(@Valid FileForm form,
                             @RequestParam(value = "data", required = false) MultipartFile multipartFile) throws Exception {
        Map<String, Object>  map = file_upload.realUpload(form, multipartFile,rvtPath);
        return new ResultData().setStatus("200").setData(map).setMsg("成功");
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



    //未用
    /**
     * 功能描述：导入平面图
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/21 15:43
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/uploadImage")
    public ResultData addImage(MultipartFile file,String project_id) throws Exception {

        return projectService.addImage(file,project_id);
    }




    //没有使用的接口---------------------------------------------------------------------------------------------------------





    /**
     * 功能描述：在线打开PDF
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/21 17:20
     * 参数：[ * @param null]
     * 返回值：
     **/
    private iMsgServer2015 MsgObj = new iMsgServer2015();

    @Value("${rvtPath}")
    String mFilePath;
    @RequestMapping("/FileDownload")
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {

            String mFileName = request.getParameter("FileName");
            if (MsgObj.MsgFileLoad(mFilePath + mFileName))
            {
                System.out.println(mFilePath + mFileName);
            }
            MsgObj.Send(response, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述：查看历史版本
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/15 10:40
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getVersion")
    public ResultData getVersion(String mid) throws Exception {
        return projectService.getVersion(mid);
    }
    /**
     * 功能描述：恢复历史版本
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/15 10:04
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/backVersion")
    public ResultData backVersion(String id) throws Exception {
        return projectService.backVersion(id);
    }


    /*----------------------模型中-----------------------------------*/
    /**
     * 功能描述：上传文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:26
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/uploadAFile")
    public ResultData uploadAffiliatedFile(MultipartFile file) throws Exception {
        try {
            //上传
            String originalFilename = file.getOriginalFilename();//文件名
            String type = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replaceAll("\\-", "");
            String file_url = contextPath+ fileService.upFileToServer(file, contextPath, fileName + type);
            return new ResultData().setStatus("200").setMsg("上传成功").setData(new Model_change().setFile_name(originalFilename).setFile_url(file_url));
        }catch (Exception e){
            log.error("模型上传失败",e);
            return new ResultData().setStatus("401").setMsg("上传失败");
        }
    }



    /*-------------------------设计变更---------------*/
    /**
     * 功能描述：新增变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/19 15:14
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/addModelChange")
    public ResultData addModelChange(Model_change model_change) throws Exception {
        return  projectService.addModelChange(model_change);

    }
    /**
     * 功能描述：编辑变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:32
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/updateModelChange")
    public ResultData updateModelChange(Model_change model_change) throws Exception {
        return projectService.updateModelChange(model_change);
    }
    /**
     * 功能描述：删除变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:33
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/deleteModelChange")
    public ResultData deleteModelChange(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        return projectService.deleteModelChange(ids);
    }
    /**
     * 功能描述：查询变更记录
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:35
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getModelChange")
    public ResultData getModelChange(String mid,String search) throws Exception {
        return projectService.getModelChange(mid,search);
    }
    /**
     * 功能描述：下载变更记录文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:37
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/downloadModelChange")
    public void modelChangeDownload(String listString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(URLEncoder.encode("[{\"id\":\"eadc26ab-5476-4178-8e0d-9b11b9cb6cd1\",\"type\":1},{\"id\":\"398c5937-61a7-4b5b-8df7-23f140e7ae10\",\"type\":0},{\"id\":\"cc794838-256f-4b43-83b0-46867e00a07a\",\"type\":0}]","UTF-8"));
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });
        projectService.modelChangeDownload(list);
    }
    /*-------------------------造价管理------------------------*/
    /**
     * 功能描述：新增造价管理
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:44
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/addModelCost")
    public  ResultData addModelCost(Model_cost model_cost) throws Exception {
        return projectService.addModelCost(model_cost);
    }
    /**
     * 功能描述：编辑造价管理
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:45
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/updateModelCost")
    public ResultData updateModelCost(Model_cost model_cost) throws Exception {
        return projectService.updateModelCost(model_cost);
    }
    /**
     * 功能描述：删除造价管理
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:47
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/deleteModelCost")
    public ResultData deleteModelCost(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        return projectService.deleteModelCost(ids);
    }
    /**
     * 功能描述：查询造价管理
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:48
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getModelCost")
    public ResultData getModelCost(String mid,String search) throws Exception {
        return projectService.getModelCost(mid,search);
    }
    /**
     * 功能描述：计算项目总价
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 10:50
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getModelPrice")
    public ResultData getModelPrice(String project_id) throws Exception {
        return projectService.getModelPrice(project_id);
    }

    /*-------------------------质量追踪管理--------------------------*/
    /**
     * 功能描述：新增质量追踪
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 11:03
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/addModelQuality")
    public ResultData addModelQuality(Model_quality model_quality) throws Exception {
        return projectService.addModelQuality(model_quality);
    }
    /**
     * 功能描述：编辑质量追踪
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 11:05
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/updateModelQuality")
    public ResultData updateModelQuality(Model_quality model_quality) throws Exception {
        return projectService.updateModelQuality(model_quality);
    }
    /**
     * 功能描述：删除质量追踪
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 11:06
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/deleteModelQuality")
    public ResultData deleteModelQuality(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        return projectService.deleteModelQuality(ids);
    }
    /**
     * 功能描述：查询质量追踪
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 11:08
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getModelQuality")
    public ResultData getModelQuality(String mid,String search) throws Exception {
        return projectService.getModelQuality(mid,search);
    }
    /**
     * 功能描述：下载质量追踪文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/11/20 11:10
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/downloadModelQuality")
    public void downloadModelQuality(String listString) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });
        projectService.downloadModelQuality(list);
    }

}