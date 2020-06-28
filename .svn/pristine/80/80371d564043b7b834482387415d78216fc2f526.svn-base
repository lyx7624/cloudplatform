package com.zcyk.mapper;

import com.zcyk.pojo.ProjectQualityPlan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 巡检计划
 */
@Repository
public interface ProjectQualityPlanMapper extends Mapper<ProjectQualityPlan> {

    /*修改状态*/
    @Update("update project_quality_plan set status = #{status} where id = #{id}")
    void updateStatus(@Param("id")String plan_id,@Param("status") int i);

    /*根据项目查询所有的计划*/
    @Select("select * from project_quality_plan where project_id = #{project_id} and status != 0 and " +
            "(unitproject_name like '%${search}%' or plan_name like '%${search}%' or create_user like '%${search}%') order by create_date desc")
    List<ProjectQualityPlan> selectByProject(@Param("search") String search,@Param("project_id") String project_id);

    @Select("select * from project_quality_plan where id = #{id} and status != 0  ")
    ProjectQualityPlan selectById(@Param("id") String plan_id);
}
