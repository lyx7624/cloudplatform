package com.zcyk.service.serviceImpl;

import com.zcyk.mapper.TemplateMapper;
import com.zcyk.mapper.TemplateNodeMapper;
import com.zcyk.pojo.T_Template;
import com.zcyk.pojo.TemplateNode;
import com.zcyk.service.TemplateNodeService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* 功能描述: 流程节点服务
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:15
*/
@Service
@Transactional
public class TemplateNodeServiceImpl implements TemplateNodeService {

    @Autowired
    TemplateNodeMapper templateNodeMapper;
    @Autowired
    TemplateMapper templateMapper;


    /**
     * 功能描述：查询该模板的所有节点 按照顺序给出
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:11
     * 参数： templateNodes所有的节点 template_id模板id
     */
    public List<TemplateNode> selNodeByTemplateId(String template_id) {
        return templateNodeMapper.selectByTemplateId(template_id);
    }


    /**
     * 功能描述：t添加流程模板
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:11
     * 参数： templateNodes所有的节点 template_id模板id
     */
    public ResultData addTemplateNode(List<TemplateNode> templateNodes , String template_id) {
        int i = 1;
        for (TemplateNode templateNode :templateNodes ){
            templateNode.setId(UUID.randomUUID().toString())
                        .setTemplate_id(template_id)
                        .setNode_order(i)//设置节点顺序
                        .setStatus(1);
             i+=templateNodeMapper.insertSelective(templateNode);
        }
        if(i-1==templateNodes.size()){
            return new ResultData().setMsg("操作成功").setStatus("200");
        }
        return new ResultData().setMsg("操作失败").setStatus("400");
    }


    /**
     * 功能描述：查询模板信息
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:11
     * 参数： templateNodes所有的节点 template_id模板id
     */
    public Map<String,Object> templateNodeInfo(String template_id){
        Map<String,Object> map = new HashMap<>();
        List<TemplateNode> templateNodes = selNodeByTemplateId(template_id);
        T_Template t_template = templateMapper.selcetTemplateById(template_id);
        map.put("tempalteNodeInfo",templateNodes);
        map.put("template",t_template);
        map.put("size",templateNodes.size());
        return map;
    }


    /**
     * 功能描述：修改节点
     * 开发人员： lyx
     * 创建时间： 2019/8/26 16:15
     * 参数： 节点数据，模板id
     * 返回值：
     * 异常：
     */
    public ResultData updateTemplateNode(ArrayList<TemplateNode> templateNodes, String template_id) {
        int i = 0;
        //如果节点有删除，不好判断，直接删除原先所有节点，再添加
        templateNodeMapper.deleteByTemplateId(template_id);
        ResultData resultData = addTemplateNode(templateNodes, template_id);
       return resultData;
    }
}
