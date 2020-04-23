package com.zcyk.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcyk.dto.Download;
import com.zcyk.dto.ResultData;
import com.zcyk.pojo.*;
import com.zcyk.service.FileService;
import com.zcyk.service.ProjectService;
import com.zcyk.service.ProjectfolderService;
import com.zcyk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WuJieFeng
 * @date 2019/8/11 12:03
 */
@Controller
@Slf4j
@RequestMapping("/projectfolder")
public class ProjectFolderController {
    @Autowired
    ProjectfolderService projectfolderService;
    @Autowired
    FileService fileService;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    /*
     * 1.文件夹整个删除 必须要是这个文件夹的所有人--文件夹权限
     * 2.文件夹名称是项目名所以不能修改
     * 3.一个项目文件夹为所有成员都能看到
     * 4.删除项目时自动删除项目文件夹？
     *
     *
     * */


    /**
     * 功能描述：根据用户查询该用户所有的项目文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 10:46
     * 参数：[ ]
     * 返回值：
     */
    @RequestMapping("/getProjectParentFolder")
    @ResponseBody
    public ResultData getProjectParentFolder(String projectfolder_id, HttpServletRequest request) throws Exception {

        ResultData resultData = new ResultData();
        User nowUser = userService.getNowUser(request);

        //应该先判断是否有项目没有生成文件夹
        List<Project> allProject = projectService.getUserAllProject(nowUser.getUser_account());
        StringBuffer msg = new StringBuffer();
        allProject.forEach(project->{
            List<ProjectFolder> projectParentFolder = projectfolderService.getProjectParentFolder(project.getId());//只有一个
            if(projectParentFolder.size()==0){//说明有遗漏没有注册成功项目文件夹
                try {
                    projectfolderService.addProjectParentFolder(project,nowUser.getId());
                } catch (Exception e) {
                    msg.append("项目生同步文件夹失败："+project.getProject_name()+"\n");
                    log.error("项目生成父文件夹失败：",e);
                }
            }
        });
        if(StringUtils.isNotBlank(msg)){
            resultData.setStatus("401").setMsg(msg.toString());
        }
        //获取项目文件夹
        List<ProjectFolder> projectFolders = projectfolderService.getProjectFolderByUser(nowUser.getUser_account());

        return resultData.setData(projectFolders);
    }

    /**
     * 功能描述：根据用户查询该用户所有的项目文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 10:46
     * 参数：[ ]
     * 返回值：
     */
    @RequestMapping("/allFolderAndFile")
    @ResponseBody
    public ResultData selectAllProjectFolder(String projectfolder_id, HttpServletRequest request) throws Exception {
        //应该先判断是否有项目没有生成文件夹
        return new ResultData().setData(projectfolderService.getProjectFolderAndFileById(projectfolder_id));
    }

    /**
     * 功能描述：删除项目文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 11:12
     * 参数：[ id 项目id]
     * 返回值：com.zcyk.dto.ResultData
     */
    @RequestMapping("deleteFolder")
    @ResponseBody
    public ResultData deleteProjectFolder(@RequestBody Map<String, Object> map,HttpServletRequest request) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
       StringBuffer msg = new StringBuffer();

        User nowUser = userService.getNowUser(request);
        String folder_id = map.get("folder_id").toString();
        //判断是否顶层文件夹
        if(StringUtils.isBlank(folder_id)){
            ResultData resultData = new ResultData();
            for (int i = 0; i < ids.size(); i++) {
                //判断项目是否存在
                ProjectFolder projectFolder = projectfolderService.getProjectFolder(ids.get(i));
                Project projectById = projectService.getProjectById(projectFolder.getProject_id());
                if(projectById.getProject_status() == 0){
                    //删除文件夹
                    projectfolderService.deleteFolder(ids.get(i));
                }else {
                    msg.append("项目存在无法删除:").append(projectById.getProject_name()).append("\n");
                }

            }
            if(msg.length()==0){
                return resultData.setMsg("删除成功").setStatus("200");
            }
            return resultData.setMsg(msg.toString()).setStatus("401");

        }
//        ProjectFolder projectFolder = projectfolderService.getProjectFolder(folder_id);
//        if(folder_id.equals(projectFolder.getParent_id())){
//            return new ResultData().setStatus("402").setMsg("此文件不能删除");
//        }

