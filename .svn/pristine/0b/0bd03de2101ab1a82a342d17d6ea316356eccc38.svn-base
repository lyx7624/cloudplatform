package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
* 功能描述: token类
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2020-4-15 10:06
*/
@Data
@Entity
@Table(name = "t_token")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TToken {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;

    private String val;

    private Long expireTime;

    private Long createTime;

    private Long updateTime;


    public TToken(String jwtToken, long currentTimeMillis) {
    }
    public TToken() {
    }
}
