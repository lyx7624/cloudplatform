package com.zcyk.mapper;

import com.zcyk.pojo.T_Template;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 功能描述: 模板表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:35
 */
@Repository
public interface TemplateMapper extends Mapper<T_Template> {

    /*根据名称查找模板*/
    @Select("select * from t_template where company_id = #{company_id} and template_name = #{template_name} and template_status = 1 order by create_time desc")
    T_Template selectTemplateByName(T_Template t_template);

    /*根据id查找模板*/
    @Select("select * from t_template where id = #{template_id} and template_status = 1")
    T_Template selcetTemplateById(String template_id);

    /*查询所有Statu=1的模板*/
    @Select("select * from t_template where template_name like '%${serach}%' and template_status=1 and company_id=#{company_id} order by create_time desc")
    List<T_Template> selectAllTemplate(@Param("company_id") String company_id, @Param("serach") String serach);

    /*删除模板*/
    @Update("update t_template set template_status = 0 where id = #{template_id}")
    int deleteTemplate(String template_id);


}
