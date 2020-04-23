package com.zcyk.filter;

import com.zcyk.mapper.UserMapper;
import com.zcyk.dto.LoginUserMap;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Autowired
    UserMapper userMapper;

    /**
     * 创建session时候的动作
     * @param event
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {

    }
 
    /**
     * 销毁session时候的动作
     * @param event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
//        HttpSession session = event.getSession();
//        String sessionId = session.getId();
        //移除loginUsers中已经被销毁的session
//        LoginUserMap.removeUser(sessionId);
//        System.out.println(userMapper.selectUserById((String)session.getAttribute("user_id")).getUser_name() +"退出登录");
        }
}
