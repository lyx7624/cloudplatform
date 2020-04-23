package com.zcyk.controller;

import com.alibaba.fastjson.JSONObject;
import com.zcyk.dto.*;
import com.zcyk.exception.JWException;
import com.zcyk.exception.ZZJException;
import com.zcyk.mapper.ModelMapper;
import com.zcyk.mapper.ProjectModelMapper;
import com.zcyk.mapper.ZZJFileResponseMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.CQBIMService;
import com.zcyk.service.ProjectService;
import com.zcyk.service.ZZJFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zcyk.util.MyFileUtils.md5HashCode;

/**
 * 功能描述: 筑智建文件和建委文件系统
 * 开发人员: xlyx
 * 创建日期: 2019/11/21 14:40
 */
@RestController
@RequestMapping("/sycFile")
@Slf4j
public class ZZJAndCQBIMControllerToJW {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ZZJFileService zzjFileService;

    @Autowired
    ZZJFileResponseMapper zzjFileResponseMapper;

    @Autowired
    ProjectService projectService;

    @Autowired
    CQBIMService cqbimService;

    @Autowired
    ProjectModelMapper projectMapper;




    /**
     * 功能描述：文件上传完了，需要项目组进行转换
     * 开发人员： lyx
     * 创建时间： 2019/11/21 14:44
     * 参数：
     * 返回值：
     * 异常： 注意：如果文件没有上传完成到服务器就点击同步会报错
     */
    @RequestMapping("/transformFile")
    public ResultData transformFile(String project_id) throws Exception {
        ResultData resultData = new ResultData();
        String res = null;
        Project project = projectService.getProjectById(project_id);
        int count = 0;
        List<Model> allModel = modelMapper.selectByPejecyId(project_id);
        if(allModel.size()==0){  //判断有没有需要同步得模型
            return resultData.setMsg("没有需要转换的模型").setStatus("400");
        }
        List<Model> unSycModel = allModel.stream().filter(model -> model.getStatu()==1).collect(Collectors.toList());
        if(unSycModel.size()!=0){//判断是不是有模型没有上传 现在设置成必须要全部模型上传了才能同步
            Map<String, List<Model>> collect = unSycModel.stream().collect(Collectors.groupingBy(Model::getModel_name));
            return resultData.setMsg("部分未同步，请先同步模型").setStatus("400").setData(collect);
        }
        if(project.getTransformStatus()==3){
            return resultData.setStatus("201").setMsg("项目组已经同步完成，无需再次同步");
        }
        ZZJ_Token ZZJToken = zzjFileService.loginZZJ(null, null);
        if(ZZJToken.getAccess_token()!=null){
            List<Model> sycModel = allModel.stream().filter(model -> model.getStatu()==2).collect(Collectors.toList());
            ZZJItem zzjItem = null;
            for (int i = 0; i <sycModel.size() ; i++) {
                ZZJFileResponse fileResponse = zzjFileResponseMapper.selectByPrimaryKey(sycModel.get(i).getModel_file_id());
                res = zzjFileService.transformFile(fileResponse, ZZJToken.getAccess_token());
                if (JSONObject.parseObject(res)==null) {
                    log.error("查询上传文件的状态，筑智建接口调用失败");
                    throw new ZZJException("查询上传文件的状态，筑智建接口调用失败");//同步出了问题
                }else if("606".equals(JSONObject.parseObject(res).getString("code"))){//表示该模型已经被转转换
                    count++;
//                        String items = JSONObject.parseObject(res).get("items").toString();
//                        System.out.println("文件已被同步过服务器"+items);//606代表已经转换了的
                }else if ("200".equals(JSONObject.parseObject(res).getString("code"))) {
                    String item = JSONObject.parseObject(res).get("item").toString();
                    zzjItem = JSONObject.parseObject(item, ZZJItem.class);
                }else {
                    String items = JSONObject.parseObject(res).get("items").toString();
                    log.error("查询上传文件的状态，筑智建接口调用失败"+items);
                    throw new ZZJException("查询上传文件的状态，筑智建接口调用失败");
                }
            }
            if(count==sycModel.size()){//此时没有taskid   就很麻烦
                resultData.setStatus("401").setMsg("转换完成");
                project.setTransformStatus(3);
            }else {
                resultData.setStatus("401").setMsg("转换中");
                project.setTaskId(zzjItem==null?null:zzjItem.getTaskId()).setTransformStatus(1);
            }


            projectMapper.updateByPrimaryKeySelective(project);
            //对工程进行修改
        }else {
            throw new ZZJException("登陆过期，请刷新页面");
        }
        return resultData;
    }


