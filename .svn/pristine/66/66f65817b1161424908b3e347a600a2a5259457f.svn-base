package com.zcyk.mapper;

import com.zcyk.pojo.Model_version;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ModelVersionMapper extends Mapper<Model_version> {
    @Select("select * from model_version where mid = #{mid} and statu = 1 order by version desc")
    List<Model_version> getVersionBymid(String mid);

    /*根据id删除历史版本*/
    @Update("update model_version set statu = #{0} where id = #{id}")
    int deleteVersionById(String id);

    /*根据mid删除历史版本*/
    @Update("update model_version set statu = 0 where mid = #{mid}")
    int deleteVersionByMid(String mid);
}
