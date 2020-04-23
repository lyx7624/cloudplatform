package com.zcyk.service;

import com.github.pagehelper.PageInfo;
import com.zcyk.pojo.SysFileLog;

/**
 * 功能描述:文件日志实体类
 * 开发人员: lyx
 * 创建日期: 2019/8/27 14:01
 */
public interface FileLogService {
    void inLog(String operation, String file_name, String fileType)throws Exception;

    PageInfo<SysFileLog> getAllLog(int pageNum, int pageSize, String search)throws Exception;
}