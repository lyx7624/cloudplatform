package com.zcyk.mapper;

import com.zcyk.pojo.ProjectDesignAlteration;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 设计变更
 */
@Repository
public interface ProjectDesignMapper extends Mapper<ProjectDesignAlteration> {


    /*修改状态*/
    @Update("update project_design_alteration set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") String id, @Param("status")int status);

    /*专项类型分组全部显示*/
    @Select("select special_name from project_design_alteration where special_name like '%${search}%' and project_id = #{project_id} and status !=0 group by special_name")
    List<ProjectDesignAlteration> selectGroupSpecialName(@Param("search")String search,@Param("project_id")String project_id);

    /*根据专项类型查询*/
    @Select("select id,significance from project_design_alteration where project_id = #{project_id} and status !=0 and special_name = #{special_name} order by write_date desc")
    List<ProjectDesignAlteration> selectBySpecialName(@Param("project_id")String project_id,@Param("special_name") String special_name);

    /*根据重要程度查询*/
    @Select("select id,report_number,special_type,status,write_date,recorder from project_design_alteration where special_name = #{special_name} and project_id = #{project_id} and status != 0 and significance like '%${significance}%'" +
            " and (report_number like '%${search}%' or special_type like '%${search}%' or recorder like '%${search}%' )")
    List<ProjectDesignAlteration> selectBySpecialAndSignificance(@Param("search")String search,@Param("special_name")String special_name,@Param("project_id") String project_id,@Param("significance") String significance);

    /*根据id显示*/
    @Select("select * from project_design_alteration pda left join t_projectmodel p on pda.project_id = p.id where pda.id = #{0} and pda.status != 0")
    ProjectDesignAlteration selectById(String id);

    /*根据项目查找全部记录*/
    @Select("select * from project_design_alteration where project_id = #{0} and status != 0 order by write_date desc")
    List<ProjectDesignAlteration> selectDesignAlterationByProjectId(String id);
}
