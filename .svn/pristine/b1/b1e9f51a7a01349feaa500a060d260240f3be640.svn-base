package com.zcyk.service.serviceImpl;

import com.zcyk.mapper.LogMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.SysLog;
import com.zcyk.pojo.User;
import com.zcyk.service.LogService;
import com.zcyk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/7/30 16:53
 */
@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    LogMapper logmapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    UserService userService;


    /**
     * 功能描述：添加日志记录
     * 开发人员： lyx
     * 创建时间： 2019/7/30 17:25
     * 参数：
     * 返回值：
     * 异常：
     */
    public void put(String conment, String methodName, String module, String description) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        User user = userService.getNowUser(request);

        SysLog sysLog = new SysLog();
        sysLog.setUpdatetime(dateFormat.format(date));
        sysLog.setUsername(user.getUser_name());
        sysLog.setContent(conment);
        sysLog.setModule(module);
        sysLog.setDescription(description);
        //保存日志
        logmapper.insertSelective(sysLog);

    }


}