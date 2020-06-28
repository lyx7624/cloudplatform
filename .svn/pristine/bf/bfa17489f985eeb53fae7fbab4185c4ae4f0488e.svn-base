package com.zcyk.mapper;

import com.zcyk.pojo.TMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
/**
* 功能描述: 消息接口
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/9/6 11:13
*/
@Repository
public interface TMessageMapper extends Mapper<TMessage> {


    /*根据用户查询消息*/
    @Select("select * from t_message where id = #{id} and status = 1 " +
            "and (createuser_id like '${search}' or title like '${search}' or source like '${search}')")
        TMessage selectByUser(@Param("search") String search,@Param("id") String id);
}
