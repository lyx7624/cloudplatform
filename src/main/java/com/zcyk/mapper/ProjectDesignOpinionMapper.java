package com.zcyk.mapper;

import com.zcyk.pojo.ProjectDesignAlterationOpinion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 设计变更意见记录
 */
@Repository
public interface ProjectDesignOpinionMapper extends Mapper<ProjectDesignAlterationOpinion> {


    /*根据变更记录id查询意见*/
    @Select("select sdari_opinion,sdari_dispose_user,sdari_dispose_date,batch_number,status,bim_opinion,bim_dispose_user,bim_dispose_date" +
            " from project_design_alteration_opinion where project_design_alteration_id = #{0} group by batch_number order by batch_number")
    List<ProjectDesignAlterationOpinion> selectByProjectDesignId(String id);

    /*根据变更记录id删除意见*/
    @Delete("delete from project_design_alteration_opinion where project_design_alteration_id = #{0}")
    void deleteByDesignAlterationId(String designAlterationId);
}