    /**
     * 功能描述：查看项目组转换状态
     * 开发人员： lyx
     * 创建时间： 2019/11/21 14:44
     * 参数：
     * 返回值：
     * 异常： 注意：如果文件没有上传完成到服务器就点击同步会报错
     */
    @RequestMapping("/transformFileStatus")
    public ResultData transformFileStatus(String project_id) throws Exception {
        ResultData resultData = new ResultData().setStatus("200").setMsg("查询成功");
        Map<String, Object> map = new HashMap<>();
            Project project = projectService.getProjectById(project_id);
            if(project.getTransformStatus()==0){//这个其实可以用status进行替换
                return resultData.setStatus("400").setMsg("请先转换项目");
            }
            if(project.getTransformStatus()==3){
                return resultData.setStatus("400").setMsg("已经完成转换");
            }

            if(project.getTaskId()==null){//如果项目文件都是删除了再重新转的  那这个工程都没有taskid   就很尴尬
                project.setTransformStatus(2);
                projectMapper.updateByPrimaryKeySelective(project);
                return resultData.setStatus("400").setMsg("转换错误，请重试");
            }
            ZZJ_Token ZZJToken = zzjFileService.loginZZJ(null, null);
            if(ZZJToken.getAccess_token()!=null) {
                String s = zzjFileService.transformStatus(project_id, project.getTaskId(), ZZJToken.getAccess_token());
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (jsonObject==null || !"200".equals(JSONObject.parseObject(s).getString("code"))) {
                    log.error("筑智建：查看项目组转换状态调用失败"+JSONObject.parseObject(s).getString("abnorm"));
                    throw new ZZJException("筑智建：查看项目组转换状态调用失败");
                }

                List<ZZJModelList> zzjModelLists = jsonObject.getJSONObject("item").toJavaObject(ZZJItem.class).getModelList();
                List<ZZJModelList> collect = zzjModelLists.stream().filter(zzjModelList -> zzjModelList.getPercent() != 100).collect(Collectors.toList());
                ZZJTaskState taskState = jsonObject.getJSONObject("item").toJavaObject(ZZJItem.class).getTaskState();
                if (taskState.getSuccessed() && taskState.getDone() && taskState.getFound()) {
                    project.setTransformStatus(3);
                    resultData.setStatus("201").setMsg("转换成功");
                } else if (taskState.getSuccessed() && !taskState.getDone() && taskState.getFound()) {
                    project.setTransformStatus(1);
                    resultData.setStatus("201").setMsg("转换中");
                } else {
                    project.setTransformStatus(2);
                    resultData.setStatus("201").setMsg("转换失败");
                }
                projectMapper.updateByPrimaryKeySelective(project);


            }else {
                resultData.setStatus("400").setMsg("登录过期，请刷新页面");
            }
        return resultData;
    }



