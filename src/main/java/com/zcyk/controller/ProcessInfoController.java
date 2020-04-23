package com.zcyk.controller;


import com.aspose.words.IFieldMergingCallback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.ResultData;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.*;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 功能描述: 流程实例表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */
@RestController
@Slf4j
@RequestMapping("ProcessInfo")
public class ProcessInfoController {
    private iMsgServer2015 MsgObj = new iMsgServer2015();

    @Autowired
    UserfolderMapper userfolderMapper;
    @Autowired
    CompanyfolderMapper companyfolderMapper;
    @Autowired
    DepartmentFolderMapper departmentFolderMapper;

    @Autowired
    UserDepartmentMapper userDepartmentMapper;

    @Autowired
    ProcessInfoService processInfoService;

    @Autowired
    ProcessNodeService processNodeService;

    @Autowired
    TemplateNodeService templateNodeService;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    HttpServletRequest request;

    @Autowired
    FileService fileService;


    @Autowired
    UserService userService;

    @Autowired
    ProjectFolderMapper projectFolderMapper;

    @Value("${contextPath}")
    String contextPath;

    @Value("${iWebOffice}")
    String iWebOffice;






    /**
     * 功能描述：新建一个流程实例
     * 开发人员： lyx
     * 创建时间： 2019/8/11 10:36
     * 参数： [processInfo]
     * 模板template_id;
     * //发起人 initiator;
     * //备注 remark;
     * //流程文件ile
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("addProcess")
    @Transactional(rollbackFor = Exception.class)
    public ResultData addProcess(ProcessInfo processInfo,MultipartFile file,String file_url,String file_name) throws Exception {
        ResultData resultData = new ResultData();

        if(StringUtils.isNotBlank(file_url) && StringUtils.isNotBlank(file_name) && file==null){//云资料跳转添加流程
            URL url = new URL(file_url);
            InputStream is = url.openStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            file = new MockMultipartFile( file_name,file_name,
                    ContentType.APPLICATION_OCTET_STREAM.toString(), is);

        }

        String originalFilename = file.getOriginalFilename();//文件名
// 不显示类型           processInfo.setFile_name(originalFilename.substring(originalFilename.lastIndexOf("\\")==-1?0:originalFilename.lastIndexOf("\\")+1,originalFilename.lastIndexOf(".")));
        //IE浏览器的原文件名是全路径，chrome是文件名，此处过滤下
        processInfo.setFile_name(originalFilename.substring(originalFilename.lastIndexOf("\\")==-1?0:originalFilename.lastIndexOf("\\")+1));
        String type = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = System.currentTimeMillis()+ UUID.randomUUID().toString().replaceAll("\\-", "");
        //上传
        String file_url2 = fileService.upFileToServer(file, contextPath, fileName + type);
        fileName = file_url2.substring(0,file_url2.lastIndexOf("."));//如果上传到已有文件就会返回一个已经有的文件的名字就不用随机的名字
        if(file_url == null)processInfo.setProcess_files_word(fileName+type);//url远程pdf没的word所以没的word
        String pdfName = fileName + ".pdf";
        //获取存储路径
        //String pdfUrl = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath() + contextPath + pdfName;
        String pdfUrl =  contextPath + pdfName;
        processInfo.setProcess_files_pdf(pdfName);
            //创建流程
            ResultData rd = processInfoService.addProcess(processInfo);//新建流程
            if ("200".equals(rd.getStatus())) {//新建流程表成功
                String template_id = processInfo.getTemplate_id();
                List<TemplateNode> templateNodes = templateNodeService.selNodeByTemplateId(template_id);//查询到所有模板节点
                //设置了第一个节点为正在处理节点
                templateNodes.get(0).setStatus(2);
                //设置处理人
                processInfoService.setProcessHanlder((String) rd.getData(), templateNodes.get(0).getHandler_id(), templateNodes.get(0).getHandler_name());
                Integer i = 0;
                for (TemplateNode templateNode : templateNodes) {
                    ProcessNode processNode = new ProcessNode();
                    BeanUtils.copyProperties(templateNode, processNode);
                    processNode.setProcess_id((String) rd.getData())
                            .setNode_order(++i);
                    processNodeService.addProcessNode(processNode);//复制节点到实例表中
                }


                //转换pdf 可能有点慢
                if(file_url == null) WordUtils.word2pdf(contextPath+file_url2, pdfUrl);//url远程pdf没的word所以不用转

            } else {//事务回滚
                throw new Exception("项目新增出现错误");
            }

        return resultData.setStatus("200").setMsg("新增成功");


    }

    /**
     * 功能描述：删除流程实例
     * 开发人员： wjf
     * 创建时间： 2019/8/25 19:41
     * 参数： [process_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/deleteProcess")
    public ResultData deleteProcess(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> process_ids = mapper.readValue(mapper.writeValueAsString(map.get("process_ids")), new TypeReference<ArrayList<String>>() {
        });
        return processInfoService.deleteProcess(process_ids);
    }

    /**
     * 功能描述：查看某个待处理流程
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:51
     * 参数：
     * 返回值：
     */
    @RequestMapping("lookProcess")
    public Map<String, Object> lookProcess(String process_id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //返回节点数组，前端怎么识别当前处理节点，且怎么返回已处理节点意见，如果多个:返回一个数组
        List<ProcessNode> processNodes = processInfoService.selAllDoNodeById(process_id);
        ProcessInfo processInfo = processInfoService.selOneProcess(process_id);
        map.put("processNodes", processNodes);
        map.put("processInfo", processInfo);
        return map;
    }


