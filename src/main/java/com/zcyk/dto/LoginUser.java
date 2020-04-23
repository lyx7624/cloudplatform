package com.zcyk.dto;

import com.zcyk.pojo.User;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 功能描述: 登录用户
 * 开发人员: xlyx
 * 创建日期: 2020-4-15 10:05
 */
@Data
@Accessors(chain = true)
public class LoginUser extends User {

    private String token;
    /** 登陆时间戳（毫秒） */
    private Long loginTime;
    /** 过期时间戳 */
    private Long expireTime;
}