    /**
     * 功能描述：同步项目模型上传到筑智建 没有判断同步
     * 开发人员： lyx
     * 创建时间： 2019/11/21 14:44
     * 参数：
     * 返回值：
     * 异常： 注意：如果文件没有上传完成到服务器就点击同步会报错
     */
    @RequestMapping("/upFileToZZJ")
    public ResultData upFileToZZJ(String id) throws Exception {
        ResultData resultData = new ResultData();
        Model model = modelMapper.selectByPrimaryKey(id);
        String project_code = projectService.getProjectById(model.getProject_id()).getProject_code();
        //注意 tags不能修改
        model.setTags(project_code.substring(0,6)+","+model.getProject_period()+","+model.getProject_type());//筑智建上传的tags
        int i = 0;
        //判断模型是否已经同步
        if(model.getStatu()==2){
            return resultData.setMsg("已经同步不需要再同步").setStatus("400");
        }
        //先登录
        ZZJ_Token ZZJToken = zzjFileService.loginZZJ(null, null);
        //判断文件状态 md5HashCode(new FileInputStream(model.getModel_url())
        String md5HashCode = md5HashCode(new FileInputStream(model.getModel_url()));
        ZZJFileResponse zzjFileResponse = null;
        while(ZZJToken.getAccess_token() != null){
            ZZJItem zzjItem = zzjFileService.getFileStatus(md5HashCode,model.getModel_size().intValue()/(1024*1024), ZZJToken.getAccess_token());//这里有可能token过期
            if(zzjItem==null){
                return resultData.setMsg("远程网络错误，请稍后重试").setStatus("400");
            }
            Integer status =zzjItem.getStatus();
            if (status == 0) {//未上传
                try {
                    //上传成功文件 在那边是有时间进行操作的，可能不能及时返回数据
                    zzjFileResponse = zzjFileService.upFileSync(model, ZZJToken.getAccess_token());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return resultData.setMsg(ex.getMessage()).setStatus("400");
                }
            }else if(status == 1){//部分未转换，可能是远程正在转换中
                if(i==1){
                    return resultData.setMsg("远程网络错误，请稍后重试").setStatus("400");
                }
                //上传成功文件 在那边是有时间进行操作的，可能不能及时返回数据
                try {
                    //上传成功文件 在那边是有时间进行操作的，可能不能及时返回数据
                    zzjFileResponse = zzjFileService.upFileSync(model, ZZJToken.getAccess_token());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return resultData.setMsg(ex.getMessage()).setStatus("400");
                }
                i++;
            } else if (status == 2) {//完整上传  直接转换 这里转换 可以使单个也可以是项目组一起
                if(zzjFileResponse == null){//说明没有进行了新添加,
                    //防止一个文件只能用在一个文件上 但是id和筑智建返回的id不一致
                    zzjFileResponse = zzjItem.getFile().setId(UUID.randomUUID().toString());
                }
                zzjFileResponse.setModelgroupid(model.getProject_id())
                        .setProjectid(model.getProject_id())
                        .setType(zzjFileResponse.getType().substring(1))//去掉返回type的前面的点
                        .setTags(model.getTags())
                        .reSetZYType(model.getModel_type());
                if (zzjFileResponseMapper.selectByPrimaryKey(zzjFileResponse.getId()) == null) {//本地没有
                    //存放返回的数据到数据库
                    zzjFileResponseMapper.insertSelective(zzjFileResponse);
                }else{
                    zzjFileResponseMapper.updateByPrimaryKeySelective(zzjFileResponse);
                }
                //更新模型
                model.setStatu(2).setModel_file_id(zzjFileResponse.getId());//设置模型同步状态为已同步
                modelMapper.updateByPrimaryKeySelective(model);
                return resultData.setMsg("同步成功").setStatus("200");
            } else {//没有进行同步到图形服务器
                model.setStatu(2).setModel_file_id(zzjFileResponse.getId());
                //更新模型
                modelMapper.updateByPrimaryKeySelective(model);
                status = zzjFileService.sycFile(md5HashCode, ZZJToken.getAccess_token());
            }
        }
        return resultData;
    }




    //#########################################建委


