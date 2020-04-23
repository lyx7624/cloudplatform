package com.zcyk.mapper;

import com.zcyk.pojo.T_Opinion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述: 意见表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:35
 */
@Repository
public interface T_OpinionMapper extends Mapper<T_Opinion> {


    /*查询该节点的意见*/
    @Select("select * from t_opinion where process_node_id = #{0} and status = 1;")
    List<T_Opinion> selectByNodeId(String id);

    /*删除节点对应的所有意见*/
    @Delete("update t_opinion set status = 0 where process_node_id = #{0}")
    void deleteByNodes(String id);
}
