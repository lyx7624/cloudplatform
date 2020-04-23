package com.zcyk.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 功能描述:签批意见表
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/11 9:03
 */
@Data
@Entity
@Table(name = "t_opinion")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class T_Opinion {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    @JsonIgnore
    private String id;
    /*处理人id*/
    @JsonIgnore
    private String user_id;

    @JsonIgnore
    private String user_name;
    private String content;

    private String creat_time;
    @JsonIgnore
    private String process_node_id;

    private Integer status;


}
