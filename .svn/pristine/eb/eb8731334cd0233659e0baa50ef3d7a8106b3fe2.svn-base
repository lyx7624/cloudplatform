package com.zcyk.service.serviceImpl;


import com.zcyk.mapper.TemplateMapper;
import com.zcyk.mapper.TemplateNodeMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.T_Template;
import com.zcyk.pojo.User;
import com.zcyk.service.TemplateService;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
* 功能描述: 模板表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:35
*/
@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    TemplateMapper templateMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    TemplateNodeMapper templateNodeMapper;

    /**
     * 功能描述：新增模板
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:16
     * 参数： [template]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData addTemplate(T_Template template) {
        User nowUser =userService.getNowUser(request);

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        template.setCreate_time(dateFormat.format(date))
                .setId(UUID.randomUUID().toString())
                .setTemplate_status(1)
                .setCompany_id(nowUser.getCompany_id())
                .setCreate_user(nowUser.getUser_name()!=null?nowUser.getUser_name():"");
        if(templateMapper.selectTemplateByName(template)!=null){
            return new ResultData().setMsg("模板已存在").setStatus("901");
        }
        int i = templateMapper.insertSelective(template);
        if(i!=0){
            return new ResultData().setMsg("操作成功").setStatus("200").setData(template.getId());
        }
        return new ResultData().setMsg("操作失败").setStatus("900");
    }
    /**
     * 功能描述：删除模板
     * 开发人员： wjf
     * 创建时间： 2019/8/25 19:41
     * 参数： [template]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData deleteTemplate(List<String> template_ids){
        int i=0;
        for(String template_id:template_ids) {
             i += templateMapper.deleteTemplate(template_id);
             //同时删除节点
            templateNodeMapper.deleteByTemplateId(template_id);
        }
        if(i==template_ids.size()){
            return new ResultData().setStatus("200").setMsg("操作成功");
        }else {
            return new ResultData().setStatus("400").setMsg("操作失败");
        }
    }


    /**
     * 功能描述：修改模板
     * 开发人员： lyx
     * 创建时间： 2019/8/26 15:49
     */
    public ResultData updateTemplate(T_Template t) {
        User nowUser =userService.getNowUser(request);

        t.setCompany_id(nowUser.getCompany_id());
        T_Template t_template = templateMapper.selcetTemplateById(t.getId());
        if(!t_template.getTemplate_name().equals(t.getTemplate_name())&&templateMapper.selectTemplateByName(t)!=null){
            return new ResultData().setMsg("模板已存在").setStatus("901");
        }
        int i = templateMapper.updateByPrimaryKeySelective(t);
        if(i!=0){
            return new ResultData().setStatus("200").setMsg("操作成功");
        }else {
            return new ResultData().setStatus("400").setMsg("操作失败");
        }
    }
}
