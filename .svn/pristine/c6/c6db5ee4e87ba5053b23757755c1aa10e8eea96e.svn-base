package com.zcyk.mapper;


import com.zcyk.pojo.TUnitproject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* 功能描述:单位工程
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/8 11:17
*/
@Repository
public interface TUnitprojectMapper extends Mapper<TUnitproject> {

    /*查询最大的子项目编码*/
    @Select("select max(code) from t_unitproject where project_id = #{0}")
    String selectMaxCodeByProject(String project_id);


    /*查看单位项目*/
    @Select("select * from t_unitproject p " +
            " where p.project_id = #{project_id} and p.status!=0" +
            " and (p.name like '%${search}%' or p.supervising_unit like '%${search}%' or p.supervising_user like '%${search}%' or p.code like '%${search}%' or p.project_type like '%${search}%') order by code")
    List<TUnitproject> selectUnitprojectByProject(@Param("search") String search, @Param("project_id")String project_id);

    @Select("select * from t_unitproject where project_id = #{0} and status!=0")
    List<TUnitproject> selectUnitprojectById(String id);

    /*根据id逻辑删除单位工程*/
    @Update("update t_unitproject set status = 0 where id = #{0}")
    Integer deleteUnitprojectByid(String id);
    /*根据项目逻辑删除单位工程*/

    @Update("update t_unitproject set status = 0 where project_id = #{0}")
    Integer deleteUnitprojectByProject(String id);

    /*单位工程重名判断*/
    @Select("select * from t_unitproject where status != 0 and project_id = #{project_id} and name=#{name}")
    TUnitproject selectByProjectAndName(@Param("name")String name,@Param("project_id") String project_id);
}
