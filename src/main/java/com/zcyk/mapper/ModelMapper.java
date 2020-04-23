package com.zcyk.mapper;

import com.zcyk.pojo.Model;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ModelMapper extends Mapper<Model> {
    /*选取项目中的多个模型*/
    @Select("select * from t_model where project_id = #{project_id} and statu != 0 and model_name like '%${search}%' order by create_time desc ")
    List<Model> getModelByProject_id(@Param("project_id") String project_id,@Param("search")String search);

    @Update("update t_model set statu = 0 where id = #{id}")
    int deleteModel(String id);


    /*根据项目查询未同步模型*/
    @Select("select * from t_model where project_id = #{project_id} and statu = 1 and model_type not in (5,6)")
    List<Model> getUnSycModel(@Param("project_id") String project_id);

    /*根据项目文件的id查询模型*/
    Model getModelByFileId(String id);

    /*根据项目查询模型*/
    @Select("select * from t_model where project_id = #{project_id} and statu != 0 and model_type not in (5,6)")
    List<Model> selectByPejecyId(String project_id);
}