    /**
     * 功能描述：同步项目模型到建委   全部文件进行的同步，没有判断
     * 开发人员： lyx
     * 创建时间： 2019/11/21 14:44
     * 参数：
     * 返回值：
     * 异常：
     */
    @RequestMapping("/upFileToJW")
    public ResultData upFileToJW(String project_id) throws Exception {
        ResultData resultData = new ResultData().setMsg("同步成功").setStatus("200");
        //直接在这里判断企业是否具有使用这个项目的资格
        BimVip bimVip = cqbimService.isVip(project_id);
        if(bimVip==null || bimVip.getStatus()==0){//验证码正确
            return resultData.setMsg("请先购买并激活").setStatus("400");
        }
        List<ZZJFileResponse> zzjFileResponses = zzjFileResponseMapper.selectByProjectId(project_id);
        Project project = projectService.getProjectById(project_id);

        if(project.getTransformStatus()!=3){//但是同步和转换没有关联啊 ，没同步也是可以转化的啊
            return resultData.setMsg("项目未完成转换，请先转换项目").setStatus("400");
        }
        //先刷新项目。将项目信息和单位工程信息同步上去
        String synMsg = cqbimService.synProject(project.getId());
        if(StringUtils.isNotBlank(synMsg)){//项目同步失败但是不影响运行
            return resultData.setStatus("400").setMsg(synMsg);
        }

        StringBuilder msg = new StringBuilder();
        String s = "";
        //查询项目下所有的模型
        List<Model> models = modelMapper.selectByPejecyId(project_id);
        Map<Object, Object> map = new HashMap<>();//构造BIM必要的fileresponse参数
        map.put("code",200);
        map.put("success",true);
        for (int i = 0;i<models.size();i++){
            Model nowModel = models.get(i);
            if(nowModel.getJWStatus()!=null&&nowModel.getJWStatus()==1){
                //已经同步成功  建委只能新增不能修改，就只能限制
                continue;
            }
            ZZJFileResponse fileResponse = zzjFileResponseMapper.selectByPrimaryKey(nowModel.getModel_file_id());
            fileResponse.setUnitprojectid(nowModel.getUnitproject_id()).setId(nowModel.getId())
                    .setTags(project.getProject_code()+"-"+nowModel.getProject_period()+"-"+nowModel.getProject_type());//fuck这边又是-拼接
            map.put("item",fileResponse);
            //添加必要参数
            fileResponse.setTaskid(project.getTaskId()).setFilestate("2");
            Map<String, String> entutyMap = fileResponse.toMap();
            entutyMap.put("fileresponse",JSONObject.toJSONString(map));

            if(nowModel.getModel_type()==4){//场布文件同步
                entutyMap.remove("zytype");
                entutyMap.put("customname",nowModel.getModel_file_name());
                s = cqbimService.UpSiteLayoutFile(entutyMap);
            }else{
                s = cqbimService.UpBimFile(entutyMap);
            }
            if(StringUtils.isBlank(s)||!"201".equals(JSONObject.parseObject(s).getString("code"))){
                String rs = JSONObject.parseObject(s).getString("abnorm");
                if(StringUtils.contains(rs,"PRIMARY")){//对已有项目的一次防范，再次点击如果已经同步直接修改状态
                    modelMapper.updateByPrimaryKeySelective(nowModel.setJWStatus(1));
                }else {
                    modelMapper.updateByPrimaryKeySelective(nowModel.setJWStatus(2));
                    log.error("模型同步到建委报错"+rs);
                    throw new JWException("模型同步到建委报错");
                }
            }else {
                modelMapper.updateByPrimaryKeySelective(nowModel.setJWStatus(1));
                log.error("模型同步到建委报错"+s);
                throw new JWException("模型同步到建委报错");
            }

        }
        if(StringUtils.isNotBlank(msg)){
            log.error("模型同步到建委报错"+s);
            throw new JWException("模型同步到建委报错");
        }
        return resultData;

    }
}