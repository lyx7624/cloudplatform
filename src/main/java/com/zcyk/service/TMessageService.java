package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.TMessage;
import com.zcyk.dto.ResultData;

import java.util.List;

/**
 * 功能描述:消息接口
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/9/6 11:14
 */
public interface TMessageService {


    /*查询登录人的所有消息*/
    PageInfo<TMessage> selAllMessage(int pageNum, int pageSize, String search)throws Exception;

    /*查询单个消息*/
    TMessage selOneMessage(String id)throws Exception;

    /*删除消息*/
    ResultData deleteMessage(List<String> allid)throws Exception;

    /*发送消息*/
    ResultData sendMessage(TMessage tMessages, List<String> ids)throws Exception;
}
