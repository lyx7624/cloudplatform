package com.zcyk.service;


import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.ProcessInfo;
import com.zcyk.pojo.ProcessNode;
import com.zcyk.dto.ResultData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 功能描述: 流程实例表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */

public interface ProcessInfoService {


    /*新建流程实例*/
    ResultData addProcess(ProcessInfo processInfo)throws Exception;

    /*删除流程实例*/
    ResultData deleteProcess(List<String> process_ids)throws Exception;


    /*获取当前登录人的待办流程*/
    PageInfo<ProcessInfo> selAllDoProcessById(int pageNum, int pageSize, String serach, String template_id)throws Exception;

    /*获取所有流程*/
    PageInfo<ProcessInfo> selectAllProcess(int pageNum, int pageSize, String serach)throws Exception;

    /*设置当前流程状态*/
    void setProcessStatusById(String process_id, String status, String process_status)throws Exception;


    /*设置当前流程状态*/
    void setProcessHanlder(String process_id, String handler_id, String handler_name)throws Exception;

    /*查询到该流程所有节点*/
    List<ProcessNode> selAllDoNodeById(String process_id)throws Exception;

    /*查询流程 根据 id*/
    ProcessInfo selOneProcess(String process_id)throws Exception;

    /*暂时不用 获取当前登录人的发起流程*/
    PageInfo<ProcessInfo> selAllGoProcessById(int pageNum, int pageSize, String serach, String template_id)throws Exception;

    /*暂时不用 获取当前登录人已处理流程*/
    PageInfo<ProcessInfo> selAllProcessed(int pageNum, int pageSize, String serach, String template_id)throws Exception;

    /*获取发起流程和已办流程*/
    PageInfo<ProcessInfo> selAllGoAndProcessed(int pageNum, int pageSize, String serach, String template_id)throws Exception;

}
