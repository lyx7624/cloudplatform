package com.zcyk.mapper;

import com.zcyk.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.IfProfileValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 登录接口
 */
@Repository
public interface UserMapper extends Mapper<User> {


    @Select("select * from t_user where  user_status = 1 and user_account = #{useraccount}")
    User selectUserByAccount(@Param("useraccount") String useraccount);

    @Update("update t_user set iscompanymanager = #{statu} where id = #{0}")
    int setCompanyManager(String user_id, int statu);

    @Select("select * from t_user where  user_status = 1 and company_id = #{0}")
    List<User> selectAllUser(String company_id);

    /*查询未在部门人员*/
    @Select("select * from t_user where user_status = 1 and company_id=#{company_id} and id not in (select user_id from  user_department) and user_name like '%${serach}%'")
    List<User> selectNoDepartmentUser(@Param("company_id") String company_id, @Param("serach") String serach);

    @Update("update t_user set company_id = #{company_id} where  user_status = 1 and id = #{user_id} ")
    int setCompanyIdByUserId(String company_id, String user_id);

    /*根据企业id删除用户*/
    /*lyx修改：删除企业 应该设置用户企业为空而不是设置用户状态为0*/
    @Update("update t_user set company_id = null where company_id = #{company_id}")
    int deleteUserByCompanyId(String company_id);

    /*查询用户且用户已存在*/
    @Select("select * from t_user where user_status = 1 and id = #{user_id}")
    User selectUserById(String user_id);

    @Select("select * from t_user where user_status = 1 and company_id = #{company_id} and (user_name like '%${index}%' or user_account like '%${index}%')")
    List<User> searchCompanyUser(@Param("company_id") String company_id, @Param("index") String index);


    /*查询企业管理员*/
    @Select("select * from t_user where user_status = 1 and company_id = #{0} and iscompanymanager = 1")
    List<User> selectAllManageUser(String id);

    @Select("select * from t_user where user_status = 1 and company_id = #{0}")
    List<User> selectCompanyAllUser(String company_id);
}
