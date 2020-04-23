package com.zcyk.controller;

import com.github.pagehelper.PageInfo;
import com.zcyk.annotation.Log;
import com.zcyk.dto.ResultData;
import com.zcyk.pojo.Company;
import com.zcyk.pojo.User;
import com.zcyk.service.CompanyService;
import com.zcyk.service.UserService;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 功能描述: 登录控制器
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: wjf
 * 版本日志: 1.0
 * 创建日期: 2019/7/23 16:12
 */
@Controller
@Slf4j
public class CompanyController {


    /*这是一个测

    试*/





    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;




    @Value("${contextPath}")
    String contextPath;


    /*验证码全局属性*/
    ExpiringMap<String, String> map = ExpiringMap.builder().variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    /**
     * 功能描述：获取验证码
     * 开发人员： wjf
     * 创建时间： 2019/7/25 16:02
     * 参数： [company_phonenum]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/getcode1")
    @ResponseBody
    public ResultData getCode(String company_phonenum) {
        String code = "";
        try {
            code = SendSms.sendMessage(company_phonenum);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (StringUtils.isNotBlank(code)) {
                /*设置验证码有效期6分钟*/
                map.put(company_phonenum, code, ExpirationPolicy.CREATED, 6000 * 10 * 6, TimeUnit.MILLISECONDS);
                return new ResultData().setMsg("发送成功").setStatus("400");
            }
            return new ResultData().setMsg("发送失败").setStatus("200");
        }

    }

    /**
     * 功能描述：企业注册
     * 开发人员： wjf
     * 创建时间： 2019/7/25 9:56
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/addCompany")
    @ResponseBody
    public ResultData Companyregister(Company company,HttpServletResponse response) throws Exception {
        ResultData rd = companyService.addCompany(company,response);
        return rd;

    }

    /**
     * 功能描述：设置是否为企业管理员
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/22 16:30
     * 参数：[被设置人的id user_id,设置状态码 status]
     * 返回值：com.zcyk.dto.ResultData
     */
    @RequestMapping("/setCompanyManager")
    @ResponseBody
    @Log(module = "企业信息管理", description = "设置是否为管理员")
    public ResultData setCompanyManager(String user_id, String status) throws Exception {
        ResultData resultData = companyService.setCompanyManager(user_id, status);
        return resultData;
    }

    /**
     * 功能描述：移交管理员
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 16:36
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/mvCompanyManager")
    @ResponseBody
    public ResultData mvCompanyManager(HttpServletRequest request, String user_id) throws Exception {
        return companyService.turnOverManager(user_id);

    }


    /**
     * 功能描述：注销企业
     * 开发人员： wjf
     * 创建时间： 2019/7/25 10:37
     * 参数： [company_id 企业id，login_code 验证码，company_phonenum 手机号码]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/deleteCompany")
    @ResponseBody
    @Log(module = "企业信息管理", description = "注销企业")
    public ResultData Companydelete(Company company) throws Exception {
        ResultData rd = companyService.deleteCompany(company, map.get(company.getCompany_phonenum()));
        map.remove(company.getCompany_phonenum());
        return rd;
    }

    /**
     * 功能描述：企业修改
     * 开发人员： wjf
     * 创建时间： 2019/7/25 11:45
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateCompany")
    @ResponseBody
    @Log(module = "企业信息管理", description = "修改企业信息")
    public ResultData Companyupdate(Company company) {
        try {
            ResultData rd = companyService.updateCompany(company);
            return rd;
        } catch (Exception e) {
            return new ResultData().setStatus("400").setMsg("修改失败");
        }
    }

    /**
     * 功能描述：修改企业手机号
     * 开发人员： wjf
     * 创建时间： 2019/7/25 11:45
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateCompany_phonenum")
    @ResponseBody
    @Log(module = "企业信息管理", description = "修改手机号")
    public ResultData Companyupdate_phonenum(Company company) throws Exception {
        ResultData rd = companyService.updateCompany_phonenum(company, map.get(company.getCompany_phonenum()));
        if (rd.getStatus().equals("200")) {
            map.remove(company.getCompany_phonenum());
        }
        return rd;
    }

    /**
     * 功能描述：查询企业
     * 开发人员： wjf
     * 创建时间： 2019/7/25 14:56
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/demandCompany")
    @ResponseBody
    public ResultData Companydemand(HttpServletRequest request) throws Exception {
        ResultData rd = companyService.demandCompany(userService.getNowUser(request).getCompany_id());
        return rd;
    }

    /**
     * 上传企业logo
     */
    @RequestMapping("/upload_logo")
    @ResponseBody
    public ResultData Companyupload_logo(MultipartFile file) throws Exception {
        ResultData rd = companyService.uploadCompany_logo(file);
        return rd;
    }

    /**
     * 根据路径获取图片
     *
     * @param path
     * @param response
     */
    @RequestMapping(value = "/image/get/{path:.+}")
    public void getImage(@PathVariable String path, HttpServletResponse response) throws Exception {
        companyService.getImage(path, response);
    }

    /**
     * 企业用户数量查询
     */
    @RequestMapping("/demandcout")
    @ResponseBody
    public int Companydemandcount(HttpServletRequest request) throws Exception {
        User user = userService.getNowUser(request);
        int i = companyService.selectAllUser(user.getCompany_id()).size();
        return i;
    }

    /**
     * 功能描述：移除企业人员
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 9:46
     * 参数：[ user_id 用户id]
     * 返回值：com.zcyk.dto.ResultData
     */
    @RequestMapping("/rmCompanyUser")
    @ResponseBody
    public ResultData rmCompanyUser(String user_id) throws Exception {
        return companyService.rmCompanyUser(user_id);
    }

    /**
     * 功能描述：邀请个人进企业
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 9:46
     * 参数：[ user]
     * 返回值：
     */
    @RequestMapping("/inviteOneUser")
    @ResponseBody
    public ResultData inviteOneUser(User user) throws Exception {
        ResultData resultData = companyService.inviteOneUser(user);
/*        //用户自己注册的时候也注册了一次 注意！！！！！！！if("200".equals(resultData.getStatus())){
            User oneUser = userService.selectOneUser((String) resultData.getData());
            String s2 = null;
            try {

                s2 = yunFileService.institute_user(oneUser.getCompany_id(),oneUser.getId());
                if(!"201".equals(JSONObject.parseObject(s2).getString("code"))){
                    throw new IOException("云资料同步失败");
                }
            } catch (IOException e) {
                resultData.setMsg("邀请成功，云资料同步失败，请手动一键同步").setStatus("400");
               e.printStackTrace();
            }
        }*/

//        if("200".equals(resultData.getStatus())){
//            String company_id =  ((User) resultData.getData()).getCompany_id();
//            Company company = companyService.getCompany(company_id);
//            //发送短信
//            String company_name = company.getCompany_name();
//            SendSms.inviteMessage(company_name.length()>20?company_name.substring(0,16)+"...":company_name,user.getUser_account());
//        }
        return resultData.setData(null);
    }

    /**
     * 功能描述：批量邀请进公司
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 9:48
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/inviteUserToCompany")
    @ResponseBody
    public Map<String, Object> inviteUserToCompany(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<>();
        ArrayList<Object> errorPeople = new ArrayList<>();

//        //短信通知
//        List<User> sucInviteUser = (List) map.get("sucInviteUser");
//        if(sucInviteUser.size()!=0){
//            Company company = companyService.getCompany(sucInviteUser.get(0).getCompany_id());
//            String company_name = company.getCompany_name();
//            for (int i = 0; i < sucInviteUser.size(); i++) {
//                SendSms.inviteMessage(company_name.length()>20?company_name.substring(0,16)+"...":company_name,sucInviteUser.get(i).getUser_account());
//            }
//        }
        return map;
    }

    /**
     * 功能描述：下载邀请模板
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/23 16:36
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/downloadExcel")
    @ResponseBody
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response) throws UnknownHostException, UnsupportedEncodingException, FileNotFoundException {
        File_download.downloadFile(contextPath + "企业人员表.xlsx", response, request);
    }

    /**
     * 功能描述：搜索企业人员
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/3 17:16
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/searchCompanyUser")
    @ResponseBody
    public PageInfo<User> searchCompanyUser(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            String index, HttpServletRequest request) throws Exception {
        return companyService.searchCompanyUser(pageNum, pageSize, index, request);
    }

    /**
     * 功能描述：获取所有公司名称
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/15 11:35
     * 参数：[ * @param null]
     * 返回值：
     */
    @RequestMapping("/getAllCompany")
    @ResponseBody
    public ResultData getCompany() {
        ResultData resultData = new ResultData();
        try {
            return resultData.setStatus("200").setMsg("成功").setData(companyService.getAllCompany());
        } catch (Exception e) {
            e.printStackTrace();
            return resultData.setStatus("400").setMsg("失败");
        }
    }

}
