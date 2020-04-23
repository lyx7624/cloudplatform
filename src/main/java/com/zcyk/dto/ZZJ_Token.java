package com.zcyk.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 功能描述: token的信息类
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/13 9:10
 */
@Data
@Accessors(chain = true)
public class ZZJ_Token implements Serializable {

    private static final long serialVersionUID = 6314027741784310221L;

    private String access_token;
    /**
     * 有效时间
     */
    private Long expires_in;
    private Long login_time;


    private String token_type;

    private String refresh_token;
    private String user_id;


}
