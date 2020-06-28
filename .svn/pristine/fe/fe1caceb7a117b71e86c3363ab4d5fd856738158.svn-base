package com.zcyk.mapper;


import com.zcyk.pojo.ProjectDynamic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工程动态
 */
@Repository
public interface ProjectDynamicMapper extends Mapper<ProjectDynamic> {


    @Select("select title,record_id,create_date,type from project_dynamic where company_id = #{company_id} and status != 0 and title like '%${search}%' order by create_date desc")
    List<ProjectDynamic> selectByCompanyId(@Param("company_id") String company_id,@Param("search") String search);

    @Update("update project_dynamic set status = #{statsu} where record_id = #{record_id}")
    void updateStatus(@Param("record_id") String record_id,@Param("statsu") int statsu);

    @Select("select id,title,record_id,create_date,type from project_dynamic where record_id = #{0} and status != 0")
    ProjectDynamic selectByRecordId(String id);
}
