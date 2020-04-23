package com.zcyk.mapper;


import com.zcyk.pojo.UserMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* 功能描述: 用户消息表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/9/6 13:58
*/

@Repository
public interface UserMessageMapper extends Mapper<UserMessage> {

    /*查询用户消息*/
    @Select("select * from user_message where user_id = #{userId} and ststus in (1,2) ")
    List<UserMessage> selectByUser(@Param("userId")  String userId);

    /*删除个人消息*/
    @Select("update user_message set ststus = 0 where message_id = #{0} and user_id = #{1}")
    int updateById(String message_id,String user_id);

    /*给所有人发信息*/
    @Insert("insert into user_message value()")
    void insertAllUser(String id);
}
