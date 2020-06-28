package com.zcyk.mapper;

import com.zcyk.pojo.TSubrecord;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* 功能描述:单位工程验收记录
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/9 15:38
*/

@Repository
public interface TSubrecordMapper extends Mapper<TSubrecord> {


    /*根据单位工程查询记录*/
    @Select("select * from t_subrecord where unitpriject_id = #{0} and status = 1")
    List<TSubrecord> selectByUnitprojectId(String id);

    /*删除记录*/
    @Update("update t_subrecord set status = 0 where unitpriject_id = #{0}")
    Integer deleteByUnitprojectId(String id);
}
