package com.zcyk.mapper;

import com.zcyk.pojo.ProjectQualityPlanDetails;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 巡检计划详情
 */
@Repository
public interface ProjectQualityPlanDetailsMapper extends Mapper<ProjectQualityPlanDetails> {


    @Select("select * from project_quality_plan_details where quality_plan_id = #{0} and status != 0 order by create_date")
    List<ProjectQualityPlanDetails> selectByPlan(String plan_id);

    @Update("update project_quality_plan_details set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") String quality_plan_id,@Param("status") int i);

    @Select("select id from project_quality_plan_details  where status != 0 and quality_plan_id =#{quality_plan_id} and id not in (${ids})")
    List<String> selectArrStatus(@Param("ids")String substring, @Param("quality_plan_id")String plan_id);
}
