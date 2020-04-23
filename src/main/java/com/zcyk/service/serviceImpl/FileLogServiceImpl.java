package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.FileMapper;
import com.zcyk.mapper.SysFilelogMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.SysFileLog;
import com.zcyk.pojo.User;
import com.zcyk.service.FileLogService;
import com.zcyk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/8/27 14:02
 */
@Service
public class FileLogServiceImpl implements FileLogService {
    @Autowired
    SysFilelogMapper sysFilelogMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;


    /**
     * 功能描述：操作进日志
     * 开发人员： lyx
     * 创建时间： 2019/8/26 19:00
     * 参数： 操作（增删改） 文件名 文件类型（文件夹或者文件）
     * 返回值：
     * 异常：
     */
    public void inLog(String operation, String file_name, String fileType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        User user = userService.getNowUser(request);

        SysFileLog sysFileLog = new SysFileLog().setId(UUID.randomUUID().toString())
                .setFile_name(file_name)
                .setUser_name(user.getUser_name())
                .setFile_type(fileType)
                .setOperation(operation)
                .setOperation_time(dateFormat.format(date))
                .setCompany_id(user.getCompany_id());

        sysFilelogMapper.insertSelective(sysFileLog);
    }


    /**
     * 功能描述：获取所有的记录
     * 开发人员： lyx
     * 创建时间： 2019/8/26 19:00
     */
    public PageInfo<SysFileLog> getAllLog(int pageNum, int pageSize, String search) {
        User user = userService.getNowUser(request);
        PageHelper.startPage(pageNum, pageSize);
        List<SysFileLog> sysFileLogs = sysFilelogMapper.selectFileLog(search, user.getCompany_id());
        PageInfo<SysFileLog> pageInfo = new PageInfo<>(sysFileLogs);
        return pageInfo;
    }


}