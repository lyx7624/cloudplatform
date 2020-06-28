package com.zcyk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcyk.annotation.Log;
import com.zcyk.dto.Download;
import com.zcyk.pojo.*;
import com.zcyk.service.*;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WuJieFeng
 * @date 2019/8/9 15:39
 */
@Controller
@RequestMapping("userfolder")
public class UserfolderController {
    @Autowired
    UserfolderService userfolderService;
    @Autowired
    CompanyfolderService companyfolderService;
    @Autowired
    DepartmentFolderService departmentFolderService;
    @Autowired
    ProjectfolderService projectfolderService;
    @Autowired
    FileService fileService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    UserService userService;

    /**
     * 功能描述：查询个人文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 15:56
     * 参数：[user_id]
     * 返回值： UserFolder
     */
    @RequestMapping("/Alluserfolder")
    @ResponseBody
    public Map<String, Object> Alluserfolder(String userfolder_id) throws Exception{
        return userfolderService.Alluserfolder(userfolder_id);
    }

    /**
     * 功能描述：新建个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 15:56
     * 参数：[ 文件夹名称 userfolder_name,文件夹父Id parent_id]
     * 返回值：
     */
    @RequestMapping("/addUserfolder")
    @ResponseBody
    @Log(module = "个人云盘", description = "新建")
    public ResultData addUserfolder(UserFolder userFolder)throws Exception {
        return userfolderService.addUserfolder(userFolder);
    }

    /**
     * 功能描述：删除个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 15:58
     * 参数：[ 文件夹id]
     * 返回值： ResultData
     */
    @RequestMapping("/deleteUserfolder")
    @ResponseBody
    public ResultData deleteUserfolder(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        String folder_id = map.get("folder_id").toString();
        return userfolderService.deleteUserfolder(ids, folder_id);

    }

    /**
     * 功能描述：修改个人文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/9 16:00
     * 参数：[ 文件夹id，文件夹父id]
     * 返回值：
     */
    @RequestMapping("/updateUserfolder")
    @ResponseBody
    public ResultData updateUserfolder(UserFolder userFolder)throws Exception {
        return userfolderService.updateUserfolder(userFolder);
    }

    /**
     * 功能描述：上传文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/10 11:29
     * 参数：[file,个人文件夹id]
     * 返回值：
     */
    @RequestMapping("/addFile")
    @ResponseBody
    public ResultData addFile(MultipartFile file, String folder_id)throws Exception {
        return fileService.upFile(file, folder_id,null);
    }

    /**
     * 多文件夹多文件下载
     */
    @RequestMapping(value = "/userFolderdownload")
    @ResponseBody
    public void userFolderdownload(String listString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(URLEncoder.encode("[{\"id\":\"eadc26ab-5476-4178-8e0d-9b11b9cb6cd1\",\"type\":1},{\"id\":\"398c5937-61a7-4b5b-8df7-23f140e7ae10\",\"type\":0},{\"id\":\"cc794838-256f-4b43-83b0-46867e00a07a\",\"type\":0}]","UTF-8"));
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });

        userfolderService.userFolderdownload(list);
    }

    /**
     * 搜索文件
     */
    @RequestMapping("/searchUserFolder")
    @ResponseBody
    public Map<String, Object> searchUserFolder(String index, String folder_id)throws Exception {
        return userfolderService.searchUserFolder(index, folder_id);
    }


    /**
     * 功能描述：查询个人云盘大小及企业容量
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/19 15:39
     * 参数：[ * @param null]
     * 返回值： 
    */
    @RequestMapping("/user_companySize")
    @ResponseBody
    public ResultData userfolder_size(HttpServletRequest request)throws Exception{
        ResultData resultData = new ResultData();
        Map<String, Object> userfolderSize = fileService.userfolder_size(userService.getNowUser(request).getId());
        Map<String, Object> companyfolderSize = fileService.companySize();
        List<Map>maps = new ArrayList<>();
        maps.add(userfolderSize);
        maps.add(companyfolderSize);
        if(userfolderSize!=null&&companyfolderSize!=null){
            return resultData.setMsg("查询成功").setStatus("200").setData(maps);
        }else {
            return resultData.setMsg("查询失败").setStatus("400");
        }
    }
}
