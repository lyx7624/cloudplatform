package com.zcyk.dto;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
* 功能描述:用户唯一登陆判断容器
 * 放在内存中
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2019/11/19 14:08
*/
public class LoginUserMap {

    /*容器：
    * */

    private static ExpiringMap<String, String> loginUsers = ExpiringMap.builder().
            expiration(1000*60*60*2, TimeUnit.MILLISECONDS)//设置单个有效时间为2个小时1000*60*60*2
            .expirationPolicy(ExpirationPolicy.ACCESSED)//过期策略为访问就刷新
            .maxSize(100000)//最大的个数为10万在线量
            .build();

 
    /**
     * 将用户和sessionId存入map
     * @param key
     * @param value
     */
    public static void setLoginUsers(String loginId, String sessionId) {
        loginUsers.put(loginId, sessionId);
    }
 
    /**
     * 获取loginUsers
     * @return
     */
    public static Map<String, String> getLoginUsers() {
        return loginUsers;
    }
 
    /**
     * 根据sessionId移除map中的值
     * @param sessionId
     */
    public static void removeUser(String sessionId) {
        for (Map.Entry<String, String> entry : loginUsers.entrySet()) {
            if (sessionId.equals(entry.getValue())) {
                loginUsers.remove(entry.getKey());
                break;
            }
        }
    }
 
    /**
     * 判断用户是否在loginusers中
     * @param loginId
     * @param sessionId
     * @return
     */
    public static Integer isInLoginUsers(String loginId, String sessionId) {
        if(!loginUsers.containsKey(loginId)){
            return 2;//登陆过期
        }
        if(loginUsers.containsKey(loginId) && sessionId.equals(loginUsers.get(loginId))){
            return 0;//表示还在
        }
        if(loginUsers.containsKey(loginId) && !sessionId.equals(loginUsers.get(loginId))){
            return 1;//异地登录
        }


        return 0;
//        return (loginUsers.containsKey(loginId) && sessionId.equals(loginUsers.get(loginId)));
    }
 
}
