package com.zcyk.service.serviceImpl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.ProcessInfoMapper;
import com.zcyk.mapper.ProcessNodeMapper;
import com.zcyk.mapper.T_OpinionMapper;
import com.zcyk.pojo.*;
import com.zcyk.service.ProcessInfoService;
import com.zcyk.service.UserService;
import com.zcyk.util.File_upload;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述: 流程实例表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:02
 */
@Service
@Transactional
public class ProcessInfoServiceImpl implements ProcessInfoService {

    @Autowired
    ProcessInfoMapper processInfoMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;

    @Autowired
    ProcessNodeMapper processNodeMapper;

    @Autowired
    T_OpinionMapper t_opinionMapper;



    @Value("${contextPath}")
    String contextPath;

    /**
     * 功能描述：新建流程实例
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:32
     * 参数： [processInfo]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData addProcess(ProcessInfo processInfo) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        User user = userService.getNowUser(request);

        processInfo.setCreate_time(dateFormat.format(date))
                .setId(UUID.randomUUID().toString())
                //.setTemplate_code_name("等待审核")//初始流程都是这个
                //设置发起人为当前登录人
                .setInitiator_name(user.getUser_name())
                .setInitiator_id(user.getId())
                .setProcess_status(1)
                .setCompany_id(user.getCompany_id());
        int i = processInfoMapper.insertSelective(processInfo);
        if(i!=0){
            return new ResultData().setMsg("添加成功").setStatus("200").setData(processInfo.getId());
        }
        return new ResultData().setMsg("添加失败").setStatus("400");
    }
    /**
     * 功能描述：删除流程实例
     * 开发人员： wjf
     * 创建时间： 2019/8/12 15:32
     * 参数： [process_id 流程id]
     * 返回值： com.zcyk.dto.ResultData
     * 异常：
     */
    public ResultData deleteProcess(List<String> process_ids){
        int i=0;
        for(String process_id:process_ids) {
            i += processInfoMapper.deleteProcess(process_id);
            //删除节点
            processNodeMapper.deleteByProcessId(process_id);
            //删除意见
            List<ProcessNode> processNodes = processNodeMapper.selByProcessId(process_id);
            for (ProcessNode processNode:processNodes){
                t_opinionMapper.deleteByNodes(processNode.getId());
            }


        }
        if(i == process_ids.size()){

            return new ResultData().setStatus("200").setMsg("删除成功");
        }else {
            return new ResultData().setStatus("400").setMsg("删除失败");
        }
    }

