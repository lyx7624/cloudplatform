package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.User;
import com.zcyk.dto.ResultData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 功能描述:
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: xlyx
 * 版本日志: 1.0
 * 创建日期: 2019/7/22 17:32
 */
public interface UserService {
    /*判断用户情况*/
    ResultData judgeUser(User user)throws Exception;

    /*用户注册:需要验证码*/
    ResultData activeUser(User user, String code)throws Exception;

    /*用户密码登录:需要验证码*/
    ResultData userLoginByPwd(User user, HttpServletResponse response)throws Exception;

    /*用户验证码登录*/
    ResultData userLoginByMsg(User user, String code);

    /*批量邀请*/
    ResultData inviteUser(MultipartFile file) throws Exception;

    /*用户修改密码:需要验证码*/
    ResultData updateUserPwd(User user, String new_password);

    /*用户忘记密码:需要验证码*/
    ResultData forgetUserPwd(User user, String code);

    /*修改用户手机号:需要验证码*/
    ResultData updateUserAccount(User user, String code);

    /*根据主键修改用户*/
    ResultData updataUser(User user);

    /*根据企业ID查询企业员工总和*/
    List<User> selectCompanyUser(String company_id);

    /*根据id获取user*/
    User selectOneUser(String id);

    /*万能查询方法*/
    List<User> selcetUserByAny(User user);

    /*万能修改方法*/
    ResultData updateUserByAny(User user);

    /*通过用户名查询用户*/
    User getUserByAccount(String user_account);

    /*修改用户名*/
    ResultData updateUserName(User user);

    /*根据企业ID查询企业员工*/
    PageInfo<User> selectAllUser(int pageNum, int pageSize, String company_id);

    /*修改性别*/
    ResultData updateUserSex(User user);

    /*获取公司所有人员*/
    List<User> getAllUser(String company_id);


    /*获取当前登录用户*/
    User getNowUser (HttpServletRequest request);
}
