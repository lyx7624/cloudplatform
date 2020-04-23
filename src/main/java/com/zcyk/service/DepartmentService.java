package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.Department;
import com.zcyk.pojo.User;
import com.zcyk.pojo.UserDepartment;
import com.zcyk.dto.ResultData;

import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/7/30 14:37
 */
public interface DepartmentService {
    /*查询下的部门*/
    List<Department> selectDepartment(String company_id) throws Exception;

    /*查询该部门下的成员及部门 不递归*/
    Map<String, Object> getDepartmentUser(String department_id) throws Exception;


    /*新增部门*/
    ResultData addDepartment(Department department) throws Exception;

    /*移除部门人员*/
    ResultData rmDepartmentUser(UserDepartment userDepartment) throws Exception;

    /*设置是否为管理员*/
    ResultData setDepartmentManager(UserDepartment userDepartment) throws Exception;

    /*更换部门*/
    ResultData UpdateUserDepartment(UserDepartment userDepartment, String newDepartment_id) throws Exception;

    /*邀请个人进部门*//*
    ResultData inviteUserToDepartment(String user_id,String department_id);*/

    /*批量邀请进部门(非电话)*/
    ResultData inviteUserToDepartment(List<String> user_ids, String department_id) throws Exception;


    /*根据主键修改部门名称*/
    ResultData updateDepartmentNameById(Department department) throws Exception;


    /*搜索部门人员*/
    PageInfo<User> searchDepartmentUser(int pageNum, int pageSize, String department_id, String index) throws Exception;

    /*查询该部门的成员*/
    PageInfo<User> selectUserByDepartment(int pageNum, int pageSize, String department_id) throws Exception;

    /*删除部门*/
    ResultData deleteDepartment(String department_id)throws Exception;

    /*查询未在部门的成员*/
    List<User> selectNoDepartmentUser(String serach)throws Exception;





}