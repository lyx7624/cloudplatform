package com.zcyk.mapper;

import com.zcyk.pojo.UserProjectRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserProjectRoleMapper extends Mapper<UserProjectRole> {

    /*查询职位人数*/
    @Select("select count(*) from user_project_role where project_id = #{project_id} and role = #{role} and status != 0")
    int selectUserCount(@Param("project_id") String project_id,@Param("role") Integer role);
    /*查询某职位人员详细信息*/
    @Select("select * from user_project_role where project_id = #{project_id} and role = #{role} and status != 0")
    List<UserProjectRole> selectUserByRole(@Param("project_id") String project_id, @Param("role") Integer role);

    /*查询某职位人员详细信息*/
    @Select("select min(role) from user_project_role where project_id = #{project_id} and user_phone = #{user_phone} and status != 0")
    Integer selectRoleByPhoneAndProject(@Param("project_id") String projectmodel_id, @Param("user_phone") String user_phone);


    @Update("update user_project_role set status = 0 where id = #{id}")
    int deleteUserById( @Param("id") String id);
    /*逻辑删除项目所有人员*/
    @Update("update user_project_role set status = 2 where project_id = #{project_id} ")
    int deleteAllUser(@Param("project_id") String projectmodel_id);

    /*查询人员项目*/
    @Select("select * from user_project_role where user_phone = #{user_phone} and status != 0")
    List<UserProjectRole> selectUserProject( @Param("user_phone") String user_phone);

    /*修改角色所在项目*/
    @Update("update user_project_role set user_phone = #{0} where user_phone = #{0}")
    void updateRoleAccountByUser(String account);
}
