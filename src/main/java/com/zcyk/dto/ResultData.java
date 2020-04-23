package com.zcyk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 功能描述:
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/7/18 16:32
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class ResultData implements Serializable {
    /*请求状态*/
    private String status;
    /*请求结果*/
    private String msg;
    /*请求数据*/
    private Object data;

    public ResultData(){
        this.msg = "操作成功";
        this.status = "200";
    }

    public static ResultData failResultData (){
       return new ResultData("400","操作失败",null);
    }


}