package com.zcyk.mapper;

import com.zcyk.pojo.TemplateNode;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述: 流程节点服务
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:15
 */
@Repository
public interface TemplateNodeMapper extends Mapper<TemplateNode> {

    /*查询模板的节点*/
    @Select("select * from template_node where template_id = #{0} and status = 1 order by node_order")
    List<TemplateNode> selectByTemplateId(String template_id);

    /*删除节点:逻辑删除*/
    @Update("update template_node set status = 0 where template_id = #{0}")
    Integer deleteByTemplateId(String template_id);
}
