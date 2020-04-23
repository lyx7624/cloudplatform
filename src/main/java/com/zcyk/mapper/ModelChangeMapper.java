package com.zcyk.mapper;

import com.zcyk.pojo.Model_change;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ModelChangeMapper extends Mapper<Model_change> {
     @Select("select * from model_change where mid = #{mid} and statu = 1 and file_name like '%${search}%' order by create_time desc")
    List<Model_change> selectModelChange(@Param("mid") String mid, @Param("search") String search);

     @Select("select * from model_change where file_url = #{url}")
    Model_change selectModelChangeByUrl(String url);
}
