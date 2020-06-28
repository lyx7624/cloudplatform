package com.zcyk.service;

import com.zcyk.pojo.TemplateNode;
import com.zcyk.dto.ResultData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述: 流程节点服务
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:15
 */
public interface TemplateNodeService {

    /*新增节点模板*/
    ResultData addTemplateNode(List<TemplateNode> templateNodes, String template_id)throws Exception;


    List<TemplateNode> selNodeByTemplateId(String template_id)throws Exception;

    /*查询模板信息*/
    Map<String, Object>templateNodeInfo(String template_id)throws Exception;

    /*修改模板节点*/
    ResultData updateTemplateNode(ArrayList<TemplateNode> templateNodes, String template_id)throws Exception;
}
