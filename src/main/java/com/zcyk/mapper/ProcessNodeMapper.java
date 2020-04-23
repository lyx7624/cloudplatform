package com.zcyk.mapper;


import com.zcyk.pojo.ProcessNode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述:流程节点表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */

@Repository
public interface ProcessNodeMapper extends Mapper<ProcessNode> {


    /*设置上级节点状态*//*节点 0已删除 1待处理 2正在处理 3完成 4退回 */
    @Update("update process_node set status = 2 where process_id=#{process_id} and status !=0 and node_order = #{node_order}")
    void setStatus(@Param("process_id") String process_id, @Param("node_order") Integer node_order);

    /*查询下一级节点*/
    @Select("select * from process_node where process_id=#{process_id} and status !=0 and node_order = #{node_order}")
    ProcessNode seletcNode(@Param("process_id") String process_id, @Param("node_order") Integer node_order);

    /*查询流程所有节点*/
    @Select("select * from process_node where process_id=#{0} and status !=0 order by node_order")
    List<ProcessNode> selByProcessId(String process_id);

    /*根据流程id删除所有的流程节点*/
    @Delete("update process_node set status = 0 where process_id = #{0}")
    void deleteByProcessId(String process_id);
}
