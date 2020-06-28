package com.zcyk.mapper;

import com.zcyk.pojo.LiveApp;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WuJieFeng
 * @date 2020/4/14 11:02
 */
@Repository
public interface LiveAppMapper extends Mapper<LiveApp> {

    @Select("select * from live_app where project_id = #{project_id} and status = 1")
    List<LiveApp> selectDeviceByProjectId(String project_id);

    @Select("select * from live_app where project_id = #{project_id} and status = 1 and type = 1")
    List<LiveApp> selectDHDeviceByProjectId(String project_id);

    @Select("select * from live_app where access_token = #{access_token} and status = 1")
    List<LiveApp> selectLiveAppByToken(String access_token);


}
