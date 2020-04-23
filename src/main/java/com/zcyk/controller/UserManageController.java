package com.zcyk.controller;


import com.github.pagehelper.PageInfo;
import com.zcyk.dto.ResultData;
import com.zcyk.mapper.UserDepartmentMapper;
import com.zcyk.pojo.SysFileLog;
import com.zcyk.pojo.User;
import com.zcyk.pojo.UserFolder;
import com.zcyk.service.*;
import com.zcyk.util.*;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 功能描述: 登录控制器
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/7/18 13:58
 */
@Controller
@Slf4j
public class UserManageController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserfolderService userfolderService;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserDepartmentMapper userDepartmentMapper;

    @Autowired
    FileService fileService;

    @Autowired
    FileLogService fileLogService;

    @Autowired
    ProjectService projectService;



    @Autowired
    UserService userServiceImpl;
    /*验证码全局属性*/
    ExpiringMap<String, String> map = ExpiringMap.builder().variableExpiration()
            .expiration(6000 * 10 * 6, TimeUnit.MILLISECONDS)//验证码有效期6分钟
            .expirationPolicy(ExpirationPolicy.CREATED)
            .maxSize(100000)
            .build();
    /**
     * 功能描述：获取短信验证码
     * 开发人员： lyx
     * 创建时间： 2019/7/25 14:04
     * 参数： [user_account]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping(value="/getCode")
    @ResponseBody
    public ResultData getCode(String user_account,HttpServletResponse response)throws Exception{
        //是否设置恶意发送
//        ResultData resultData = userServiceImpl.judgeUser(new User().setUser_account(user_account));
//        if("404".equals(resultData.getStatus())){
//            return resultData;
//        }
        String code = "";
        try {
            code = SendSms.sendMessage(user_account);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(StringUtils.isNotBlank(code)){
            if(user_account.length()==11) {
                /*设置验证码有效期6分钟*/
                map.put(user_account, code);
                response.setHeader("num_code", code);
                return new ResultData().setMsg("发送成功").setStatus("400");
            }else if(user_account.length()==17){
                map.put(user_account, code);
                response.setHeader("num_code", code);
                return new ResultData().setMsg("发送成功").setStatus("400").setData(code);
            }
        }
