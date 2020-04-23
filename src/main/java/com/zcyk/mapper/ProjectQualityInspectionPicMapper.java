package com.zcyk.mapper;

import com.zcyk.pojo.ProjectQualityInspectionPic;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 质量巡检图片
 */
@Repository
public interface ProjectQualityInspectionPicMapper extends Mapper<ProjectQualityInspectionPic> {


    //删除图片
    @Delete("delete from project_quality_inspection_pic where project_quality_inspection_id = #{0}")
    void deleteByprojectQualityId(String projectQualityId);

    @Select("select pic_url,pic_name from project_quality_inspection_pic where project_quality_inspection_id = #{0}")
    List<ProjectQualityInspectionPic> selectByQuality(String quality_id);
}
