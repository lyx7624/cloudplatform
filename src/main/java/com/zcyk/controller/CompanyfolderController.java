package com.zcyk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcyk.dto.Download;
import com.zcyk.pojo.*;
import com.zcyk.service.CompanyfolderService;
import com.zcyk.service.FileService;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
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
 * @date 2019/8/6 16:58
 */
@Controller
@RequestMapping("companyfolder")
public class CompanyfolderController {
    @Autowired
    CompanyfolderService companyfolderService;
    @Autowired
    FileService fileService;
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    ObjectMapper mapper;

    /**
     * 功能描述：新建企业文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/8 19:31
     * 参数：[ companyPublicfolder , company_id]
     * 返回值： [ ResultData]
     */
    @RequestMapping("/addCompanyfolder")
    @ResponseBody
    public ResultData addCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception {
        ResultData rd = companyfolderService.addCompanyfolder(companyPublicfolder);
        return rd;
    }

    /**
     * 功能描述：修改企业文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/8 19:43
     * 参数：[ companyPublicfolder]
     * 返回值：[ ResultData]
     */
    @RequestMapping("/updateCompanyfolder")
    @ResponseBody
    public ResultData updateCompanyfolder(CompanyPublicfolder companyPublicfolder) throws Exception {
        ResultData rd = companyfolderService.updateCompanyfolder(companyPublicfolder);
        return rd;
    }

    /**
     * 功能描述：删除企业文件夹
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/8 19:46
     * 参数：[ companyPublicfolder]
     * 返回值： [ ResultData]
     */
    @RequestMapping("/deleteCompanyfolder")
    @ResponseBody
    public ResultData deleteCompanyfolder(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<String> ids = mapper.readValue(mapper.writeValueAsString(map.get("ids")), new TypeReference<ArrayList<String>>() {
        });
        String folder_id = map.get("folder_id").toString();
        ResultData rd = companyfolderService.deleteCompanyfolder(ids, folder_id);
        return rd;
    }

    /**
     * 功能描述：查询企业文件夹及文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/10 10:31
     * 参数：[ 企业id company_id]
     * 返回值：
     */
    @RequestMapping("/Companyfolder")
    @ResponseBody
    public Map<String, Object> Companyfolder(String companyfolder_id) throws Exception {
        return companyfolderService.Companyfolder(companyfolder_id);
    }

    /**
     * 功能描述：上传文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/10 11:29
     * 参数：[file,企业文件夹id]
     * 返回值：
     */
    @RequestMapping("/addCompanyFile")
    @ResponseBody
    public ResultData addFile(MultipartFile file, String folder_id,String file_url,String file_name,@RequestParam(defaultValue = "0") int cover) throws Exception {

            return fileService.upFile(file, folder_id,cover);
    }

    /**
     * 功能描述：修改文件
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/10 11:32
     * 参数：file_name 新的文件名, id 当前文件id, folder_id 文件id]
     * 返回值：
     */
    @RequestMapping("/updateCompanyFileName")
    @ResponseBody
    public ResultData updateFileName(File file) throws Exception {
        ResultData resultData = new ResultData();
        if(null!=fileService.findFile(file)){
           return resultData.setMsg("文件已存在").setStatus("400");
        }
        fileService.updateFileName(file);
        return resultData;
    }

    /**
     * 搜索文件
     */
    @RequestMapping("/searchCompanyFolder")
    @ResponseBody
    public Map<String, Object> searchUserFolder(String index, String folder_id) throws Exception {
        return companyfolderService.searchUserFolder(index, folder_id);
    }

    /**
     * 多文件夹多文件下载
     */
    @RequestMapping(value = "/companyFolderdownload")
    @ResponseBody
    public void companyFolderdownload(String listString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(URLEncoder.encode("[{\"id\":\"eadc26ab-5476-4178-8e0d-9b11b9cb6cd1\",\"type\":1},{\"id\":\"398c5937-61a7-4b5b-8df7-23f140e7ae10\",\"type\":0},{\"id\":\"cc794838-256f-4b43-83b0-46867e00a07a\",\"type\":0}]","UTF-8"));
        List<Download> list = mapper.readValue(listString, new TypeReference<List<Download>>() {
        });
        companyfolderService.companyFolderdownload(list);
    }
}