    /**
     * 功能描述：根据当前登录人获取到他的发起流程以及已处理流程
     * 开发人员： lyx
     * 创建时间： 2019/8/11 11:29
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     *
     * @return
     */
    @RequestMapping("selAllGoProcess")
    public PageInfo<ProcessInfo> selAllGoProcess
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search,
     @RequestParam(defaultValue = "") String template_id) throws Exception {
        return processInfoService.selAllGoAndProcessed(pageNum, pageSize, search, template_id);


    }

    /**
     * 功能描述：根据当前登录人获取到他的代办流程
     * 开发人员： lyx
     * 创建时间： 2019/8/11 11:29
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    @RequestMapping("selAllDoProcess")
    public PageInfo<ProcessInfo> selAllDoProcess
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search,
     @RequestParam(defaultValue = "") String template_id) throws Exception {

        return processInfoService.selAllDoProcessById(pageNum, pageSize, search, template_id);

    }


    /**
     * 功能描述：获取该公司所有流程
     * 开发人员： lyx
     * 创建时间： 2019/8/11 11:29
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    @RequestMapping("allProcess")
    public PageInfo<ProcessInfo> selectAllProcess
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search) throws Exception {

        return processInfoService.selectAllProcess(pageNum, pageSize, search);

    }




    /**
     * 功能描述：保存流程文件到云盘中
     * 开发人员： lyx
     * 创建时间： 2019/8/11 11:29
     * 参数： file_url file_name folder_id
     */
    @RequestMapping("/savefile")
    public ResultData savefile(File file,HttpServletRequest request) throws Exception{
        User user = userService.getNowUser(request);
        UserDepartment userDepartment = userDepartmentMapper.judeManager(user.getId());
        if(StringUtils.isBlank(file.getFolder_id())){//id为空则表示过来的顶级文件夹
            String file_type = file.getFile_type();
            switch (file_type){
                case "1"://归档到
                    UserFolder userFolder = userfolderMapper.selectTopFolder(user.getId());
                    file.setFolder_id(userFolder.getId());
                    break;
                case "2":
                    CompanyPublicfolder companyPublicfolder= companyfolderMapper.selectCompanyPublicfolderByCompanyId(user.getCompany_id());
                    file.setFolder_id(companyPublicfolder.getId());
                    break;
                case "3":
                    DepartmentFolder departmentFolder= departmentFolderMapper.selectDepartFloderByParentId(userDepartment.getDepartment_id()).get(0);
                    file.setFolder_id(departmentFolder.getId());
                    break;

            }

        }

        java.io.File thisFile = new java.io.File(contextPath + file.getFile_url());
        //去掉原有的后缀
        String type = file.getFile_url().substring(file.getFile_url().lastIndexOf("."));//判断文件类型
        try {
            FileInputStream fileInputStream = new FileInputStream(thisFile);
            MultipartFile multipartFile = new MockMultipartFile( file.getFile_name()+type,file.getFile_name()+type,
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            return fileService.upFile(multipartFile, file.getFolder_id(),null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * 功能描述：获取待办流程总数
     * 开发人员： lyx
     * 创建时间： 2019/8/11 11:29
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    @RequestMapping("countProcess")
    public ResultData countDoProcess() throws Exception{
        ResultData resultData = new ResultData();
        Map<String,Object> map = new HashMap<>();
        try {
            map.put("goProcess", processInfoService.selAllDoProcessById(1, 10, "","").getTotal());
            map.put("processed",processInfoService.selAllGoAndProcessed(1, 10, "", "").getTotal());
            resultData.setMsg("操作成功").setStatus("200").setData(map);

        }catch (Exception e){
            map.put("goProcess", "0");
            map.put("processed","0");
            resultData.setMsg("操作失败").setStatus("400").setData(map);
        }

        return resultData;

    }

    /**
     * 功能描述：将pdf放在项目中可以直接访问，以时间戳为临时文件，如果有人在同一时间点中，可能会抛出异常
     * 开发人员： lyx
     * 创建时间： 2020-4-2 15:14
     * 参数： [path 文件名]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/getPDF")
    public ResultData getInfoPDF(@NotNull String path) throws IOException {
        String newPath = System.currentTimeMillis() +"!"+ path;
        FileUtils.copyFile(new java.io.File(contextPath + path),new java.io.File(contextPath+"\\temp\\"+newPath));
        return new ResultData().setData(newPath);
    }

    /**
     * 功能描述：保存
     * 开发人员： lyx
     * 创建时间： 2020-4-2 15:14
     * 参数： [path 文件名]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/savePDF")
    public ResultData saveInfoPDF(@NotNull String path) throws IOException {
        String fileName = path.split("!")[1];
        FileUtils.copyFile(new java.io.File(iWebOffice+"/asd"),new java.io.File(contextPath + fileName));
        java.io.File file = new java.io.File(contextPath + "/temp/" + path);
        if(file.exists()){
            if(file.delete())log.info("文件未删除:"+path);
        }
        return new ResultData().setMsg("保存成功");
    }

    /**
     * 功能描述：最终保存
     * 开发人员： lyx
     * 创建时间： 2020-4-2 15:14
     * 参数： [path 文件名]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/SavePDFToServer")
    public ResultData endSavePDF(@NotNull String path) throws IOException {
        String fileName = path.split("!")[1];
        java.io.File file = new java.io.File(contextPath + "/temp/" + path);
        if(file.exists()){
            file.delete();
        }
        FileUtils.copyFile(new java.io.File(iWebOffice+"/asd"),new java.io.File(contextPath + fileName));
        return new ResultData().setMsg("保存成功");
    }



}
