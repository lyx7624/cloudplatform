package com.zcyk.mapper;

import com.zcyk.pojo.Model_quality;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ModelQualityMapper extends Mapper<Model_quality> {
    @Select("select * from model_quality where file_url = #{url}")
    Model_quality selectModelQualityByUrl(String url);

    @Select("select * from model_quality where mid = #{mid} and statu = 1 and file_name like '%${search}%' order by create_time desc")
    List<Model_quality> selectModelQuality(@Param("mid") String mid, @Param("search") String search);
}
