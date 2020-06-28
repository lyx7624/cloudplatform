package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * 视频监控应用表
 * @author WuJieFeng
 * @date 2020/4/14 10:46
 */
@Entity
@Data
@Accessors(chain = true)
@Table(name = "live_app")
@JsonInclude
public class LiveApp {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*项目id*/
    private String project_id;
    /*设备序列号*/
    private String app_id;

    private String secret_id;

    private String device_id;

    private String device_code;

    /*token*/
    private String access_token;

    private String app_name;

    private Date token_time;

    private Integer status;

    private Integer type;
}
