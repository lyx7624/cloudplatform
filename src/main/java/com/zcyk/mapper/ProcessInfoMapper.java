package com.zcyk.mapper;


import com.zcyk.pojo.ProcessInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
* 功能描述: 流程实例表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:02
*/
@Repository
public interface ProcessInfoMapper extends Mapper<ProcessInfo> {

    /*根据发起人查询该发起的流程*/
    @Select("select * from process_info where  " +
            " (title like '%${serach}%' or initiator_name like '%${serach}%' or hanlder_name like '%${serach}%') " +
            "and initiator_id = #{initiator_Id} and process_status != 0 and template_id like '%${template_id}%' and company_id = #{company_id}  order by create_time desc")
    List<ProcessInfo> selAllProcessByInitiator_Id(@Param("company_id")String company_id ,@Param("initiator_Id")String initiator_Id,@Param("serach") String serach,@Param("template_id")String template_id);

    /*根据处理人查询待处理的流程*/       //如果用户传过来的为空and就会匹配到空
    @Select("select * from process_info where" +
            " (title like '%${serach}%' or initiator_name like '%${serach}%' or hanlder_name like '%${serach}%' or remark like '%${serach}%')" +
            "and hanlder_id = #{user_id}  and process_status = 1  and company_id = #{company_id} and template_id like '%${template_id}%' order by create_time desc" )
    List<ProcessInfo> selAllProcessByHanlder_Id(@Param("company_id")String company_id ,@Param("user_id") String user_id,@Param("serach") String serach,@Param("template_id")String template_id);

    /*设置当前流程进度*/
    @Update("update process_info set template_node_name = #{status},process_status=#{process_status} where id = #{process_id} ")
    void setStatusById(@Param("status") String status,@Param("process_id") String process_id,@Param("process_status")String process_status);

    /*删除流程实例*/
    @Update("update process_info set process_status = 0 where id = #{process_id}")
    int deleteProcess(String process_id);


    /*设置当前流程文件url*/
    @Update("update process_info set process_files_word = #{0} where process_id = #{1} ")
    int setFileUrlById(String url, String process_id);

    /*获取所有流程*/
    @Select("select * from process_info where" +
            " (title like '%${serach}%' or template_name like '%${serach}%' or initiator_name like '%${serach}%' or hanlder_name like '%${serach}%') and process_status in (1,2,3)"+
            " and company_id = #{company_id}  order by create_time desc")
    List<ProcessInfo> selectAllstatus(@Param("company_id") String company_id,@Param("serach") String serach);



    /*获取所有已处理流程*/
    @Select("select * from process_info where " +
            " (title like '%${serach}%' or initiator_name like '%${serach}%' or hanlder_name like '%${serach}%') and process_status != 0 "+
            " and id in (select process_id from process_node where handler_id = #{user_id} and status in (3,4))" +
            " and template_id like '%${template_id}%' and company_id = #{company_id}" +
            " order by create_time desc ")
    List<ProcessInfo> selAllProcessed(@Param("company_id")String company_id,@Param("user_id") String user_id,@Param("serach") String serach,@Param("template_id") String template_id);


    /*获取已处理和发起的流程*/
    @Select("select * from process_info where process_status in (1,2,3) and template_id like '%${template_id}%' and company_id = #{company_id} " +
            " and (title like '%${serach}%' or initiator_name like '%${serach}%' or hanlder_name like '%${serach}%') " +
            " and (initiator_id = #{user_id} or id in (select process_id from process_node where handler_id = #{user_id} and status in (3,4)))" +
            " order by create_time desc ")
    List<ProcessInfo> selAllGoAndProcessed(@Param("company_id")String company_id ,@Param("user_id") String user_id,@Param("serach") String serach,@Param("template_id") String template_id);

}