        //非顶级文件夹删除
        return projectfolderService.deleteProjectFolder(ids, folder_id,nowUser);
    }

    /**
     * 功能描述：修改项目文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 11:16
     * 参数：[ id 文件夹id，folder_name 新的文件夹名称]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("updateFolder")
    @ResponseBody
    public ResultData updateProjectFolder(ProjectFolder projectFolder,HttpServletRequest request) throws Exception {
        //判断权限
        User nowUser = userService.getNowUser(request);
        if(!projectfolderService.jugle(nowUser,projectFolder.getId(),0)){
            return new ResultData("400","没有权限操作",null);
        }
        return projectfolderService.updateProjectFolder(projectFolder);
    }

    /**
     * 功能描述：新增项目文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 11:16
     * 参数：[ id 文件夹id，folder_name 新的文件夹名称]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("addFolder")
    @ResponseBody
    public ResultData addFolder(ProjectFolder projectFolder,HttpServletRequest request) throws Exception {
        User nowUser = userService.getNowUser(request);
        return projectfolderService.addProjectFolder(projectFolder,nowUser);
    }
    /**
     * 功能描述：上传文件
     * springboot全局异常处理
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 12:14
     * 参数：[ file，projectfolder_id ，cover]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public ResultData addFile(MultipartFile file, String folder_id,String file_url,String file_name,@RequestParam(defaultValue = "0") int cover) throws Exception {
        if(StringUtils.isNotBlank(file_url) && StringUtils.isNotBlank(file_name) && file==null){//云资料归档
            URL url = new URL(file_url);
            InputStream is = url.openStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            file = new MockMultipartFile( file_name,file_name,ContentType.APPLICATION_OCTET_STREAM.toString(), is);
        }
        return fileService.upFile(file, folder_id ,cover);
    }

    /**
     * 功能描述：上传文件
     * springboot全局异常处理
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 12:14
     * 参数：[ file，projectfolder_id ，cover]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/getProjectTopFolder")
    @ResponseBody
    public ResultData getProjectTopFolder(String project_id) throws Exception {
        ResultData resultData = new ResultData();
        List<ProjectFolder> projectParentFolder = projectfolderService.getProjectParentFolder(project_id);
        if(projectParentFolder.size()<1){
            return resultData.setMsg("项目文件夹未同步，点击云存储进行同步").setStatus("400");
        }else {
            return resultData.setData(projectParentFolder.get(0));
        }
    }

    /**
     * 功能描述：修改文件名
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/11 12:16
     * 参数：[file_name 新的文件名, id 当前文件id, folder_id 文件id]
     * 返回值：  com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateFileName")
    public ResultData updateFileName(File file,HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        User nowUser = userService.getNowUser(request);
        if(projectfolderService.jugle(nowUser,file.getId(),1)) {
            return new ResultData("400", "没有权限操作", null);
        }
        if(null!=fileService.findFile(file)){
            return resultData.setMsg("文件已存在").setStatus("400");
        }
        fileService.updateFileName(file);
        return resultData;
    }

    /**
     * 搜索项目文件夹及文件
     *不
     * @param index
     * @param folder_id
     * @return
     */
    @RequestMapping("/searchProjectFolders")
    @ResponseBody
    public Map<String, Object> searchProjectFolder(String index, String folder_id,HttpServletRequest request) throws Exception {
        User nowUser =userService.getNowUser(request);
        return projectfolderService.searchProjectFolder(index, folder_id,nowUser);
    }

    /**
     * 多文件夹多文件下载
     */
    @RequestMapping(value = "/projectFolderdownload")
    @ResponseBody
    public void projectFolderdownload(String listString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(URLEncoder.encode("[{\"id\":\"eadc26ab-5476-4178-8e0d-9b11b9cb6cd1\",\"type\":1},{\"id\":\"398c5937-61a7-4b5b-8df7-23f140e7ae10\",\"type\":0},{\"id\":\"cc794838-256f-4b43-83b0-46867e00a07a\",\"type\":0}]","UTF-8"));
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });
        projectfolderService.downloadFolderAndFileZip(list);
    }
}
