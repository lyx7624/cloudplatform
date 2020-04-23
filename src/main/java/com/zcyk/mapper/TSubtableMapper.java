package com.zcyk.mapper;


import com.zcyk.pojo.TSubtable;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* 功能描述:单位工程验收记录表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/9 15:39
*/
@Repository
public interface TSubtableMapper extends Mapper<TSubtable> {


    /*根据记录id查询表格*/
    @Select("select * from t_subtable where subrecord_id = #{0} and status = 1")
    List<TSubtable> selectByRecord(String id);
}
