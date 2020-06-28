package com.zcyk.mapper;

import com.zcyk.pojo.Department;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 功能描述: 部门管理接口
 * 开发人员: lyx
 * 创建日期: 2019/7/30 14:38
 */
@Repository
public interface DepartmentMapper extends Mapper<Department> {

    /*根据父id查询子部门*/
    @Select("select * from t_department where department_status = 1 and parent_id=#{parentId}")
    List<Department> selectDepartmentByParentID(String parentId);

    /*根据id修改部门名称*/
    @Update("update t_department set department_name = #{department_name} where id = #{id}")
    int updateDepartmentNameById(Department department);

    /*根据用户id查询部门id*/
    @Select("select * from t_department t where department_status = 1 and t.id = (select department_id from user_department where user_id = #{0})")
    Department selectDepartmentByUser_Id(String user_id);

    /*删除部门设置状态为0*/
    @Update("update t_department set department_status = 0 where company_id = #{company_id}")
    int deleteDepartmentByCompanyId(String company_id);

    /*查询该企业下面的所有部门*/
    @Select("select * from t_department where department_status = 1 and company_id = #{company_id}")
    List<Department> selectDepartmentByCompanyId(String company_id);

    /*检查是否重名*/
    @Select("select * from t_department where department_status = 1 and department_name = #{department_name} and parent_id = #{parent_id}")
    Department selectOneDepartment(Department department);
}