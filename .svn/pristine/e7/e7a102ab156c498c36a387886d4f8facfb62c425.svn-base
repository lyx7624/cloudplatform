package com.zcyk.mapper;

import com.zcyk.pojo.DepartmentFolder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/8/9 10:06
 */
@Repository
public interface DepartmentFolderMapper extends Mapper<DepartmentFolder> {

    /*根据文件夹名称查询文件夹*/
    @Select("select * from department_folder t where t.department_name = #{0} and folder_statu = 1")
    DepartmentFolder selectOneDepartFloderByName(String name);

    /*删除部门文件夹*/
    @Update("update department_folder set folder_statu = 0 where department_id = #{0}")
    int deleteDepartmentAllfolder(String department_id);

    /*根据id删除部门文件夹*/
    @Update("update department_folder set folder_statu = 0 where id = #{id}")
    int deleteDepartmentfolder(String id);


    /*根据父id查询文件夹*/
    @Select("select * from department_folder  where parent_id = #{0} and folder_statu = 1 order by folder_createtime DESC")
    List<DepartmentFolder> selectDepartFloderByParentId(String id);

    /*搜索部门文件夹*/
    @Select("select * from department_folder where parent_id = #{folder_id} and folder_statu=1 and (folder_name like '%${index}%' or folder_createuser like '%${index}%')")
    List<DepartmentFolder> searchDepartmentFolder(@Param("index") String index, @Param("folder_id") String folder_id);


    /*查询单个文件夹*/
    @Select("select * from department_folder where  folder_statu=1 and folder_name = #{folder_name} and parent_id = #{parent_id}")
    DepartmentFolder selectOneFilder(DepartmentFolder departmentFolder);
}