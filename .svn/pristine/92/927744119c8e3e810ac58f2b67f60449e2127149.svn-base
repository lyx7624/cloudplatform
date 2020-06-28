package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.dto.LoginUser;
import com.zcyk.dto.LoginUserMap;
import com.zcyk.dto.ResultData;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.*;
import com.zcyk.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 企业注册
 * 开发人员：WuJieFeng
 * 创建日期：2019/7/23 16:10
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    @Value("${user.token.expire.seconds}")
    Long EXPIRE_SECONDS;


    @Autowired
    CompanyMapper companyMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    UserService userService;
    @Autowired
    CompanyfolderService companyfolderService;
    @Autowired
    FileMapper fileMapper;
    @Autowired
    UserDepartmentMapper userDepartmentMapper;
    @Autowired
    UserfolderMapper userfolderMapper;

    @Autowired
    UserfolderService userfolderService;
    @Autowired
    DepartmentMapper departmentMapper;


    @Autowired
    UserProjectRoleMapper userProjectRoleMapper;
    @Autowired
    DepartmentFolderService departmentFolderService;
    @Autowired
    CompanyfolderMapper companyfolderMapper;
    @Autowired
    DepartmentFolderMapper departmentFolderMapper;
    @Autowired
    ProjectFolderMapper projectFolderMapper;
    @Autowired
    ProjectfolderService projectfolderService;

    @Autowired
    FileService fileService;


    //上传企业logo的路径
    @Value(value = "${contextPath}")
    public String contextPath;

    @Override

    /**
     * 功能描述：企业注册
     * 开发人员：wjf
     * 创建时间： 2019/7/25 9:12
     * 参数： [company code 服务器验证码]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData addCompany(Company company,HttpServletResponse response) throws Exception {
        ResultData rd = new ResultData();
        Company cp = companyMapper.checkCompanyName(company.getCompany_name());
        if(cp!=null){
            return rd.setStatus("401").setMsg("该企业已注册");
        }
        /*获取注册人信息*/
        User user = userService.getNowUser(request);
        /*企业联系人姓名*/
        company.setCompany_people(user.getUser_name());
        /*企业联系人电话*/
        company.setCompany_phonenum(user.getUser_account());
        /*企业创建时间*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*企业到期时间*/
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.YEAR, 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = df.format(instance.getTime());
        /*企业编码*/
        int i = 1;
        String str = String.format("%03d", i);
        /*默认企业容量*/
        BigDecimal qyrl = new BigDecimal("20");

        company.setCompany_capacity(qyrl)/*默认企业容量*/
                .setCompany_endtime(s)/*企业到期时间*/
                .setCompany_id(UUID.randomUUID().toString())/*企业id*/
                .setCompany_status(1)/*企业状态*/
                .setCompany_createtime(sdf.format(new Date()))/*企业创建时间*/
                .setCompany_code(str)
                .setCompany_logo_url("logo.png");/*企业编码*/
        /*设为企业管理员*/
        user.setCompany_id(company.getCompany_id());
        user.setIscompanymanager(1);

        /*注册时创建一个企业文件夹*/
        CompanyPublicfolder folder = new CompanyPublicfolder();
        folder.setFolder_createuser(user.getUser_name())
                .setParent_id(null);
        companyfolderService.addTopCompanyfolder(company.getCompany_id());
        /*在企业文件夹类创建一个文件*/
        Integer statu = companyMapper.insert(company);
        userService.updataUser(user);
        if (statu != 0) {
            rd.setStatus("200");
            rd.setMsg("注册成功");
            //因为要直接跳转到首页，返回用户和保存用户
            rd.setData(user.setIscompanymanager(1).setFoldersize(new BigDecimal(0)).setCompany_id(company.getCompany_id()));
            //放入map中
            LoginUser loginUser =(LoginUser) new LoginUser()
                    .setExpireTime(System.currentTimeMillis() + EXPIRE_SECONDS)
                    .setId(user.getId());
            String jwtToken = JwtUtil.createJWTToken(loginUser);
            response.setHeader("user_token",jwtToken);
            //放到内存中 用于异地登录判断
            LoginUserMap.setLoginUsers(user.getId(), jwtToken);

//            request.getSession().setAttribute("user_id",user.getId());
//            LoginUserMap.setLoginUsers(user.getId(), request.getSession().getId());
//            response.setHeader("user_token",user.getId());


            /*注册时创建一个用户文件夹*/
            userfolderService.addUserTopfolder(user.getId());
        } else {
            rd.setStatus("400");
            rd.setMsg("注册失败，请重新注册");
        }
        return rd;
    }

    /**
     * 功能描述：设置是否是企业管理员
     * 开发人员：Wujiefeng
     * 创建时间：2019/8/22 16:11
     * 参数：[ 被设置用户的 user_id，设置的状态码 status]
     * 返回值：com.zcyk.dto.ResultData
     */
    public ResultData setCompanyManager(String user_id, String status) {
        ResultData rd = new ResultData();
        int statu = Integer.parseInt(status);
        User nowUser = userService.getNowUser(request);
        if ( nowUser.getIscompanymanager() != 0) {
            if(nowUser.getId().equals(user_id)){
                return rd.setStatus("402").setMsg("不能设置自己");
            }
            if (userMapper.setCompanyManager(user_id, statu) != 0) {
                rd.setStatus("200");
                rd.setMsg("设置成功");
            } else {
                rd.setStatus("400");
                rd.setMsg("设置失败");
            }
        } else {
            rd.setStatus("401");
            rd.setMsg("你不是企业管理员");
        }
        return rd;
    }


    /**
     * 功能描述：移交管理员
     * 开发人员： lyx
     * 创建时间： 2019/9/17 9:32
     */
    public ResultData turnOverManager(String user_id) {
        ResultData rd = new ResultData();
        User nowUser = userService.getNowUser(request);
        if ( nowUser.getIscompanymanager() != 0) {
            if(nowUser.getIscompanymanager()==1){
                return rd.setStatus("402").setMsg("不能移交给管理员");
            }
            if (userMapper.setCompanyManager(user_id,1)!=0) {
                //将自己设置成不是管理员
                userMapper.setCompanyManager(nowUser.getId(),0);
                rd.setStatus("200");
                rd.setMsg("设置成功");
            } else {
                rd.setStatus("400");
                rd.setMsg("设置失败");
            }
        } else {
            rd.setStatus("401");
            rd.setMsg("你不是企业管理员");
        }
        return rd;
    }



    /**
     * 功能描述：注销企业（status设为0）
     * 开发人员： wjf
     * 创建时间： 2019/7/23 10:30
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */

    public ResultData deleteCompany(Company company, String code) throws Exception {
        ResultData rd = new ResultData();
        //根据企业id删除
        Example e = new Example(Company.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("company_id", company.getCompany_id());
        //删除：设置企业状态码为0
        company.setCompany_status(0);
        if (code != null) {
            System.out.println(company.getLogin_code());
            if (code.equals(company.getLogin_code())) {//判断验证码是否正确
                if (1 != company.getCompany_status()) {
                    //1、删除企业云盘的所有文件夹及文件
                    List<String> ids = new ArrayList<>();
                    //企业id作为父id 找到企业顶级文件夹
                    CompanyPublicfolder companyPublicfolder = companyfolderMapper.selectCompanyfolderByParentId(company.getCompany_id()).get(0);
                    ids.add(companyPublicfolder.getId());
                    companyfolderService.deleteCompanyfolder(ids, company.getCompany_id());
                    //2、删除部门云盘所有文件夹及文件
                    List<String> department_ids = new ArrayList<>();
                    //找到企业下所有部门
                    List<Department> departments = departmentMapper.selectDepartmentByCompanyId(company.getCompany_id());
                    for (Department department : departments) {
                        //部门id作为父id 找到所有部门的顶级文件夹
                        DepartmentFolder departmentFolder = departmentFolderMapper.selectDepartFloderByParentId(department.getId()).get(0);
                        department_ids.add(departmentFolder.getId());
                        departmentFolderService.deleteDepartmentfolder(department_ids, department.getId());
                    }
                    //3、删除个人文件夹及文件
                    List<String> user_ids = new ArrayList<>();
                    //找到企业下所有用户
                    List<User> users = userMapper.selectAllUser(company.getCompany_id());
                    for (User user : users) {
                        //用户id作为父id 找到所以有个人顶级文件夹
                        UserFolder userFolder = userfolderMapper.selectUserfolderByparent_Id(user.getId()).get(0);
                        user_ids.add(userFolder.getId());
                        userfolderService.deleteUserfolder(user_ids, user.getId());
                    }

                    //4、删除所有项目文件夹及文件
                    List<String> project_ids = new ArrayList<>();
                    //找到企业下所有项目

                    //5、删除所有部门
                    departmentMapper.deleteDepartmentByCompanyId(company.getCompany_id());
                    //6、删除用户部门表信息
                    for (Department department : departments) {
                        userDepartmentMapper.deleteDepartmentAllUser(department.getId());
                    }
                    //8、删除用户项目表信息

                    //9、删除所有用户
                    /*lyx修改：删除企业 应该设置用户企业为空而不是设置用户状态为0*/
                    userMapper.deleteUserByCompanyId(company.getCompany_id());
                    //10、删除企业
                    companyMapper.updateByExampleSelective(company, e);
                    rd.setStatus("200").setMsg("注销成功");
                } else {
                    rd.setStatus("400");
                    rd.setMsg("企业删除失败");
                }
            } else {
                rd.setStatus("402");
                rd.setMsg("验证码错误，请重试");
            }
        } else {
            rd.setStatus("403");
            rd.setMsg("验证码已过期");
        }
        return rd;
    }

    /**
     * 功能描述：修改企业联系人手机
     * 开发人员： wjf
     * 创建时间： 2019/7/26 11:26
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData updateCompany_phonenum(Company company, String code) {
        ResultData rd = new ResultData();
        //根据企业id修改
        Example e = new Example(Company.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("company_id", company.getCompany_id());
        if (code != null) {
            System.out.println(company.getLogin_code());
            if (code.equals(company.getLogin_code())) {//判断验证码是否正确
                int i = companyMapper.updateByExampleSelective(company, e);
                if (i != 0) {
                    rd.setStatus("200");
                    rd.setMsg("修改成功");
                } else {
                    rd.setStatus("400");
                    rd.setMsg("修改失败");
                }
            } else {
                rd.setStatus("402");
                rd.setMsg("验证码错误，请重试");
            }
        } else {
            rd.setStatus("403");
            rd.setMsg("验证码已过期");
        }
        return rd;
    }


    /**
     * 功能描述：修改企业
     * 开发人员： wjf
     * 创建时间： 2019/7/23 11:26
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData updateCompany(Company company) {
        ResultData rd = new ResultData();
        if(company.getCompany_name()!=null) {
            Company cp = companyMapper.selectOne(new Company().setCompany_name(company.getCompany_name()));
//        System.out.println(s);
//        Company cp = companyMapper.checkCompanyName(company.getCompany_name());
            Company Ocp = companyMapper.selectByPrimaryKey(company.getCompany_id());
            if (cp != null && !cp.getCompany_id().equals(Ocp.getCompany_id())) {
                return rd.setStatus("401").setMsg("该企业名已注册");
            }
        }
        //根据企业id修改
        User user = userService.getNowUser(request);
        if (0 == user.getIscompanymanager()) {
            return rd.setStatus("401").setMsg("没有企业管理员权限");
        }
        Example e = new Example(Company.class);
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("company_id", user.getCompany_id());
        int i = companyMapper.updateByExampleSelective(company, e);
        if (i != 0) {
            rd.setStatus("200");
            rd.setMsg("修改成功");
        } else {
            rd.setStatus("400");
            rd.setMsg("修改失败");
        }
        return rd;
    }

    /**
     * 功能描述：企业查询
     * 开发人员： wjf
     * 创建时间： 2019/7/23 14:26
     * 参数： [company]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData demandCompany(String company_id) {
        ResultData rd = new ResultData();
        BigDecimal size = new BigDecimal(0);

        //根据企业id查询
        Company company1 = companyMapper.selectByPrimaryKey(company_id);

        List<User> users = userMapper.selectAllUser(company_id);
        for (User user : users) {
            List<File> allfile = fileMapper.Allfile(user.getId());
            for (File file : allfile) {
                size = size.add(file.getFile_size());
//                System.out.println(size);
            }
        }
        //统计企业容量
        BigDecimal a = new BigDecimal(1024);
        if (size.compareTo(a) == -1) {
            company1.setCompany_size(size).setSize_type("b");
            if (company1 != null) {
                rd.setStatus("200");
                rd.setMsg("查询成功");
                rd.setData(company1);
            } else {
                rd.setStatus("400");
                rd.setStatus("查询失败");
            }
            return rd;
        } else {
            size = size.divide(a, 0, ROUND_HALF_UP);
        }
        if (size.compareTo(a) == -1) {
            company1.setCompany_size(size).setSize_type("KB");
            if (company1 != null) {
                rd.setStatus("200");
                rd.setMsg("查询成功");
                rd.setData(company1);
            } else {
                rd.setStatus("400");
                rd.setStatus("查询失败");
            }
            return rd;
        } else {
            size = size.divide(a, 0, ROUND_HALF_UP);
        }
        if (size.compareTo(a) == -1) {
            company1.setCompany_size(size).setSize_type("MB");
            if (company1 != null) {
                rd.setStatus("200");
                rd.setMsg("查询成功");
                rd.setData(company1);
            } else {
                rd.setStatus("400");
                rd.setStatus("查询失败");
            }
            return rd;
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size.divide(a, 2, ROUND_HALF_UP);
            company1.setCompany_size(size).setSize_type("GB");
            if (company1 != null) {
                rd.setStatus("200");
                rd.setMsg("查询成功");
                rd.setData(company1);
            } else {
                rd.setStatus("400");
                rd.setStatus("查询失败");
            }
            return rd;
        }
    }

    /**
     * 功能描述：企业用户人数查询
     * 开发人员： wjf
     * 创建时间： 2019/7/30 9:14
     * 参数： [company]
     * 返回值： i
     */
    public List<User> demandCompanycount(String company_id) {

        return userService.selectCompanyUser(company_id);
    }

    /**
     * 功能描述：企业logo上传
     * 开发人员： wjf
     * 创建时间： 2019/7/30 15:26
     * 参数： [file]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData uploadCompany_logo(MultipartFile file) {

        ResultData rd = new ResultData();
        User user = userService.getNowUser(request);

        if (0 == user.getIscompanymanager()) {
            return rd.setStatus("401").setMsg("没有企业管理员权限");
        }
        Example example = new Example(Company.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("company_id", user.getCompany_id());
        if (file.isEmpty()) {
            rd.setStatus("401");
            rd.setMsg("文件为空，上传失败");
        }
        try {
            String url = fileService.upFileToServer(file, contextPath, null);
//             String url = file.getOriginalFilename();
            companyMapper.updateByExampleSelective(new Company().setCompany_logo_url(URLEncoder.encode(url, "utf-8")), example);
            rd.setStatus("200").setMsg("上传成功").setData(URLEncoder.encode(url, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            rd.setStatus("400").setMsg("上传失败");
        }
        return rd;
    }

    /**
     * 功能描述：获取服务器图片
     * 开发人员： liQiang
     * 创建时间： 2019/9/3 16:10
     * 参数： [path，response]
     *
     * @param path
     * @param response 返回值： 图片流
     */
    @Override
    public void getImage(String path, HttpServletResponse response) throws IOException {
        FileInputStream fis = null;
        OutputStream out = null;
        response.setContentType("image/gif");
        out = response.getOutputStream();
        java.io.File file = new java.io.File(contextPath + path);
        fis = new FileInputStream(file);
        byte[] b = new byte[fis.available()];
        fis.read(b);
        out.write(b);
        out.flush();
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<User> selectAllUser(String company_id) throws Exception {
        return userService.getAllUser(company_id);
    }

    /**
     * 功能描述：电话单个邀进公司
     * 开发人员： lyx
     * 创建时间： 2019/8/8 16:30
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData inviteOneUser(User user) throws Exception {
        ResultData rd = new ResultData();
        User nowUser = userService.getNowUser(request);
        if (0 == nowUser.getIscompanymanager()) {
            return rd.setStatus("401").setMsg("没有企业管理员权限");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        ResultData resultData = userService.judgeUser(user);
        int i = 0;
        if ("404".equals(resultData.getStatus())) {//未在其他企业，且未注册，-->进行注册
            //设置用户信息
            Date date = new Date();
            user.setUser_createtime(dateFormat.format(date));
            user.setId(UUID.randomUUID().toString());
            user.setUser_status(0);
            user.setCompany_id(nowUser.getCompany_id());//获取当前登录人（管理员）的id
            user.setIscompanymanager(0);
            userMapper.insertSelective(user);
            rd.setData(user);
            i++;
        } else if ("204".equals(resultData.getStatus()) || "201".equals(resultData.getStatus()) || "203".equals(resultData.getStatus())) {//已注册，未在企业--->修改所在企业id
            user.setCompany_id(nowUser.getCompany_id());//应该获取当前管理员的企业id
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("user_account", user.getUser_account());
            userMapper.updateByExampleSelective(user, example);
            rd.setData(user);
            i++;
        }
        if (i != 0) {
            return rd.setStatus("200").setMsg("邀请成功");
        } else {
            return rd.setStatus("400").setMsg("邀请失败，已加入公司");//已加入公司或者就在本公司
        }

    }


    /**
     * 功能描述：邀请用户进入公司通过Excel方式批量导入
     * 开发人员： lyx
     * 创建时间： 2019/7/23 10:19
     * 参数： [user]
     * 返回值： com.zcyk.dto.ResultData
     */
    public Map<String, Object> inviteUserToCompany(MultipartFile file) throws Exception {
        Map<String, Object> map = new HashMap<>();
        ResultData rd = new ResultData();
        User nowUser = userService.getNowUser(request);
        if (0 == nowUser.getIscompanymanager()) {
            map.put("401", "没有企业管理员权限");
            return map;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        List<User> userList = ReadExcel.readExcelContentz(file);
        List<User> unInviteUser = new ArrayList<>();//已经在其他公司
        List<User> numInviteUser = new ArrayList<>();//电话号码格式错误
        List<User> sucInviteUser = new ArrayList<>();//成功的
        Integer total = userList.size();
        int i = 0;//判断是否全部添加
        for (User user : userList) {
            if (!PhoneFormatCheckUtils.isPhoneLegal(user.getUser_account())) {//电话号码不正确
                numInviteUser.add(user);
                continue;
            }
            //先判断用户是否已经在其他企业，在进行添加
            ResultData resultData = userService.judgeUser(user);
            if ("404".equals(resultData.getStatus())) {//未在其他企业，且未注册，-->进行注册
                //设置用户信息
                Date date = new Date();
                user.setUser_createtime(dateFormat.format(date))
                        .setId(UUID.randomUUID().toString())
                        .setUser_status(0)
                        .setCompany_id(nowUser.getCompany_id())
                        .setIscompanymanager(0)
                        .setSex(0);
                userMapper.insertSelective(user);
                i++;
                sucInviteUser.add(user);

                //已注册，未在企业--->修改所在企业id,201已被邀请未激活的直接换公司
            } else if ("204".equals(resultData.getStatus()) || "201".equals(resultData.getStatus()) || "203".equals(resultData.getStatus())) {
                user.setCompany_id(nowUser.getCompany_id());
                Example example = new Example(User.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("user_account", user.getUser_account());
                userMapper.updateByExampleSelective(user, example);
                sucInviteUser.add(user);
                i++;
            }else {//202 已在其他企业，返回具体哪些人
                unInviteUser.add(user);
            }
        }

        map.put("total", total);
        map.put("sucInviteUser", sucInviteUser);
        map.put("unInviteUser", unInviteUser);
        map.put("numInviteUser", numInviteUser);

        return map;
    }

    /**
     * 功能描述：移除企业人员
     * 开发人员： wjf
     * 创建时间： 2019/8/23 10:19
     * 参数： [ user_id 用户id]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData rmCompanyUser(String user_id) throws Exception {
        User nowUser = userService.getNowUser(request);
        if (nowUser.getIscompanymanager() != 1) {
            return new ResultData().setStatus("401").setMsg("你不是企业管理员");
        }
        if ((userService.selectOneUser(user_id)).getIscompanymanager() != 0) {
            return new ResultData().setStatus("402").setMsg("对方也是管理员");
        }
        int i = userMapper.setCompanyIdByUserId("", user_id);
        List<String> ids = new ArrayList<>();
        UserFolder userFolder = userfolderService.selectUserfolderByParentId(nowUser.getId());
        ids.add(userFolder.getId());
        userfolderService.deleteUserfolder(ids, nowUser.getId());
        if (i != 0) {
            userDepartmentMapper.deleteDepartmentOneUser(user_id);
            return new ResultData().setStatus("200").setMsg("移除成功");
        } else {
            return new ResultData().setStatus("400").setMsg("移除失败");
        }
    }

    /**
     * 功能描述：搜索企业人员
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/3 17:00
     * 参数：[ * @param null]
     * 返回值：
     */
    public PageInfo<User> searchCompanyUser(int pageNum, int pageSize, String index,HttpServletRequest request) {
        Object user_id = request.getSession().getAttribute("user_id");
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.searchCompanyUser(userMapper.selectByPrimaryKey(user_id).getCompany_id(), index);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    /**
     * 功能描述：所有企业
     * 开发人员：Wujiefeng
     * 创建时间：2019/10/15 11:30
     * 参数：[ * @param null]
     * 返回值：
     */
    public List<String> getAllCompany(){
        List<String> allCompany = companyMapper.getAllCompany();
        return allCompany;
    }

    @Override
    public Company getCompany(String company_id) {
        return companyMapper.selectById(company_id);
    }
}