package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.DepartmentFolderMapper;
import com.zcyk.mapper.DepartmentMapper;
import com.zcyk.mapper.UserDepartmentMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.Department;
import com.zcyk.pojo.User;
import com.zcyk.pojo.UserDepartment;
import com.zcyk.service.DepartmentFolderService;
import com.zcyk.service.DepartmentService;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 功能描述:部门管理
 * 开发人员: xlyx
 * 创建日期: 2019/7/30 14:37
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    UserDepartmentMapper userDepartmentMapper;

    @Autowired
    UserMapper userMapper;


    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Autowired
    DepartmentFolderService departmentFolderService;

    @Autowired
    DepartmentFolderMapper departmentFolderMapper;


    /**
     * 功能描述：判断当前登录人是否有权限操作
     * 开发人员： lyx
     * 创建时间： 2019/8/27 9:16
     */
    private boolean judegManager(String department_id) {
        User user = userService.getNowUser(request);
        Integer iscompanymanager = user.getIscompanymanager();
        if (iscompanymanager == 1) {//如果是企业管理员拥有所有权限
            return true;
        }
        Integer integer = userDepartmentMapper.selectUserManage(user.getId(), department_id);
        if (integer != null && integer == 1) {//是部门管理员获取所有权限
            return true;
        }
        return false;
    }


    /*递归查询*/
    private List<Department> selectUserDepartment(List<Department> departments) {
        for (Department department : departments) {
            List<Department> chlidDepartments = departmentMapper.selectDepartmentByParentID(department.getId());
            department.setChildren(chlidDepartments);
            if (chlidDepartments.size() != 0) {
                selectUserDepartment(chlidDepartments);
            }
        }
        return departments;
    }

    /**
     * 功能描述：根据id修改部门名称
     * 开发人员： lyx
     * 创建时间： 2019/8/8 20:57
     * 参数： [id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData updateDepartmentNameById(Department department) {
        ResultData rd = new ResultData();
        if (!judegManager(department.getId())) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }

        int i = departmentMapper.updateDepartmentNameById(department);
        if (i != 0) {
            rd.setMsg("修改成功");
            rd.setStatus("200");
        } else {
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }


    /**
     * 功能描述： 删除部门
     * 开发人员： lyx
     * 创建时间： 2019/8/22 9:40
     */
    public ResultData deleteDepartment(String department_id) {
        ResultData rd = new ResultData();
        if (!judegManager(department_id)) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }
        int i = departmentMapper.deleteByPrimaryKey(department_id);
        if (i != 0) {
            //删除文件夹 状态为0
            departmentFolderMapper.deleteDepartmentAllfolder(department_id);
            //移除成员
            userDepartmentMapper.deleteDepartmentAllUser(department_id);
            rd.setMsg("删除成功");
            rd.setStatus("200");
        } else {
            rd.setMsg("修改失败");
            rd.setStatus("400");
        }
        return rd;
    }

    /**
     * 功能描述：查询下的部门 涉及子部门
     * 开发人员： lyx
     * 创建时间： 2019/7/30 14:47
     * 参数： 企业id
     * 返回值： [List<Department>  该公司下所有的部门]
     */
    public List<Department> selectDepartment(String company_id) {
        //部门条件查询
        Example example = new Example(Department.class);
        example.orderBy("department_createtime");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parent_id", company_id);
        List<Department> departments = departmentMapper.selectByExample(example);
        return selectUserDepartment(departments);
    }


    /**
     * 功能描述：查询下的部门及人员不涉及子部门
     * 开发人员： lyx
     * 创建时间： 2019/7/30 14:47
     * 参数： 企业id
     * 返回值： [List<Department>  该公司下所有的部门]
     */
    public Map<String, Object> getDepartmentUser(String department_id) {
        Map<String, Object> map = new HashMap<>();
        List<Department> departments = null;
        List<User> users = null;
        if (StringUtils.isBlank(department_id)) {//如果为空则显示一级部门及未在部门成员
            String company_id  = userService.getNowUser(request).getCompany_id();
            departments = departmentMapper.selectDepartmentByParentID(company_id);
            users = userMapper.selectNoDepartmentUser(company_id,"");
        } else {
            departments = departmentMapper.selectDepartmentByParentID(department_id);
            users = userDepartmentMapper.selectUserByDepartmentId(department_id);
        }
        map.put("users", users);
        map.put("departments", departments);
        return map;

    }


    /**
     * 功能描述：查询该部门下的成员
     * 开发人员： lyx
     * 创建时间： 2019/8/21 14:26
     * 参数：
     */
    public PageInfo<User> selectUserByDepartment(int pageNum, int pageSize, String department_id) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userDepartmentMapper.selectUserByDepartmentId(department_id);
        for (User user : userList) {
            Integer s = userDepartmentMapper.selectUserManage(user.getId(), department_id);
            user.setIsdepartmentmanager(s);
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    /**
     * 功能描述：搜索部门人员
     * 开发人员：Wujiefeng
     * 创建时间：2019/9/3 17:20
     * 参数：[ * @param null]
     * 返回值：
     */
    public PageInfo<User> searchDepartmentUser(int pageNum, int pageSize, String department_id, String index) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userDepartmentMapper.searchDepartmentUser(department_id, index);
        for (User user : userList) {
            Integer s = userDepartmentMapper.selectUserManage(user.getId(), department_id);
            user.setIscompanymanager(s);
        }
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    /**
     * 功能描述：添加部门
     * 开发人员： lyx
     * 创建时间： 2019/8/8 8:58
     * 参数： [department]
     * 返回值： com.zcyk.dto.ResultData
     */
    public ResultData addDepartment(Department department) throws Exception {
        ResultData rd = new ResultData();
        User user = userService.getNowUser(request);

        if (!judegManager(department.getId())) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }

        Department one = departmentMapper.selectOneDepartment(department);
        if (one != null) {
            rd.setMsg("部门已存在");
            rd.setStatus("401");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        BigDecimal bg = new BigDecimal(1024 * 10 * 10);
        //获取当前登录用户
        String company_id = user.getCompany_id();
        department.setId(UUID.randomUUID().toString())
                .setCompany_id(company_id)
                .setDepartment_status(1)
                .setDepartment_createtime(dateFormat.format(date))
                .setDepartment_capacity(bg);

        int i = departmentMapper.insertSelective(department);
        if (i != 0) {
            rd.setMsg("新增成功");
            rd.setStatus("200");
            rd.setData(department.getId());
            //给该部门添加文件夹
            departmentFolderService.addDepartmentParentFolder(department);
        } else {
            rd.setMsg("新增失败");
            rd.setStatus("400");
        }
        return rd;

    }

    /**
     * 功能描述：把人员移除部门
     * 开发人员： lyx
     * 创建时间： 2019/8/8 11:09
     * 参数： [user_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData rmDepartmentUser(UserDepartment userDepartment) {
        ResultData rd = new ResultData();
        if (!judegManager(userDepartment.getDepartment_id())) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }
        int i = userDepartmentMapper.delete(userDepartment);
        if (i != 0) {
            rd.setMsg("移除成功");
            rd.setStatus("200");
        } else {
            rd.setMsg("移除失败");
            rd.setStatus("400");
        }
        return rd;
    }

    /**
     * 功能描述：设置是否为管路员
     * 开发人员： lyx
     * 创建时间： 2019/8/8 11:11
     * 参数： [user_id, department_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @Override
    public ResultData setDepartmentManager(UserDepartment userDepartment) {
        ResultData rd = new ResultData();
        if (!judegManager(userDepartment.getDepartment_id())) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }
        Example example = new Example(UserDepartment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_id", userDepartment.getUser_id());
        criteria.andEqualTo("department_id", userDepartment.getDepartment_id());
        int i = userDepartmentMapper.updateByExampleSelective(new UserDepartment().setIsdepartmentmanager(userDepartment.getIsdepartmentmanager()), example);
        if (i != 0) {
            rd.setMsg("设置成功");
            rd.setStatus("200");
        } else {
            rd.setMsg("设置失败");
            rd.setStatus("400");
        }
        return rd;
    }


    /**
     * 功能描述：新增个人进部门
     * 开发人员： lyx
     * 创建时间： 2019/8/8 17:05
     * 参数： [user_id, department_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
   /* public ResultData inviteUserToDepartment(String user_id, String department_id) {

        Integer s = userDepartmentMapper.selectUserManage(user_id, department_id);
        if(s!=null){
            return new ResultData().setMsg("已在当前部门").setStatus("401");
        }

        UserDepartment userDepartment = new UserDepartment().setUser_id(user_id)
                .setDepartment_id(department_id)
                .setId(UUID.randomUUID().toString())
                .setIsdepartmentmanager(0);
        int i = userDepartmentMapper.insertSelective(userDepartment);
        if(i!=0){
            return new ResultData().setMsg("添加成功").setStatus("200");
        }
        return new ResultData().setMsg("添加失败").setStatus("400");
    }*/


    /**
     * 功能描述：批量邀请
     * 开发人员： lyx
     * 创建时间： 2019/8/22 18:19
     * 参数：
     * 返回值：
     * 异常：如果有人在其他客户端操作，就会报异常
     */
    public ResultData inviteUserToDepartment(List<String> user_ids, String department_id) {
        ResultData rd = new ResultData();
        if (!judegManager(department_id)) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }
        UserDepartment userDepartment = new UserDepartment()
                .setDepartment_id(department_id)
                .setIsdepartmentmanager(0);
        int i = 0;
        for (String user_id : user_ids) {
            Integer s = userDepartmentMapper.selectUserManage(user_id, department_id);
            if (s != null) {//表示已在部门
                continue;//防止多出处添加
            }
            userDepartment.setUser_id(user_id)
                    .setId(UUID.randomUUID().toString());
            i += userDepartmentMapper.insertSelective(userDepartment);
        }
        if (i == 0) {
            return rd.setStatus("400").setMsg("失败");
        }
        if (i < user_ids.size()) {
            return rd.setStatus("201").setMsg("部分员工已在本部门");
        }


        return rd.setStatus("200").setMsg("成功");

    }

    /**
     * 功能描述：更换部门
     * 开发人员： lyx
     * 创建时间： 2019/8/8 11:21
     * 参数： [user_id, oldDepartment_id, newDepartment_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData UpdateUserDepartment(UserDepartment userDepartment, String newDepartment_id) {
        ResultData rd = new ResultData();
        if (!judegManager(userDepartment.getDepartment_id())) {
            rd.setMsg("没有权限");
            rd.setStatus("403");
            return rd;
        }
        Integer integer = userDepartmentMapper.selectUserManage(userDepartment.getUser_id(), newDepartment_id);
        if(integer!=null){
            rd.setMsg("已在本部门");
            rd.setStatus("401");
            return rd;
        }
        Example example = new Example(UserDepartment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_id", userDepartment.getUser_id());
        UserDepartment NowUserDepartment = userDepartmentMapper.selectOneByExample(example);
        int i = 0;
        if (NowUserDepartment == null) {//没在部门：新增
            i = userDepartmentMapper.insertSelective(new UserDepartment().setId(UUID.randomUUID().toString()).setDepartment_id(newDepartment_id).setIsdepartmentmanager(0).setUser_id(userDepartment.getUser_id()));
        }else{
            i = userDepartmentMapper.updateByExampleSelective(new UserDepartment().setDepartment_id(newDepartment_id).setIsdepartmentmanager(0), example);
        }
        if (i != 0) {
            rd.setMsg("设置成功");
            rd.setStatus("200");
        } else {
            rd.setMsg("设置失败");
            rd.setStatus("400");
        }
        return rd;
    }


    /**
     * 功能描述：查询没在部门的成员
     * 开发人员： lyx
     * 创建时间： 2019/8/23 10:09
     */
    public List<User> selectNoDepartmentUser(String serach) {
        String company_id =  userService.getNowUser(request).getCompany_id();
        return userMapper.selectNoDepartmentUser(company_id,serach);
    }


}