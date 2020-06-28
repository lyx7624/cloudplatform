package com.zcyk.mapper;

import com.zcyk.pojo.ProjectDesignAlterationPic;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 设计变更意见记录
 */
@Repository
public interface ProjectDesignPicMapper extends Mapper<ProjectDesignAlterationPic> {


    @Select("select pic_url,pic_name from project_design_alteration_pic where project_design_alteration_id = #{0}")
    List<ProjectDesignAlterationPic> selectByProjectDesignId(String id);


    @Update("delete from project_design_alteration_pic  where project_design_alteration_id =#{0}")
    void deleteByDesignAlterationId(String designAlterationId);


}
