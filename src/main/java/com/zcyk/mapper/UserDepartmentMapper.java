package com.zcyk.mapper;

import com.zcyk.pojo.User;
import com.zcyk.pojo.UserDepartment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 功能描述: 部门人员管理接口
 * 开发人员: lyx
 * 创建日期: 2019/7/30 14:38
 */
@Repository
public interface UserDepartmentMapper extends Mapper<UserDepartment> {

    /*删除部门：删除人员*/
    @Delete("delete from user_department  where department_id = #{0}")
     int deleteDepartmentAllUser(String department_id);


    @Select("select * from t_user u where u.id not in(select distinct user_id from user_department)")
    @ResultType(User.class)
    List<User> selectNoDepartmentUsers();

    @Select("select * from t_user u where u.id in (select distinct user_id from user_department where department_id = #{department_id})")
    List<User> selectUserByDepartmentId(String department_id);
    /*搜索部门人员*/
    @Select("select * from t_user where id in (select distinct user_id from user_department where department_id = #{department_id}) and (user_name like '%${index}%' or user_account like '%${index}%')")
    List<User>searchDepartmentUser(@Param("department_id") String department_id,@Param("index") String index);

    /*多部门判断是否是该部门管理员*/
    @Select("select isdepartmentmanager from user_department  where user_id=#{user_id} and department_id=#{department_id}")
    Integer selectUserManage(String user_id, String department_id);

    /**/
    @Select("select isdepartmentmanager from user_department  where user_id=#{user_id}")
    UserDepartment selectUserDepartment(String user_id);

    /*将用户移除部门*/
    @Delete("delete from user_department where user_id = #{user_id}")
    int deleteDepartmentOneUser(String user_id);


    /*单部门判断是否是部门管理员*/
    @Select("select * from user_department where user_id = #{0}")
    UserDepartment judeManager(String user_id);

}