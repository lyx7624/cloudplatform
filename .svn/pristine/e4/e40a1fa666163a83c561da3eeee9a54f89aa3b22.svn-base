package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.LoginUser;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.User;
import com.zcyk.service.UserService;
import com.zcyk.dto.LoginUserMap;
import com.zcyk.util.JwtUtil;
import com.zcyk.util.ReadExcel;
import com.zcyk.dto.ResultData;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/7/22 17:45
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Value("${user.token.expire.seconds}")
    Long EXPIRE_SECONDS;


    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest request;
    /**
     * 功能描述：注册用户
     * 开发人员： lyx
     * 创建时间： 2019/7/23 9:12
     * 参数： [user code 服务器验证码]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData  activeUser(User user, String code) {
        ResultData rd = new ResultData();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        //根据用户账号
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_account",user.getUser_account());
        ResultData resultData = judgeUser(user);//判断用户状态
        user.setUser_status(1);//设置用户状态
        int i;//修改结果判断
        if(code!=null){
            if(code.equals(user.getLogin_code())){//验证码正确
                switch (resultData.getStatus()){
                    //已注册直接返回
                    case "204":
                    case "202":
                        rd.setMsg("已注册");
                        rd.setStatus("401");
                        break;
                    //未激活进行激活
                    case "201":
                    case "203":
                        // 加密
                        //user.setUser_password(new BCryptPasswordEncoder().encode(user.getUser_password().trim()));
                        user.setUser_createtime(dateFormat.format(date));
                        i = userMapper.updateByExampleSelective(user, example);
                        if(i!=0){
                            String id = userMapper.selectUserByAccount(user.getUser_account()).getId();
                            request.getSession().setAttribute("user_id",id);
                            rd.setStatus("200");
                            rd.setMsg("注册成功");
                            rd.setData(id);
                        }else{
                            rd.setStatus("400");
                            rd.setMsg("注册失败");
                        }
                        break;
                    //未注册未被邀请 ，产生新的用户
                    case "404":
                        /*手动设置用户的相关信息*/
                        // 加密
                        //user.setUser_password(new BCryptPasswordEncoder().encode(user.getUser_password().trim()));
                        user.setId(UUID.randomUUID().toString())
                                .setUser_createtime(dateFormat.format(date))
                                .setIsdepartmentmanager(0);
                        i = userMapper.insertSelective(user);
                        if(i!=0){
                            rd.setStatus("200");
                            rd.setMsg("注册成功");
                            request.getSession().setAttribute("user_id",user.getId());
                        }else{
                            rd.setStatus("400");
                            rd.setMsg("注册失败");
                        }
                        break;
                }

            }else{
                rd.setStatus("402");
                rd.setMsg("验证码错误");
            }
        }else{
            rd.setStatus("403");
            rd.setMsg("验证码过期");
        }


        return rd;
    }


    /**
     * 功能描述：判断用户状态
     * 开发人员： lyx
     * 创建时间： 2019/7/22 17:54
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData judgeUser(User user){
        ResultData rd = new ResultData();
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_account",user.getUser_account());
        User nowUser = userMapper.selectOneByExample(example);
        if (nowUser != null) {//不考虑已被邀请就退出企业（状态为0且没有企业），那样只能自己进入企业再退出
            rd.setData(nowUser);//返回当前用户
            if (nowUser.getUser_status() == 1 && isBlank(nowUser.getCompany_id())) {//判断是是否加入了企业
                rd.setStatus("204");
                rd.setMsg("已注册未加入企业");
            } else if (nowUser.getUser_status() == 0&& isNotBlank(nowUser.getCompany_id())) {
                rd.setStatus("201");
                rd.setMsg("已被邀请未激活");
            }else if (nowUser.getUser_status() == 0&& isBlank(nowUser.getCompany_id())) {
                rd.setStatus("203");
                rd.setMsg("已被邀未激活请就退出企业");
            }else if (nowUser.getUser_status() == 1&&isNotBlank(nowUser.getCompany_id())) {
                rd.setStatus("202");
                rd.setMsg("已注册且加入了企业");
            }
        } else {
            rd.setStatus("404");
            rd.setMsg("没有注册或被邀请");
        }
        return rd;
    }



    /**
     * 功能描述：邀请用户进入公司通过Excel方式批量导入
     * 开发人员： lyx
     * 创建时间： 2019/7/23 10:19
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData inviteUser(MultipartFile file) throws Exception {
        User nowUser = getNowUser(request);
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResultData rd = new ResultData();
        List<User> userList = ReadExcel.readExcelContentz(file);
        List<User> unInviteUser = new ArrayList<>();
        int i = 0;//判断是否全部添加
        for (User user: userList) {
            //先判断用户是否已经在其他企业，在进行添加
            ResultData resultData = judgeUser(user);
            if("400".equals(resultData.getStatus())){//未在其他企业，且未注册，-->进行注册
                //设置用户信息
                Date date = new Date();
                user.setUser_createtime(dateFormat.format(date));
                user.setId(UUID.randomUUID().toString());
                //还应该有个部门列！！！！！
                user.setUser_status(0);
                user.setCompany_id(nowUser.getCompany_id());
                user.setIscompanymanager(0);
                userMapper.insertSelective(user);
                i++;
            }else if("204".equals(resultData.getStatus()) ||"200".equals(resultData.getStatus()) ||"203".equals(resultData.getStatus())){//已注册，未在企业--->修改所在企业id
                user.setCompany_id(nowUser.getCompany_id());
                Example example = new Example(User.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("user_account",user.getUser_account());
                userMapper.updateByExampleSelective(user,example);
                i++;
            }else{//已在其他企业，返回具体哪些人
                unInviteUser.add(user);
            }
        }
        if(i==userList.size()){
            rd.setStatus("200");
            rd.setMsg("添加成功");
        }else if (i<userList.size()){
            rd.setStatus("400");
            rd.setMsg("部分用户未添加成功，已在其他企业注册");
            rd.setData(unInviteUser);
        }
        return rd;
    }





    /**
     * 功能描述：用户密码登录,情况：1.未激活登录提示进行注册2.已激活就登录
     * 开发人员： lyx
     * 创建时间： 2019/7/24 9:51
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData userLoginByPwd(User user, HttpServletResponse response){
        ResultData rd = new ResultData();
        ResultData resultData = judgeUser(user);
        User data = (User) resultData.getData();
        if(user.getLogin_code()==null){
            rd.setMsg("验证码为空");
            rd.setStatus("405");
            return rd;
        }
        if(user.getLogin_code().equals(request.getSession().getAttribute("code"))){//判断验证码是否正确
            switch (Integer.valueOf(resultData.getStatus())){
                case 404:return resultData;
                case 203:
                case 204:
                case 201:
                    if(null!=data.getUser_password() && data.getUser_password().equals(user.getUser_password())) {//登录
                        //未注册或者未激活的状态
                        LoginUser loginUser =(LoginUser) new LoginUser()
                                .setExpireTime(System.currentTimeMillis() + EXPIRE_SECONDS)
                                .setId(data.getId());
                        String jwtToken = JwtUtil.createJWTToken(loginUser);
                        response.setHeader("user_token",jwtToken);
//                        //放到内存中 用于异地登录判断
                        LoginUserMap.setLoginUsers(data.getId(), jwtToken);

//                        request.getSession().setAttribute("user_id",data.getId());
//                        LoginUserMap.setLoginUsers(data.getId(), request.getSession().getId());
//                        response.setHeader("user_token",data.getId());





                    }
                    return resultData;
                case 202:
                    if(data.getUser_password().equals(user.getUser_password())){//已经注册且激活，判断密码是否正确


//
//                        request.getSession().setAttribute("user_id",data.getId());
//                        LoginUserMap.setLoginUsers(data.getId(), request.getSession().getId());
//                        response.setHeader("user_token",data.getId());

                        //保存sessionId到map中
                        //放入map中
                        LoginUser loginUser =(LoginUser) new LoginUser()
                                .setExpireTime(System.currentTimeMillis() + EXPIRE_SECONDS)
                                .setId(data.getId());
                        String jwtToken = JwtUtil.createJWTToken(loginUser);
                        response.setHeader("user_token",jwtToken);
//                        //放到内存中 用于异地登录判断
                        LoginUserMap.setLoginUsers(data.getId(), jwtToken);

                        rd.setMsg("登录成功");
                        rd.setStatus("200");
                        rd.setData(data);
                    }else {
                        rd.setMsg("密码错误");
                        rd.setStatus("401");
                    }
            }
        }else{
            rd.setMsg("验证码错误");
            rd.setStatus("402");
        }


        return rd;
    }



    /**
     * 功能描述：短信验证登录
     * 开发人员： lyx
     * 创建时间： 2019/7/24 10:57
     * 参数： [user]  服务器code保存的验证码
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData userLoginByMsg(User user, String code){
        ResultData rd = new ResultData();
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_account",user.getUser_account());
        User nowUser = userMapper.selectOneByExample(example);
        if(nowUser!=null){
            if(code.equals(user.getLogin_code())){//判断用户验证码是否正确
                rd.setStatus("200");
                rd.setMsg("登陆成功");
                rd.setData(nowUser);
            }else{
                rd.setStatus("402");
                rd.setMsg("验证码不正确");
            }
        }else{
            rd.setStatus("404");
            rd.setMsg("账号不存在");
        }
        return rd;
    }


    /**
     * 功能描述：修改密码
     * 开发人员： lyx
     * 创建时间： 2019/7/30 11:32
     * 参数： [user, code]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData updateUserPwd(User user, String new_password) {
        ResultData rd = new ResultData();
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_account",user.getUser_account());//根据什么修改看情况
        User nowUser = userMapper.selectOneByExample(example);
        if(!user.getUser_password().equals(nowUser.getUser_password())){
            rd.setStatus("404");
            rd.setMsg("密码错误");
        }else{
            nowUser.setUser_password(new_password);
            int i = userMapper.updateByExampleSelective(nowUser, example);
            if(i!=0){
                rd.setStatus("200");
                rd.setMsg("修改成功");
            }else{
                rd.setStatus("400");
                rd.setMsg("修改失败");
            }
        }

        return rd;
    }

    /**
     * 功能描述：用户忘记密码，修改密码
     * 开发人员： lyx
     * 创建时间： 2019/7/24 17:04
     * 参数： [user, code]修改的用户，服务器保存的验证码
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData forgetUserPwd(User user, String code){
        ResultData rd = new ResultData();
        ResultData userData = judgeUser(user);//判断用户状态
        if("404".equals(userData.getStatus())){//未注册
            rd.setStatus("404");
            rd.setMsg("用户未注册");
        }else{
            if(code!=null){
                if(code.equals(user.getLogin_code())){//验证码验证
                    Example example = new Example(User.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("user_account",user.getUser_account());
                    int i = userMapper.updateByExampleSelective(user, example);
                    if(i!=0){
                        rd.setMsg("修改成功");
                        rd.setStatus("200");
                    }else {
                        rd.setMsg("修改失败");
                        rd.setStatus("400");
                    }
                }else{
                    rd.setMsg("验证码错误");
                    rd.setStatus("402");
                }
            }else {
                rd.setMsg("验证码已过期");
                rd.setStatus("403");
            }

        }
        return rd;
    }


    /**
     * 功能描述：修改账户（电话号码）
     * 开发人员： lyx
     * 创建时间： 2019/7/30 11:30
     * 参数： [user 用户, code 服务器验证码]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData updateUserAccount(User user, String code) {

        ResultData rd = new ResultData();
        User thisUser = userMapper.selectUserByAccount(user.getUser_account());
        if(thisUser!=null){
            rd.setMsg("此号码已经被注册");
            rd.setStatus("401");
            return rd;
        }

        if(code!=null){
            if(code.equals(user.getLogin_code())) {//验证码验证
                int i = userMapper.updateByPrimaryKeySelective(user);
                if(i!=0){
                    rd.setMsg("修改成功");
                    rd.setStatus("200");
                }else {
                    rd.setMsg("修改失败");
                    rd.setStatus("400");
                }
            }else{
                rd.setMsg("验证码错误");
                rd.setStatus("402");
            }
        }else {
            rd.setMsg("验证码已过期");
            rd.setStatus("403");
        }

        return rd;
    }

    /**
     * 功能描述：修改是否是企业管理员
     * 开发人员： lyx
     * 创建时间： 2019/7/30 11:30
     * 参数： [user 用户, code 服务器验证码]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData changeManager(User user) {
        ResultData rd = new ResultData();
        user.setIscompanymanager(1);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_account",user.getUser_account());
        int i = userMapper.updateByExample(user, example);
        if(i!=0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setMsg("修改 失败");
            rd.setStatus("400");
        }
        return rd;
    }

    /**
     * 功能描述：根据userid 修改user信息
     * 开发人员： lyx
     * 创建时间： 2019/7/31 13:55
     * 参数： [user ]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData updataUser(User user){
        ResultData rd = new ResultData();
        int i = userMapper.updateByPrimaryKeySelective(user);
        if(i!=0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }


    /**
     * 功能描述：根据企业id查询企业人员总和
     * 开发人员： lyx
     * 创建时间： 2019/7/31 14:08
     * 参数： [user]
     * 返回值： java.lang.Integer
     * 异常：
     */
    public List<User> selectCompanyUser(String company_id){

        return userMapper.selectCompanyAllUser(company_id);

    }

    /**
     * 功能描述：根据id查找user
     * 开发人员： lyx
     * 创建时间： 2019/8/8 9:19
     * 参数： [id]
     * 返回值： com.zcyk.pojo.User
     * 异常：
     */
    public User selectOneUser(String id) {
        return userMapper.selectByPrimaryKey(id);

    }

    /**
     * 功能描述：根据用户查询用户信息
     * 开发人员：Wujiefeng
     * 创建时间：2019/7/31 15:43
     * 参数：[ * @param null]
     * 返回值：
     */
    public  List<User> selcetUserByAny(User user){
        List<User> users = userMapper.select(user);
        return users;
    }

    /**
     * 功能描述：根据id修改任意属性
     * 开发人员： lyx
     * 创建时间： 2019/8/8 8:54
     * 参数： [user ]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData updateUserByAny(User user) {
        ResultData rd = new ResultData();
        int i = userMapper.updateByPrimaryKeySelective(user);
        if(i!=0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }



    /**
     * 功能描述：查询该企业下所有员工
     * 开发人员： lyx
     * 创建时间： 2019/8/21 14:20
     * 异常：
     */
    public  PageInfo<User> selectAllUser(int pageNum, int pageSize,String company_id) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAllUser(company_id);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    /**
     * 功能描述：修改用户名
     * 开发人员： lyx
     * 创建时间： 2019/8/13 9:56
     * 参数：
     * 返回值：
     * 异常：
     */
    public ResultData updateUserName(User user) {
        ResultData rd = new ResultData();
        int i = userMapper.updateByPrimaryKeySelective(user);
        if(i!=0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }


    /**
     * 功能描述：修改性别
     * 开发人员： lyx
     * 创建时间： 2019/9/9 14:07
     * 参数： 性别 sex:1 男 0女 用户id
     * 返回值：
     * 异常：
     */
    public ResultData updateUserSex(User user) {
        ResultData rd = new ResultData();
        int i =  userMapper.updateByPrimaryKeySelective(user);
        if(i!=0){
            rd.setMsg("修改成功");
            rd.setStatus("200");
        }else{
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }

    /**
     * 功能描述：通过用户名查询用户
     * 开发人员： lyx
     * 创建时间： 2019/8/13 9:56
     * 参数：
     * 返回值：
     * 异常：
     */
    public User getUserByAccount(String useraccount) {
        return userMapper.selectUserByAccount(useraccount);
    }



    /*获取公司所有人员*/
    public List<User> getAllUser(String company_id) {
        return userMapper.selectAllUser(company_id);
    }

    @Override
    public User getNowUser(HttpServletRequest request) {
        String user_id =(String) request.getSession().getAttribute("user_id");
        String user_token = request.getHeader("user_token");
        Map<String, Object> mapFromJWT = JwtUtil.getMapFromJWT(user_token);
        return userMapper.selectUserById(MapUtils.getString(mapFromJWT,"LOGIN_USER_KEY"));
//          String user_token = request.getHeader("user_token");
//          return userMapper.selectUserById(user_token);

    }
}