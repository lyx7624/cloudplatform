package com.zcyk.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.TMessage;
import com.zcyk.service.TMessageService;
import com.zcyk.dto.PageData;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 功能描述: 消息控制器
 * 开发人员: lyx
 * 创建日期: 2019/9/6 11:15
 */
@Controller
public class TMessageController {


    @Autowired
    TMessageService tMessageService;


    /**
     * 功能描述： 查询所有消息(查询登录人的消息)
     * 开发人员： lyx
     * 创建时间： 2019/9/6 11:16
     * 参数：分页参数和搜索参数
     */
    public PageInfo<TMessage> getAllMessage
    (@RequestParam(defaultValue = "1") int pageNum,
     @RequestParam(defaultValue = "10") int pageSize,
     @RequestParam(defaultValue = "") String search) throws Exception {
        return tMessageService.selAllMessage(pageNum, pageSize, search);
    }

    /**
     * 功能描述： 查询查看单个消息
     * 开发人员： lyx
     * 创建时间： 2019/9/6 11:16
     * 参数：该消息id
     * 返回值：消息体
     */
    public TMessage lookMessage(String id) throws Exception {
        return tMessageService.selOneMessage(id);
    }

    /**
     * 功能描述： 删除消息（个人）
     * 创建时间： 2019/9/6 11:16
     * 参数：
     * 返回值：
     * 异常：
     */
    public ResultData delMessage(String ids) throws Exception {
        List<String> allid = JSON.parseArray(ids, String.class);
        return tMessageService.deleteMessage(allid);
    }

    /**
     * 功能描述： 发送
     * 创建时间： 2019/9/6 11:16
     * 参数：tMessage 消息体 ids 接收人的id
     * 返回值：
     * 异常：
     */
    public ResultData sendMessage(@RequestBody PageData pageData) throws Exception{

        TMessage tMessages = JSON.parseObject(JSON.toJSONString(pageData.get("tMessage")), TMessage.class);
        //如果全部企业则为空，所以多少传点东西
        List<String> ids = JSON.parseArray(JSON.toJSONString(pageData.get("ids")), String.class);
        return tMessageService.sendMessage(tMessages, ids);
    }


}