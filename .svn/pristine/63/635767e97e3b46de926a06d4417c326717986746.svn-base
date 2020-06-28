package com.zcyk.service.serviceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.*;
import com.zcyk.pojo.*;
import com.zcyk.service.ProcessInfoService;
import com.zcyk.service.ProcessNodeService;
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
* 功能描述:流程节点表
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/8/11 9:02
*/

@Service
@Transactional
public class ProcessNodeServiceImpl implements ProcessNodeService {

    @Autowired
    ProcessNodeMapper processNodeMapper;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;
    @Autowired
    T_OpinionMapper t_opinionMapper;
    @Autowired
    ProcessInfoService processInfoService;

    @Autowired
    ProcessInfoMapper processInfoMapper;

    @Autowired
    TemplateMapper templateMapper;

    @Autowired
    UserMapper userMapper;

    /**
     * 功能描述：流程节点实例
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:36
     * 参数： [processNode]
     * 返回值： void
     * 异常：
     */
    public void addProcessNode(ProcessNode processNode) {

       /* if(processNode.getNode_order()==0){//设置序号为1节点处理人为当前发起人？是否设置id
            processNode.setHandler(userService.selectOneUser(GetUserUtils.getUserId(request)).getUser_name());
        }*/
        processNode.setId(UUID.randomUUID().toString());
        processNodeMapper.insertSelective(processNode);
    }

    /**
     * 功能描述：处理人处理流程
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:25
     * 参数： [processNode, t_opinion]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData updaProcessNodeByHandler(ProcessNode processNode2,T_Opinion t_opinion) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ProcessNode processNode = processNodeMapper.selectByPrimaryKey(processNode2.getId());
        processNode.setHandler_time(dateFormat.format(date))
                    .setStatus(processNode2.getStatus());
        int i = 0;
        //将处理结果存入数据库
        i+=processNodeMapper.updateByPrimaryKeySelective(processNode);

        //设置审批意见
        t_opinion.setCreat_time(dateFormat.format(date))
                .setProcess_node_id(processNode.getId())
                .setId(UUID.randomUUID().toString())
                .setStatus(1);
                //.setUser_id(GetUserUtils.getUserId(request));//要userid有毛线用
        i+=t_opinionMapper.insertSelective(t_opinion);

        //查询该节点的流程
        String process_id = processNodeMapper.selectByPrimaryKey(processNode.getId()).getProcess_id();

        ProcessInfo processInfo = new ProcessInfo().setId(process_id);
        //具体处理
        if(processNode.getStatus()==4 && i==2){//退回
            //查询到上一个流程节点
            ProcessNode lastNode = processNodeMapper.seletcNode(process_id,processNode.getNode_order()-1);
            if(lastNode==null){//如何处理第一节点就退回的情况？
                //设置流程为已完成
                processInfoMapper.updateByPrimaryKeySelective(processInfo
                        .setTemplate_node_name(processNode.getHandler_name()+"退回，"+"申请未通过")
                        .setProcess_status(3));
            }else{
                //设置上级节点为未处理
                processNodeMapper.setStatus(process_id,lastNode.getNode_order());
                //设置流程的当前状态和处理人
                processInfoMapper.updateByPrimaryKeySelective(processInfo
                                .setTemplate_node_name(processNode.getHandler_name()+"退回，"+"待"+lastNode.getHandler_name()+"处理")
                                .setProcess_status(1)
                                .setHanlder_id(lastNode.getHandler_id())
                                .setHanlder_name(lastNode.getHandler_name()));
            }
            return new ResultData().setMsg("处理成功").setStatus("200");
        }
        if(processNode.getStatus()==3 && i==2){//同意
            //查询到下一个流程节点
            ProcessNode nextNode = processNodeMapper.seletcNode(process_id,processNode.getNode_order()+1);
            if(nextNode==null){//为空表示下一个节点没有了，就是流程完成
                processInfoMapper.updateByPrimaryKeySelective(processInfo
                        .setTemplate_node_name(processNode.getHandler_name()+"已处理，"+"申请通过")
                        .setProcess_status(2));
                return new ResultData().setMsg("流程结束").setStatus("201");
            }else{
                //设置下级节点为未处理；为什么要设置，就是退回后再处理操作
                processNodeMapper.setStatus(process_id,nextNode.getNode_order());
                //设置流程的当前状态
                processInfoMapper.updateByPrimaryKeySelective(processInfo
                        .setTemplate_node_name(processNode.getHandler_name()+"已处理，"+"待"+nextNode.getHandler_name()+"处理")
                        .setProcess_status(1)
                        .setHanlder_id(nextNode.getHandler_id())
                        .setHanlder_name(nextNode.getHandler_name()));
                return new ResultData().setMsg("处理成功").setStatus("200");
            }
        }

        return new ResultData().setMsg("处理失败").setStatus("400");

    }


    /**
     * 功能描述：查询所有的模板
     * 开发人员： lyx
     * 创建时间： 2019/8/21 11:38
     * 参数：
     * 返回值：
     * 异常：
     */
    public  PageInfo<T_Template> selectAllTemplate(int pageNum,int pageSize,String serach) {
        User user = userService.getNowUser(request);
        PageHelper.startPage(pageNum, pageSize);
        List<T_Template> t_templates = templateMapper.selectAllTemplate(user.getCompany_id(), serach);
        PageInfo<T_Template> pageInfo = new PageInfo<>(t_templates);
        return pageInfo;
    }

    /**
     * 功能描述：查询所有的处理人
     * 开发人员： lyx
     * 创建时间： 2019/8/21 11:38
     * 参数：
     */
    public List<User> getUsers() {
        User user = userService.getNowUser(request);
        return userMapper.selectAllUser(user.getCompany_id());
    }
}
