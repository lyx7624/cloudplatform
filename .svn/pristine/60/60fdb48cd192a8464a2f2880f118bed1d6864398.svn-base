package com.zcyk.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.*;
import com.zcyk.service.ProcessNodeService;
import com.zcyk.service.TemplateNodeService;
import com.zcyk.service.TemplateService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
@RequestMapping("TemplateNode")
@RestController
public class TemplateNodeController {

    @Autowired
    TemplateNodeService templateNodeService;
    @Autowired
    TemplateService templateService;

    @Autowired
    ProcessNodeService processNodeService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    HttpServletRequest request;

    /**
     * 功能描述：新建节点模板
     * 开发人员： lyx
     * 创建时间： 2019/8/11 9:23
     * 参数： [templateNode
     * 节点名称 node_name;
     * 模板id template_name;
     * 节点顺序 template_order;
     * 备注 remark;
     * 处理人 handler;
     * 是否需要盖章 int isstamp;]
     * 返回值： com.zcyk.dto.ResultData
     */
    @RequestMapping("addTemplateNode")
    @ResponseBody
    public ResultData addTemplateNode(@RequestBody Map<String, Object> map) throws Exception {

        ArrayList<TemplateNode> templateNodes = null;
        try {
            templateNodes = mapper.readValue(mapper.writeValueAsString(map.get("templateNodes")), new TypeReference<ArrayList<TemplateNode>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        T_Template t = new T_Template();
        t.setTemplate_name(map.get("template_name").toString());

        ResultData rd = templateService.addTemplate(t);
        if (!"200".equals(rd.getStatus())) {
            return rd;
        }

        return templateNodeService.addTemplateNode(templateNodes, (String) rd.getData());
    }

    /**
     * 功能描述：实现节点处理
     * 开发人员： lyx
     * 创建时间： 2019/8/11 12:05
     * 参数： [processNode
     * id
     * isstamp是否盖章
     * status 1 同意 2 退回
     * content 意见]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("disposeNode")
    @ResponseBody
    public ResultData disposeNode(ProcessNode processNode, T_Opinion t_opinion) throws Exception {
        return processNodeService.updaProcessNodeByHandler(processNode, t_opinion);

    }


    /**
     * 功能描述：查询所有的模板
     * 开发人员： lyx
     * 创建时间： 2019/8/11 9:23
     */
    @RequestMapping("/getAllTemplateNode")
    @ResponseBody
    public PageInfo<T_Template> addTemplateNode
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search) throws Exception {

        return processNodeService.selectAllTemplate(pageNum, pageSize, search);

    }


    /**
     * 功能描述：查询所有的处理人
     * 开发人员： lyx
     * 创建时间： 2019/8/11 9:23
     */
    @RequestMapping("/getAllTemplateUser")
    @ResponseBody
    public List<User> getUsers() throws Exception {
        return processNodeService.getUsers();

    }

    /**
     * 功能描述：查询模板信息
     * 开发人员： lyx
     * 创建时间： 2019/8/11 9:23
     */
    @RequestMapping("/templateNodeInfo")
    @ResponseBody
    public Map<String, Object> templateNodeInfo(String template_id) throws Exception {
        return templateNodeService.templateNodeInfo(template_id);
    }

    /**
     * 功能描述：删除模板
     * 开发人员： wjf
     * 创建时间： 2019/8/25 19:41
     * 参数： [template_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/deleteTemplate")
    @ResponseBody
    public ResultData deleteTemplate(@RequestBody Map<String, Object> map) throws Exception {
        List<String> template_ids = null;
        template_ids = mapper.readValue(mapper.writeValueAsString(map.get("template_ids")), new TypeReference<ArrayList<String>>() {});

        return templateService.deleteTemplate(template_ids);
    }

    /**
     * 功能描述：修改模板
     * 开发人员： lyx
     * 创建时间： 2019/8/25 19:41
     * 参数： [template_id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    @RequestMapping("/updateTemplate")
    @ResponseBody
    ResultData updateTemplate(@RequestBody Map<String, Object> map) throws Exception {
        ArrayList<TemplateNode> templateNodes = null;
        try {
            templateNodes = mapper.readValue(mapper.writeValueAsString(map.get("templateNodes")), new TypeReference<ArrayList<TemplateNode>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        T_Template t = new T_Template();
        t.setTemplate_name(map.get("template_name").toString());
        t.setId(map.get("template_id").toString());
        //修改名称
        ResultData rd = templateService.updateTemplate(t);
        if (!"200".equals(rd.getStatus())) {
            return rd;
        }
        //修改该模板节点
        return templateNodeService.updateTemplateNode(templateNodes, map.get("template_id").toString());
    }


}
