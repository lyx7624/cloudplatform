package com.zcyk.mapper;

import com.zcyk.pojo.SysFileLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/8/26 18:16
 */
@Repository
public interface SysFilelogMapper extends Mapper<SysFileLog> {


    /*模糊检索*/
    @Select("select * from sys_filelog where  company_id=#{company_id} and (user_name like '%${search}%' or operation like '%${search}%' or file_name like '%${search}%' or file_type like '%${search}%') order by operation_time desc")
    List<SysFileLog> selectFileLog(@Param("search") String search,@Param("company_id")String company_id);
}