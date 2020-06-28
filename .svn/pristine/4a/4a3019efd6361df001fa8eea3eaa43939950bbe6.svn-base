package com.zcyk.mapper;

import com.zcyk.pojo.ProjectQualityInspection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 质量巡检
 */
@Repository
public interface ProjectQualityInspectionMapper extends Mapper<ProjectQualityInspection> {


    @Select("select * from project_quality_inspection where project_id = #{project_id} and status != 0 " +
            " and (inspection_number like '%${search}%' or  executor like '%${search}%' or quality_inspector like '%${search}%' " +
            "or sub_project like '%${search}%' or item_project like '%${search}%' or toponym like '%${search}%') order by inspection_number desc")
    List<ProjectQualityInspection> selectQualityByProjectId(@Param("project_id") String project_id,@Param("search") String search);

    @Select("select pqi.*,tp.project_name from project_quality_inspection pqi left join t_projectmodel tp on pqi.project_id = tp.id" +
            " where pqi.id = #{0} and pqi.status != 0")
    ProjectQualityInspection selectQualityById(String id);

    @Update("update project_quality_inspection set status = #{status} where id = #{id}")
    void setQualityStatus(@Param("id") String id,@Param("status")Integer status);

    /*查询该工程下的当天的集合*/
    @Select("select count(0) from project_quality_inspection where project_id = #{project_id} and status != 0 and inspection_number like '${format}%'")
    int selectDateCount(@Param("format") String format,@Param("project_id") String project_id);

    @Update("update project_quality_inspection set status = #{status} where quality_plan_details_id = #{quality_plan_details_id}")
    void updateStatus(@Param("quality_plan_details_id") String plan_id,@Param("status") int i);
    /*根据质量问题查询*/
    @Select("select * from project_quality_inspection where quality_plan_details_id = #{quality_plan_details_id} and status != 0")
    ProjectQualityInspection selectByDetailsId(@Param("quality_plan_details_id") String id);
}
