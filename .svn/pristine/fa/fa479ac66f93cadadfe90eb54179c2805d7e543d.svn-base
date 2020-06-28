package com.zcyk.service;


import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.ProcessNode;
import com.zcyk.pojo.T_Opinion;
import com.zcyk.pojo.T_Template;
import com.zcyk.pojo.User;
import com.zcyk.dto.ResultData;

import java.util.List;

/**
 * 功能描述:流程节点表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */

public interface ProcessNodeService {


    /*实例流程*/
    void addProcessNode(ProcessNode processNode)throws Exception;

    /*处理流程*/
    ResultData updaProcessNodeByHandler(ProcessNode processNode, T_Opinion t_opinion)throws Exception;

    /*查询所有的模板*/
    PageInfo<T_Template> selectAllTemplate(int pageNum, int pageSize, String serach)throws Exception;

    /*查询到所有的处理人*/
    List<User> getUsers()throws Exception;
}