//
        return new ResultData().setMsg("发送失败").setStatus("200");
    }


    /**
     * 功能描述：图片验证码
     * 开发人员： lyx
     * 创建时间： 2019/7/25 17:54
     */
    @RequestMapping("/getCodePic")
    @ResponseBody
    public void pic(HttpServletResponse response)throws Exception{
        ValidateCode vCode = new ValidateCode(160,50,5,150);//获取图片
        response.setContentType("image/png;charset=utf-8");
        request.getSession().setAttribute("code",vCode.getCode());//将验证码存入session
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Access-Control-Expose-Headers", "SessionId,login_code,Content-Type");
//            response.setHeader("SessionId", GetUserUtils.getSessionId(request));//发送SessionId到前端
//            response.setHeader("login_code",vCode.getCode());
            vCode.write(outputStream);
        } catch (IOException e) {
            log.error("登录获取图片验证码失败",e);
            e.printStackTrace();
        }
    }
    /**
     * 功能描述：登录
     * http://localhost:8080/login?user_account=13996241752&user_password=123456&login_code=DY45V
     * 开发人员： lyx
     * 创建时间： 2019/7/24 17:54
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/login")
    @ResponseBody
    public ResultData login(User user,HttpServletRequest request,HttpServletResponse response) throws Exception {
        ResultData resultData = userServiceImpl.userLoginByPwd(user,response);
        User nowUser = (User) resultData.getData();
        //将用户容量返回
        if("200".equals(resultData.getStatus())){
            if(userDepartmentMapper.judeManager(nowUser.getId())!=null && userDepartmentMapper.judeManager(nowUser.getId()).getIsdepartmentmanager()==1) {
                nowUser.setIsdepartmentmanager(1);
            }
            //设置用户企业文件夹：有企业，且个人文件夹为空的时候新建个人文件夹
            UserFolder thisFolder = userfolderService.selectUserfolderByParentId(nowUser.getId());
            boolean haveFolder = thisFolder == null || thisFolder.getFolder_statu() == 0;//没有个人文件夹或者已经退出公司个人文件夹不能用
            if(StringUtils.isNotBlank(nowUser.getCompany_id())&& haveFolder){
                userfolderService.addUserTopfolder(nowUser.getId());
            }
            UserFolder userFolder = userfolderService.selectUserfolderByParentId(nowUser.getId());
            Map<String, Object> map = fileService.folderSize(nowUser.getId());
            nowUser.setFoldersize((BigDecimal)map.get("userSize")).setSize_type((String)map.get("size_type"));
        }
        return resultData.setData(nowUser);

    }

    /**
     * 功能描述：退出登录
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/loginOut")
    @ResponseBody
    public void loginOut(HttpServletRequest request)throws Exception{
        request.getSession().invalidate();
    }



    /**
     * 功能描述：注册
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/singin")
    @ResponseBody
    public ResultData singin(User user)throws Exception{
        ResultData resultData = userServiceImpl.activeUser(user, map.get(user.getUser_account()));
        if("200".equals(resultData.getStatus()))map.remove(user.getUser_account());

        return resultData;
    }

    /**
     * 功能描述：忘记密码
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user  ！！传过来的user账号要是新账号]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/forgetPwd")
    @ResponseBody
    public ResultData forgetPwd(User user)throws Exception{

        ResultData resultData = userServiceImpl.forgetUserPwd(user, map.get(user.getUser_account()));
        if("200".equals(resultData.getStatus()))map.remove(user.getUser_account());
        return resultData;

    }
    /**
     * 功能描述：修改密码
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user  old_password 新密码]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updatePwd")
    @ResponseBody
    public ResultData updatePwd(User user, String new_password)throws Exception{

        return  userServiceImpl.updateUserPwd(user, new_password);

    }
    /**
     * 功能描述：修改账号
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateAccount")
    @ResponseBody
    public ResultData updateAccount(User user)throws Exception{
        ResultData resultData =  userServiceImpl.updateUserAccount(user, map.get(user.getUser_account()));
        if("200".equals(resultData.getStatus()))map.remove(user.getUser_account());
        if("200".equals(resultData.getStatus())){//修改项目信息
            projectService.updateRoleAccount(user.getUser_account());
        }

        return resultData;

    }

    /**
     * 功能描述：修改姓名
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user id user_account]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateName")
    @ResponseBody
    public ResultData updateName(User user)throws Exception{
        return  userServiceImpl.updateUserName(user);

    }
    /**
     * 功能描述：修改性别
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数：  性别 sex:1 男 0女 用户id
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/updateSex")
    @ResponseBody
    public ResultData updateSex(User user)throws Exception{
        return  userServiceImpl.updateUserSex(user);

    }

    /**
     * 功能描述：查询个人信息
     * 开发人员： lyx
     * 创建时间： 2019/7/25 10:42
     * 参数： [user id user_account]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("/getOneUser")
    @ResponseBody
    public User selectOneUser(String id)throws Exception{
        User user = userServiceImpl.selectOneUser(id);
        Map<String, Object> map = fileService.folderSize(id);
        BigDecimal size = (BigDecimal) map.get("userSize");
        String size_type = (String) map.get("size_type");
        user.setFoldersize(size).setSize_type(size_type);
        return user;

    }




    /**
     * 功能描述：查询日志
     * 开发人员： lyx
     * 创建时间： 2019/8/26 19:11
     */
    @RequestMapping("getAllLog")
    @ResponseBody
    public PageInfo<SysFileLog> getAllLog(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,String search)throws Exception{
        return fileLogService.getAllLog(pageNum,pageSize,search);
    }
}