    /**
     * 暂时不用
     * 功能描述：获取当前发起的流程
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:49
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    @RequestMapping("selAllProcessById")
    public PageInfo<ProcessInfo> selAllGoProcessById(int pageNum,int pageSize,String serach,String template_id) {
       /* PageHelper.startPage(pageNum,pageSize/2);
        List<ProcessInfo> processInfos = processInfoMapper.selAllProcessByInitiator_Id(GetUserUtils.getUserId(request), serach, template_id);
        PageInfo<ProcessInfo> pageInfo = new PageInfo<>(processInfos);
        return pageInfo;*/
       return null;
    }

    /**
     * 功能描述：获取当前代办流程
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:49
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    public  PageInfo<ProcessInfo> selAllDoProcessById(int pageNum,int pageSize,String serach,String template_id) {
        User nowUser = userService.getNowUser(request);
        PageHelper.startPage(pageNum,pageSize);
        List<ProcessInfo> processInfos = processInfoMapper.selAllProcessByHanlder_Id(nowUser.getCompany_id(), nowUser.getId(), serach, template_id);
        PageInfo<ProcessInfo> pageInfo = new PageInfo<>(processInfos);
        return pageInfo;
    }


    /**
     * 暂时不用
     * 功能描述：获取当前登录人已处理流程
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:58
     * 参数： 流程id
     */
    public PageInfo<ProcessInfo> selAllProcessed(int pageNum,int pageSize,String serach,String template_id) {
        /*PageHelper.startPage(pageNum,pageSize/2);
        List<ProcessInfo> processInfos = processInfoMapper.selAllProcessed(GetUserUtils.getUserId(request),serach,template_id);
        PageInfo<ProcessInfo> pageInfo = new PageInfo<>(processInfos);
        return pageInfo;*/
        return null;
    }

    /**
     * 功能描述：获取当前登录人已处理流程和发起流程
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:58
     * 参数： 流程id
     */
    public PageInfo<ProcessInfo> selAllGoAndProcessed(int pageNum, int pageSize, String serach, String template_id) {
        User nowUser = userService.getNowUser(request);

  /*      //已办
        PageHelper.startPage(pageNum,pageSize/2);
        List<ProcessInfo> processInfos1 = processInfoMapper.selAllProcessed(company_id,GetUserUtils.getUserId(request),serach,template_id);
        //发起
        PageHelper.startPage(pageNum,pageSize/2);
        List<ProcessInfo> processInfos2 = processInfoMapper.selAllProcessByInitiator_Id(company_id,GetUserUtils.getUserId(request), serach, template_id);
        //合二为一
        processInfo.addAll(processInfos1);
        processInfo.addAll(processInfos2);*/
        // 没分开的
        PageHelper.startPage(pageNum,pageSize);
        List<ProcessInfo>  processInfos = processInfoMapper.selAllGoAndProcessed(nowUser.getCompany_id(), nowUser.getId(), serach, template_id);
        PageInfo<ProcessInfo> pageInfo = new PageInfo<>(processInfos);
        return pageInfo;
    }

    /**
     * 功能描述：获取所有流程
     * 开发人员： lyx
     * 创建时间： 2019/8/12 15:49
     * 参数： []
     * 返回值： java.util.List<com.zcyk.pojo.ProcessInfo>
     * 异常：
     */
    public PageInfo<ProcessInfo> selectAllProcess(int pageNum, int pageSize, String serach){
        String company_id = userService.getNowUser(request).getCompany_id();
        PageHelper.startPage(pageNum,pageSize);
        List<ProcessInfo> processInfos = processInfoMapper.selectAllstatus(company_id, serach);
        PageInfo<ProcessInfo> pageInfo = new PageInfo<>(processInfos);
        return pageInfo;
    }

    /**
     * 功能描述：设置当前流程状态
     * 开发人员： lyx
     * 创建时间： 2019/8/12 16:31
     * 参数： [process_id, status]
     * 返回值： void
     * 异常：
     */
    public void setProcessStatusById(String process_id, String status,String process_status) {
        processInfoMapper.setStatusById(status,process_id,process_status);
    }

    /**
     * 功能描述：设置当前流程文件url
     * 开发人员： lyx
     * 创建时间： 2019/8/12 16:31
     * 参数： [process_id, status]
     * 返回值： void
     * 异常：
     */
    public void setFileUrlById(String process_id, String url) {
        processInfoMapper.setFileUrlById(url,process_id);
    }






    /**
     * 功能描述：设置节点处理人
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:36
     */
    public void setProcessHanlder(String process_id,String handler_id, String handler_name) {
        processInfoMapper.updateByPrimaryKeySelective(new ProcessInfo()
                .setId(process_id)
                .setHanlder_id(handler_id)
                .setHanlder_name(handler_name)
                .setTemplate_node_name("等待"+handler_name+"处理"));
    }


    /**
     * 功能描述：查询该流程所有节点
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:58
     * 参数： 流程id
     */
    public List<ProcessNode> selAllDoNodeById(String process_id) {
        List<ProcessNode> processNodes = processNodeMapper.selByProcessId(process_id);
        for(ProcessNode processNode : processNodes){
            List<T_Opinion> opinions = t_opinionMapper.selectByNodeId(processNode.getId());
            processNode.setOpinions(opinions);
        }
        return processNodes;
    }

    /**
     * 功能描述：查询流程
     * 开发人员： lyx
     * 创建时间： 2019/8/23 16:58
     * 参数： 流程id
     */
    public ProcessInfo selOneProcess(String process_id) {
        return processInfoMapper.selectByPrimaryKey(process_id);
    }



}
