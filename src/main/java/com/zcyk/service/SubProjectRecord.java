package com.zcyk.service;

import com.zcyk.pojo.File;
import com.zcyk.pojo.TSubrecord;
import com.zcyk.dto.ResultData;

import java.util.List;

/**
* 功能描述:分部工程记录
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/10/9 15:24
*/

public interface SubProjectRecord {


    /*新建单位工程添加记录*/
    ResultData addUnitprojectRecord(String id);


    /*查看某个单位工程的记录*/
    List<TSubrecord> selectRecordByUnitproject(String id);

    /*根据表号查询文件*/
    List<File> selectTableFile(String id);

    /*上传文件 从云盘*/
    Integer insertFileByYun(File file);

    /*删除某个单位工程记录&表格*/
    Integer deleteByUnitprojectId(String id);


}
